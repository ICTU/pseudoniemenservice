package nl.ictu;

import static java.util.Map.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.CollectionUtils;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestingWebApplicationTests {

    @Autowired
    private Environment environment;
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("""
            Given the Spring Boot application is running with actuator enabled
            When accessing the /actuator/health endpoint
            Then the response should contain a status of 'UP'
            """)
    void testActuatorHealthEndpoint() {
        final int actuatorPort = environment.getProperty("local.management.port", Integer.class);
        assertThat(
                restTemplate.getForObject("http://localhost:" + actuatorPort + "/actuator/health",
                        String.class)
        ).contains("{\"status\":\"UP\"}");
    }

    @Test
    @DisplayName("""
            Given a request to get a token with a BSN identifier
            When sending the request to /v1/getToken
            Then the response should include a token
            And the token can be used to exchange for the identifier type BSN
            """)
    void testGetAtokenExchangeForBSN() {
        // get a token
        final var getTokenBody = Map.of("recipientOIN", "54321543215432154321", "identifier",
                Map.of("type", "BSN", "value", "012345679"));
        final var httpEntityGetToken = new HttpEntity<>(getTokenBody,
                new HttpHeaders(CollectionUtils.toMultiValueMap(
                        of("callerOIN", List.of("0912345012345012345012345")))));
        final var tokenExchange = restTemplate.exchange("/v1/getToken", HttpMethod.POST,
                httpEntityGetToken, Map.class);
        assertThat(tokenExchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(tokenExchange)
                .extracting("body")
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Void.class))
                .containsKey("token");

        // change token for identifier
        final var token = (String) tokenExchange.getBody().get("token");
        final var exchangeTokenBody = Map.of("token", token, "identifierType", "BSN");
        final var httpEntityExchangeToken = new HttpEntity<>(exchangeTokenBody,
                new HttpHeaders(CollectionUtils.toMultiValueMap(
                        of("callerOIN", List.of("54321543215432154321")))));
        final var identifierExchange = restTemplate.exchange("/v1/exchangeToken", HttpMethod.POST,
                httpEntityExchangeToken,
                Map.class);
        assertThat(identifierExchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(identifierExchange)
                .extracting("body")
                .asInstanceOf(InstanceOfAssertFactories.map(String.class, Map.class))
                .containsExactly(entry("identifier", Map.of("type", "BSN", "value", "012345679")));
    }
}
