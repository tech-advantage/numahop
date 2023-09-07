package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportSearchDTO;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ConditionReportValueMapper.class})
public abstract class ConditionReportMapper {

    public static final ConditionReportMapper INSTANCE = Mappers.getMapper(ConditionReportMapper.class);
    public static final ConditionReportValueMapper INSTANCE_VALUE = Mappers.getMapper(ConditionReportValueMapper.class);

    public abstract ConditionReportDTO reportToDTO(ConditionReport report);

    public abstract ConditionReportSearchDTO reportToSearchDTO(ConditionReport report);

    @AfterMapping
    protected void updateSearchDTO(final ConditionReport report, @MappingTarget final ConditionReportSearchDTO dto) {
        // Tri par type
        report.getDetails()
              .stream()
              .max(Comparator.comparing(ConditionReportDetail::getPosition))
              // Alimentation du DTO
              .ifPresent(det -> {
                  dto.setDate(det.getDate());
                  dto.setType(det.getType().name());
                  dto.setProperties(det.getDescriptions().stream().map(INSTANCE_VALUE::bindingToDTO).collect(Collectors.toList()));
              });
    }
}
