package fr.progilone.pgcn.repository.exchange.cines;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.library.Library;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

/**
 * Created by Sébastien on 30/12/2016.
 */
public interface CinesReportRepository extends JpaRepository<CinesReport, String>, CinesReportRepositoryCustom {

    CinesReport findByIdentifier(String identifier);

    CinesReport findFirstByDocUnitPgcnIdOrderByDateSentDesc(String pgcnId);

    List<CinesReport> findAllByIdentifierIn(List<String> identifiers);

    List<CinesReport> findByStatusIn(CinesReport.Status... status);

    List<CinesReport> findByDocUnitIdentifierOrderByLastModifiedDateDesc(String identifier);

    List<CinesReport> findByDocUnitIdentifierIn(List<String> docUnitIds);

    Page<CinesReport> findAllByDocUnitState(DocUnit.State state, Pageable pageable);

    @Modifying
    void deleteByDocUnitIdentifier(String identifier);

    long countByDocUnitLibraryAndStatusIn(Library library, CinesReport.Status... status);
}
