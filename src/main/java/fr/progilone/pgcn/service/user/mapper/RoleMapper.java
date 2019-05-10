package fr.progilone.pgcn.service.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.user.RoleDTO;
import fr.progilone.pgcn.domain.user.Role;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDTO roleToRoleDTO(Role role);
}
