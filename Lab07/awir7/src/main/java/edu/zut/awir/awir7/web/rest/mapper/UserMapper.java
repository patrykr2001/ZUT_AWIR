package edu.zut.awir.awir7.web.rest.mapper;

import edu.zut.awir.awir7.model.User;
import edu.zut.awir.awir7.web.rest.dto.UserDto;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        return new User(dto.getId(), dto.getName(), dto.getEmail());
    }
}

