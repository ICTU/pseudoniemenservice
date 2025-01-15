package nl.appsource.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.appsource.pseudoniemenservice.generated.server.model.WsGetTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.service.map.WsGetTokenResponseMapper;
import nl.appsource.service.map.WsIdentifierOinBsnMapper;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public final class GetTokenService {

    private final WsIdentifierOinBsnMapper wsIdentifierOinBsnMapper;
    private final WsGetTokenResponseMapper wsGetTokenResponseMapper;

    public WsGetTokenResponse getWsGetTokenResponse(final String recipientOIN,
                                                    final WsIdentifier identifier)
        throws InvalidCipherTextException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        final long creationDate = System.currentTimeMillis();
        // check is callerOIN allowed to communicatie with sinkOIN
        final String bsn = wsIdentifierOinBsnMapper.map(identifier, recipientOIN);
        return wsGetTokenResponseMapper.map(bsn, creationDate, recipientOIN);
    }
}
