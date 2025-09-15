package karina.lang;


public class UnwrapException extends RuntimeException {

    public UnwrapException(String message) {
        super(message);
    }

    public UnwrapException(String message, Throwable cause) {
        super(message, cause);
    }

}
