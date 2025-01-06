package nl.ictu.service.map;

import lombok.RequiredArgsConstructor;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WsIdentifierOinBsnMapper {

    private final EncryptedBsnMapper encryptedBsnMapper;

    /**
     * Maps a given {@link WsIdentifier} object to the corresponding representation based on its type.
     * If the type is BSN, it returns the value as-is. If the type is ORGANISATION_PSEUDO, it applies
     * a mapping operation using an encrypted BSN mapper.
     *
     * @param identifier the {@link WsIdentifier} to be mapped, containing the identifier's value and type
     * @param recipientOIN the recipient organization identification number (OIN) used for mapping in case of ORGANISATION_PSEUDO type
     * @return the mapped value of the identifier based on its type
     * @throws IllegalArgumentException if the identifier type is unsupported
     */
    public String map(final WsIdentifier identifier, final String recipientOIN) {

        final String bsnValue = identifier.getValue();
        switch (identifier.getType()) {
            case BSN -> {
                return bsnValue;
            }
            case ORGANISATION_PSEUDO -> {
                return encryptedBsnMapper.map(bsnValue, recipientOIN);
            }
            default -> throw new IllegalArgumentException(
                    "Unsupported identifier type: " + identifier.getType());
        }
    }
}
