package fr.progilone.pgcn.service.train.mapper;

import fr.progilone.pgcn.domain.dto.train.SimpleTrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SimpleTrainMapper {

    SimpleTrainMapper INSTANCE = Mappers.getMapper(SimpleTrainMapper.class);

    SimpleTrainDTO trainToSimpleTrainDTO(Train train);
}
