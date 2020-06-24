package study.net;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public final class ServerConfiguration {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_POOL_SIZE = 50;
    private static final long DEFAULT_TIMEOUT = 60000;
    private static final int DEFAULT_URL_MAX_SIZE = 50;
    private static final String HTTP_1_1 = "HTTP/1.1";

    private final int port;
    private final int requestSizeBytes;
    private final long requestTimeout;
    private final int urlMaxSize;
    private final String protocol;
    private Map<String, Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>>> routes;

    private ServerConfiguration(int port, int requestSizeBytes, long requestTimeout, int urlMaxSize,
                                Map<String, Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>>> routes, String protocol) {
        this.port = port;
        this.requestSizeBytes = requestSizeBytes;
        this.requestTimeout = requestTimeout;
        this.urlMaxSize = urlMaxSize;
        this.routes = routes;
        this.protocol = protocol;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int port;
        private int requestSizeBytes;
        private long requestTimeout;
        private int urlMaxSize;
        private Map<String, Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>>> routes = new ConcurrentHashMap<>();
        private String protocol = HTTP_1_1;

        public Builder port(int port) {
            if (port < 0) {
                throw new IllegalArgumentException("negative port size " + port);
            }
            if (port == 0) {
                port = DEFAULT_PORT;
            }
            this.port = port;
            return this;
        }

        public Builder requestSizeBytes(int poolSize) {
            if (poolSize <= 0) {
                poolSize = DEFAULT_POOL_SIZE;
            }
            this.requestSizeBytes = poolSize;
            return this;
        }

        public Builder requestTimeout(long requestTimeout) {
            if (requestTimeout <= 0) {
                requestTimeout = DEFAULT_TIMEOUT;
            }
            this.requestTimeout = requestTimeout;
            return this;
        }

        public Builder urlMaxSize(int urlMaxSize) {
            if (urlMaxSize <= 0) {
                urlMaxSize = DEFAULT_URL_MAX_SIZE;
            }
            this.urlMaxSize = urlMaxSize;
            return this;
        }

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public ServerConfiguration build() {
            return new ServerConfiguration(port, requestSizeBytes, requestTimeout, urlMaxSize, routes, protocol);
        }

        public Routes routes() {
            return new Routes();
        }

        public class Routes {
            public Routes add(String route, BiConsumer<HttpResponse, HttpRequest> handler) {
                routes.computeIfAbsent(route, k -> new HashMap<>()).put(HttpMethod.GET, handler);
                return this;
            }

            public Routes add(String route, HttpMethod method, BiConsumer<HttpResponse, HttpRequest> handler) {
                routes.computeIfAbsent(route, k -> new HashMap<>()).put(method, handler);
                return this;
            }

            public Builder build() {
                return Builder.this;
            }
        }
    }

    public int getPort() {
        return port;
    }

    public int getRequestSizeBytes() {
        return requestSizeBytes;
    }

    public long getRequestTimeout() {
        return requestTimeout;
    }

    public int getUrlMaxSize() {
        return urlMaxSize;
    }

    public Map<String, Map<HttpMethod, BiConsumer<HttpResponse, HttpRequest>>> routes() {
        return routes;
    }

    public String getProtocol() {
        return protocol;
    }
}
