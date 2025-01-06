package nl.ictu.controller;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import nl.ictu.controller.stub.StubController;
import nl.ictu.controller.stub.StubService;
import nl.ictu.service.exception.IdentifierPrivateKeyException;
import nl.ictu.service.exception.InvalidOINException;
import nl.ictu.service.exception.InvalidWsIdentifierRequestTypeException;
import nl.ictu.service.exception.InvalidWsIdentifierTokenException;
import nl.ictu.service.exception.TokenPrivateKeyException;
import nl.ictu.service.exception.WsGetTokenProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({GlobalExceptionHandler.class, StubController.class})
class GlobalExceptionHandlerTest {

    public static final String SERVICE_ERROR_MESSAGE = "Service error";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StubService stubService;

    // Test for handleGenericException
    @Test
    @DisplayName("""
            Given an invalid endpoint
            When a GET request is made
            Then an internal server error is returned with an appropriate message
            """)
    void handleGenericException_ShouldReturnInternalServerErrorWithMessage() throws Exception {

        mockMvc.perform(get("/non-existent-endpoint")) // Assuming no controller is mapped to this
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(
                        "An unexpected error occurred: No static resource non-existent-endpoint."));
    }

    @Test
    @DisplayName("""
            Given a stubbed controller and service
            When the service throws various exceptions
            Then the system responds with UNPROCESSABLE_ENTITY and the exception message
            """)
    void exchangeToken_ShouldReturnUnprocessableEntity() {
        // GIVEN: a stubbed controller and service
        // WHEN: the service throws an exception
        final var exceptions = List.of(
                new IdentifierPrivateKeyException(SERVICE_ERROR_MESSAGE),
                new InvalidWsIdentifierRequestTypeException(SERVICE_ERROR_MESSAGE),
                new InvalidWsIdentifierTokenException(SERVICE_ERROR_MESSAGE),
                new TokenPrivateKeyException(SERVICE_ERROR_MESSAGE),
                new WsGetTokenProcessingException(SERVICE_ERROR_MESSAGE),
                new InvalidOINException(SERVICE_ERROR_MESSAGE)
        );
        exceptions.forEach(this::testExceptionHandlingBehavior);
    }

    /**
     * Tests the behavior of exception handling by simulating a scenario where the stub service
     * throws the given RuntimeException. This test verifies that when an exception is thrown by the
     * service, the system responds with the appropriate HTTP status code.
     *
     * @param ex the RuntimeException to be thrown by the stub service during the test
     */
    private void testExceptionHandlingBehavior(final Exception ex) {

        try {
            doThrow(ex)
                    .when(stubService)
                    .throwAStubbedException();
            // THEN: perform the POST request
            mockMvc.perform(get("/stubby"))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(content().string(ex.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}