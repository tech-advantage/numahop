package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by Sébastien on 28/06/2017.
 */
public interface ImportedDocUnitRepositoryCustom {

    Page<String> findIdentifiersByImportReport(ImportReport report,
                                               List<DocUnit.State> states,
                                               boolean withErrors,
                                               boolean withDuplicates,
                                               Pageable pageable);
}
