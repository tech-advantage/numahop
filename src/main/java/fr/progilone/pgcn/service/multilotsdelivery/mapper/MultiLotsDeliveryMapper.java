package fr.progilone.pgcn.service.multilotsdelivery.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotForDeliveryDTO;
import fr.progilone.pgcn.domain.dto.multilotsdelivery.MultiLotsDeliveryDTO;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.service.delivery.mapper.DeliveryMapper;
import fr.progilone.pgcn.service.lot.mapper.LotMapper;

@Mapper(uses = {DeliveryMapper.class})
public abstract class MultiLotsDeliveryMapper {

    public static final MultiLotsDeliveryMapper INSTANCE = Mappers.getMapper(MultiLotsDeliveryMapper.class);
    
    @Mappings({@Mapping(target = "lots", ignore = true)})
    public abstract MultiLotsDeliveryDTO objToDto(MultiLotsDelivery obj);
    
    
    
    @AfterMapping
    protected void loadLots(final MultiLotsDelivery obj, @MappingTarget final MultiLotsDeliveryDTO dto) {
        
        // lots des livraisons
        final List<SimpleLotForDeliveryDTO> lots = obj.getDeliveries().stream()
            .map(d->d.getLot())
            .map(LotMapper.INSTANCE::lotToSimpleLotForDeliveryDTO)
            .collect(Collectors.toList());
       
       dto.getLots().addAll(lots);
       
       // statut des livraisons
       final List<Delivery.DeliveryStatus> statuses  = obj.getDeliveries().stream()
             .map(d->d.getStatus())
             .collect(Collectors.toList());
       
       if (statuses.contains(Delivery.DeliveryStatus.DELIVERING)) {
           dto.setStatus(MultiLotsDelivery.DeliveryStatus.DELIVERING.name());
       } else if (statuses.contains(Delivery.DeliveryStatus.TO_BE_CONTROLLED)) {
           dto.setStatus(MultiLotsDelivery.DeliveryStatus.TO_BE_CONTROLLED.name());
       } else if (statuses.contains(Delivery.DeliveryStatus.SAVED) || statuses.isEmpty()) {
           dto.setStatus(MultiLotsDelivery.DeliveryStatus.SAVED.name());
       } else {
           dto.setStatus(MultiLotsDelivery.DeliveryStatus.TREATED.name());
       }
       
        
    }
    
    
}
