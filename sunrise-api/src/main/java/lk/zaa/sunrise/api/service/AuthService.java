package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.User;
import lk.zaa.sunrise.api.exception.InvalidCredentialsException;
import lk.zaa.sunrise.api.repository.UserRepository;
import lk.zaa.sunrise.api.security.JwtUtil;
import lk.zaa.sunrise.common.dto.LoginRequest;
import lk.zaa.sunrise.common.dto.LoginResponse;
import lk.zaa.sunrise.common.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** Implements the Login sequence diagram from Task A, Figure 3. */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Role role = (user instanceof Administrator) ? Role.ADMINISTRATOR : Role.RECEPTIONIST;
        String token = jwtUtil.generateToken(user.getUsername(), role.name());

        return new LoginResponse(token, user.getFullName(), role);
    }
}
