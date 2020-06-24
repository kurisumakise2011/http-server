package study.net;

import study.net.exception.HttpLogicException;
import study.net.exception.PhysicalSocketException;
import study.net.exception.ThreadRuntimeException;
import study.net.exception.UnsupportedServerOperation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultExceptionalHandler implements ExceptionalHandler {
    private static final Logger LOG = Logger.getLogger(DefaultExceptionalHandler.class.getName());

    @Override
    public void handle(Runnable task, Consumer<HttpBody> recover) {
        try {
            task.run();
        } catch (HttpLogicException e) {
            LOG.log(Level.SEVERE, "http logical exception", e);
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            write(String.format("{\"error\":\"%s\"}", e.getMessage()).getBytes(), content);
            recover.accept(new HttpBody(e.code(), content, "application/json"));
        } catch (PhysicalSocketException | ThreadRuntimeException e) {
            LOG.log(Level.SEVERE, "runtime exception", e);
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            write("{\"error\":\"Internal server error\"}".getBytes(), content);
            recover.accept(new HttpBody(500, content, "application/json"));
        } catch (UnsupportedServerOperation e) {
            LOG.log(Level.SEVERE, "unsupported exception", e);
            ByteArrayOutputStream content = new ByteArrayOutputStream();
            write(" {\"error\":\"Not implemented\"}".getBytes(), content);
            recover.accept(new HttpBody(501, content, "application/json"));
        }
    }

    private void write(byte[] data, ByteArrayOutputStream content) {
        try {
            content.write(data);
        } catch (IOException e) {
            throw new ThreadRuntimeException(e);
        }
    }
}
