package nl.appsource.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenRequest;
import nl.appsource.pseudoniemenservice.generated.server.model.WsExchangeTokenResponse;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifier;
import nl.appsource.pseudoniemenservice.generated.server.model.WsIdentifierTypes;
import nl.appsource.service.ExchangeTokenService;
import nl.appsource.service.exception.InvalidOINException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangeTokenController.class)
class ExchangeTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ExchangeTokenService exchangeTokenService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("""
        Given a valid token and identifier type
        When calling exchangeToken()
        Then it returns 200 OK with the expected identifier in the response
        """)
    @SneakyThrows
    void exchangeToken_ShouldReturnOk() {
        // GIVEN: a request payload
        final var requestPayload = WsExchangeTokenRequest.builder()
            .token("testToken")
            .identifierType(WsIdentifierTypes.BSN)
            .build();
        // AND: a mock service response
        final var responsePayload = new WsExchangeTokenResponse();
        responsePayload.setIdentifier(WsIdentifier.builder()
            .type(WsIdentifierTypes.BSN)
            .value("convertedIdentifier")
            .build());
        // WHEN: the service is called, return the response payload
        when(exchangeTokenService.exchangeToken(eq("TEST_OIN"), any(WsExchangeTokenRequest.class)))
            .thenReturn(responsePayload);
        // THEN: perform the POST request
        mockMvc.perform(post("/v1/exchangeToken")
                .header("callerOIN", "TEST_OIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier.value").value("convertedIdentifier"))
            .andExpect(jsonPath("$.identifier.type").value("BSN"));
    }

    @Test
    @DisplayName("""
        Given an invalid token and identifier type
        When calling exchangeToken()
        Then it returns 422 UNPROCESSABLE_ENTITY with the appropriate error
        """)
    @SneakyThrows
    void exchangeToken_ShouldReturnUnprocessableEntity() {
        // GIVEN: a request payload
        final var requestPayload = WsExchangeTokenRequest.builder()
            .token("testToken").identifierType(WsIdentifierTypes.ORGANISATION_PSEUDO)
            .build();
        // WHEN: the service throws an exception
        doThrow(new InvalidOINException("Service error"))
            .when(exchangeTokenService)
            .exchangeToken(eq("FAIL_OIN"), any(WsExchangeTokenRequest.class));
        // THEN: perform the POST request
        mockMvc.perform(post("/v1/exchangeToken")
                .header("callerOIN", "FAIL_OIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload)))
            .andExpect(status().isUnprocessableEntity());
    }
}
