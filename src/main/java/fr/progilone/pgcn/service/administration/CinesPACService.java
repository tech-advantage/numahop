package fr.progilone.pgcn.service.administration;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.repository.administration.CinesPACRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CinesPACService {

    private final CinesPACRepository cinesPACRepository;

    @Autowired
    public CinesPACService(final CinesPACRepository cinesPACRepository) {
        this.cinesPACRepository = cinesPACRepository;
    }

    @Transactional
    public CinesPAC findOne(String identifier) {
        return cinesPACRepository.findOne(identifier);
    }

    @Transactional
    public List<CinesPAC> findAllForLibrary(String identifier) {
        return cinesPACRepository.findAllForLibrary(identifier);
    }
}
