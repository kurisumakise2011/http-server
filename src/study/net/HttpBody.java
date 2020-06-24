package study.net;

import java.io.ByteArrayOutputStream;

public class HttpBody {
    private int code;
    private ByteArrayOutputStream stream;
    private String contentType;

    public HttpBody(int code, ByteArrayOutputStream stream, String contentType) {
        this.code = code;
        this.stream = stream;
        this.contentType = contentType;
    }

    public int code() {
        return code;
    }

    public ByteArrayOutputStream stream() {
        return stream;
    }

    public String contentType() {
        return contentType;
    }
}
