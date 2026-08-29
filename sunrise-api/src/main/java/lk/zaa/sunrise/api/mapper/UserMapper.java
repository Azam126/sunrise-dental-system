package lk.zaa.sunrise.api.mapper;

import lk.zaa.sunrise.api.entity.Administrator;
import lk.zaa.sunrise.api.entity.User;
import lk.zaa.sunrise.common.dto.UserDto;
import lk.zaa.sunrise.common.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        Role role = (user instanceof Administrator) ? Role.ADMINISTRATOR : Role.RECEPTIONIST;
        return new UserDto(user.getUserId(), user.getUsername(), user.getFullName(), role);
    }
}
