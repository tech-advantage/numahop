package fr.progilone.pgcn.service.train.mapper;

import fr.progilone.pgcn.domain.dto.train.TrainDTO;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.service.document.mapper.PhysicalDocumentMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {SimpleProjectMapper.class,
                PhysicalDocumentMapper.class})
public interface TrainMapper {

    TrainMapper INSTANCE = Mappers.getMapper(TrainMapper.class);

    TrainDTO trainToTrainDTO(Train train);
}
