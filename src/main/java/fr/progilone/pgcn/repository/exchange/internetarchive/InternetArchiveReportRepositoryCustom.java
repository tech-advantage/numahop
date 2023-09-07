package fr.progilone.pgcn.repository.exchange.internetarchive;

import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import java.time.LocalDate;
import java.util.List;

public interface InternetArchiveReportRepositoryCustom {

    List<InternetArchiveReport> findAll(List<String> libraries, LocalDate fromDate, boolean failures);
}
