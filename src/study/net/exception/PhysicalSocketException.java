package study.net.exception;

public class PhysicalSocketException extends RuntimeException {
    public PhysicalSocketException() {
        super();
    }

    public PhysicalSocketException(String message) {
        super(message);
    }

    public PhysicalSocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhysicalSocketException(Throwable cause) {
        super(cause);
    }
}
