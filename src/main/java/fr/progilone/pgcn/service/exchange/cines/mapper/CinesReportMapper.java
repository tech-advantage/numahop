package fr.progilone.pgcn.service.exchange.cines.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.exchange.CinesReportDTO;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;

@Mapper
public interface CinesReportMapper {

	CinesReportMapper INSTANCE = Mappers.getMapper(CinesReportMapper.class);

    CinesReportDTO cinesReportToCinesReportDTO(CinesReport report);
}
