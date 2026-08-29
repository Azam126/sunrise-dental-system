package lk.zaa.sunrise.api.service;

import lk.zaa.sunrise.api.entity.User;
import lk.zaa.sunrise.api.exception.DuplicateUsernameException;
import lk.zaa.sunrise.api.mapper.UserMapper;
import lk.zaa.sunrise.api.pattern.UserFactory;
import lk.zaa.sunrise.api.repository.UserRepository;
import lk.zaa.sunrise.common.dto.NewUserRequest;
import lk.zaa.sunrise.common.dto.UserDto;
import org.springframework.stereotype.Service;
import java.util.List;

/** Backs the Administrator-only "Manage Staff Accounts" screen. */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserFactory userFactory, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userFactory = userFactory;
        this.userMapper = userMapper;
    }

    public UserDto createUser(NewUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException("Username already taken: " + request.getUsername());
        }
        User user = userFactory.create(request);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    public void removeUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
