package nl.appsource.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles generic exceptions and returns an appropriate HTTP response with an error message.
     *
     * @param ex the Exception to be handled
     * @return a ResponseEntity containing a generic error message and an INTERNAL_SERVER_ERROR
     * (500) status
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Void> handleGenericException(final Exception ex) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
