package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Sebastien on 08/12/2016.
 */
public interface ImportedDocUnitRepository extends JpaRepository<ImportedDocUnit, String>, ImportedDocUnitRepositoryCustom {

    @Query("select i.identifier " + "from ImportedDocUnit i "
           + "where i.report = ?1")
    Page<String> findIdentifiersByImportReport(ImportReport report, Pageable pageable);

    @Query("select d.identifier " + "from ImportedDocUnit i "
           + "join i.docUnit d "
           + "where i.report = ?1 "
           + "and d.state= ?2")
    Page<String> findDocUnitIdentifiersByImportReport(ImportReport report, DocUnit.State state, Pageable pageable);

    @Query("select distinct i " + "from ImportedDocUnit i "
           + "left join fetch i.duplicatedUnits "
           + "left join fetch i.messages "
           + "join fetch i.docUnit d "
           + "left join fetch d.library l "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "where i.identifier = ?1 ")
    ImportedDocUnit findByIdentifier(String identifier);

    @Query("select distinct i " + "from ImportedDocUnit i "
           + "left join fetch i.messages "
           + "left join fetch i.docUnit d "
           + "left join fetch d.library l "
           + "left join fetch d.records r "
           + "left join fetch i.duplicatedUnits dup "
           + "left join fetch dup.records rdup "
           + "where i.identifier in ?1 ")
    List<ImportedDocUnit> findByIdentifiersIn(List<String> identifiers, Sort sort);

    @Query("select distinct i " + "from ImportedDocUnit i "
           + "join i.duplicatedUnits dup "
           + "where dup.identifier = ?1 ")
    List<ImportedDocUnit> findByDuplicatedUnits(String identifier);

    @Query("select distinct i " + "from ImportedDocUnit i "
           + "join fetch i.docUnit d "
           + "where i.report.identifier = ?1 "
           + "and i.parentKey in ?2")
    List<ImportedDocUnit> findByReportIdentifierAndParentKeyIn(String reportId, Collection<String> parentKeys);

    @Modifying
    @Query(value = "delete from exc_doc_unit where identifier in ?1", nativeQuery = true)
    void deleteByIds(Collection<String> identifiers);

    @Modifying
    @Query(value = "delete from exc_doc_unit_dupl where imp_unit in ?1", nativeQuery = true)
    void deleteDuplicatedUnitsByImportedDocUnitIds(Collection<String> identifiers);

    @Modifying
    @Query(value = "delete from exc_doc_unit_dupl where doc_unit = ?1", nativeQuery = true)
    void deleteDuplicatedUnitsByDocUnitIdentifier(String identifier);

    @Modifying
    @Query(value = "delete from exc_doc_unit_msg where imp_unit in ?1", nativeQuery = true)
    void deleteMessagesByImportedDocUnitIds(Collection<String> identifiers);

    @Modifying
    void deleteByDocUnitIdentifier(String identifier);

    @Modifying
    @Query("update ImportedDocUnit set process = ?2 where identifier = ?1")
    void updateProcess(String identifier, ImportedDocUnit.Process process);

    @Modifying
    @Query("update ImportedDocUnit u set u.docUnit = null where u.docUnit.identifier in ?1")
    void setDocUnitNullByDocUnitIdIn(List<String> docUnitIds);
}
