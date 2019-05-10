package fr.progilone.pgcn.repository.exchange.internetarchive;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * @author jbrunet
 * Créé le 2 mai 2017
 */
public interface InternetArchiveReportRepository extends JpaRepository<InternetArchiveReport, String>, InternetArchiveReportRepositoryCustom {

    InternetArchiveReport findByIdentifier(String identifier);

    List<InternetArchiveReport> findAllByIdentifierIn(List<String> identifiers);

    List<InternetArchiveReport> findByStatusIn(InternetArchiveReport.Status... status);

    List<InternetArchiveReport> findByDocUnitIdentifierOrderByLastModifiedDateDesc(String docUnitId);

    List<InternetArchiveReport> findByDocUnitIdentifierIn(List<String> docUnitIds);

    Page<InternetArchiveReport> findAllByDocUnitState(DocUnit.State state, Pageable pageable);

    @Modifying
    void deleteByDocUnitIdentifier(String identifier);

    long countByDocUnitLibraryAndStatusIn(Library library, InternetArchiveReport.Status... status);
}
