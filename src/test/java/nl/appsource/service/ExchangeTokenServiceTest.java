package nl.appsource.service;

import lombok.SneakyThrows;
import nl.appsource.model.Token;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.service.crypto.AesGcmCryptographerService;
import nl.appsource.service.crypto.TokenConverter;
import nl.appsource.service.map.BsnTokenMapper;
import nl.appsource.service.map.OrganisationPseudoTokenMapper;
import nl.appsource.service.validate.OINValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeTokenServiceTest {

    private final String callerOIN = "123456789";
    private final String encryptedToken = "encryptedTokenValue";
    private final String decodedToken = "decodedTokenValue";

    @Mock
    private AesGcmCryptographerService aesGcmCryptographerService;
    @Mock
    private TokenConverter tokenConverter;
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
    @SneakyThrows
    void testExchangeToken_BsnIdentifier() {
        // GIVEN
        final WsExchangeTokenRequest request = WsExchangeTokenRequest.builder()
            .token(encryptedToken)
            .identifierType(BSN)
            .build();
        // Stubbing dependencies
        when(aesGcmCryptographerService.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenConverter.deSerialize(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(true);
        WsExchangeTokenResponse expectedResponse = mock(WsExchangeTokenResponse.class);
        when(bsnTokenMapper.map(mockToken)).thenReturn(expectedResponse);
        // WHEN
        final WsExchangeTokenResponse actualResponse = exchangeTokenService.exchangeToken(callerOIN, request);
        // THEN
        verify(aesGcmCryptographerService).decrypt(encryptedToken, callerOIN);
        verify(tokenConverter).deSerialize(decodedToken);
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
    @SneakyThrows
    void testExchangeToken_OrganisationPseudoIdentifier() {
        // GIVEN
        final WsExchangeTokenRequest request = WsExchangeTokenRequest.builder()
            .token(encryptedToken)
            .identifierType(ORGANISATION_PSEUDO)
            .build();
        // Stubbing dependencies
        when(aesGcmCryptographerService.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenConverter.deSerialize(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(true);
        final WsExchangeTokenResponse expectedResponse = mock(WsExchangeTokenResponse.class);
        when(organisationPseudoTokenMapper.map(callerOIN, mockToken)).thenReturn(expectedResponse);
        // WHEN
        final WsExchangeTokenResponse actualResponse = exchangeTokenService.exchangeToken(callerOIN, request);
        // THEN
        verify(aesGcmCryptographerService).decrypt(encryptedToken, callerOIN);
        verify(tokenConverter).deSerialize(decodedToken);
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
    @SneakyThrows
    void testExchangeToken_InvalidOIN() {
        // GIVEN
        final WsExchangeTokenRequest request = WsExchangeTokenRequest.builder()
            .token(encryptedToken)
            .identifierType(BSN)
            .build();
        // Stubbing dependencies
        when(aesGcmCryptographerService.decrypt(encryptedToken, callerOIN)).thenReturn(decodedToken);
        when(tokenConverter.deSerialize(decodedToken)).thenReturn(mockToken);
        when(oinValidator.isValid(callerOIN, mockToken)).thenReturn(false); // Invalid OIN
        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> exchangeTokenService.exchangeToken(callerOIN, request));
        verify(aesGcmCryptographerService).decrypt(encryptedToken, callerOIN);
        verify(tokenConverter).deSerialize(decodedToken);
        verify(oinValidator).isValid(callerOIN, mockToken);
        verifyNoInteractions(bsnTokenMapper, organisationPseudoTokenMapper);
    }
}
