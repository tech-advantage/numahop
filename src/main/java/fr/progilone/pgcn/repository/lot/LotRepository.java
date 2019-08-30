package fr.progilone.pgcn.repository.lot;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;

public interface LotRepository extends JpaRepository<Lot, String>, LotRepositoryCustom {

    @Query("select l.identifier from Lot l")
    List<String> findAllIdentifiers();

    @Query("select l.identifier from Lot l where lower(l.label) like %?1%")
    Page<String> findAllIdentifiers(String filter, Pageable pageable);

    @Query("select distinct l "
        + "from Lot l "
        + "left join fetch l.project "
        + "left join fetch l.platforms "
        + "where l.identifier in ?1 ")
    List<Lot> findByIdentifierIn(Iterable<String> ids, Sort sort);

    @Query("select distinct l "
        + "from Lot l "
        + "left join fetch l.project "
        + "left join fetch l.platforms "
        + "where l.identifier in ?1 ")
    List<Lot> findByIdentifierIn(Iterable<String> ids);

    @Query("from Lot l "
           + "left join fetch l.project "
           + "left join fetch l.platforms "
           + "left join fetch l.docUnits "
           + "left join fetch l.activeFTPConfiguration "
           + "left join fetch l.activeOcrLanguage "
           + "where l.identifier = ?1")
    Lot findOneWithDependencies(String identifier);
    
    @Query("from Lot l "
            + "join fetch l.project p "
            + "join fetch p.library lib "
            + "left join fetch l.activeFTPConfiguration "
            + "left join fetch p.activeFTPConfiguration "
            + "left join fetch lib.activeFTPConfiguration "
            + "where l.identifier = ?1")
    Lot findOneWithActiveFtpConfig(String identifier);
    
    @Query("from Lot l "
            + "left join fetch l.activeCheckConfiguration "
            + "left join fetch l.activeFormatConfiguration "
            + "left join fetch l.activeOcrLanguage "
            + "left join fetch l.project p "
            + "left join fetch p.activeCheckConfiguration "
            + "left join fetch p.activeFormatConfiguration "
            + "left join fetch p.library lib "
            + "left join fetch lib.activeCheckConfiguration "
            + "left join fetch lib.activeFormatConfiguration "
            + "left join fetch lib.activeOcrLangConfiguration "
            + "where l.identifier = ?1")
    Lot findOneWithActiveCheckConfiguration(String identifier);

    @Query("from Lot l "
            + "left join fetch l.project p "
            + "left join fetch l.platforms "
            + "left join fetch l.docUnits "
            + "left join fetch l.activeFTPConfiguration "
            + "left join fetch l.activeCheckConfiguration "
            + "left join fetch p.library "
            + "where l.identifier = ?1")
     Lot findOneWithMoreDependencies(String identifier);

    @Query("select distinct l "
           + "from Lot l "
           + "left join fetch l.project "
           + "left join fetch l.platforms "
           + "left join fetch l.activeFTPConfiguration "
           + "left join fetch l.activeOcrLanguage "
           + "where l.id in ?1")
    List<Lot> findAllWithDependencies(final Collection<String> ids);
    
    @Query("from Lot lo "
            + "left join fetch lo.project p "
            + "left join fetch p.library l "            
            + "where lo.status = 'CLOSED' "
            + "and lo.filesArchived = false "
            + "and l.identifier = ?1 "
            + "and lo.realEndDate < ?2 ")
     List<Lot> getClosedLotsByLibrary(String libraryId, LocalDate dateTo);

    List<Lot> findByLabelAndProject(String label, Project project);

    List<Lot> findAllByActive(boolean active);
    
    List<Lot> findAllByActiveAndTypeAndStatus(boolean active, Lot.Type type, Lot.LotStatus status);
    
    @Query("from Lot lo "
            + "left join fetch lo.provider "
            + "left join fetch lo.docUnits du "
            + "left join fetch du.workflow  "
            + "where (lo.type = 'PHYSICAL' and lo.status = 'ONGOING') "
            + "or  (lo.type = 'DIGITAL') "
            + "and lo.active = ?1")
    List<Lot> findAllByActiveForDelivery(boolean active);
    
    @Query("from Lot lo "
            + "left join fetch lo.project " 
            + "left join fetch lo.provider "
            + "where lo.type = 'PHYSICAL' and lo.status = 'ONGOING' "
            + "and lo.active = ?1")
    List<Lot> findAllActiveForMultiLotsDelivery(boolean active);

    List<Lot> findAllByProjectIdentifier(String id);
    
    List<Lot> findAllByActiveAndTypeAndStatusAndProjectIdentifier(boolean active, Lot.Type type, Lot.LotStatus status, String id);
    
    List<Lot> findAllByActiveAndProjectLibraryIdentifierIn(boolean active, List<String> libraries);
    
    List<Lot> findAllByActiveAndProjectIdentifierIn(boolean active, List<String> projects);

    Long countByActiveFTPConfiguration(FTPConfiguration conf);
    
    Long countByActiveExportFTPConfiguration(ExportFTPConfiguration conf);

    Long countByActiveCheckConfiguration(CheckConfiguration conf);
    
    Long countByActiveFormatConfiguration(ViewsFormatConfiguration conf);

    Long countByProvider(User user);

    Long countByWorkflowModel(WorkflowModel model);
    /* attention : recupere aussi lots inactifs pour la reprise de données */
    List<Lot> findAllByProjectLibraryIdentifierIn(List<String> libraries);

    @Query("from Lot lo "
            + "left join fetch lo.docUnits du "
            + "left join fetch du.workflow "            
            + "where du.identifier = ?1 ")
    Lot findOneByDocUnit(String id);
    
    @Query("from Lot lo "
            + "left join fetch lo.workflowModel wf "
            + "left join fetch wf.modelStates "
            + "left join fetch lo.project  "
            + "where lo.identifier = ?1 ")
    Lot findOneWithWorkflowModel(String id);
}
