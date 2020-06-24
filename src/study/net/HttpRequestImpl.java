package study.net;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {
    private Map<String, Object> attributes = new HashMap<>();
    private Map<String, List<String>> headers;
    private Map<String, List<String>> parameters;
    private URI uri;
    private URL url;
    private String address;
    private String method;
    private String charset;
    private InputStream body;
    private String protocol;

    public HttpRequestImpl(URI uri, URL url, String method, Map<String, List<String>> parameters,
                           Map<String, List<String>> headers, String address, String charset, InputStream body,
                            String protocol) {
        this.uri = uri;
        this.url = url;
        this.method = method;
        this.parameters = parameters;
        this.headers = headers;
        this.address = address;
        this.charset = charset;
        this.body = body;
        this.protocol = protocol;
    }

    @Override
    public String protocol() {
        return protocol;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public String uriAsString() {
        return uri.getPath();
    }

    @Override
    public String contextPath() {
        return uri.getPath();
    }

    @Override
    public URL url() {
        return url;
    }

    @Override
    public String urlAsString() {
        return url.toExternalForm();
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }

    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    @Override
    public void putAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @Override
    public String getParameter(String name) {
        List<String> params = parameters.get(name);
        return params != null && !params.isEmpty() ? params.iterator().next()  : null;
    }

    @Override
    public List<String> getParameters(String name) {
        return parameters.get(name);
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public List<String> headers(String key) {
        return headers.get(key);
    }

    @Override
    public String header(String key) {
        List<String> list = headers.get(key);
        return list != null && !list.isEmpty() ? list.iterator().next() : null;
    }

    @Override
    public String address() {
        return address;
    }

    @Override
    public void encoding(String charset) {
        this.charset = charset;
    }

    @Override
    public String encoding() {
        return charset;
    }

    @Override
    public InputStream body() {
        return body;
    }

    @Override
    public Reader charBody() {
        return new InputStreamReader(body);
    }

    @Override
    public int port() {
        return uri.getPort();
    }
}
