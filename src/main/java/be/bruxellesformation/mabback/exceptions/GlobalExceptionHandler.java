package be.bruxellesformation.mabback.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Provides handling for exceptions. */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpositionException.class)
    public ResponseEntity handleException(Exception ex){
        if (ex instanceof ExpositionException){
            ExpositionException expoExcep = (ExpositionException) ex;
            return handleExpoException(expoExcep);
        } else {
            return handleInternalException(ex);
        }
    }

    private ResponseEntity handleExpoException(ExpositionException exception){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }

    private ResponseEntity handleInternalException(Exception exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }
}
