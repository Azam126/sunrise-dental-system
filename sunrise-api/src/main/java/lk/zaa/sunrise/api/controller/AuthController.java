package lk.zaa.sunrise.api.controller;

import jakarta.validation.Valid;
import lk.zaa.sunrise.api.service.AuthService;
import lk.zaa.sunrise.common.dto.LoginRequest;
import lk.zaa.sunrise.common.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
