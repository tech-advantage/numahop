package fr.progilone.pgcn.repository.document;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;

public interface DocUnitRepository extends JpaRepository<DocUnit, String>, DocUnitRepositoryCustom {

    @Query("select d "
            + "from DocUnit d "
            + "left join fetch d.library "
            + "where d.identifier = ?1")
     DocUnit findOneWithLibrary(String identifier);
    
    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.physicalDocuments pd "
           + "left join fetch d.project "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "where d.identifier = ?1")
    DocUnit findOneWithDependencies(String identifier);

    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.parent parent "
           + "left join fetch d.library "
           + "left join fetch d.workflow "
           + "left join fetch d.physicalDocuments pd "
           + "left join fetch d.digitalDocuments dd "
           + "left join fetch d.activeOcrLanguage "
           + "left join fetch dd.pages pages "
           + "left join fetch pages.files f "
           + "left join fetch d.project "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch d.exportData ed "
           + "left join fetch ed.properties "
           + "left join fetch p.type "
           + "left join fetch d.archiveItem ai "
           + "left join fetch ai.collections "
           + "left join fetch ai.subjects "
           + "left join fetch ai.headers "
           + "where d.identifier = ?1")
    DocUnit findOneWithAllDependencies(String identifier);
    
    @Query("select d "
            + "from DocUnit d "
            + "left join fetch d.activeOcrLanguage "
            + "left join fetch d.lot l "
            + "left join fetch l.activeOcrLanguage "
            + "where d.identifier = ?1")
    DocUnit findOneWithOcrLanguage(String identifier);

    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.workflow w "
           + "left join fetch w.model "
           + "where d.identifier = ?1")
    DocUnit findOneWithAllDependenciesForWorkflow(String identifier);

    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.project p "
           + "left join fetch p.provider "
           + "left join fetch d.lot l "
           + "left join fetch l.provider "
           + "where d.identifier = ?1")
    DocUnit findOneWithAllDependenciesForInheritance(String identifier);

    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.physicalDocuments pd "
           + "left join fetch d.digitalDocuments dd "
           + "left join fetch dd.deliveries del "
           + "left join fetch d.project "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "left join fetch d.workflow "
           + "where d.identifier = ?1")
    DocUnit findOneForDisplay(String identifier);

    @Query("select distinct d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.physicalDocuments pd "
           + "left join fetch d.digitalDocuments dd "
           + "left join fetch d.project "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "left join fetch d.automaticCheckResults "
           + "where d.parent.identifier = ?1")
    List<DocUnit> findByParentIdentifier(String parentId);

    List<DocUnit> findByIdentifierIn(Iterable<String> idDocs);

    @Query("select distinct sd from DocUnit d join d.sibling s join s.docUnits sd where d.identifier = ?1")
    List<DocUnit> findSiblingsByIdentifier(String id);

    @Query("select distinct d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.physicalDocuments pd "
           + "left join fetch d.digitalDocuments dd "
           + "left join fetch dd.pages pages "
           + "left join fetch pages.files f "
           + "left join fetch d.project "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "where d.identifier in ?1")
    List<DocUnit> findByIdentifierInWithDependencies(Iterable<String> idDocs);

    @Query("select distinct d " + "from DocUnit d " + "left join fetch d.records r " + "where d.identifier in ?1")
    List<DocUnit> findByIdentifierInWithRecords(Iterable<String> idDocs);

    @Query("select d from DocUnit d "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "where d.identifier = ?1")
    DocUnit findOneByIdentifierWithRecords(String idDoc);

    @Query("select d from DocUnit d " 
            + "left join fetch d.library "
            + "left join fetch d.physicalDocuments p " 
            + "left join fetch d.records r " 
            + "where d.identifier in ?1")
    Set<DocUnit> findByIdentifierInWithDocs(Iterable<String> idDocs);

    @Query("select distinct d "
           + "from DocUnit d "
           + "left join fetch d.library "
           + "left join fetch d.project "
           + "left join fetch d.lot "
           + "where d.identifier in ?1")
    List<DocUnit> findByIdentifierInWithProj(Iterable<String> idDocs);

    @Query("select distinct d "
           + "from DocUnit d "
           + "left join fetch d.physicalDocuments p "
           + "left join fetch d.lot l "
           + "where l.identifier in ?1 and p.digitalId in ?2")
    DocUnit findOneByLotIdentifierAndDigitalId(String identifier, String digitalId);

    List<DocUnit> findAllByState(DocUnit.State state);

    Page<DocUnit> findAllByState(DocUnit.State state, Pageable pageable);

    @Override
    List<DocUnit> findAll();

    List<DocUnit> findAllByProjectIdentifier(String identifier);

    List<DocUnit> findAllByLotIdentifier(String identifier);

    DocUnit getOneByPgcnIdAndState(String pgcnId, DocUnit.State state);

    Long countByPgcnIdAndState(String pgcnId, DocUnit.State state);

    Long countByPgcnIdAndStateAndIdentifierNot(String pgcnId, DocUnit.State state, final String identifier);

    Long countByLibraryAndState(Library library, DocUnit.State state);

    Long countByLot(Lot lot);

    @Modifying
    @Query("update DocUnit u set u.parent = null where u.parent.identifier in ?1")
    void setParentNullByParentIdIn(List<String> parentIds);

    @Query("select d "
           + "from DocUnit d "
           + "left join fetch d.exportData ed "
           + "left join fetch ed.properties "
           + "left join fetch d.archiveItem ai "
           + "left join fetch ai.collections "
           + "left join fetch ai.subjects "
           + "left join fetch ai.headers "
           + "where d.identifier = ?1")
    DocUnit findOneWithExportDependencies(String identifier);

    @Query("select distinct d from DocUnit d "
           + "left join fetch d.parent parent "
           + "left join fetch d.library lib "
           + "left join fetch d.exportData ed "
           + "left join fetch ed.properties "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "left join fetch d.workflow w "
           + "where d.archivable = true and w != null "
           + "and lib.identifier in ?1")
    List<DocUnit> findByLibraryWithCinesExportDep(String libraryId);
    
    @Query("select distinct d from DocUnit d "
            + "left join fetch d.parent parent "
            + "left join fetch d.library lib "
            + "left join fetch d.records r "
            + "left join fetch r.properties p "
            + "left join fetch p.type "
            + "left join fetch d.workflow w "
            + "where d.distributable = true and w != null "
            + "and lib.identifier in ?1")
     List<DocUnit> findByLibraryWithOmekaExportDep(String libraryId);

    @Query("select distinct d from DocUnit d "
           + "left join fetch d.library lib "
           + "left join fetch d.records r "
           + "left join fetch r.properties p "
           + "left join fetch p.type "
           + "left join fetch d.archiveItem ai "
           + "left join fetch ai.collections "
           + "left join fetch ai.subjects "
           + "left join fetch ai.headers "
           + "left join fetch d.workflow w "
           + "where d.distributable = true and w != null "
           + "and lib.identifier in ?1")
    List<DocUnit> findByLibraryWithArchiveExportDep(String libraryId);

    @Query("select distinct d.type from DocUnit d where d.type is not null order by d.type asc")
    Page<String> findDistinctTypes(final Pageable pageable);

    @Query("select distinct d.type from DocUnit d where lower(d.type) like %?1% order by d.type asc")
    Page<String> findDistinctTypes(final String search, final Pageable pageable);
}
