package nl.appsource.service.map;

import lombok.RequiredArgsConstructor;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncryptedBsnMapper {

    private final AesGcmSivCryptographerService aesGcmSivCryptographerService;

    /**
     * Maps the encrypted business service number to its decrypted value using the given recipient OIN.
     *
     * @param bsnValue     the encrypted business service number to be decrypted
     * @param recipientOIN the recipient OIN key used for decryption
     * @return the decrypted business service number
     */
    public String map(final String bsnValue, final String recipientOIN) {

        final var decodedIdentifier = aesGcmSivCryptographerService.decrypt(bsnValue, recipientOIN);
        return decodedIdentifier.getBsn();
    }
}
