package fr.progilone.pgcn.service.multilotsdelivery.mapper;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.multilotsdelivery.MultiLotsDeliveryDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery.DeliveryMethod;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery.DeliveryPayment;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.lot.LotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UIMultiLotsDeliveryMapper {

    private final LotService lotService;
    private final DeliveryService deliveryService;

    @Autowired
    public UIMultiLotsDeliveryMapper(final LotService lotService, final DeliveryService deliveryService) {
        this.lotService = lotService;
        this.deliveryService = deliveryService;
    }

    public void mapInto(final MultiLotsDeliveryDTO dto, final MultiLotsDelivery multi) {

        if (StringUtils.isNotBlank(dto.getIdentifier())) {
            multi.setIdentifier(dto.getIdentifier());
        }
        multi.setLabel(dto.getLabel());
        multi.setDescription(dto.getDescription());
        multi.setPayment(DeliveryPayment.valueOf(dto.getPayment()));
        multi.setMethod(DeliveryMethod.valueOf(dto.getMethod()));
        if (StringUtils.isNotBlank(dto.getStatus())) {
            multi.setStatus(Delivery.DeliveryStatus.valueOf(dto.getStatus()));
        }
        multi.setReceptionDate(dto.getReceptionDate());
        multi.setControlNotes(dto.getControlNotes());
        multi.setDigitizingNotes(dto.getDigitizingNotes());
        multi.setFolderPath(dto.getFolderPath());
        multi.setSelectedByTrain(dto.isSelectedByTrain());
        multi.setTrainId(dto.getTrainId());

        multi.getDeliveries().stream().forEach(deliv -> {
            deliveryService.delete(deliv.getIdentifier());
        });
        multi.getDeliveries().clear();

        dto.getLots().stream().filter(lo -> lo.getIdentifier() != null).forEach(lo -> {

            final Lot lot = lotService.getOne(lo.getIdentifier());
            final Delivery delivery = buildNewDelivery(dto, lot);
            delivery.setMultiLotsDelivery(multi);
            multi.getDeliveries().add(delivery);
        });
    }

    /**
     *
     * @param dto
     * @param lot
     * @return
     */
    private Delivery buildNewDelivery(final MultiLotsDeliveryDTO dto, final Lot lot) {

        final Delivery delivery = new Delivery();
        delivery.setLot(lot);
        delivery.setLabel(dto.getLabel().concat(" - ").concat(lot.getLabel()));

        delivery.setDescription(dto.getDescription());
        delivery.setPayment(Delivery.DeliveryPayment.valueOf(dto.getPayment()));
        delivery.setMethod(Delivery.DeliveryMethod.valueOf(dto.getMethod()));
        if (StringUtils.isNotBlank(dto.getStatus())) {
            delivery.setStatus(Delivery.DeliveryStatus.valueOf(dto.getStatus()));
        } else {
            delivery.setStatus(Delivery.DeliveryStatus.SAVED);
        }
        delivery.setFolderPath(dto.getFolderPath());
        delivery.setReceptionDate(dto.getReceptionDate());
        delivery.setDigitizingNotes(dto.getDigitizingNotes());
        delivery.setControlNotes(dto.getControlNotes());
        return delivery;
    }

}
