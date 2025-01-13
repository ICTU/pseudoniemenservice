package nl.appsource.service.map;

import lombok.SneakyThrows;
import nl.appsource.model.Identifier;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BsnPseudoMapperTest {

    @Mock
    private AesGcmSivCryptographerService aesGcmSivCryptographerService;
    @InjectMocks
    private BsnPseudoMapper bsnPseudoMapper;

    @Test
    @DisplayName("""
        Given a valid BSN and OIN
        When encryption succeeds
        Then a valid WsExchangeIdentifierResponse is returned
        """)
    @SneakyThrows
    void map_WhenEncryptionSucceeds_ShouldReturnWsExchangeIdentifierResponse() {
        // GIVEN
        final var bsn = "123456789";
        final var oin = "OIN_X";
        final var encryptedValue = "encryptedBsn123";
        when(aesGcmSivCryptographerService.encrypt(any(Identifier.class), eq(oin)))
            .thenReturn(encryptedValue);
        // WHEN
        final var response = bsnPseudoMapper.map(bsn, oin);
        // THEN
        assertNotNull(response);
        assertNotNull(response.getIdentifier());
        assertEquals(ORGANISATION_PSEUDO, response.getIdentifier().getType());
        assertEquals(encryptedValue, response.getIdentifier().getValue());
    }

    @Test
    @DisplayName("""
        Given a BSN and OIN
        When encryption throws IOException
        Then an IOException is thrown
        """)
    @SneakyThrows
    void map_WhenEncryptThrowsIOException_ShouldThrowIOException() {
        // GIVEN
        final var bsn = "987654321";
        final var oin = "OIN_IO";
        when(aesGcmSivCryptographerService.encrypt(any(Identifier.class), eq(oin)))
            .thenThrow(new IOException("Simulated I/O error"));
        // WHEN & THEN
        assertThrows(IOException.class, () -> bsnPseudoMapper.map(bsn, oin));
    }

    @Test
    @DisplayName("""
        Given a BSN and OIN
        When encryption throws InvalidCipherTextException
        Then an InvalidCipherTextException is thrown
        """)
    @SneakyThrows
    void map_WhenEncryptThrowsInvalidCipherTextException_ShouldThrowInvalidCipherTextException() {
        // GIVEN
        final var bsn = "111222333";
        final var oin = "OIN_CIPHER";
        when(aesGcmSivCryptographerService.encrypt(any(Identifier.class), eq(oin)))
            .thenThrow(new InvalidCipherTextException("Simulated cipher error"));
        // WHEN & THEN
        assertThrows(InvalidCipherTextException.class, () -> bsnPseudoMapper.map(bsn, oin));
    }
}
