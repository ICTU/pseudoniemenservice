package nl.ictu.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.crypto.AesGcmCryptographer;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Class for testing {@link AesGcmCryptographer}
 */
@Slf4j
@ActiveProfiles("test")
class TestAesGcmCryptographer {

    private final AesGcmCryptographer aesGcmCryptographer = new AesGcmCryptographer(
            new Base64Wrapper(),
            new MessageDigestWrapper(),
            new PseudoniemenServiceProperties().setTokenPrivateKey(
                    "bFUyS1FRTVpON0pCSFFRRGdtSllSeUQ1MlRna2txVmI=")
    );
    private final Set<String> testStrings = new HashSet<>(
            Arrays.asList("a", "bb", "dsv", "ghad", "dhaht", "uDg5Av", "d93fdvv", "dj83hzHo",
                    "38iKawKv9", "dk(gkzm)Mh", "gjk)s3$g9cQ"));

    @Test
    @DisplayName("""
            Given a set of test strings
            When encrypting and decrypting each string with a specific key
            Then the decrypted string should be equal to the original plain string
            """)
    void testEncyptDecryptForDifferentStringLengths() {

        testStrings.forEach(plain -> {
            try {
                // GIVEN
                final var crypted = aesGcmCryptographer.encrypt(plain, "helloHowAreyo12345678");
                // WHEN
                final var actual = aesGcmCryptographer.decrypt(crypted, "helloHowAreyo12345678");
                // THEN
                assertThat(actual).isEqualTo(plain);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    @DisplayName("""
            Given the same plaintext message and encryption key
            When encrypting the message twice
            Then the resulting ciphertexts should be different due to IV randomness
            """)
    void testCiphertextIsDifferentForSamePlaintext() throws Exception {
        // GIVEN
        final var plaintext = "This is a test message to ensure ciphertext is different!";
        // WHEN
        final var encryptedMessage1 = aesGcmCryptographer.encrypt(plaintext, "aniceSaltGorYu");
        final var encryptedMessage2 = aesGcmCryptographer.encrypt(plaintext, "aniceSaltGorYu");
        // THEN
        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isNotEqualTo(encryptedMessage2);
    }
}
