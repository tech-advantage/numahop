package fr.progilone.pgcn.service.user.mapper;

import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.dto.user.UserDTO;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.service.library.mapper.SimpleLibraryMapper;
import fr.progilone.pgcn.service.workflow.mapper.SimpleWorkflowMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AddressMapper.class,
                RoleMapper.class,
                SimpleLibraryMapper.class,
                SimpleWorkflowMapper.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO userToUserDTO(User user);

    SimpleUserDTO userToSimpleUserDTO(User user);
}
