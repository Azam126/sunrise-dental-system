package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.Receptionist;
import lk.zaa.sunrise.api.entity.User;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserFactoryTest {

    private PasswordEncoder passwordEncoder;
    private UserFactory factory;

    @BeforeEach
    void setUp() {
        passwordEncoder = mock(PasswordEncoder.class);
        factory = new UserFactory(passwordEncoder);
    }

    @Test
    @DisplayName("Role.ADMINISTRATOR produces an Administrator instance")
    void createsAdministrator() {
        when(passwordEncoder.encode("Admin@123")).thenReturn("hashed-admin-pw");
        NewUserRequest request = requestFor(Role.ADMINISTRATOR, "admin2", "Admin@123", "Second Admin");

        User user = factory.create(request);

        assertThat(user).isInstanceOf(Administrator.class);
        assertThat(user.getUsername()).isEqualTo("admin2");
        assertThat(user.getPasswordHash()).isEqualTo("hashed-admin-pw");
        assertThat(user.getFullName()).isEqualTo("Second Admin");
    }

    @Test
    @DisplayName("Role.RECEPTIONIST produces a Receptionist instance")
    void createsReceptionist() {
        when(passwordEncoder.encode("Front@123")).thenReturn("hashed-front-pw");
        NewUserRequest request = requestFor(Role.RECEPTIONIST, "reception2", "Front@123", "Second Receptionist");

        User user = factory.create(request);

        assertThat(user).isInstanceOf(Receptionist.class);
        assertThat(user.getUsername()).isEqualTo("reception2");
    }

    @Test
    @DisplayName("The password is never stored in plain text — it always goes through the encoder")
    void neverStoresPlainTextPassword() {
        when(passwordEncoder.encode("PlainText1")).thenReturn("$2a$10$totallyHashedValue");
        NewUserRequest request = requestFor(Role.RECEPTIONIST, "u", "PlainText1", "Name");

        User user = factory.create(request);

        assertThat(user.getPasswordHash()).isNotEqualTo("PlainText1");
        assertThat(user.getPasswordHash()).startsWith("$2a$");
    }

    private NewUserRequest requestFor(Role role, String username, String password, String fullName) {
        NewUserRequest request = new NewUserRequest();
        request.setRole(role);
        request.setUsername(username);
        request.setPassword(password);
        request.setFullName(fullName);
        return request;
    }
}
