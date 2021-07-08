package fr.progilone.pgcn.service.lot.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.lot.LotDTO;
import fr.progilone.pgcn.domain.dto.lot.LotListDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.administration.mapper.CinesPACMapper;
import fr.progilone.pgcn.service.administration.mapper.InternetArchiveCollectionMapper;
import fr.progilone.pgcn.service.administration.mapper.OmekaListMapper;
import fr.progilone.pgcn.service.administration.mapper.SimpleViewsFormatConfigurationMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.SimpleCheckConfigurationMapper;
import fr.progilone.pgcn.service.document.mapper.SimpleDocUnitMapper;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.SimpleFTPConfigurationMapper;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.project.mapper.ProjectMapper;
import fr.progilone.pgcn.service.project.mapper.SimpleProjectMapper;
import fr.progilone.pgcn.service.user.mapper.AddressMapper;
import fr.progilone.pgcn.service.user.mapper.UserMapper;
import fr.progilone.pgcn.service.workflow.mapper.SimpleWorkflowMapper;

@Mapper(uses = {AddressMapper.class,
                SimpleDocUnitMapper.class,
                SimpleProjectMapper.class,
                ProjectMapper.class,
                SimpleFTPConfigurationMapper.class,
                SimpleCheckConfigurationMapper.class,
                SimpleViewsFormatConfigurationMapper.class,
                SimpleWorkflowMapper.class,
                UserMapper.class,
                InternetArchiveCollectionMapper.class,
                CinesPACMapper.class,
                OmekaListMapper.class,
                OcrLanguageMapper.class})
public interface LotMapper {

    LotMapper INSTANCE = Mappers.getMapper(LotMapper.class);

    LotDTO lotToLotDTO(Lot lot);

    @Mappings({@Mapping(target = "projectIdentifier", source = "projectId")})
    LotListDTO lotToLotListDTO(Lot lot);

    @Mappings({@Mapping(target = "providerLogin", source = "provider.login")})
    SimpleLotDTO lotToSimpleLotDTO(Lot lot);

    SimpleLotForDeliveryDTO lotToSimpleLotForDeliveryDTO(Lot lot);
    
}
