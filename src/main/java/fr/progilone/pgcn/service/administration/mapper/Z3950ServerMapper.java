package fr.progilone.pgcn.service.administration.mapper;

import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Created by Sébastien on 21/12/2016.
 */
@Mapper
public interface Z3950ServerMapper {

    Z3950ServerMapper INSTANCE = Mappers.getMapper(Z3950ServerMapper.class);

    Z3950ServerDTO z3950ServerToDto(Z3950Server z3950Server);

    List<Z3950ServerDTO> z3950ServerToDtos(List<Z3950Server> z3950Servers);
}
