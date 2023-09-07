package fr.progilone.pgcn.service.imagemetadata;

import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataProperty;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageMetadataService {

    private final ImageMetadataRepository imageMetadataRepository;
    private final ImageMetadataValuesService imageMetadataValuesService;

    @Autowired
    public ImageMetadataService(final ImageMetadataRepository imageMetadataRepository, final ImageMetadataValuesService imageMetadataValuesService) {
        this.imageMetadataRepository = imageMetadataRepository;
        this.imageMetadataValuesService = imageMetadataValuesService;
    }

    @Transactional(readOnly = true)
    public List<ImageMetadataProperty> findAll() {
        return imageMetadataRepository.findAll();
    }

    @Transactional
    public ImageMetadataProperty save(final ImageMetadataProperty metadata) throws PgcnValidationException {
        validateSave(metadata);
        return imageMetadataRepository.save(metadata);
    }

    @Transactional
    public List<ImageMetadataProperty> saveList(List<ImageMetadataProperty> metaToSaved) throws PgcnBusinessException {
        // check metadata to remove
        List<ImageMetadataProperty> metadataToRemove = findAll().stream().filter(metaStored -> !metaToSaved.contains(metaStored)).collect(Collectors.toList());
        deleteAll(metadataToRemove);

        final PgcnList<PgcnError> errors = new PgcnList<>();

        metaToSaved.forEach(meta -> errors.addAll(validateSave(meta)));
        errors.addAll(checkDuplicate(metaToSaved, ImageMetadataProperty::getLabel, "label"));
        errors.addAll(checkDuplicate(metaToSaved, ImageMetadataProperty::getIptcTag, "Tag Iptc"));
        errors.addAll(checkDuplicate(metaToSaved, ImageMetadataProperty::getXmpTag, "Tag Xmp"));

        metadataToRemove.forEach(meta -> {
            if (meta.getErrors() != null && !meta.getErrors().isEmpty()) {
                errors.addAll(meta.getErrors());
            }
        });

        if (!errors.isEmpty()) {
            throw new PgcnBusinessException(errors);
        }

        return imageMetadataRepository.saveAll(metaToSaved);
    }

    private PgcnList<PgcnError> checkDuplicate(final List<ImageMetadataProperty> metaList, Function<ImageMetadataProperty, String> getField, String field) {
        final Set<String> control = new HashSet<>();
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        metaList.forEach(meta -> {
            String value = getField.apply(meta);
            if (value != null && !value.isEmpty()
                && !control.add(value)) {
                errors.add(builder.reinit()
                                  .setCode(PgcnErrorCode.IMAGE_METADATA_SAME_PROPERTY_EXIST)
                                  .addComplement(meta.getLabel())
                                  .addComplement(field)
                                  .addComplement(value)
                                  .build());
            }
        });

        return errors;
    }

    @Transactional
    public void delete(final ImageMetadataProperty metadata) throws PgcnValidationException {
        validateDeletion(metadata);
        imageMetadataRepository.delete(metadata);
    }

    @Transactional
    public void deleteAll(final List<ImageMetadataProperty> metadataList) {
        imageMetadataRepository.deleteAll(metadataList.stream().filter(this::validateDeletion).collect(Collectors.toList()));
    }

    /**
     * Validation de la sauvegarde
     *
     * @param metadata
     * @throws PgcnValidationException
     */
    private PgcnList<PgcnError> validateSave(final ImageMetadataProperty metadata) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Le libellé est renseigné
        if (StringUtils.isBlank(metadata.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.IMAGE_METADATA_FIELDS_MANDATORY).addComplement("label").build());
        }

        if (StringUtils.isBlank(metadata.getIptcTag()) && StringUtils.isBlank(metadata.getXmpTag())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.IMAGE_METADATA_FIELDS_MANDATORY).addComplement("iptcTag & xmpTag").build());
        }

        if (metadata.getType() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.IMAGE_METADATA_FIELDS_MANDATORY).addComplement("type").build());
        }

        if (!errors.isEmpty()) {
            metadata.setErrors(errors);
        }

        return errors;
    }

    /**
     * Validation de la suppression
     *
     * @param metadata
     * @throws PgcnValidationException
     */
    private boolean validateDeletion(final ImageMetadataProperty metadata) {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // check if metadata is already used by doc units
        Integer count = imageMetadataValuesService.countByMetadata(metadata);
        if (count != null && count > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.IMAGE_METADATA_PROPERTY_USED).addComplement(metadata.getLabel()).build());
            metadata.setErrors(errors);
            // throw new PgcnValidationException(metadata, errors);
            return false;
        }

        return true;
    }

    public ImageMetadataProperty findPropertyByIdentifier(String label) {
        return imageMetadataRepository.findOneImageMetadataPropertyByIdentifier(label);
    }
}
