package study.net;

import java.io.ByteArrayInputStream;

public final class BodyHeaderPair {
    private final String headers;
    private final ByteArrayInputStream body;

    public BodyHeaderPair(String headers, ByteArrayInputStream body) {
        this.headers = headers;
        this.body = body;
    }

    public String headers() {
        return headers;
    }

    public ByteArrayInputStream body() {
        return body;
    }
}
