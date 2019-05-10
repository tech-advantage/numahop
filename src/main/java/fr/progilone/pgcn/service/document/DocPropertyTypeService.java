package fr.progilone.pgcn.service.document;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.document.DocPropertyTypeRepository;
import fr.progilone.pgcn.service.exchange.MappingService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class DocPropertyTypeService {

    private final DocPropertyTypeRepository docPropertyTypeRepository;
    private final DocPropertyService docPropertyService;
    private final MappingService mappingService;

    @Autowired
    public DocPropertyTypeService(final DocPropertyTypeRepository docPropertyTypeRepository,
                                  final DocPropertyService docPropertyService,
                                  final MappingService mappingService) {
        this.docPropertyTypeRepository = docPropertyTypeRepository;
        this.docPropertyService = docPropertyService;
        this.mappingService = mappingService;
    }

    @Transactional(readOnly = true)
    public List<DocPropertyType> findAll() {
        return docPropertyTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DocPropertyType> findAllBySuperType(final DocPropertyType.DocPropertySuperType superType) {
        return docPropertyTypeRepository.findAllBySuperType(superType);
    }

    @Transactional(readOnly = true)
    public List<DocPropertyType> findAllByIdentifierIn(final List<String> fields) {
        return CollectionUtils.isNotEmpty(fields) ? docPropertyTypeRepository.findAll(fields) : Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public DocPropertyType findOne(final String identifier) {
        return docPropertyTypeRepository.findOne(identifier);
    }

    @Transactional
    public DocPropertyType save(final DocPropertyType propertyType) throws PgcnValidationException {
        validateSave(propertyType);
        handleRank(propertyType);
        return docPropertyTypeRepository.save(propertyType);
    }

    @Transactional
    public void delete(final DocPropertyType type) throws PgcnValidationException {
        validateDeletion(type);
        docPropertyTypeRepository.delete(type);
    }

    /**
     * Validation de la sauvegarde
     *
     * @param type
     * @throws PgcnValidationException
     */
    private void validateSave(final DocPropertyType type) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le libellé est renseigné
        if (StringUtils.isBlank(type.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_PROP_TYPE_LABEL_MANDATORY).setField("label").build());
        }

        if (!errors.isEmpty()) {
            type.setErrors(errors);
            throw new PgcnValidationException(type, errors);
        }
    }

    /**
     * Validation de la suppression
     *
     * @param type
     * @throws PgcnValidationException
     */
    private void validateDeletion(final DocPropertyType type) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le type de propriété est utilisé sur une propriété
        Integer count = docPropertyService.countByType(type);
        if (count != null && count > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_PROP_TYPE_DEL_USED_PROP).build());
        }

        // Le type de propriété est utilisé sur une règle de mapping
        count = mappingService.countByPropertyType(type);
        if (count != null && count > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_PROP_TYPE_DEL_USED_MAPPING).build());
        }

        if (!errors.isEmpty()) {
            type.setErrors(errors);
            throw new PgcnValidationException(type, errors);
        }
    }

    /**
     * Définit le rang s'il n'est pas renseigné
     *
     * @param propertyType
     */
    private void handleRank(DocPropertyType propertyType) {
        if (propertyType.getRank() == null) {
            final Integer currentRank = docPropertyTypeRepository.findCurrentRankForPropertyType(propertyType.getSuperType());
            Integer nextRank = currentRank != null ? currentRank + 1 : 1;
            propertyType.setRank(nextRank);
        }
    }
}
