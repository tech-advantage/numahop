package fr.progilone.pgcn.repository.exchange.internetarchive;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author jbrunet
 *         Créé le 2 mai 2017
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

    @Query("select r " + "from InternetArchiveReport r "
           + "left join fetch r.docUnit doc "
           + "where r.status = 'ARCHIVED' "
           + "AND doc.arkUrl is NULL "
           + "AND r.dateArchived > ?1")
    List<InternetArchiveReport> findAllByStatusArchivedAndEmptyDocUnitArk(final LocalDateTime date);
}
