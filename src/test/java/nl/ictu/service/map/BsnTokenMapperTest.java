package nl.ictu.service.map;

import static nl.ictu.pseudoniemenservice.generated.server.model.WsIdentifierTypes.BSN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import nl.ictu.model.Token;
import nl.ictu.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BsnTokenMapperTest {

    private BsnTokenMapper bsnTokenMapper;

    @BeforeEach
    void setUp() {

        bsnTokenMapper = new BsnTokenMapper();
    }

    @Test
    @DisplayName("""
            Given a token with a valid BSN
            When mapped
            Then the response contains an identifier with the correct BSN type and value
            """)
    void map_WhenTokenHasValidBsn_ShouldReturnResponseWithBsnIdentifier() {
        // GIVEN
        final var token = Token.builder()
                .bsn("123456789")
                .build();
        // WHEN
        final var response = bsnTokenMapper.map(token);
        // THEN
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getIdentifier(), "Identifier should not be null");
        assertEquals(BSN, response.getIdentifier().getType(), "Identifier type should be BSN");
        assertEquals("123456789", response.getIdentifier().getValue(),
                "Identifier value should match token’s BSN");
    }

    @Test
    @DisplayName("""
            Given a token without a BSN
            When mapped
            Then the response contains an identifier with BSN type but a null value
            """)
    void map_WhenTokenHasNoBsn_ShouldHandleNullBsnGracefully() {
        // GIVEN
        final var token = Token.builder().build(); // No BSN set
        // WHEN
        final var response = bsnTokenMapper.map(token);
        // THEN
        assertNotNull(response, "Response should not be null even if BSN is null");
        assertNotNull(response.getIdentifier(), "Identifier should not be null");
        assertEquals(BSN, response.getIdentifier().getType(),
                "Identifier type should still be BSN");
        assertNull(response.getIdentifier().getValue(),
                "Value should be null if token’s BSN is null");
    }
}
