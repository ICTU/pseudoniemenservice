package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import nl.ictu.crypto.AesGcmSivCryptographer;
import nl.ictu.model.Identifier;
import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link OrganisationPseudoTokenMapper}.
 */
@ExtendWith(MockitoExtension.class)
class OrganisationPseudoTokenMapperTest {

    @Mock
    private AesGcmSivCryptographer aesGcmSivCryptographer;
    @InjectMocks
    private OrganisationPseudoTokenMapper organisationPseudoTokenMapper;

    @Test
    @DisplayName("""
            Given a valid token and caller OIN
            When encryption succeeds
            Then the response should contain the encrypted identifier
            """)
    void map_WhenEncryptionSucceeds_ShouldReturnEncryptedTokenResponse() throws Exception {
        // GIVEN
        final var callerOIN = "TEST_OIN";
        final var token = Token.builder().bsn("123456789").build();
        final var encryptedValue = "encryptedBSN";
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenReturn(encryptedValue);
        // WHEN
        final var response = organisationPseudoTokenMapper.map(callerOIN, token);
        // THEN
        assertEquals(ORGANISATION_PSEUDO, response.getIdentifier().getType(),
                "The identifier type should be ORGANISATION_PSEUDO");
        assertEquals(encryptedValue, response.getIdentifier().getValue(),
                "The identifier value should match the encrypted BSN");
    }

    @Test
    @DisplayName("""
            Given a valid token and caller OIN
            When encryption fails with InvalidCipherTextException
            Then an InvalidCipherTextException should be thrown
            """)
    void map_WhenEncryptionFails_ShouldThrowInvalidCipherTextException() throws Exception {
        // GIVEN
        final var callerOIN = "FAILING_OIN";
        final var token = Token.builder().bsn("987654321").build();
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenThrow(new InvalidCipherTextException("Simulated cipher error"));
        // WHEN & THEN
        assertThrows(InvalidCipherTextException.class,
                () -> organisationPseudoTokenMapper.map(callerOIN, token),
                "Expected InvalidCipherTextException to be thrown");
    }

    @Test
    @DisplayName("""
            Given a valid token and caller OIN
            When encryption fails with IOException
            Then an IOException should be thrown
            """)
    void map_WhenEncryptionThrowsIOException_ShouldThrowIOException() throws Exception {
        // GIVEN
        final var callerOIN = "IO_EXCEPTION_OIN";
        final var token = Token.builder().bsn("555555555").build();
        when(aesGcmSivCryptographer.encrypt(any(Identifier.class), eq(callerOIN)))
                .thenThrow(new IOException("Simulated I/O error"));
        // WHEN & THEN
        assertThrows(IOException.class,
                () -> organisationPseudoTokenMapper.map(callerOIN, token),
                "Expected IOException to be thrown");
    }
}
