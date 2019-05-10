package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.repository.administration.InternetArchiveCollectionRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des {@link InternetArchiveConfiguration}
 *
 * @author jbrunet
 * Créé le 19 avr. 2017
 */
@Service
public class InternetArchiveCollectionService {

    private final InternetArchiveCollectionRepository iaCollectionRepository;

    @Autowired
    public InternetArchiveCollectionService(final InternetArchiveCollectionRepository iaCollectionRepository) {
        this.iaCollectionRepository = iaCollectionRepository;
    }

    @Transactional(readOnly = true)
    public InternetArchiveCollection findOne(String identifier) {
        return iaCollectionRepository.findOne(identifier);
    }

    @Transactional(readOnly = true)
    public List<InternetArchiveCollection> findAll(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return iaCollectionRepository.findAllWithDependencies();
        } else {
            return iaCollectionRepository.findAllForLibraries(identifiers);
        }
    }
}
