package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import nl.ictu.model.Identifier;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.crypto.AesGcmSivCryptographer;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BsnPseudoMapper {

    public static final String V_1 = "v1";
    private final AesGcmSivCryptographer aesGcmSivCryptographer;

    /**
     * Maps a given BSN (Burger Service Nummer) and OIN (Organisatie-identificatienummer) to a
     * {@link WsExchangeIdentifierResponse} containing a pseudo-anonymous identifier.
     *
     * @param bsn the BSN to be encrypted and included in the identifier
     * @param oin the OIN used as the salt for encryption
     * @return a {@link WsExchangeIdentifierResponse} containing the pseudo-anonymous identifier
     * @throws IOException if an I/O error occurs during the encryption process
     * @throws InvalidCipherTextException if encryption fails due to invalid cipher text
     */
    public WsExchangeIdentifierResponse map(final String bsn, final String oin)
            throws IOException, InvalidCipherTextException {

        return WsExchangeIdentifierResponse.builder()
                .identifier(WsIdentifier.builder()
                        .type(ORGANISATION_PSEUDO)
                        .value(aesGcmSivCryptographer.encrypt(Identifier.builder()
                                .version(V_1)
                                .bsn(bsn)
                                .build(), oin))
                        .build())
                .build();
    }
}
