package study.net.exception;

public class ThreadRuntimeException extends RuntimeException {
    public ThreadRuntimeException() {
        super();
    }

    public ThreadRuntimeException(String message) {
        super(message);
    }

    public ThreadRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreadRuntimeException(Throwable cause) {
        super(cause);
    }
}
