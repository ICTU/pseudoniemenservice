package nl.appsource.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(SpringExtension.class)
class IndexControllerTest {

    private final IndexController indexController = new IndexController();
    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();

    @Test
    @DisplayName("""
        Given a request to the root endpoint
        When performing a GET request
        Then the response redirects to Swagger UI
        """)
    @SneakyThrows
    void testRedirectToSwaggerUi() {
        // WHEN & THEN
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/"))
            .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
            .andExpect(MockMvcResultMatchers.redirectedUrl("/swagger-ui/index.html"));
    }
}
