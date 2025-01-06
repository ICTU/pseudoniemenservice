package nl.ictu.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.ictu.pseudoniemenservice.generated.server.api.ExchangeTokenApi;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.ictu.service.ExchangeTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public final class ExchangeTokenController implements ExchangeTokenApi, VersionOneController {

    private final ExchangeTokenService exchangeTokenService;

    /**
     * Handles the exchange of a token and returns the corresponding identifier in a response. This
     * method validates the caller's OIN, processes the incoming token using the specified
     * identifier type, and constructs a response accordingly.
     *
     * @param callerOIN                           The identifier of the requesting organization
     *                                            (OIN).
     * @param wsExchangeTokenForIdentifierRequest The request containing the token and identifier
     *                                            type details.
     * @return A response entity containing the converted identifier or a status indicating failure.
     */
    @Override
    public ResponseEntity<WsExchangeTokenResponse> exchangeToken(final String callerOIN,
            final WsExchangeTokenRequest wsExchangeTokenForIdentifierRequest) {

        final var wsExchangeTokenResponse = exchangeTokenService.exchangeToken(callerOIN,
                wsExchangeTokenForIdentifierRequest);
        return ResponseEntity.ok(wsExchangeTokenResponse);
    }
}
