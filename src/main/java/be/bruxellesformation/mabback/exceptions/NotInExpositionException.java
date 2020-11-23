package be.bruxellesformation.mabback.exceptions;

public class NotInExpositionException extends RuntimeException {
    public NotInExpositionException() {
    }

    public NotInExpositionException(String message) {
        super(message);
    }
}
