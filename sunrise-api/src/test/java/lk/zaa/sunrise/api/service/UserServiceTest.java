package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.Receptionist;
import lk.zaa.sunrise.api.exception.DuplicateUsernameException;
import lk.zaa.sunrise.api.mapper.UserMapper;
import lk.zaa.sunrise.api.pattern.UserFactory;
import lk.zaa.sunrise.api.repository.UserRepository;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.dto.UserDto;
import lk.zaa.sunrise.common.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserFactory userFactory;

    private UserService service(UserRepository repo, UserFactory factory) {
        return new UserService(repo, factory, new UserMapper());
    }

    @Test
    @DisplayName("Creating a user with a free username succeeds")
    void createsUserWhenUsernameIsFree() {
        UserService userService = service(userRepository, userFactory);
        NewUserRequest request = new NewUserRequest();
        request.setUsername("newstaff");
        request.setPassword("Pass@123");
        request.setFullName("New Staff Member");
        request.setRole(Role.RECEPTIONIST);

        when(userRepository.existsByUsername("newstaff")).thenReturn(false);
        Receptionist created = new Receptionist("newstaff", "hashed", "New Staff Member");
        when(userFactory.create(request)).thenReturn(created);

        UserDto dto = userService.createUser(request);

        assertThat(dto.getUsername()).isEqualTo("newstaff");
        assertThat(dto.getRole()).isEqualTo(Role.RECEPTIONIST);
        verify(userRepository).save(created);
    }

    @Test
    @DisplayName("Creating a user with a taken username throws DuplicateUsernameException and never reaches the factory")
    void rejectsDuplicateUsername() {
        UserService userService = service(userRepository, userFactory);
        NewUserRequest request = new NewUserRequest();
        request.setUsername("reception");
        request.setPassword("x");
        request.setFullName("x");
        request.setRole(Role.RECEPTIONIST);

        when(userRepository.existsByUsername("reception")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(DuplicateUsernameException.class)
                .hasMessageContaining("reception");

        verifyNoInteractions(userFactory);
        verify(userRepository, never()).save(any());
    }
}
