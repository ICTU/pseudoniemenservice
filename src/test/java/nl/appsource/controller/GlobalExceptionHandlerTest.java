package nl.appsource.controller;

import lombok.SneakyThrows;
import nl.appsource.controller.stub.StubController;
import nl.appsource.controller.stub.StubService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({GlobalExceptionHandler.class, StubController.class})
class GlobalExceptionHandlerTest {

    public static final String SERVICE_ERROR_MESSAGE = "Service error";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StubService stubService;

    // Test for handleGenericException
    @Test
    @DisplayName("""
        Given an invalid endpoint
        When a GET request is made
        Then an internal server error is returned
        """)
    @SneakyThrows
    void handleGenericException_ShouldReturnInternalServerErrorWithMessage() {

        mockMvc.perform(get("/non-existent-endpoint")) // Assuming no controller is mapped to this
            .andExpect(status().isInternalServerError());
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