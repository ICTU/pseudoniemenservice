package nl.appsource.service.crypto;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.configuration.PseudoniemenServiceProperties;
import nl.appsource.model.Identifier;
import nl.appsource.utils.AesUtility;
import nl.appsource.utils.Base64Wrapper;
import nl.appsource.utils.MessageDigestWrapper;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Advanced Encryption Standard Galois/Counter Mode synthetic initialization vector.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AesGcmSivCryptographerServiceImpl implements AesGcmSivCryptographerService {

    private static final int MAC_SIZE = 128;
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
    @Override
    public AEADParameters createSecretKey(final String salt) {

        final byte[] nonce16 = messageDigestWrapper.instance().digest(salt.getBytes(StandardCharsets.UTF_8));
        final byte[] nonce12 = Arrays.copyOf(nonce16, NONCE_LENTH);
        final String identifierPrivateKey = pseudoniemenServiceProperties.getIdentifierPrivateKey();
        final KeyParameter keyParameter = new KeyParameter(base64Wrapper.decode(identifierPrivateKey));
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
    @Override
    public String encrypt(final Identifier identifier, final String salt) throws InvalidCipherTextException, IOException {

        final String plaintext = identifierConverter.serialize(identifier);
        final GCMSIVBlockCipher cipher = new GCMSIVBlockCipher(AesUtility.getAESEngine());
        cipher.init(true, createSecretKey(salt));
        final byte[] plainTextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
        final byte[] ciphertext = new byte[cipher.getOutputSize(plainTextBytes.length)];
        final int outputLength = cipher.processBytes(plainTextBytes, 0, plainTextBytes.length, ciphertext, 0);
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
    @Override
    public Identifier decrypt(final String ciphertextString, final String salt) throws InvalidCipherTextException, JsonProcessingException {

        final GCMSIVBlockCipher cipher = new GCMSIVBlockCipher(AesUtility.getAESEngine());
        cipher.init(false, createSecretKey(salt));
        final byte[] ciphertext = base64Wrapper.decode(ciphertextString);
        final byte[] plaintext = new byte[cipher.getOutputSize(ciphertext.length)];
        final int outputLength = cipher.processBytes(ciphertext, 0, ciphertext.length, plaintext, 0);
        cipher.doFinal(plaintext, outputLength);
        cipher.reset();
        final String encodedIdentifier = new String(plaintext, StandardCharsets.UTF_8);
        return identifierConverter.deSerialize(encodedIdentifier);
    }
}


