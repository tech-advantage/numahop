package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Jonathan.
 */
public interface BibliographicRecordRepository extends JpaRepository<BibliographicRecord, String>, BibliographicRecordRepositoryCustom {

    @Query("select r " + "from BibliographicRecord r "
           + "left join fetch r.docUnit "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "left join fetch r.library "
           + "where r.identifier = ?1")
    BibliographicRecord findOneWithDependencies(String identifier);

    @Query("select distinct r " + "from BibliographicRecord r "
           + "left join fetch r.docUnit d "
           + "where d.identifier = ?1")
    List<BibliographicRecord> findAllByDocUnitIdentifier(String identifier);

    Page<BibliographicRecord> findAllByDocUnitState(DocUnit.State state, Pageable pageable);

    @Query("select distinct r " + "from BibliographicRecord r "
           + "left join fetch r.docUnit d "
           + "where r.identifier in ?1")
    List<BibliographicRecord> findAllByIdentifierIn(List<String> identifiers);

    Long countByLibraryAndDocUnitState(Library library, DocUnit.State state);

    @Query("select d " + "from BibliographicRecord r "
           + "join r.docUnit d "
           + "where r.identifier = ?1")
    DocUnit findDocUnitByIdentifier(String identifier);
}
