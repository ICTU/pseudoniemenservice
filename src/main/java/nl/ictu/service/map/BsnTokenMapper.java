package nl.appsource.service.map;

import lombok.RequiredArgsConstructor;
import nl.appsource.model.Token;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import org.springframework.stereotype.Component;

import static nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;

@Component
@RequiredArgsConstructor
public class BsnTokenMapper {

    /**
     * Maps a given Token to a WsExchangeTokenResponse. Populates the response with a WsIdentifier
     * containing the BSN value from the provided token.
     *
     * @param token the Token object containing BSN and other data
     * @return a WsExchangeTokenResponse containing the identifier with the BSN value
     */
    public WsExchangeTokenResponse map(final Token token) {

        return WsExchangeTokenResponse.builder()
            .identifier(WsIdentifier.builder()
                .type(BSN)
                .value(token.getBsn())
                .build())
            .build();
    }
}
