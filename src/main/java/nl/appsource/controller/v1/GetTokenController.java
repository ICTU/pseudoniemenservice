package nl.appsource.controller.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.pseudoniemenservice.generated.server.api.GetTokenApi;
import nl.appsource.pseudoniemenservice.generated.server.model.WsGetTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.service.GetTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public final class GetTokenController implements GetTokenApi, VersionOneController {

    private final GetTokenService getTokenService;

    /**
     * Retrieves a token based on the provided caller identifier and request details.
     *
     * @param callerOIN         The identifier of the caller organization initiating the request.
     * @param wsGetTokenRequest The request object containing the recipient organization identifier
     *                          and additional details.
     * @return A ResponseEntity containing the token if the request is successful, or a
     * ResponseEntity with a status of UNPROCESSABLE_ENTITY if the token cannot be retrieved.
     */
    @Override
    public ResponseEntity<WsGetTokenResponse> getToken(final String callerOIN,
                                                       final WsGetTokenRequest wsGetTokenRequest) {

        final String recipientOIN = wsGetTokenRequest.getRecipientOIN();
        final WsIdentifier identifier = wsGetTokenRequest.getIdentifier();
        try {
            final WsGetTokenResponse wsGetTokenResponse = getTokenService.getWsGetTokenResponse(
                recipientOIN, identifier);
            return ResponseEntity.ok(wsGetTokenResponse);
        } catch (final Exception e) {
            log.error("", e);
            return ResponseEntity.unprocessableEntity().build();
        }
    }
}
