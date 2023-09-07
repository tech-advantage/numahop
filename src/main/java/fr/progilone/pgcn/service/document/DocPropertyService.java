package fr.progilone.pgcn.service.document;

import static fr.progilone.pgcn.domain.document.BibliographicRecord.*;

import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.repository.document.DocPropertyRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocPropertyService {

    private final DocPropertyRepository docPropertyRepository;

    @Autowired
    public DocPropertyService(final DocPropertyRepository docPropertyRepository) {
        this.docPropertyRepository = docPropertyRepository;
    }

    @Transactional(readOnly = true)
    public List<DocProperty> findAll() {
        return docPropertyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public DocProperty findOne(final String identifier) {
        return docPropertyRepository.findById(identifier).orElse(null);
    }

    /**
     * Sauvegarde et gère le rank
     *
     * @param property
     * @return
     */
    @Transactional
    public DocProperty save(final DocProperty property) {
        handleRank(property);

        return docPropertyRepository.save(property);
    }

    /**
     * Nombre de propriétés correspondant au type passé en paramètres
     *
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public Integer countByType(final DocPropertyType type) {
        return docPropertyRepository.countByType(type);
    }

    /**
     * Gére le rang si besoin
     *
     * @param property
     */
    private void handleRank(final DocProperty property) {
        if (property.getRank() == null) {
            final PropertyOrder propertyOrder = property.getRecord().getPropertyOrder();
            Integer currentRank = null;

            if (propertyOrder == PropertyOrder.BY_PROPERTY_TYPE) {
                currentRank = docPropertyRepository.findCurrentRankForProperty(property.getRecord(), property.getType());

            } else if (propertyOrder == PropertyOrder.BY_CREATION) {
                currentRank = docPropertyRepository.findCurrentRankForProperty(property.getRecord());
            }

            final Integer nextRank = currentRank != null ? currentRank + 1
                                                         : 1;
            property.setRank(nextRank);
        }
    }
}
