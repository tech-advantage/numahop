package fr.progilone.pgcn.service.exchange.internetarchive.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.dto.exchange.InternetArchiveReportDTO;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;

@Mapper
public interface InternetArchiveReportMapper {

	InternetArchiveReportMapper INSTANCE = Mappers.getMapper(InternetArchiveReportMapper.class);

    InternetArchiveReportDTO internetArchiveReportToInternetArchiveReportDTO(InternetArchiveReport report);
}
