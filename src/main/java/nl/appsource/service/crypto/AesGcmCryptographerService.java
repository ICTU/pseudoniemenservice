package nl.appsource.service.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface AesGcmCryptographerService {
    String encrypt(String plaintext, String salt)
        throws IllegalBlockSizeException,
        BadPaddingException,
        InvalidAlgorithmParameterException,
        InvalidKeyException,
        NoSuchAlgorithmException,
        NoSuchPaddingException;

    SecretKey createSecretKey(String salt);

    String decrypt(String ciphertextWithIv, String salt)
        throws NoSuchPaddingException,
        NoSuchAlgorithmException,
        InvalidAlgorithmParameterException,
        InvalidKeyException,
        IllegalBlockSizeException,
        BadPaddingException;
}
