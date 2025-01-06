package nl.ictu.service;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.ORGANISATION_PSEUDO;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierRequest;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeIdentifierResponse;
import nl.ictu.service.exception.InvalidWsIdentifierRequestTypeException;
import nl.ictu.service.map.BsnPseudoMapper;
import nl.ictu.service.map.PseudoBsnMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public final class ExchangeIdentifierService {

    private final BsnPseudoMapper bsnPseudoMapper;
    private final PseudoBsnMapper pseudoBsnMapper;

    /**
     * Processes the exchange of an identifier between different types based on specific mappings
     * and returns the corresponding response.
     *                                                 caller.
     * @param wsExchangeIdentifierForIdentifierRequest The request object containing details of the
     *                                                 identifier to be exchanged, including its
     *                                                 value, type, recipient OIN, and recipient
     *                                                 identifier type.
     * @return A {@link WsExchangeIdentifierResponse} containing the exchanged identifier. Returns
     * null if no appropriate mapping exists for the provided inputs.
     */
    @SneakyThrows
    public WsExchangeIdentifierResponse exchangeIdentifier(
            final WsExchangeIdentifierRequest wsExchangeIdentifierForIdentifierRequest) {

        final var wsIdentifierRequest = wsExchangeIdentifierForIdentifierRequest.getIdentifier();
        final var recipientOIN = wsExchangeIdentifierForIdentifierRequest.getRecipientOIN();
        final var recipientIdentifierType = wsExchangeIdentifierForIdentifierRequest.getRecipientIdentifierType();
        if (BSN.equals(wsIdentifierRequest.getType()) && ORGANISATION_PSEUDO.equals(
                recipientIdentifierType)) {
            return bsnPseudoMapper.map(wsIdentifierRequest.getValue(), recipientOIN);
        } else if (ORGANISATION_PSEUDO.equals(wsIdentifierRequest.getType()) && BSN.equals(
                recipientIdentifierType)) {
            return pseudoBsnMapper.map(wsIdentifierRequest.getValue(), recipientOIN);
        }
        throw new InvalidWsIdentifierRequestTypeException(
                "Invalid WsIdentifierRequest type cannot be processed.");
    }
}
