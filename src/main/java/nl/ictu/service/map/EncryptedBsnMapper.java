package nl.ictu.service.map;

import lombok.RequiredArgsConstructor;
import nl.ictu.crypto.AesGcmSivCryptographer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptedBsnMapper {

    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    /**
     * Maps the encrypted business service number to its decrypted value using the given recipient OIN.
     *
     * @param bsnValue     the encrypted business service number to be decrypted
     * @param recipientOIN the recipient OIN key used for decryption
     * @return the decrypted business service number
     */
    public String map(final String bsnValue, final String recipientOIN) {

        final var decodedIdentifier = aesGcmSivCryptographer.decrypt(bsnValue, recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
