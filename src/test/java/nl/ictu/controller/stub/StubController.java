package nl.ictu.controller.stub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller contains endpoints for interacting with stubbed services. It demonstrates a
 * simple REST controller setup with a GET endpoint.This stubbed controller is used to test behavior
 * exception handling in congestion with the GlobalExceptionHandler class
 */
@RestController
public class StubController {

    @Autowired
    public StubService service;

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/stubby")
    public ResponseEntity<String> get() {

        service.throwAStubbedException();
        return ResponseEntity.ok("stubbed body");
    }
}
