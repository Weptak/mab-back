package be.bruxellesformation.mabback.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    Exception expositionException = new ExpositionException("exposition test message");
    Exception generalException = new Exception("general message");

    @Test
    void handleException() {
        ResponseEntity reponse =  globalExceptionHandler.handleException(expositionException);
        assertEquals(reponse.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(reponse.getBody(), "exposition test message");

        reponse = globalExceptionHandler.handleException(generalException);
        assertEquals(reponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(reponse.getBody(), "general message");
    }
}