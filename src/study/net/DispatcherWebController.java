package study.net;

import study.net.exception.HttpLogicException;
import study.net.exception.UnsupportedServerOperation;

import java.util.Map;
import java.util.function.BiConsumer;

public class DispatcherWebController {

    public void dispatch(HttpResponse response, HttpRequest request, ServerThreadDelegator thread) {
        Map<String, Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>>> routes = thread.getConfiguration().routes();

        String path = request.uriAsString();
        int index;
        if ((index = path.indexOf("?")) != -1) {
            path = path.substring(0, index);
        }

        Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>> methods = routes.get(path);

        if (methods == null) {
            throw new HttpLogicException("not found", 404);
        }

        BiConsumer<HttpResponse, HttpRequest> handler = methods.get(getMethod(request.method()));
        if (handler == null) {
            throw new HttpLogicException("not allowed method", 405);
        }

        handler.accept(response, request);
    }

    private HttpMethod getMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedServerOperation("no such method :: " + method);
        }
    }
}
