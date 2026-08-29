package lk.zaa.sunrise.api.controller;

import jakarta.validation.Valid;
import lk.zaa.sunrise.api.service.UserService;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.dto.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Restricted to ROLE_ADMINISTRATOR by SecurityConfig ("/api/admin/**").
 * Backs the "Manage Staff Accounts" use case (Administrator only).
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody NewUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> list() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> remove(@PathVariable Long userId) {
        userService.removeUser(userId);
        return ResponseEntity.noContent().build();
    }
}
