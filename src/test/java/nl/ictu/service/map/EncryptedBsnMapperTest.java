package nl.ictu.service.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import nl.ictu.crypto.AesGcmSivCryptographer;
import nl.ictu.model.Identifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EncryptedBsnMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private EncryptedBsnMapper encryptedBsnMapper;

    @Test
    @DisplayName("""
            Given an encrypted BSN and a recipient OIN
            When decryption succeeds
            Then the decrypted BSN is returned
            """)
    void map_WhenDecryptSucceeds_ShouldReturnDecryptedBsn() {
        // GIVEN
        final var encryptedBsn = "someEncryptedValue";
        final var recipientOin = "testOIN";
        final var expectedBsn = "123456789";
        final var decryptedIdentifier = Identifier.builder()
                .bsn(expectedBsn)
                .build();
        when(aesGcmSivCryptographer.decrypt(encryptedBsn, recipientOin))
                .thenReturn(decryptedIdentifier);
        // WHEN
        String result = encryptedBsnMapper.map(encryptedBsn, recipientOin);
        // THEN
        assertEquals(expectedBsn, result,
                "The decrypted BSN should match the expected value");
    }
}
