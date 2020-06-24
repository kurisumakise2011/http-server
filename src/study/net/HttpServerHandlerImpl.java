package study.net;

import study.net.exception.HttpLogicException;
import study.net.exception.ThreadRuntimeException;
import study.net.exception.UnsupportedServerOperation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerHandlerImpl implements HttpServerHandler {
    private static final Logger LOG = Logger.getLogger(HttpServerHandlerImpl.class.getName());

    private ExceptionalHandler exceptionalHandler;
    private DispatcherWebController webController = new DispatcherWebController();

    public HttpServerHandlerImpl(ExceptionalHandler exceptionalHandler) {
        this.exceptionalHandler = exceptionalHandler;
    }

    @Override
    public void handle(ServerThreadDelegator thread, Socket socket) {
        processStreams(thread, socket);
    }

    protected void processStreams(ServerThreadDelegator thread, Socket socket) {
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {
            exceptionalHandler.handle(() -> {
                try {
                    BodyHeaderPair pair = readStream(input);
                    if (pair.headers().isEmpty()) {
                        return;
                    }
                    HttpRequest request = httpRequest(thread, socket, pair);
                    HttpResponse response = httpResponse(request.protocol());
                    webController.dispatch(response, request, thread);
                    writeStream(response, output);
                } catch (IOException | URISyntaxException e) {
                    throw new ThreadRuntimeException(e);
                }
            }, (body) -> {
                String protocol = thread.getConfiguration().getProtocol();
                HttpResponse response = new HttpResponseImpl(body.code(), Charset.defaultCharset().name(), body.stream(), body.contentType(), protocol);
                writeStream(response, output);
            });
        } catch (IOException e) {
            throw new ThreadRuntimeException("IO exception :: ", e);
        } finally {
            HandlerThread t = (HandlerThread)Thread.currentThread();
            t.finish();
        }
    }

    private HttpRequest httpRequest(ServerThreadDelegator delegator, Socket socket, BodyHeaderPair pair) throws MalformedURLException, URISyntaxException {
        String[] lines = pair.headers().split("\r\n");
        if (lines.length < 2) {
            throw new HttpLogicException("could not parse request", 400);
        }

        String info = lines[0];
        String[] values = info.split(" ");

        String method = values[0];
        if (!"GET".equalsIgnoreCase(method)) {
            throw new UnsupportedServerOperation("server does not support method :: " + method);
        }

        String path = values[1];

        if (path.length() > delegator.getConfiguration().getUrlMaxSize()) {
            throw new HttpLogicException("URI too long", 414);
        }

        int queryIndex = path.indexOf("?");
        String query = null;
        if (queryIndex != -1) {
            query = path.substring(queryIndex + 1);
        }
        String protocol = values[2];

        URI uri = new URI("http", null, socket.getLocalAddress().getHostName(), socket.getLocalPort(), path, query, null);
        URL url = uri.toURL();

        Map<String, List<String>> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] kv = lines[i].split(":");
            headers.computeIfAbsent(kv[0].trim(), (k) -> new LinkedList<>()).addAll(Arrays.asList(kv[1].trim().split(";")));
        }

        Map<String, List<String>> params = resolveParamsByMethod(query, HttpMethod.valueOf(method));

        return new HttpRequestImpl(uri, url, method, params, headers, socket.getInetAddress().getHostAddress(), Charset.defaultCharset().name(), pair.body(), protocol);
    }

    private Map<String, List<String>> resolveParamsByMethod(String query, HttpMethod method) {
        if (HttpMethod.GET == method) {
            return getParametersFromQuery(query);
        } else if (HttpMethod.HEAD == method) {
            return getParametersFromQuery(query);
        } else if (HttpMethod.POST == method) {
            // handling post and etc ...
            return Collections.emptyMap();
        }
        return Collections.emptyMap();
    }

    private Map<String, List<String>> getParametersFromQuery(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> parameters = new HashMap<>();
        String[] values = query.split("&");

        for (String value : values) {
            String[] kv = value.split("=");
            parameters.computeIfAbsent(kv[0].replaceFirst("\\[\\d+]", ""), k -> new LinkedList<>()).add(kv[1]);
        }

        return parameters;
    }

    private HttpResponse httpResponse(String protocol) {
        return new HttpResponseImpl(protocol);
    }

    private BodyHeaderPair readStream(InputStream input) throws IOException {
        byte[] buffer = new byte[2 << 12];
        int n;
        while ((n = input.read(buffer, 0, buffer.length)) != -1) {
            String http = new String(buffer, 0, n);
            if (http.endsWith("\r\n\r\n")) {
                return new BodyHeaderPair(http, null);
            }
        }
        // Because of only GET method is supported so there is no need to handle body
        return new BodyHeaderPair("", null);
    }

    void writeStream(HttpResponse response, OutputStream output) {
        try {
            output.write((response.protocol() + " " + response.status() + "\r\n").getBytes());
            for (Map.Entry<String, List<String>> entry : response.headers().entrySet()) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                output.write((key + ": " + String.join("; ", value) + "\r\n").getBytes(response.charset()));
            }
            ByteArrayOutputStream stream = response.body();
            output.write("\r\n".getBytes(response.charset()));
            output.write(stream.toByteArray());
            output.flush();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "IO exception", e);
            throw new HttpLogicException("could not make a response", 500);
        }
    }

}
