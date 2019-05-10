package fr.progilone.pgcn.service.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.user.AddressDTO;
import fr.progilone.pgcn.domain.user.Address;

@Mapper
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressDTO addressToAddressDTO(Address address);
}
