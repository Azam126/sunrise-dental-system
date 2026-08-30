package lk.zaa.sunrise.api.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Proves the access-control rules in SecurityConfig actually hold end-to-end
 * with real JWTs — not just that AdminUserController is annotated correctly,
 * but that a Receptionist token is genuinely refused by /api/admin/**.
 */
class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("A protected endpoint returns 401 when no token is supplied at all")
    void protectedEndpointRejectsMissingToken() throws Exception {
        mockMvc.perform(get("/api/dentists"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("A protected endpoint returns 401 for a garbage/invalid token")
    void protectedEndpointRejectsInvalidToken() throws Exception {
        mockMvc.perform(get("/api/dentists").header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("A valid Receptionist token CAN reach an ordinary protected endpoint")
    void receptionistCanReachOrdinaryEndpoint() throws Exception {
        String token = loginAndGetToken(RECEPTIONIST_USERNAME, RECEPTIONIST_PASSWORD);

        mockMvc.perform(get("/api/dentists").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("A valid Receptionist token is REFUSED on an /api/admin/** endpoint (403)")
    void receptionistIsForbiddenFromAdminEndpoint() throws Exception {
        String token = loginAndGetToken(RECEPTIONIST_USERNAME, RECEPTIONIST_PASSWORD);

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("A valid Administrator token CAN reach an /api/admin/** endpoint")
    void administratorCanReachAdminEndpoint() throws Exception {
        String token = loginAndGetToken(ADMIN_USERNAME, ADMIN_PASSWORD);

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
