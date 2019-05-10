package fr.progilone.pgcn.service.administration.omeka;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.repository.administration.omeka.OmekaListRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des {@link InternetArchiveConfiguration}
 *
 * @author Progilone
 * Créé le 29 aout 2018
 */
@Service
public class OmekaListService {

    private final OmekaListRepository omekaListRepository;

    @Autowired
    public OmekaListService(final OmekaListRepository omekaListRepository) {
        this.omekaListRepository = omekaListRepository;
    }

    @Transactional(readOnly = true)
    public OmekaList findOne(final String identifier) {
        return omekaListRepository.findOne(identifier);
    }

    @Transactional(readOnly = true)
    public List<OmekaList> findAllByLibraryAndType(final String identifier, final OmekaList.ListType type) {
        return omekaListRepository.findAllByLibraryAndType(identifier, type);
    }

    @Transactional
    public void delete(final List<OmekaList> ols) {
        omekaListRepository.delete(ols);
    }
}
