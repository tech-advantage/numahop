package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.exchange.ImportReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ImportReportRepositoryCustom {

    /**
     * Recherche rapide d'imports
     */
    Page<ImportReport> search(String search, List<String> users, List<ImportReport.Status> status, List<String> libraries, Pageable pageRequest);
}
