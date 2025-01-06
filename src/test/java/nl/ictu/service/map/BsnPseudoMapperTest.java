package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import nl.ictu.crypto.AesGcmSivCryptographer;
import nl.ictu.model.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BsnPseudoMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private BsnPseudoMapper bsnPseudoMapper;

    @Test
    @DisplayName("""
            Given a valid BSN and OIN
            When encryption succeeds
            Then a valid WsExchangeIdentifierResponse is returned
            """)
    void map_WhenEncryptionSucceeds_ShouldReturnWsExchangeIdentifierResponse() throws Exception {
        // GIVEN
        final var bsn = "123456789";
        final var oin = "OIN_X";
        final var encryptedValue = "encryptedBsn123";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
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
    void map_WhenEncryptThrowsIOException_ShouldThrowIOException() throws Exception {
        // GIVEN
        final var bsn = "987654321";
        final var oin = "OIN_IO";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
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
    void map_WhenEncryptThrowsInvalidCipherTextException_ShouldThrowInvalidCipherTextException()
            throws Exception {
        // GIVEN
        final var bsn = "111222333";
        final var oin = "OIN_CIPHER";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(oin)))
                .thenThrow(new InvalidCipherTextException("Simulated cipher error"));
        // WHEN & THEN
        assertThrows(InvalidCipherTextException.class, () -> bsnPseudoMapper.map(bsn, oin));
    }
}
