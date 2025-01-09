package nl.appsource.service.crypto;

import lombok.SneakyThrows;
import nl.appsource.model.Identifier;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AEADParameters;

import java.io.IOException;

public interface AesGcmSivCryptographerService {
    AEADParameters createSecretKey(String salt);

    String encrypt(Identifier identifier, String salt)
        throws InvalidCipherTextException, IOException;

    @SneakyThrows
    Identifier decrypt(String ciphertextString, String salt);
}
