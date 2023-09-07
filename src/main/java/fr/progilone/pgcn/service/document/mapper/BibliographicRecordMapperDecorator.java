package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.BibliographicRecord.PropertyOrder;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleBibliographicRecordDTO;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class BibliographicRecordMapperDecorator implements BibliographicRecordMapper {

    private final BibliographicRecordMapper delegate;

    public BibliographicRecordMapperDecorator(final BibliographicRecordMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public BibliographicRecordDTO bibliographicRecordToBibliographicRecordDTO(final BibliographicRecord record) {
        final BibliographicRecordDTO dto = delegate.bibliographicRecordToBibliographicRecordDTO(record);

        if (record.getPropertyOrder() == PropertyOrder.BY_PROPERTY_TYPE) {
            createWeightedValueOrderByType(dto.getProperties());
        } else {
            createWeightedValue(dto.getProperties());
        }
        return dto;
    }

    @Override
    public DocUnitBibliographicRecordDTO bibliographicRecordToDocUnitBibliographicRecordDTO(final BibliographicRecord record) {
        final DocUnitBibliographicRecordDTO dto = delegate.bibliographicRecordToDocUnitBibliographicRecordDTO(record);

        if (record.getPropertyOrder() == PropertyOrder.BY_PROPERTY_TYPE) {
            createWeightedValueOrderByType(dto.getProperties());
        } else {
            createWeightedValue(dto.getProperties());
        }
        return dto;
    }

    @Override
    public SimpleBibliographicRecordDTO bibliographicRecordToSimpleBibliographicRecordDTO(final BibliographicRecord record) {
        final SimpleBibliographicRecordDTO dto = delegate.bibliographicRecordToSimpleBibliographicRecordDTO(record);
        // handle title
        if (StringUtils.isBlank(dto.getTitle()) && record.getDocUnit() != null) {
            dto.setTitle(record.getDocUnit().getLabel());
        }
        return dto;
    }

    /**
     * Create weighted values for properties
     *
     * @param properties
     */
    private void createWeightedValueOrderByType(final List<DocPropertyDTO> properties) {
        final int maxPropertyRank = properties.stream()
                                              .mapToInt(p -> p.getRank() != null ? p.getRank()
                                                                                 : 0)
                                              .max()
                                              .orElse(Integer.MIN_VALUE);
        properties.forEach(property -> {
            final DocPropertyTypeDTO type = property.getType();
            final int rank = property.getRank() != null ? property.getRank()
                                                        : 0;
            property.setWeightedRank((double) (rank + type.getRank() * maxPropertyRank));
        });
    }

    /**
     * Create weighted values for properties
     *
     * @param properties
     */
    private void createWeightedValue(final List<DocPropertyDTO> properties) {
        properties.forEach(property -> {
            final int rank = property.getRank() != null ? property.getRank()
                                                        : 0;
            property.setWeightedRank((double) rank);
        });
    }
}
