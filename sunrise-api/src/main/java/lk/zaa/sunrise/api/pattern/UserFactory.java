package lk.zaa.sunrise.api.pattern;

import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.Receptionist;
import lk.zaa.sunrise.api.entity.User;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * FACTORY PATTERN.
 *
 * The concrete User subclass to instantiate (Administrator vs Receptionist)
 * is only known at runtime, from the "role" field an Administrator picks on
 * the "Manage Staff Accounts" screen. Centralising that decision here means
 * UserService (and any future caller) never needs an if/else on Role — new
 * roles only require a new case in this one class.
 */
@Component
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User create(NewUserRequest request) {
        String hash = passwordEncoder.encode(request.getPassword());
        Role role = request.getRole();

        return switch (role) {
            case ADMINISTRATOR -> new Administrator(request.getUsername(), hash, request.getFullName());
            case RECEPTIONIST -> new Receptionist(request.getUsername(), hash, request.getFullName());
        };
    }
}
