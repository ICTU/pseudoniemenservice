package nl.appsource.service.map;

import lombok.SneakyThrows;
import nl.appsource.model.Identifier;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptedBsnMapperTest {

    @Mock
    private AesGcmSivCryptographerService aesGcmSivCryptographerService;
    @InjectMocks
    private EncryptedBsnMapper encryptedBsnMapper;

    @Test
    @DisplayName("""
        Given an encrypted BSN and a recipient OIN
        When decryption succeeds
        Then the decrypted BSN is returned
        """)
    @SneakyThrows
    void map_WhenDecryptSucceeds_ShouldReturnDecryptedBsn() {
        // GIVEN
        final String encryptedBsn = "someEncryptedValue";
        final String recipientOin = "testOIN";
        final String expectedBsn = "123456789";
        final Identifier decryptedIdentifier = Identifier.builder()
            .bsn(expectedBsn)
            .build();
        when(aesGcmSivCryptographerService.decrypt(encryptedBsn, recipientOin))
            .thenReturn(decryptedIdentifier);
        // WHEN
        String result = encryptedBsnMapper.map(encryptedBsn, recipientOin);
        // THEN
        assertEquals(expectedBsn, result,
            "The decrypted BSN should match the expected value");
    }
}
