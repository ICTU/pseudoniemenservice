package nl.ictu.service;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import nl.ictu.crypto.AesGcmCryptographer;
import nl.ictu.crypto.TokenCoder;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.service.exception.InvalidOINException;
import nl.ictu.service.map.BsnTokenMapper;
import nl.ictu.service.map.OrganisationPseudoTokenMapper;
import nl.ictu.service.validate.OINValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenServiceTest {

    private final String callerOIN = "123456789";
    private final String encryptedToken = "encryptedTokenValue";
    private final String decodedToken = "decodedTokenValue";

    @Mock
    private AesGcmCryptographer aesGcmCryptographer;
    @Mock
    private TokenCoder tokenCoder;
    @Mock
    private OINValidator oinValidator;
    @Mock
    private OrganisationPseudoTokenMapper organisationPseudoTokenMapper;
    @Mock
    private BsnTokenMapper bsnTokenMapper;
    @InjectMocks
    private ExchangeTokenService exchangeTokenService;

    private Token mockToken;

    @BeforeEach
    void setUp() {

        mockToken = Token.builder()
                .recipientOIN(callerOIN)
                .bsn("987654321")
                .build();
    }

    @Test
    @DisplayName("""
            Given a BSN identifier
            When exchangeToken() is called
            Then it should return a valid response mapped by BsnTokenMapper
            """)
    void testExchangeToken_BsnIdentifier() throws Exception {
        // GIVEN
        final var request = WsExchangeTokenRequest.builder()
                .token(encryptedToken)
                .identifierType(BSN)
                .build();
        // Stubbing dependencies
        when(aesGcmCryptographer.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenCoder.decode(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(true);
        var expectedResponse = mock(WsExchangeTokenResponse.class);
        when(bsnTokenMapper.map(mockToken)).thenReturn(expectedResponse);
        // WHEN
        final var actualResponse = exchangeTokenService.exchangeToken(callerOIN, request);
        // THEN
        verify(aesGcmCryptographer).decrypt(encryptedToken, callerOIN);
        verify(tokenCoder).decode(decodedToken);
        verify(oinValidator).isValid(callerOIN, mockToken);
        verify(bsnTokenMapper).map(mockToken);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("""
            Given an ORGANISATION_PSEUDO identifier
            When exchangeToken() is called
            Then it should return a valid response mapped by OrganisationPseudoTokenMapper
            """)
    void testExchangeToken_OrganisationPseudoIdentifier() throws Exception {
        // GIVEN
        final var request = WsExchangeTokenRequest.builder()
                .token(encryptedToken)
                .identifierType(ORGANISATION_PSEUDO)
                .build();
        // Stubbing dependencies
        when(aesGcmCryptographer.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenCoder.decode(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(true);
        final var expectedResponse = mock(WsExchangeTokenResponse.class);
        when(organisationPseudoTokenMapper.map(callerOIN, mockToken)).thenReturn(expectedResponse);
        // WHEN
        final var actualResponse = exchangeTokenService.exchangeToken(callerOIN, request);
        // THEN
        verify(aesGcmCryptographer).decrypt(encryptedToken, callerOIN);
        verify(tokenCoder).decode(decodedToken);
        verify(oinValidator).isValid(callerOIN, mockToken);
        verify(organisationPseudoTokenMapper).map(callerOIN, mockToken);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("""
            Given an invalid OIN
            When exchangeToken() is called
            Then it should throw InvalidOINException
            """)
    void testExchangeToken_InvalidOIN() throws Exception {
        // GIVEN
        final var request = WsExchangeTokenRequest.builder()
                .token(encryptedToken)
                .identifierType(BSN)
                .build();
        // Stubbing dependencies
        when(aesGcmCryptographer.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenCoder.decode(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(false); // Invalid OIN
        // WHEN & THEN
        assertThrows(InvalidOINException.class, () -> exchangeTokenService.exchangeToken(callerOIN, request));
        verify(aesGcmCryptographer).decrypt(encryptedToken, callerOIN);
        verify(tokenCoder).decode(decodedToken);
        verify(oinValidator).isValid(callerOIN, mockToken);
        verifyNoInteractions(bsnTokenMapper, organisationPseudoTokenMapper);
    }
}
