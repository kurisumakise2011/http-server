package study.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {
    private int code;
    private Map<String, List<String>> headers = new HashMap<>();
    private String charset;
    private int length;
    private ByteArrayOutputStream output;
    private String protocol;

    public HttpResponseImpl(String protocol) {
        this(200, Charset.defaultCharset().name(), 0, new ByteArrayOutputStream(), null, protocol);
    }

    public HttpResponseImpl(ByteArrayOutputStream output, String protocol) {
        this(200, Charset.defaultCharset().name(), 0, output, null, protocol);
    }

    public HttpResponseImpl(int code, ByteArrayOutputStream output, String protocol) {
        this(code, Charset.defaultCharset().name(), 0, output, null, protocol);
    }

    public HttpResponseImpl(String charset, ByteArrayOutputStream output, String protocol) {
        this(200, charset, 0, output, null, protocol);
    }

    public HttpResponseImpl(String charset, ByteArrayOutputStream output, String contentType, String protocol) {
        this(200, charset, 0, output, contentType, protocol);
    }

    public HttpResponseImpl(int code, String charset, ByteArrayOutputStream output, String contentType, String protocol) {
        this(code, charset, 0, output,  contentType, protocol);
    }

    public HttpResponseImpl(int code, String charset, int length, ByteArrayOutputStream output, String contentType, String protocol) {
        this.charset = charset;
        this.length = length;
        this.output = output;
        this.code = code;
        if (contentType != null) {
            this.header("Content-Type", contentType);
        }
        this.protocol = protocol;
    }

    @Override
    public void status(int code) {
        this.code = code;
    }

    @Override
    public int status() {
        return code;
    }

    @Override
    public void header(String header, String value) {
        headers.computeIfAbsent(header, k ->  new LinkedList<>()).add(value);
    }

    @Override
    public void headers(String header, List<String> headers) {
        this.headers.put(header, headers);
    }

    @Override
    public Map<String, List<String>> headers() {
        return headers;
    }

    @Override
    public List<String> headers(String header) {
        return headers.get(header);
    }

    @Override
    public String header(String header) {
        List<String> headers = this.headers.get(header);
        return headers == null || headers.isEmpty() ? null : headers.iterator().next();
    }

    @Override
    public void redirect(String url) {
        headers.put("Location", Collections.singletonList(url));
    }

    @Override
    public ByteArrayOutputStream body() {
        return output;
    }

    @Override
    public Writer charBody() {
        return new OutputStreamWriter(output);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void contentLength(int length) {
        this.length = length;
    }

    @Override
    public int contentLength() {
        return length;
    }

    @Override
    public String contentType() {
        List<String> headers = this.headers.get("Content-Type");
        return headers != null ? String.join("; ", headers) : null;
    }

    @Override
    public String charset() {
        return charset;
    }

    @Override
    public void charset(String charset) {
        this.charset = charset;
    }

    @Override
    public String protocol() {
        return protocol;
    }

}
