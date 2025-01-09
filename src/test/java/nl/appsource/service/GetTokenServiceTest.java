package nl.appsource.service;

import nl.appsource.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.appsource.service.exception.WsGetTokenProcessingException;
import nl.appsource.service.map.WsGetTokenResponseMapper;
import nl.appsource.service.map.WsIdentifierOinBsnMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTokenServiceTest {

    private final String recipientOIN = "123456789";
    private final String bsn = "987654321";

    @Mock
    private WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;

    @Mock
    private WsGetTokenResponseMapper wsGetTokenResponseMapper;

    @InjectMocks
    private GetTokenService getTokenService;

    @BeforeEach
    void setUp() {
        // Initialize common test data if needed
    }

    @Test
    @DisplayName("""
        Given a valid identifier of type BSN
        When getWsGetTokenResponse() is called
        Then it should return a valid response
        """)
    void testGetWsGetTokenResponse_ValidInput() throws Exception {
        // GIVEN
        final var identifier = WsIdentifier.builder()
            .type(WsIdentifierTypes.BSN)
            .value(bsn)
            .build();
        final var expectedResponse = mock(WsGetTokenResponse.class);

        // Stubbing dependencies
        when(wsIdentifierOinBsnMapper.map(identifier, recipientOIN)).thenReturn(bsn);
        when(wsGetTokenResponseMapper.map(eq(bsn), anyLong(), eq(recipientOIN))).thenReturn(expectedResponse);

        // WHEN
        final var actualResponse = getTokenService.getWsGetTokenResponse(recipientOIN, identifier);

        // THEN
        verify(wsIdentifierOinBsnMapper).map(identifier, recipientOIN);
        verify(wsGetTokenResponseMapper).map(eq(bsn), anyLong(), eq(recipientOIN));
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("""
        Given an unexpected error during processing
        When getWsGetTokenResponse() is called
        Then it should throw WsGetTokenProcessingException with the correct message
        """)
    void testGetWsGetTokenResponse_UnexpectedError() {
        // GIVEN
        final var identifier = WsIdentifier.builder()
            .type(WsIdentifierTypes.BSN)
            .value(bsn)
            .build();
        final var exceptionMessage = "Unexpected processing error";

        // Stubbing dependencies
        when(wsIdentifierOinBsnMapper.map(identifier, recipientOIN))
            .thenThrow(new RuntimeException(exceptionMessage));

        // WHEN & THEN
        final var exception = assertThrows(WsGetTokenProcessingException.class,
            () -> getTokenService.getWsGetTokenResponse(recipientOIN, identifier));

        // Assert exception message
        assertEquals(exceptionMessage, exception.getMessage());
        verify(wsIdentifierOinBsnMapper).map(identifier, recipientOIN);
        verifyNoInteractions(wsGetTokenResponseMapper);
    }
}
