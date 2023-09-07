package fr.progilone.pgcn.service.multilotsdelivery;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.multilotsdelivery.MultiLotsDeliveryRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MultiLotsDeliveryService {

    private final MultiLotsDeliveryRepository multiRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public MultiLotsDeliveryService(final MultiLotsDeliveryRepository multiRepository, final DeliveryRepository deliveryRepository) {
        this.multiRepository = multiRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Transactional
    public MultiLotsDelivery save(final MultiLotsDelivery multi) throws PgcnValidationException, PgcnBusinessException {
        if (multi.getIdentifier() == null) {
            multi.setStatus(Delivery.DeliveryStatus.SAVED);
        }
        deliveryRepository.saveAll(multi.getDeliveries());
        return multiRepository.save(multi);
    }

    @Transactional(readOnly = true)
    public MultiLotsDelivery getOne(final String id) {
        return multiRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public MultiLotsDelivery findOneByIdWithDeliveries(final String id) {
        return multiRepository.findOneByIdWithDeliveries(id);
    }

    @Transactional
    public void delete(final String identifier) {
        final MultiLotsDelivery multi = multiRepository.findOneByIdWithDeliveries(identifier);
        multiRepository.delete(multi);
        // esDeliveryService.deleteAsync(multi);
    }

    /**
     * Lance une recherche paginée
     *
     * @param search
     * @param projects
     * @param lots
     * @param status
     * @param dateFrom
     * @param dateTo
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<MultiLotsDelivery> search(final String search,
                                          final List<String> libraries,
                                          final List<String> projects,
                                          final List<String> lots,
                                          final List<String> providers,
                                          final List<Delivery.DeliveryStatus> status,
                                          final LocalDate dateFrom,
                                          final LocalDate dateTo,
                                          final Integer page,
                                          final Integer size) {

        final Pageable pageRequest = PageRequest.of(page, size);
        return multiRepository.search(search, libraries, projects, lots, null, providers, status, dateFrom, dateTo, null, pageRequest);
    }

    @Transactional
    public List<Lot> findLotsByTrainIdentifier(final String trainId) {
        return multiRepository.findLotsByTrainIdentifier(trainId);
    }

    @Transactional
    public void closeMultiLotDelivery(final Delivery delivery) {
        final MultiLotsDelivery multiLotsDelivery = delivery.getMultiLotsDelivery();

        if (multiLotsDelivery != null) {
            final List<Delivery> deliveries = multiLotsDelivery.getDeliveries();
            if (deliveries.stream().noneMatch(del -> del.getStatus() != Delivery.DeliveryStatus.CLOSED)) {
                multiLotsDelivery.setStatus(Delivery.DeliveryStatus.CLOSED);
                save(multiLotsDelivery);
            }
        }
    }
}
