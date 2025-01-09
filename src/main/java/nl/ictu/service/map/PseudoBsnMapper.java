package nl.appsource.service.map;


import lombok.RequiredArgsConstructor;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;

@Component
@RequiredArgsConstructor
public class PseudoBsnMapper {

    private final AesGcmSivCryptographerService aesGcmSivCryptographerService;

    /**
     * Maps a given pseudonym and organizational identification number (OIN) to a
     * {@link WsExchangeIdentifierResponse}. The pseudonym is decrypted using the
     * provided OIN to derive the corresponding BSN (Burger Service Nummer) value.
     *
     * @param pseudo the pseudonym string to be decrypted
     * @param oin    the organizational identification number used as a decryption key
     * @return a {@link WsExchangeIdentifierResponse} containing the decrypted BSN encapsulated in a {@link WsIdentifier}
     * @throws IOException                if an I/O error occurs during the decryption process
     * @throws InvalidCipherTextException if decryption fails due to invalid cipher text
     */
    public WsExchangeIdentifierResponse map(final String pseudo, final String oin)
        throws IOException, InvalidCipherTextException {

        return WsExchangeIdentifierResponse.builder()

            .identifier(WsIdentifier.builder()
                .type(BSN)
                .value(aesGcmSivCryptographerService.decrypt(pseudo, oin).getBsn())
                .build())
            .build();
    }

}
