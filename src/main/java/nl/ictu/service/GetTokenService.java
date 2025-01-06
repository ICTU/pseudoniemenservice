package nl.ictu.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.ictu.service.exception.WsGetTokenProcessingException;
import nl.ictu.service.map.WsGetTokenResponseMapper;
import nl.ictu.service.map.WsIdentifierOinBsnMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public final class GetTokenService {

    private final WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;
    private final WsGetTokenResponseMapper wsGetTokenResponseMapper;

    /**
     * Generates an encrypted token response based on the given recipient OIN and identifier.
     * Validates the identifier type and maps it to the corresponding BSN before creating the
     * encrypted token.
     *
     * @param recipientOIN the recipient's organizational identification number
     * @param identifier   the identifier containing value and type information
     * @return a {@link WsGetTokenResponse} containing the encrypted token, or null if the
     * identifier is invalid or BSN mapping fails
     */
    @SneakyThrows
    public WsGetTokenResponse getWsGetTokenResponse(final String recipientOIN,
            final WsIdentifier identifier) {

        final var creationDate = System.currentTimeMillis();
        // check is callerOIN allowed to communicatie with sinkOIN
        try {
            final var bsn = wsIdentifierOinBsnMapper.map(identifier, recipientOIN);
            return wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN);
        } catch (Exception ex) {
            final var exceptionMessage = ex.getMessage();
            log.warn(exceptionMessage);
            throw new WsGetTokenProcessingException(exceptionMessage);
        }
    }
}
