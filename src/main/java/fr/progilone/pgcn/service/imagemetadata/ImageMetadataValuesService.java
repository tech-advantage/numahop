package fr.progilone.pgcn.service.imagemetadata;

import fr.progilone.pgcn.domain.dto.imagemetadata.ImageMetadataValuesDTO;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataProperty;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataValue;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataValuesRepository;
import fr.progilone.pgcn.service.document.mapper.ImageMetadataValuesMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ImageMetadataValuesService {

    private final ImageMetadataValuesRepository imageMetadataValuesRepository;
    private final ImageMetadataValuesMapper imageMetadataValuesMapper;

    @Autowired
    public ImageMetadataValuesService(final ImageMetadataValuesRepository imageMetadataValuesRepository, final ImageMetadataValuesMapper imageMetadataValuesMapper) {
        this.imageMetadataValuesRepository = imageMetadataValuesRepository;
        this.imageMetadataValuesMapper = imageMetadataValuesMapper;
    }

    @Transactional(readOnly = true)
    public Integer countByMetadata(final ImageMetadataProperty metadata) {
        return imageMetadataValuesRepository.countByMetadata(metadata);
    }

    @Transactional
    public void deleteAll(final List<ImageMetadataValue> metadataList) throws PgcnValidationException {
        // metadataList.forEach(this::validateDeletion);
        imageMetadataValuesRepository.deleteAll(metadataList);
    }

    @Transactional
    public List<ImageMetadataValue> saveList(List<ImageMetadataValuesDTO> valuesDto) throws PgcnValidationException {
        // check metadata to remove
        List<ImageMetadataValue> metadataToRemove = getValuesByDocUnit(valuesDto.get(0).getDocUnitId()).stream()
                                                                                                       .filter(meta -> !valuesDto.stream()
                                                                                                                                 .anyMatch(valDto -> StringUtils.equals(valDto.getIdentifier(),
                                                                                                                                                                        meta.getIdentifier())))
                                                                                                       .collect(Collectors.toList());

        deleteAll(metadataToRemove);

        List<ImageMetadataValue> values = valuesDto.stream().map(valDto -> {
            ImageMetadataValue val = new ImageMetadataValue();
            imageMetadataValuesMapper.mapInto(valDto, val);
            return val;
        }).collect(Collectors.toList());

        final PgcnList<PgcnError> errors = new PgcnList<>();

        errors.addAll(validateSave(values));

        if (!errors.isEmpty()) {
            throw new PgcnValidationException(null, errors);
        }

        List<ImageMetadataValue> savedValues = imageMetadataValuesRepository.saveAll(values);

        return savedValues.stream().map(value -> imageMetadataValuesRepository.findOneWithDependencies(value.getIdentifier())).collect(Collectors.toList());
    }

    @Transactional
    public List<ImageMetadataValue> saveValuesList(List<ImageMetadataValue> values) throws PgcnValidationException {
        List<ImageMetadataValue> savedValues = imageMetadataValuesRepository.saveAll(values);

        return savedValues.stream().map(value -> imageMetadataValuesRepository.findOneWithDependencies(value.getIdentifier())).collect(Collectors.toList());
    }

    @Transactional
    public List<ImageMetadataValue> getValuesByDocUnit(String docUnitId) {
        return imageMetadataValuesRepository.findAllByDocUnitIdentifier(docUnitId);
    }

    public PgcnList<PgcnError> validateSave(final List<ImageMetadataValue> values) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        final Set<ImageMetadataProperty> control = new HashSet<>();

        values.forEach(value -> {
            if (!value.getMetadata().isRepeat()) {
                if (!control.add(value.getMetadata())) {
                    errors.add(builder.reinit()
                                      .setCode(PgcnErrorCode.IMAGE_METADATA_PROPERTY_USED)
                                      .addComplement(value.getMetadata().getLabel())
                                      .addComplement("Not be able to be repeat")
                                      .build());
                }
            }
        });

        return errors;
    }
}
