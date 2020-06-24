package study.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public interface HttpResponse {

    void status(int code);

    int status();

    void header(String header, String value);

    void headers(String header, List<String> headers);

    Map<String, List<String>> headers();

    List<String> headers(String header);

    String header(String header);

    void redirect(String url);

    ByteArrayOutputStream body();

    Writer charBody();

    void flush() throws IOException;

    void contentLength(int length);

    int contentLength();

    String contentType();

    String charset();

    void charset(String charset);

    String protocol();
}

