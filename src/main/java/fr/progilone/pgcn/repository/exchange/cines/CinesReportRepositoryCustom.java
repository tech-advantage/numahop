package fr.progilone.pgcn.repository.exchange.cines;

import fr.progilone.pgcn.domain.exchange.cines.CinesReport;

import java.time.LocalDate;
import java.util.List;

public interface CinesReportRepositoryCustom {

    List<CinesReport> findAll(List<String> libraries, LocalDate fromDate, boolean failures);
}
