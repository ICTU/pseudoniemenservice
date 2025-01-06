package nl.ictu.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.crypto.AesGcmSivCryptographer;
import nl.ictu.crypto.IdentifierConverter;
import nl.ictu.model.Identifier;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Class for testing {@link AesGcmSivCryptographer}
 */
@Slf4j
@ActiveProfiles("test")
class TestAesGcmSivCryptographer {

    private final AesGcmSivCryptographer aesGcmSivCryptographer = new AesGcmSivCryptographer(
            new PseudoniemenServiceProperties().setIdentifierPrivateKey(
                    "QTBtVEhLN3EwMHJ3QXN1ZUFqNzVrT3hDQTBIWWNIZTU="),
            new MessageDigestWrapper(),
            new IdentifierConverter(new ObjectMapper()),
            new Base64Wrapper()
    );
    private final Set<String> testStrings = new HashSet<>(
            Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo",
                    "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    @DisplayName("""
            Given a set of test strings
            When encrypting and decrypting each string with a specific key
            Then the decrypted identifier's BSN should be equal to the original plain string
            """)
    void testEncyptDecryptForDifferentStringLengths() {

        testStrings.forEach(plain -> {
            try {
                // GIVEN
                final var crypted = aesGcmSivCryptographer.encrypt(Identifier.builder()
                                .bsn(plain)
                                .build(),
                        "helloHowAreyo12345678");
                // WHEN
                final var actual = aesGcmSivCryptographer.decrypt(crypted,
                        "helloHowAreyo12345678");
                // THEN
                assertThat(actual.getBsn()).isEqualTo(plain);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @DisplayName("""
            Given the same plaintext message and encryption key
            When encrypting the message twice
            Then the resulting ciphertexts should be the same due to SIV mode
            """)
    void testCiphertextIsTheSameForSamePlaintext() throws Exception {
        // GIVEN
        final var plaintext = "This is a test message to ensure ciphertext is different!";
        final var identifier = Identifier.builder().bsn(plaintext).build();
        // WHEN
        final var encryptedMessage1 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");
        final var encryptedMessage2 = aesGcmSivCryptographer.encrypt(identifier, "aniceSaltGorYu");
        // THEN
        // Assert that the two ciphertexts are the same
        assertThat(encryptedMessage1).isEqualTo(encryptedMessage2);
    }
}
