package study.net;

import study.net.exception.ThreadRuntimeException;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThreadDelegator extends Thread {
    private static final Logger LOG = Logger.getLogger(ServerThreadDelegator.class.getName());

    private HttpServerHandler handler;
    private Map<String, Object> context = new HashMap<>();
    private ServerConfiguration configuration;
    private Socket socket;
    private long timeout;

    public ServerThreadDelegator(HttpServerHandler handler, Socket socket, long timeout, ServerConfiguration configuration) {
        this.handler = handler;
        this.socket = socket;
        this.timeout = timeout;
        this.configuration = configuration;
    }

    @Override
    public void run() {
        try {
            HandlerThread thread = new HandlerThread(() -> {
                handler.handle(this, socket);
            }, socket);
            thread.start();
            try {
                thread.join(timeout);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ThreadRuntimeException("thread has been interrupted", e);
            }

            if (thread.isRunning()) {
                LOG.warning(() -> "interrupting thread " + thread.getName());
                thread.interrupt();
                throw new ThreadRuntimeException("request timeout :: socket " + socket.toString());
            }
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "could not close :: socket " + socket.toString(), e);
            }
        }

    }

    public Map<String, Object> getContext() {
        return context;
    }

    public ServerConfiguration getConfiguration() {
        return configuration;
    }
}
