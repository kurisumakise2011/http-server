package study.net.exception;

public class UnsupportedServerOperation extends RuntimeException {
    public UnsupportedServerOperation() {
        super();
    }

    public UnsupportedServerOperation(String message) {
        super(message);
    }

    public UnsupportedServerOperation(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedServerOperation(Throwable cause) {
        super(cause);
    }
}
