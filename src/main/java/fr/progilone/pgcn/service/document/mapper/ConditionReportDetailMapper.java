package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDetailDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDetailVelocityDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueVelocityDTO;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueVelocityDTO.ValueType.*;

@Mapper(uses = {ConditionReportValueMapper.class})
public abstract class ConditionReportDetailMapper {

    public static final ConditionReportDetailMapper INSTANCE = Mappers.getMapper(ConditionReportDetailMapper.class);

    @Mappings({@Mapping(target = "date", dateFormat = "dd/MM/yyyy"), @Mapping(target = "insurance", numberFormat = "#,##0.##")})
    public abstract ConditionReportDetailDTO detailToDTO(ConditionReportDetail detail);

    @Mappings({@Mapping(target = "date", dateFormat = "dd/MM/yyyy"),
               @Mapping(target = "insurance", numberFormat = "#,##0.##"),
               @Mapping(target = "descriptions", ignore = true)})
    public abstract ConditionReportDetailVelocityDTO detailToVelocityDTO(ConditionReportDetail detail);

    @AfterMapping
    protected void updateDTO(ConditionReportDetail detail, @MappingTarget ConditionReportDetailDTO dto) {
        final List<ConditionReportValueDTO> reducedDescriptions = joinValuesByProperty(dto.getDescriptions());

        dto.setBindings(getDescriptionList(reducedDescriptions, DescriptionProperty.Type.BINDING));
        dto.setDescriptions(getDescriptionList(reducedDescriptions, DescriptionProperty.Type.DESCRIPTION));
        dto.setNumberings(getDescriptionList(reducedDescriptions, DescriptionProperty.Type.NUMBERING));
        dto.setVigilances(getDescriptionList(reducedDescriptions, DescriptionProperty.Type.VIGILANCE));
    }

    @AfterMapping
    protected void updateDTO(ConditionReportDetail detail, @MappingTarget ConditionReportDetailVelocityDTO dto) {
        detail.getDescriptions().stream().collect(Collectors.groupingBy(Description::getProperty))
              // Descriptions regroupées par propriétés
              .forEach((property, descriptions) -> {
                  final ConditionReportValueVelocityDTO valueDto = new ConditionReportValueVelocityDTO();
                  valueDto.setPropertyCode(property.getCode());
                  valueDto.setPropertyId(property.getIdentifier());
                  valueDto.setPropertyLabel(property.getLabel());
                  valueDto.setPropertyType(property.getType().name());

                  descriptions.forEach(desc -> {
                      final DescriptionValue value = desc.getValue();
                      final String comment = desc.getComment();

                      if (value != null && StringUtils.isNotBlank(value.getLabel())) {
                          valueDto.addValue(value.getLabel(), LIST);
                      }
                      if (StringUtils.isNotBlank(comment)) {
                          valueDto.addValue(comment, COMMENT);
                      }
                  });

                  getDescriptionList(dto, property.getType()).add(valueDto);
              });
    }

    /**
     * Réduction de la liste de valeurs par regroupement par propriété
     *
     * @param values
     * @return
     */
    private List<ConditionReportValueDTO> joinValuesByProperty(final List<ConditionReportValueDTO> values) {
        return values.stream()
                     // Regroupement desc descriptions par prorpriété
                     .collect(Collectors.groupingBy(ConditionReportValueDTO::getPropertyId)).values().stream()
                     // Concaténation des valeur par propriété
                     .map(v -> v.stream().reduce((a, b) -> {
                         a.setComment(concat(a.getComment(), b.getComment()));
                         a.setValue(concat(a.getValue(), b.getValue()));
                         return a;
                     })).filter(Optional::isPresent).map(Optional::get)
                     // Liste de propriétés réduite
                     .collect(Collectors.toList());
    }

    private String concat(final String a, final String b) {
        return StringUtils.isNotBlank(a) ? StringUtils.isNotBlank(b) ? a + ", " + b : a : StringUtils.isNotBlank(b) ? b : "";
    }

    /**
     * Filtrage + Tri de la liste de descriptions
     *
     * @param descriptions
     * @param type
     * @return
     */
    private List<ConditionReportValueDTO> getDescriptionList(final List<ConditionReportValueDTO> descriptions, final DescriptionProperty.Type type) {
        return descriptions.stream().filter(d -> DescriptionProperty.Type.valueOf(d.getPropertyType()) == type).collect(Collectors.toList());
    }

    private List<ConditionReportValueVelocityDTO> getDescriptionList(final ConditionReportDetailVelocityDTO dto,
                                                                     final DescriptionProperty.Type type) {
        switch (type) {
            case VIGILANCE:
                return dto.getVigilances();
            case BINDING:
                return dto.getBindings();
            case NUMBERING:
                return dto.getNumberings();
            case DESCRIPTION:
            default:
                return dto.getDescriptions();
        }
    }
}
