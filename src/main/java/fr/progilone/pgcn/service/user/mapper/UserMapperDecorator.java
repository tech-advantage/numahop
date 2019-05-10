package fr.progilone.pgcn.service.user.mapper;

import fr.progilone.pgcn.domain.dto.user.UserDTO;
import fr.progilone.pgcn.domain.user.User;

public abstract class UserMapperDecorator implements UserMapper {

    private final UserMapper delegate;

    public UserMapperDecorator(UserMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public UserDTO userToUserDTO(User user) {
        UserDTO dto = delegate.userToUserDTO(user);
        return dto;
    }
}
