package nl.appsource.service.crypto;

import nl.appsource.model.v1.Token;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AesGcmCryptographerService {
    String encryptToken(final Token token, final String salt)
        throws IllegalBlockSizeException,
        BadPaddingException,
        InvalidAlgorithmParameterException,
        InvalidKeyException,
        NoSuchAlgorithmException,
        NoSuchPaddingException, IOException;

    SecretKey createSecretKey(String salt);

    String decrypt(String ciphertextWithIv, String salt)
        throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        InvalidAlgorithmParameterException,
        InvalidKeyException,
        IllegalBlockSizeException,
        BadPaddingException;
}
