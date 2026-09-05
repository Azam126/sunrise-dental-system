package lk.zaa.sunrise.api.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.Receptionist;
import lk.zaa.sunrise.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Shared base for full-stack ("does the whole request/response cycle behave
 * correctly, including real JWT security and a real — if in-memory —
 * database") integration tests. MockMvc is wired to the real
 * SecurityFilterChain, so a token obtained from loginAndGetToken() is a
 * genuine JWT that JwtAuthFilter/SecurityConfig validate exactly as they
 * would in production.
 *
 * BUG FIX: the class-level doc comment here used to claim "each subclass
 * gets a fresh H2 database" - that was never actually true. Spring's test
 * framework caches and reuses one ApplicationContext (and therefore one H2
 * database, since its URL uses DB_CLOSE_DELAY=-1) across every test class
 * that shares this exact configuration - which is all four
 * AbstractIntegrationTest subclasses. seedStaffAccounts() re-inserting
 * 'admin'/'reception' before every single @Test method, across every
 * subclass, without ever clearing the table first, hit the username unique
 * constraint on the second call onwards (caught by a real local run this
 * sandbox could never perform). Deleting first makes it idempotent
 * regardless of whether the context/database is fresh or reused.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected UserRepository userRepository;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected ObjectMapper objectMapper;

    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "Admin@123";
    protected static final String RECEPTIONIST_USERNAME = "reception";
    protected static final String RECEPTIONIST_PASSWORD = "Front@123";

    @BeforeEach
    void seedStaffAccounts() {
        userRepository.deleteAll();
        userRepository.save(new Administrator(ADMIN_USERNAME, passwordEncoder.encode(ADMIN_PASSWORD), "Nadeesha Perera"));
        userRepository.save(new Receptionist(RECEPTIONIST_USERNAME, passwordEncoder.encode(RECEPTIONIST_PASSWORD), "Kasun Silva"));
    }

    protected String loginAndGetToken(String username, String password) throws Exception {
        String body = """
                {"username": "%s", "password": "%s"}
                """.formatted(username, password);

        String responseJson = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(responseJson);
        return node.get("token").asText();
    }
}
