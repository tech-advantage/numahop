package fr.progilone.pgcn.service.exchange.cines;

import fr.progilone.pgcn.domain.exchange.cines.CinesLanguageCode;
import fr.progilone.pgcn.repository.exchange.cines.CinesLanguageCodeRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CinesLanguageCodeService {

    private final CinesLanguageCodeRepository repository;

    @Autowired
    public CinesLanguageCodeService(final CinesLanguageCodeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CinesLanguageCode> findAllActive(final boolean active) {
        return repository.findByOrderByIdentifier();
    }

    @Transactional
    public List<CinesLanguageCode> update(final List<CinesLanguageCode> cinesCodes) {

        final List<CinesLanguageCode> current = repository.findByOrderByIdentifier();
        // suppression possible
        current.stream().filter(cod -> !cinesCodes.contains(cod)).forEach(cod -> {
            repository.deleteById(cod.getIdentifier());
        });

        return repository.saveAll(cinesCodes);
    }

    @Transactional(readOnly = true)
    public String getCinesLanguageByIdentifier(final String identifier) {
        final CinesLanguageCode langCode = repository.findOneByIdentifier(identifier);
        return (langCode == null ? identifier
                                 : langCode.getLabel());
    }

}
