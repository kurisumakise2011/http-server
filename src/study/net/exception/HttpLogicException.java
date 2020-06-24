package study.net.exception;

public class HttpLogicException extends RuntimeException {
    private int code;

    public HttpLogicException(int code) {
        this.code = code;
    }

    public HttpLogicException(String message, int code) {
        super(message);
        this.code = code;
    }

    public HttpLogicException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public HttpLogicException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public int code() {
        return code;
    }
}
