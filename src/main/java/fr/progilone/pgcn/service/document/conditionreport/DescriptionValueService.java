package fr.progilone.pgcn.service.document.conditionreport;

import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionRepository;
import fr.progilone.pgcn.repository.document.conditionreport.DescriptionValueRepository;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DescriptionValueService {

    private final DescriptionRepository descriptionRepository;
    private final DescriptionValueRepository descriptionValueRepository;

    @Autowired
    public DescriptionValueService(final DescriptionRepository descriptionRepository, final DescriptionValueRepository descriptionValueRepository) {
        this.descriptionRepository = descriptionRepository;
        this.descriptionValueRepository = descriptionValueRepository;
    }

    @Transactional(readOnly = true)
    public List<DescriptionValue> findAll() {
        return descriptionValueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<DescriptionValue> findByProperty(final DescriptionProperty property) {
        return descriptionValueRepository.findByProperty(property);
    }

    @Transactional(readOnly = true)
    public List<DescriptionValue> findByPropertyIdentifier(final String propertyId) {
        return descriptionValueRepository.findByPropertyIdentifier(propertyId);
    }

    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        descriptionValueRepository.findById(identifier).ifPresent(value -> {
            // Validation de la suppression
            validateDeletion(value);
            // Suppression
            descriptionValueRepository.delete(value);
        });
    }

    private void validateDeletion(final DescriptionValue value) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Descriptions
        final Long descCount = descriptionRepository.countByValue(value);
        if (descCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DESC_VALUE_DEL_EXISTS_DESC).setAdditionalComplement(descCount).build());
        }

        if (!errors.isEmpty()) {
            value.setErrors(errors);
            throw new PgcnValidationException(value, errors);
        }
    }

    @Transactional
    public DescriptionValue save(final DescriptionValue value) throws PgcnValidationException {
        validate(value);
        return descriptionValueRepository.save(value);
    }

    private void validate(final DescriptionValue value) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isBlank(value.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DESC_VALUE_LABEL_MANDATORY).setField("label").build());
        }
        // la ppté parente est obligatoire
        if (value.getProperty() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.DESC_VALUE_PROPERTY_MANDATORY).setField("property").build());
        }

        if (!errors.isEmpty()) {
            value.setErrors(errors);
            throw new PgcnValidationException(value, errors);
        }
    }
}
