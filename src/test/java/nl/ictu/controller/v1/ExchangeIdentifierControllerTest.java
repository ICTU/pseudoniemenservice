package nl.ictu.controller.v1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.ExchangeIdentifierService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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
    void testExchangeIdentifier_Success() {
        // GIVEN
        final var callerOIN = "123456789";
        final var request = new WsExchangeIdentifierRequest();
        final var expectedResponse = new WsExchangeIdentifierResponse();
        when(service.exchangeIdentifier(request)).thenReturn(expectedResponse);
        // WHEN
        final var response = controller.exchangeIdentifier(callerOIN, request);
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
    void testExchangeIdentifier_ServiceThrowsException() {
        // GIVEN
        final var callerOIN = "123456789";
        final var request = new WsExchangeIdentifierRequest();
        final var exception = new RuntimeException("Service error");
        when(service.exchangeIdentifier(request)).thenThrow(exception);
        // WHEN & THEN
        final var thrownException = assertThrows(RuntimeException.class,
                        () -> controller.exchangeIdentifier(callerOIN, request));
        assertEquals("Service error", thrownException.getMessage());
        verify(service).exchangeIdentifier(request); // Ensure service method is called
    }
}
