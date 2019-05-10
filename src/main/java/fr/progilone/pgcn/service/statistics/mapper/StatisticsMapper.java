package fr.progilone.pgcn.service.statistics.mapper;

import fr.progilone.pgcn.domain.dto.statistics.WorkflowDeliveryProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.WorkflowDocUnitProgressDTO;
import fr.progilone.pgcn.domain.dto.statistics.csv.WorkflowDeliveryProgressCsvDTO;
import fr.progilone.pgcn.domain.dto.statistics.csv.WorkflowDocUnitProgressCsvDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatisticsMapper {

    /**
     * Conversion WorkflowDeliveryProgressDTO -> WorkflowDeliveryProgressCsvDTO
     *
     * @param uiDto
     * @return
     */
    public static List<WorkflowDeliveryProgressCsvDTO> toWorkflowDeliveryProgressCsvDTO(final List<WorkflowDeliveryProgressDTO> uiDto) {
        if (CollectionUtils.isEmpty(uiDto)) {
            return Collections.emptyList();
        }
        return uiDto.stream().flatMap(dto -> dto.getWorkflow().stream().map(w -> {
            final WorkflowDeliveryProgressCsvDTO csvDto = new WorkflowDeliveryProgressCsvDTO();
            csvDto.setLibraryName(dto.getLibraryName());
            csvDto.setProjectName(dto.getProjectName());
            csvDto.setLotLabel(dto.getLotLabel());
            csvDto.setDeliveryLabel(dto.getDeliveryLabel());
            csvDto.setKey(w.getKey());
            csvDto.setCount(w.getCount());
            return csvDto;
        })).collect(Collectors.toList());
    }

    /**
     * Conversion WorkflowDeliveryProgressDTO -> WorkflowDeliveryProgressCsvDTO
     *
     * @param uiDto
     * @return
     */
    public static List<WorkflowDocUnitProgressCsvDTO> toWorkflowDocUnitProgressCsvDTO(final List<WorkflowDocUnitProgressDTO> uiDto) {
        if (CollectionUtils.isEmpty(uiDto)) {
            return Collections.emptyList();
        }
        return uiDto.stream().flatMap(dto -> dto.getWorkflow().stream().map(w -> {
            final WorkflowDocUnitProgressCsvDTO csvDto = new WorkflowDocUnitProgressCsvDTO();
            csvDto.setLibraryName(dto.getLibraryName());
            csvDto.setProjectName(dto.getProjectName());
            csvDto.setLotLabel(dto.getLotLabel());
            csvDto.setDocPgcnId(dto.getDocPgcnId());
            csvDto.setDocLabel(dto.getDocLabel());
            csvDto.setTotalPage(dto.getTotalPage());
            csvDto.setKey(w.getKey());
            csvDto.setStatus(w.getStatus());
            return csvDto;
        })).collect(Collectors.toList());
    }

}
