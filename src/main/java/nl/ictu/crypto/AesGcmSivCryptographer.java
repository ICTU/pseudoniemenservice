package nl.ictu.crypto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.configuration.PseudoniemenServiceProperties;
import nl.ictu.model.Identifier;
import nl.ictu.utils.AesUtility;
import nl.ictu.utils.Base64Wrapper;
import nl.ictu.utils.MessageDigestWrapper;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

/**
 * Advanced Encryption Standard Galois/Counter Mode synthetic initialization vector.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AesGcmSivCryptographer {

    public static final int MAC_SIZE = 128;
    private static final int NONCE_LENTH = 12;
    private final PseudoniemenServiceProperties pseudoniemenServiceProperties;
    private final MessageDigestWrapper messageDigestWrapper;
    private final IdentifierConverter identifierConverter;
    private final Base64Wrapper base64Wrapper;

    /**
     * Creates AEADParameters using the given salt to generate a nonce and a private key for the
     * encryption process.
     *
     * @param salt the salt used to derive the nonce for the encryption process
     * @return AEADParameters containing the key, MAC size, and nonce for encryption
     */
    private AEADParameters createSecretKey(final String salt) {

        final var nonce16 = messageDigestWrapper.getMessageDigestInstance()
                .digest(salt.getBytes(StandardCharsets.UTF_8));
        final var nonce12 = Arrays.copyOf(nonce16, NONCE_LENTH);
        final var identifierPrivateKey = pseudoniemenServiceProperties.getIdentifierPrivateKey();
        final var keyParameter = new KeyParameter(
                base64Wrapper.decode(identifierPrivateKey));
        return new AEADParameters(keyParameter, MAC_SIZE, nonce12);
    }

    /**
     * Encrypts the given {@code Identifier} using a salt and returns the resulting Base64-encoded
     * ciphertext. This method leverages AES-GCM-SIV encryption for secure and authenticated
     * encryption.
     *
     * @param identifier the identifier object to be encrypted
     * @param salt       a string used to derive a nonce and key for encryption
     * @return the Base64-encoded string representation of the ciphertext
     * @throws InvalidCipherTextException if encryption process fails
     * @throws IOException                if an I/O error occurs during encryption
     */
    public String encrypt(final Identifier identifier, final String salt)
            throws InvalidCipherTextException, IOException {

        final var plaintext = identifierConverter.encode(identifier);
        final var cipher = new GCMSIVBlockCipher(AesUtility.getAESEngine());
        cipher.init(true, createSecretKey(salt));
        final var plainTextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        final var ciphertext = new byte[cipher.getOutputSize(plainTextBytes.length)];
        final var outputLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length,
                ciphertext, 0);
        cipher.doFinal(ciphertext, outputLength);
        cipher.reset();
        return base64Wrapper.encodeToString(ciphertext);
    }

    /**
     * Decrypts the given Base64-encoded ciphertext string using the provided salt. This method uses
     * AES-GCM-SIV decryption to securely retrieve the original plaintext.
     *
     * @param ciphertextString the Base64-encoded string containing the ciphertext to be decrypted
     * @param salt             a string used to derive the nonce and key for decryption
     * @return the decrypted {@code Identifier} object
     */
    @SneakyThrows
    public Identifier decrypt(final String ciphertextString, final String salt) {

        final var cipher = new GCMSIVBlockCipher(AesUtility.getAESEngine());
        cipher.init(false, createSecretKey(salt));
        final var ciphertext = base64Wrapper.decode(ciphertextString);
        final var plaintext = new byte[cipher.getOutputSize(ciphertext.length)];
        final var outputLength = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext,
                0);
        cipher.doFinal(plaintext, outputLength);
        cipher.reset();
        final var encodedIdentifier = new String(plaintext, StandardCharsets.UTF_8);
        return identifierConverter.decode(encodedIdentifier);
    }
}


