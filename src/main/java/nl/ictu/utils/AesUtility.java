package nl.ictu.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import lombok.experimental.UtilityClass;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;

@UtilityClass
public class AesUtility {

    public static final int IV_LENGTH = 12;
    public static final int TAG_LENGTH = 128;
    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * Generates a random Initialization Vector (IV) for use with AES-GCM encryption.
     *
     * @return a GCMParameterSpec containing the randomly generated IV and the specified tag length
     */
    // Method to generate a random Initialization Vector (IV)
    public static GCMParameterSpec generateIV() {

        byte[] iv = new byte[IV_LENGTH]; // AES block size is 16 bytes
        SECURE_RANDOM.nextBytes(iv);
        return new GCMParameterSpec(TAG_LENGTH, iv);
    }

    /**
     * Creates a {@link GCMParameterSpec} instance using the specified initialization vector (IV).
     *
     * @param iv the byte array representing the initialization vector; must not be null, and its
     *           length should match the expected IV length for AES-GCM
     * @return a GCMParameterSpec initialized with the provided IV and the predefined tag length
     */
    public static GCMParameterSpec createIVfromValues(final byte[] iv) {

        return new GCMParameterSpec(TAG_LENGTH, iv);
    }

    /**
     * Creates and initializes a {@link Cipher} instance using the AES/GCM/NoPadding
     * transformation.
     *
     * @return a Cipher instance initialized with the AES/GCM/NoPadding transformation
     * @throws NoSuchPaddingException   if the requested padding scheme is not available
     * @throws NoSuchAlgorithmException if the AES algorithm in GCM mode is not available
     */
    public static Cipher createCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {

        return Cipher.getInstance(CIPHER);
    }

    /**
     * Returns an instance of {@link MultiBlockCipher} configured as an AES engine.
     *
     * @return a new instance of a {@link MultiBlockCipher} configured to use AES encryption
     */
    public static MultiBlockCipher getAESEngine() {

        return AESEngine.newInstance();
    }
}
