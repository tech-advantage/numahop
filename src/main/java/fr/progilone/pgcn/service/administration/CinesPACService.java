package fr.progilone.pgcn.service.administration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.repository.administration.CinesPACRepository;
import fr.progilone.pgcn.security.SecurityUtils;

@Service
public class CinesPACService {

    private final CinesPACRepository cinesPACRepository;

    @Autowired
    public CinesPACService(final CinesPACRepository cinesPACRepository) {
        this.cinesPACRepository = cinesPACRepository;
    }

    @Transactional
    public CinesPAC findOne(final String identifier) {
        return cinesPACRepository.findOne(identifier);
    }

    @Transactional
    public List<CinesPAC> findAllForLibrary(final String identifier) {
        return cinesPACRepository.findAllForLibrary(identifier);
    }
    
    @Transactional
    public List<CinesPAC> findAllForConfiguration(String configurationId) {
        return cinesPACRepository.findAllByConfPac(configurationId);
    }
    
    @Transactional
    public CinesPAC findByName(final String name) {
        final String libraryId = SecurityUtils.getCurrentUserLibraryId();
        return cinesPACRepository.findByNameAndLibrary(name, libraryId);
    }
}
