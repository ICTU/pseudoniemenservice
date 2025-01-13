package nl.appsource.controller.v1;

import lombok.SneakyThrows;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.appsource.service.ExchangeIdentifierService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeIdentifierControllerTest {

    @Mock
    private ExchangeIdentifierService service;
    @InjectMocks
    private ExchangeIdentifierController controller;

    @Test
    @DisplayName("""
        Given a valid request and service response
        When calling exchangeIdentifier()
        Then it returns 200 OK with the expected response
        """)
    @SneakyThrows
    void testExchangeIdentifier_Success() {
        // GIVEN
        final String callerOIN = "123456789";
        final WsExchangeIdentifierRequest request = new WsExchangeIdentifierRequest();
        final WsExchangeIdentifierResponse expectedResponse = new WsExchangeIdentifierResponse();
        when(service.exchangeIdentifier(request)).thenReturn(expectedResponse);
        // WHEN
        final ResponseEntity<WsExchangeIdentifierResponse> response = controller.exchangeIdentifier(callerOIN, request);
        // THEN
        assertEquals(ResponseEntity.ok(expectedResponse), response);
        verify(service).exchangeIdentifier(request); // Ensure service method is called
    }

    @Test
    @DisplayName("""
        Given a valid request and service throws an exception
        When calling exchangeIdentifier()
        Then it throws the same exception with the correct message
        """)
    @SneakyThrows
    void testExchangeIdentifier_ServiceThrowsException() {
        // GIVEN
        final String callerOIN = "123456789";
        final WsExchangeIdentifierRequest request = new WsExchangeIdentifierRequest();
        final RuntimeException exception = new RuntimeException("Service error");
        when(service.exchangeIdentifier(request)).thenThrow(exception);
        // WHEN
        final ResponseEntity<WsExchangeIdentifierResponse> response = controller.exchangeIdentifier(callerOIN, request);
        // THEN
        assertEquals(ResponseEntity.unprocessableEntity().build(),response);
        verify(service).exchangeIdentifier(request); // Ensure service method is called
    }
}
