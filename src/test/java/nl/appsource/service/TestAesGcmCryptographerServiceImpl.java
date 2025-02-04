package nl.appsource.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.configuration.PseudoniemenServiceProperties;
import nl.appsource.service.crypto.AesGcmCryptographerServiceImpl;
import nl.appsource.service.serializer.TokenSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Class for testing {@link AesGcmCryptographerServiceImpl}
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ObjectMapper.class, AesGcmCryptographerServiceImpl.class, TestAesGcmCryptographerServiceImpl.TestConfiguration.class, TokenSerializer.class})
class TestAesGcmCryptographerServiceImpl {

    @Autowired
    private AesGcmCryptographerServiceImpl aesGcmCryptographerService;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfiguration {
        @Bean
        public PseudoniemenServiceProperties pseudoniemenServiceProperties() {
            return new PseudoniemenServiceProperties()
                .setTokenPrivateKey("i4dfBykN5Fjw9p3ADxvpRUhpbFSXepRSOcRGuaiJ4iQ=")
                .setIdentifierPrivateKey("b2RPRGh6aThiMmluVEpMWVVJM2lOTGlWekVCU2hDMEU=");
        }
    }

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
                final String crypted = aesGcmCryptographerService.encrypt(plain, "helloHowAreyo12345678");
                // WHEN
                final String actual = aesGcmCryptographerService.decrypt(crypted, "helloHowAreyo12345678");
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
    @SneakyThrows
    void testCiphertextIsDifferentForSamePlaintext() {
        // GIVEN
        final String plaintext = "This is a test message to ensure ciphertext is different!";
        // WHEN
        final String encryptedMessage1 = aesGcmCryptographerService.encrypt(plaintext, "aniceSaltGorYu");
        final String encryptedMessage2 = aesGcmCryptographerService.encrypt(plaintext, "aniceSaltGorYu");
        // THEN
        // Assert that the two ciphertexts are different
        assertThat(encryptedMessage1).isNotEqualTo(encryptedMessage2);
    }
}
