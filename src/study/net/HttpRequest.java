package study.net;

import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public interface HttpRequest {

    String protocol();

    URI getUri();

    String uriAsString();

    String contextPath();

    URL url();

    String urlAsString();

    Map<String, Object> attributes();

    Object getAttribute(String key);

    void removeAttribute(String key);

    void putAttribute(String key, Object value);

    String getParameter(String name);

    List<String> getParameters(String name);

    /**
     * According to term of reference only the GET method is supported by the HTTP server
     * But in real life the newest HTTP protocol is HTTP3 and it supports the following request methods:
     * GET
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
     * HEAD
     * The HEAD method asks for a response identical to that of a GET request, but without the response body.
     * POST
     * The POST method is used to submit an entity to the specified resource, often causing a change in state or side effects on the server.
     * PUT
     * The PUT method replaces all current representations of the target resource with the request payload.
     *
     * DELETE
     * The DELETE method deletes the specified resource.
     * CONNECT
     * The CONNECT method establishes a tunnel to the server identified by the target resource.
     *
     * OPTIONS
     * The OPTIONS method is used to describe the communication options for the target resource.
     * TRACE
     * The TRACE method performs a message loop-back test along the path to the target resource.
     *
     * PATCH
     * The PATCH method is used to apply partial modifications to a resource.
     * @return requested method
     */
    String method();

    List<String> headers(String key);

    String header(String key);

    String address();

    void encoding(String charset);

    String encoding();

    InputStream body();

    Reader charBody();

    int port();
}
