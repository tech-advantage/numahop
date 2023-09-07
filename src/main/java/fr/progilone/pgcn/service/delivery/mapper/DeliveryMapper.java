package fr.progilone.pgcn.service.delivery.mapper;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.delivery.DeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryForViewerDTO;
import fr.progilone.pgcn.domain.dto.delivery.SimpleDeliveryLotDTO;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckResultMapper;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LotMapper.class,
                AutomaticCheckResultMapper.class})
public interface DeliveryMapper {

    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    @Mappings({@Mapping(target = "multiLotsDelId", source = "multiLotsDelivery.identifier"),
               @Mapping(target = "multiLotsDelLabel", source = "multiLotsDelivery.label")})
    DeliveryDTO deliveryToDeliveryDTO(Delivery delivery);

    SimpleDeliveryForViewerDTO delivToDtoForViewer(Delivery delivery);

    SimpleDeliveryDTO deliveryToSimpleDeliveryDTO(Delivery delivery);

    SimpleDeliveryLotDTO deliveryToSimpleDeliveryLotDTO(Delivery delivery);
}
