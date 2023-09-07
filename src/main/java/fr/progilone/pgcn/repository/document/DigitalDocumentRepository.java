package fr.progilone.pgcn.repository.document;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DigitalDocumentRepository extends JpaRepository<DigitalDocument, String>, DigitalDocumentRepositoryCustom {

    @Query("select dd from DigitalDocument dd " + "left join fetch dd.docUnit du "
           + "left join fetch du.lot l "
           + "left join fetch l.activeCheckConfiguration c "
           + "where dd.identifier = ?1")
    DigitalDocument getOneWithCheckConfiguration(String identifier);

    @Query("select dd from DigitalDocument dd " + "left join fetch dd.pages pg "
           + "where pg.number != null and dd.identifier = ?1")
    DigitalDocument getOneWithPages(String identifier);

    @Query("select dd from DigitalDocument dd " + "left join fetch dd.pages pg "
           + "left join fetch dd.docUnit du "
           + "where pg.number != null and dd.identifier = ?1")
    DigitalDocument getOneWithDocUnitAndPages(String identifier);

    @Query("select dd from DigitalDocument dd " + "left join fetch dd.pages pg "
           + "left join fetch dd.physicalDocuments pd "
           + "where pg.number != null and dd.identifier = ?1")
    DigitalDocument getOneWithPagesAndPhysical(String identifier);

    @Query("select distinct dd from DigitalDocument dd " + "left join fetch dd.docUnit du "
           + "left join du.lot l "
           + "where dd.digitalId = ?1 "
           + "and l.identifier = ?2")
    List<DigitalDocument> getAllByDigitalIdAndLotIdentifier(String digitalId, String lotIdentifier);

    Set<DigitalDocument> getAllByStatusIn(DigitalDocument.DigitalDocumentStatus... toCheck);

    @Query("select distinct dd from DigitalDocument dd " + "left join dd.checks gc "
           + "join dd.pages p "
           + "left join p.checks c "
           + "where dd.identifier = ?1")
    DigitalDocument getOneWithChecks(String identifier);

    @Query("select dd from DigitalDocument dd " + "inner join fetch dd.docUnit du "
           + "left join fetch du.library "
           + "where dd.identifier = ?1")
    DigitalDocument getOneWithDocUnitAndLibrary(final String identifier);

    @Query("select distinct dd from DigitalDocument dd " + "where dd.digitalId = ?1 "
           + "order by deliveryDate desc")
    List<DigitalDocument> getAllByDigitalId(String digitalId);

    @Query("select d " + "from DigitalDocument dd "
           + "join dd.docUnit d "
           + "where dd.identifier = ?1")
    DocUnit findDocUnitByIdentifier(String identifier);

}
