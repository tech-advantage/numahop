package fr.progilone.pgcn.repository.exchange;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Sebastien on 08/12/2016.
 */
public interface ImportReportRepository extends JpaRepository<ImportReport, String>, ImportReportRepositoryCustom {

    @Query("select r "
           + "from ImportReport r "
           + "left join fetch r.mapping "
           + "left join fetch r.mappingChildren "
           + "left join fetch r.csvMapping "
           + "left join fetch r.parentReport "
           + "left join fetch r.project "
           + "left join fetch r.lot "
           + "join fetch r.library "
           + "where r.identifier = ?1")
    ImportReport findByIdentifier(String identifier);

    Page<ImportReport> findByLibraryIdentifierIn(List<String> library, Pageable pageable);

    List<ImportReport> findByLibraryIdentifier(String libraryId);

    List<ImportReport> findByStatusIn(ImportReport.Status... status);

    @Query("select d.identifier "
           + "from ImportReport r "
           + "join r.docUnits i "
           + "join i.docUnit d "
           + "where r.identifier = ?1 "
           + "and d.state = ?2")
    List<String> findDocUnitIdentifiersByReportAndDocUnitState(String report, DocUnit.State state);

    @Query("select i.identifier "
           + "from ImportReport r "
           + "join r.docUnits i "
           + "where r.identifier = ?1")
    List<String> findImportIdentifiersByReport(String report);

    @Modifying
    @Query("update ImportReport r set r.mapping = null where r.mapping.identifier = ?1")
    void setMappingNull(String mappingId);

    @Modifying
    @Query("update ImportReport r set r.mappingChildren = null where r.mappingChildren.identifier = ?1")
    void setMappingChildrenNull(String mappingId);

    @Modifying
    @Query("update ImportReport r set r.csvMapping = null where r.csvMapping.identifier = ?1")
    void setCsvMappingNull(String mappingId);

    @Modifying
    @Query("update ImportReport r set r.lot = null where r.lot.identifier in ?1")
    void setLotNullByLotIdIn(List<String> lotIds);

    @Modifying
    @Query("update ImportReport r set r.project = null where r.project.identifier in ?1")
    void setProjectNullByProjectIdIn(List<String> projectIds);

    @Modifying
    @Query("update ImportReport r set r.parentReport = null where r.parentReport.identifier = ?1")
    void setParentNull(String identifier);
}
