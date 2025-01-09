package nl.appsource.service.map;

import lombok.RequiredArgsConstructor;
import nl.appsource.model.Identifier;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.service.crypto.AesGcmSivCryptographerService;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

@Component
@RequiredArgsConstructor
public class BsnPseudoMapper {

    public static final String V_1 = "v1";
    private final AesGcmSivCryptographerService aesGcmSivCryptographerService;

    /**
     * Maps a given BSN (Burger Service Nummer) and OIN (Organisatie-identificatienummer) to a
     * {@link WsExchangeIdentifierResponse} containing a pseudo-anonymous identifier.
     *
     * @param bsn the BSN to be encrypted and included in the identifier
     * @param oin the OIN used as the salt for encryption
     * @return a {@link WsExchangeIdentifierResponse} containing the pseudo-anonymous identifier
     * @throws IOException                if an I/O error occurs during the encryption process
     * @throws InvalidCipherTextException if encryption fails due to invalid cipher text
     */
    public WsExchangeIdentifierResponse map(final String bsn, final String oin)
        throws IOException, InvalidCipherTextException {

        return WsExchangeIdentifierResponse.builder()
            .identifier(WsIdentifier.builder()
                .type(ORGANISATION_PSEUDO)
                .value(aesGcmSivCryptographerService.encrypt(Identifier.builder()
                    .version(V_1)
                    .bsn(bsn)
                    .build(), oin))
                .build())
            .build();
    }
}
