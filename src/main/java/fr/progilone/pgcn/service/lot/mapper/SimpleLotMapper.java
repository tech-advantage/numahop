package fr.progilone.pgcn.service.lot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;

@Mapper(uses = {SimpleProjectMapper.class, OcrLanguageMapper.class})
public interface SimpleLotMapper {

    SimpleLotMapper INSTANCE = Mappers.getMapper(SimpleLotMapper.class);

    SimpleLotDTO lotToSimpleLotDTO(Lot lot);
}
