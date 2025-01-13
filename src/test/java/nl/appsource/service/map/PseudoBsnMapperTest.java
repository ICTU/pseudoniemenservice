package nl.appsource.service.map;

import lombok.SneakyThrows;
import nl.appsource.model.Identifier;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link PseudoBsnMapper}.
 */
@ExtendWith(MockitoExtension.class)
class PseudoBsnMapperTest {

    @Mock
    private AesGcmSivCryptographerService aesGcmSivCryptographerService;
    @InjectMocks
    private PseudoBsnMapper pseudoBsnMapper;

    @Test
    @DisplayName("""
        Given a valid pseudo and OIN
        When decryption succeeds
        Then the response should contain the decrypted BSN
        """)
    @SneakyThrows
    void map_WhenDecryptionSucceeds_ShouldReturnDecryptedBsn() {
        // GIVEN
        String pseudo = "someEncryptedString";
        String oin = "TEST_OIN";
        // Suppose the decrypted Identifier has BSN "123456789"
        final Identifier decryptedIdentifier = Identifier.builder()
            .bsn("123456789")
            .build();
        when(aesGcmSivCryptographerService.decrypt(pseudo, oin))
            .thenReturn(decryptedIdentifier);
        // WHEN
        WsExchangeIdentifierResponse response = pseudoBsnMapper.map(pseudo, oin);
        // THEN
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getIdentifier(), "Identifier should not be null");
        assertEquals(BSN, response.getIdentifier().getType(), "Type should be BSN");
        assertEquals("123456789", response.getIdentifier().getValue(),
            "Decrypted BSN value should match");
    }
}
