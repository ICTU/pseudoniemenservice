package nl.appsource.service.map;

import lombok.SneakyThrows;
import nl.appsource.model.Token;
import nl.appsource.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.appsource.service.crypto.AesGcmCryptographerService;
import nl.appsource.service.crypto.TokenConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.security.InvalidKeyException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WsGetTokenResponseMapperTest {

    private final String bsn = "987654321";
    private final long creationDate = System.currentTimeMillis();
    private final String recipientOIN = "123456789";
    private final String encodedToken = "encoded-token";

    @Mock
    private AesGcmCryptographerService aesGcmCryptographerService;

    @Mock
    private TokenConverter tokenConverter;

    @InjectMocks
    private WsGetTokenResponseMapper wsGetTokenResponseMapper;

    @Test
    @DisplayName("""
        Given a valid bsn, creation date, and recipient OIN
        When token encoding and encryption succeed
        Then the response should contain the encrypted token
        """)
    @SneakyThrows
    void testMap_Success() {
        final String encryptedToken = "encrypted-token";
        // GIVEN
        final Token token = Token.builder()
            .version(WsGetTokenResponseMapper.V_1)
            .bsn(bsn)
            .creationDate(creationDate)
            .recipientOIN(recipientOIN)
            .build();
        when(tokenConverter.serialize(token)).thenReturn(encodedToken);
        when(aesGcmCryptographerService.encrypt(encodedToken, recipientOIN)).thenReturn(encryptedToken);
        // WHEN
        final WsGetTokenResponse response = wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN);
        // THEN
        verify(tokenConverter).serialize(token);
        verify(aesGcmCryptographerService).encrypt(encodedToken, recipientOIN);
        org.junit.jupiter.api.Assertions.assertEquals(encryptedToken, response.getToken());
    }

    @Test
    @DisplayName("""
        Given a valid bsn, creation date, and recipient OIN
        When token encoding fails with IOException
        Then an IOException should be thrown
        """)
    @SneakyThrows
    void testMap_EncodingIOException() {
        // GIVEN
        final Token token = Token.builder()
            .version(WsGetTokenResponseMapper.V_1)
            .bsn(bsn)
            .creationDate(creationDate)
            .recipientOIN(recipientOIN)
            .build();
        when(tokenConverter.serialize(token)).thenThrow(new IOException("Encoding failed"));
        // WHEN & THEN
        assertThrows(IOException.class,
            () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenConverter).serialize(token);
        verifyNoInteractions(aesGcmCryptographerService);
    }

    @Test
    @DisplayName("""
        Given a valid bsn, creation date, and recipient OIN
        When encryption fails with InvalidKeyException
        Then an InvalidKeyException should be thrown
        """)
    @SneakyThrows
    void testMap_EncryptionError() {
        // GIVEN
        final Token token = Token.builder()
            .version(WsGetTokenResponseMapper.V_1)
            .bsn(bsn)
            .creationDate(creationDate)
            .recipientOIN(recipientOIN)
            .build();
        when(tokenConverter.serialize(token)).thenReturn(encodedToken);
        when(aesGcmCryptographerService.encrypt(encodedToken, recipientOIN))
            .thenThrow(new InvalidKeyException("Invalid encryption key"));
        // WHEN & THEN
        assertThrows(InvalidKeyException.class,
            () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenConverter).serialize(token);
        verify(aesGcmCryptographerService).encrypt(encodedToken, recipientOIN);
    }

    @Test
    @DisplayName("""
        Given a valid bsn, creation date, and recipient OIN
        When encryption fails with a runtime exception
        Then a RuntimeException should be thrown
        """)
    @SneakyThrows
    void testMap_UnexpectedError() {
        // GIVEN
        final Token token = Token.builder()
            .version(WsGetTokenResponseMapper.V_1)
            .bsn(bsn)
            .creationDate(creationDate)
            .recipientOIN(recipientOIN)
            .build();
        when(tokenConverter.serialize(token)).thenReturn(encodedToken);
        when(aesGcmCryptographerService.encrypt(encodedToken, recipientOIN)).thenThrow(
            new RuntimeException("Unexpected error"));
        // WHEN & THEN
        assertThrows(RuntimeException.class,
            () -> wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN));
        verify(tokenConverter).serialize(token);
        verify(aesGcmCryptographerService).encrypt(encodedToken, recipientOIN);
    }
}
