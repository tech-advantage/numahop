package fr.progilone.pgcn.service.document.mapper;

import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportAttachmentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ConditionReportAttachmentMapper {

    ConditionReportAttachmentMapper INSTANCE = Mappers.getMapper(ConditionReportAttachmentMapper.class);

    ConditionReportAttachmentDTO attachmentToDTO(ConditionReportAttachment atachment);
}
