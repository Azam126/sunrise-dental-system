package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.Receptionist;
import lk.zaa.sunrise.api.exception.InvalidCredentialsException;
import lk.zaa.sunrise.api.repository.UserRepository;
import lk.zaa.sunrise.api.security.JwtUtil;
import lk.zaa.sunrise.common.dto.LoginRequest;
import lk.zaa.sunrise.common.dto.LoginResponse;
import lk.zaa.sunrise.common.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @InjectMocks private AuthService authService;

    private Receptionist receptionist;

    @BeforeEach
    void setUp() {
        receptionist = new Receptionist("reception", "hashed-password", "Kasun Silva");
    }

    @Test
    @DisplayName("Correct username and password returns a token and the user's role")
    void loginSucceedsWithCorrectCredentials() {
        when(userRepository.findByUsername("reception")).thenReturn(Optional.of(receptionist));
        when(passwordEncoder.matches("Front@123", "hashed-password")).thenReturn(true);
        when(jwtUtil.generateToken("reception", "RECEPTIONIST")).thenReturn("fake.jwt.token");

        LoginResponse response = authService.login(new LoginRequest("reception", "Front@123"));

        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getFullName()).isEqualTo("Kasun Silva");
        assertThat(response.getRole()).isEqualTo(Role.RECEPTIONIST);
    }

    @Test
    @DisplayName("Correct credentials for an Administrator report Role.ADMINISTRATOR")
    void loginReportsAdministratorRoleCorrectly() {
        Administrator admin = new Administrator("admin", "hashed-admin-pw", "Nadeesha Perera");
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("Admin@123", "hashed-admin-pw")).thenReturn(true);
        when(jwtUtil.generateToken("admin", "ADMINISTRATOR")).thenReturn("fake.admin.token");

        LoginResponse response = authService.login(new LoginRequest("admin", "Admin@123"));

        assertThat(response.getRole()).isEqualTo(Role.ADMINISTRATOR);
    }

    @Test
    @DisplayName("Wrong password throws InvalidCredentialsException without revealing which field was wrong")
    void loginFailsWithWrongPassword() {
        when(userRepository.findByUsername("reception")).thenReturn(Optional.of(receptionist));
        when(passwordEncoder.matches("wrongPassword", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("reception", "wrongPassword")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }

    @Test
    @DisplayName("Unknown username throws the same InvalidCredentialsException as a wrong password")
    void loginFailsWithUnknownUsername() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("ghost", "anyPassword")))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid username or password");
    }
}
