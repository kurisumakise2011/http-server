package study.net;

import study.net.exception.PhysicalSocketException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.logging.Logger;

public final class Server {
    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    private ServerSocket socket;
    private ServerConfiguration configuration;
    private ExecutorService executorService;
    private HttpServerHandler handler;

    private Server(ServerConfiguration configuration, ExecutorService service, ExceptionalHandler exceptionalHandler) {
        this.configuration = configuration;
        this.executorService = service;
        this.handler = new HttpServerHandlerImpl(exceptionalHandler);
    }

    /**
     * There are two communication protocols that one can use for socket programming:
     * User Datagram Protocol (UDP) and Transfer Control Protocol (TCP).
     */
    public void run() {
        openSocket();
        listen();
    }

    private void openSocket() {
        try {
            this.socket = new ServerSocket(configuration.getPort());
        } catch (IOException e) {
            throw new PhysicalSocketException("could not open a socket on port::" + configuration.getPort()
                    + "\n might be it's binding by another process", e);
        }
    }

    private void listen() {
        LOG.info("server started on " + configuration.getPort());
        new Thread(() -> {
            while (true) {
                try {
                    Socket accepted = socket.accept();
                    LOG.info("accepted request :: " + socket.toString());
                    executorService.execute(new ServerThreadDelegator(handler, accepted, configuration.getRequestTimeout(), configuration));
                } catch (IOException e) {
                    throw new PhysicalSocketException(e);
                }
            }
        }).start();
    }

    public void stop(Runnable shutdownHook) {
        try {
            shutdownHook.run();
            executorService.shutdownNow();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public void join() {
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Builder {
        private ServerConfiguration configuration;
        private ExecutorService executorService;
        private ExceptionalHandler exceptionalHandler = (task, recover) -> {

        };

        public Builder configuration(ServerConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public Builder exceptionalHandler(ExceptionalHandler handler) {
            this.exceptionalHandler = handler;
            return this;
        }

        public Server build() {
            return new Server(configuration, executorService, exceptionalHandler);
        }
    }


}
