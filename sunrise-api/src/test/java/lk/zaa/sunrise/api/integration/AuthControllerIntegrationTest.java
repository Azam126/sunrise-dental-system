package lk.zaa.sunrise.api.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercises POST /api/auth/login through the real HTTP layer, real Spring Security, and a real (H2) database. */
class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("POST /api/auth/login with correct credentials returns 200, a token, and the right role")
    void loginSucceedsAndReturnsToken() throws Exception {
        String body = """
                {"username": "reception", "password": "Front@123"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Kasun Silva"))
                .andExpect(jsonPath("$.role").value("RECEPTIONIST"));
    }

    @Test
    @DisplayName("POST /api/auth/login with a wrong password returns 401 with a clear message")
    void loginFailsWithWrongPassword() throws Exception {
        String body = """
                {"username": "reception", "password": "wrong-password"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login with a blank username returns 400 with a validation message")
    void loginFailsValidationForBlankUsername() throws Exception {
        String body = """
                {"username": "", "password": "Front@123"}
                """;

        mockMvc.perform(post("/api/auth/login").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0]").value("Username is required"));
    }
}
