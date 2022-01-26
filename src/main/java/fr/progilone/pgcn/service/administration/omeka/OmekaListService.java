package fr.progilone.pgcn.service.administration.omeka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.InternetArchiveConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.repository.administration.omeka.OmekaListRepository;
import fr.progilone.pgcn.security.SecurityUtils;

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
    public List<OmekaList> findAllByConfOmekaAndType(final String identifier, final OmekaList.ListType type) {
        return omekaListRepository.findAllByConfOmekaAndType(identifier, type);
    }

    @Transactional(readOnly = true)
    public List<OmekaList> findAllByLibraryAndType(final String identifier, final OmekaList.ListType type) {
        return omekaListRepository.findAllByLibraryAndType(identifier, type);
    }

    @Transactional
    public void delete(final List<OmekaList> ols) {
        omekaListRepository.delete(ols);
    }

    @Transactional
    public OmekaList findByName(final String name) {
        final String libraryId = SecurityUtils.getCurrentUserLibraryId();
        return omekaListRepository.findByNameAndLibrary(name, libraryId);
    }
}
