package fr.progilone.pgcn.service.delivery.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.delivery.ManualDeliveryDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.lot.LotService;

@Service
public class UIDeliveryMapper {

	public UIDeliveryMapper() {
	}

	@Autowired
    LotService lotService;

    public void mapInto(final ManualDeliveryDTO deliveryDTO, final Delivery delivery) {
        delivery.setIdentifier(deliveryDTO.getIdentifier());

        final SimpleLotDTO lotDTO = deliveryDTO.getLot();
        if (lotDTO != null && lotDTO.getIdentifier() != null) {
            //Récupère bibliotheque depuis repository
            final Lot lot = lotService.findByIdentifier(lotDTO.getIdentifier());
            delivery.setLot(lot);
        }
        delivery.setLabel(deliveryDTO.getLabel());
        delivery.setDescription(deliveryDTO.getDescription());
        if(deliveryDTO.getPayment() != null) {
            delivery.setPayment(Delivery.DeliveryPayment.valueOf(deliveryDTO.getPayment()));
        }
        if(deliveryDTO.getMethod() != null) {
            delivery.setMethod(Delivery.DeliveryMethod.valueOf(deliveryDTO.getMethod()));
        }
        delivery.setReceptionDate(deliveryDTO.getReceptionDate());
        delivery.setFolderPath(deliveryDTO.getFolderPath());
        delivery.setImgFormat(deliveryDTO.getImgFormat());
        delivery.setDigitizingNotes(deliveryDTO.getDigitizingNotes());
        delivery.setControlNotes(deliveryDTO.getControlNotes());
    }
}
