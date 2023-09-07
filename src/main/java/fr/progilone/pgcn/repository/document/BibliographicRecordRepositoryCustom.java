package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author jbrunet
 */
public interface BibliographicRecordRepositoryCustom {

    /**
     * Recherche rapide de notices
     *
     * @param search
     * @param libraries
     * @param orphan
     * @param pageable
     * @return
     */
    Page<BibliographicRecord> search(final String search,
                                     final List<String> libraries,
                                     final List<String> projects,
                                     final List<String> lots,
                                     final List<String> statuses,
                                     final List<String> trains,
                                     final LocalDate lastModifiedDateFrom,
                                     final LocalDate lastModifiedDateTo,
                                     final LocalDate createdDateFrom,
                                     final LocalDate createdDateTo,
                                     final Boolean orphan,
                                     final Pageable pageable);

}
