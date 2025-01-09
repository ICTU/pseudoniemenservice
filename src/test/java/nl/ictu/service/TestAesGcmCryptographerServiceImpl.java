package nl.ictu.service;

import lombok.extern.slf4j.Slf4j;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.service.crypto.AesGcmCryptographerService;
import nl.ictu.service.crypto.AesGcmCryptographerServiceImpl;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class for testing {@link AesGcmCryptographerServiceImpl}
 */
@Slf4j
@ActiveProfiles("test")
class TestAesGcmCryptographerServiceImpl {

    private final AesGcmCryptographerService aesGcmCryptographerService = new AesGcmCryptographerServiceImpl(
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
                final var crypted = aesGcmCryptographerService.encrypt(plain, "helloHowAreyo12345678");
                // WHEN
                final var actual = aesGcmCryptographerService.decrypt(crypted, "helloHowAreyo12345678");
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
        final var encryptedMessage1 = aesGcmCryptographerService.encrypt(plaintext, "aniceSaltGorYu");
        final var encryptedMessage2 = aesGcmCryptographerService.encrypt(plaintext, "aniceSaltGorYu");
        // THEN
        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isNotEqualTo(encryptedMessage2);
    }
}
