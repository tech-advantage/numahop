package fr.progilone.pgcn.service.delivery;

import fr.progilone.pgcn.domain.delivery.DeliverySlipConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.library.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service de gestion du paramétrage des bordereaux.
 */
@Service
public class DeliveryConfigurationService {

    private final LibraryService libraryService;

    @Autowired
    public DeliveryConfigurationService(final LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @Transactional
    public Optional<DeliverySlipConfiguration> getOneByLibrary(final String identifier) {
        final Library library = libraryService.findOne(identifier);
        if(library == null) {
            return Optional.empty();
        }
        DeliverySlipConfiguration config = library.getDeliverySlipConfiguration();
        if(config == null) {
            config = new DeliverySlipConfiguration();
            config.setPgcnId(true);
            config.setDate(true);
            config.setTrain(true);
            config.setTitle(true);
            config.setLot(true);
            config.setNbPages(true);
            config.setRadical(true);
            library.setDeliverySlipConfiguration(config);
            config.setLibrary(library);
            libraryService.save(library);
            final Library savedLibrary = libraryService.findOneWithDependencies(identifier);
            config = savedLibrary.getDeliverySlipConfiguration();
        }
        return Optional.of(config);
    }

    @Transactional
    public DeliverySlipConfiguration update(final DeliverySlipConfiguration deliverySlipConfiguration) {
        final Library library = libraryService.findOne(deliverySlipConfiguration.getLibrary().getIdentifier());
        library.setDeliverySlipConfiguration(deliverySlipConfiguration);
        libraryService.save(library);
        return deliverySlipConfiguration;
    }
}
