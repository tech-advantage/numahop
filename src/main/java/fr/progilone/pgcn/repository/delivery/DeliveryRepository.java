package fr.progilone.pgcn.repository.delivery;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.lot.Lot;

public interface DeliveryRepository extends JpaRepository<Delivery, String>, DeliveryRepositoryCustom {

    Delivery findOneByLabel(String label);
    
    Delivery findByIdentifier(String identifier);
    
    @Query("select distinct d "
            + "from Delivery d "
            + "join fetch d.documents "
            + "where d.identifier = ?1")
    Delivery findOneWithDocuments(String identifier);

    @Query("select distinct d "
           + "from Delivery d "
           + "join d.documents doc "
           + "join fetch d.lot l "
           + "left join fetch l.docUnits du "
           + "left join fetch l.activeCheckConfiguration "
           + "left join fetch l.activeFTPConfiguration "
           + "left join fetch du.physicalDocuments "
           + "where d.identifier = ?1")
    Delivery findOneWithDependencies(String id);

    @Query("select distinct doc "
            + "from DeliveredDocument doc "
            + "left join doc.delivery d "
            + "join fetch doc.digitalDocument dig "
           + "where d.identifier = ?1")
    Set<DeliveredDocument> findSimpleDeliveredDocumentsByDeliveryIdentifier(String id);
    
    @Query("select distinct doc "
            + "from DeliveredDocument doc "
            + "join fetch doc.delivery d "
            + "join fetch doc.digitalDocument dig "
            + "left join fetch doc.checkSlip "
            + "where dig.identifier = ?1 "
            + "order by doc.deliveryDate desc ")
    List<DeliveredDocument> findDeliveredDocumentsByDigitalDocIdentifier(String id);
    
    @Query("select doc from DeliveredDocument doc " 
            + "join fetch doc.digitalDocument dig "
            + "join fetch doc.checkSlip cs "
            + "join fetch cs.slipLines "
            + "left join fetch dig.docUnit "
            + "where doc.delivery.identifier = ?1 "
            + "and doc.digitalDocument.identifier = ?2")
    DeliveredDocument getOneWithSlip(String deliveryId, String digitalDocumentId);
    
    @Query("select doc from DeliveredDocument doc " 
            + "join fetch doc.digitalDocument "
            + "where doc.delivery.identifier = ?1 "
            + "and doc.digitalDocument.identifier = ?2")
    DeliveredDocument getOneWithDigitalDoc(String deliveryId, String digitalDocumentId);
    
    @Modifying
    @Query("delete from DeliveredDocument doc "
           + "where doc.delivery.identifier = ?1 and doc.digitalDocument.identifier = ?2")
    void deleteDeliveredDocument(String deliveryId, String digitalDocumentId);
    
    @Query("select distinct doc "
            + "from DeliveredDocument doc "
            + "join fetch doc.delivery d "
           + "where d.identifier = ?1")
    Set<DeliveredDocument> findDeliveredDocumentsByDeliveryIdentifier(String id);
    
    @Query("select distinct dd from DeliveredDocument dd "
        + "join fetch dd.delivery d "
        + "join fetch dd.digitalDocument dig "
        + "join fetch d.lot lo "
        + "join fetch lo.activeFTPConfiguration ftpConf "
        + "where dd.status = 'VALIDATED' and dd.deliveryDate < ?1")
    Set<DeliveredDocument> getValidatedDeliveredDocs(LocalDate dateFrom);
    

    List<Delivery> findByLotIdentifier(String lotId);

    List<Delivery> findAllByLotProjectIdentifier(String projectId);

    Delivery findByLotIdentifierAndMultiLotsDeliveryIdentifier(String lotId, String multiLotsId);
    
    @Query("select distinct d "
            + "from Delivery d "
            + "join fetch d.lot l "
            + "where l.identifier in ?1")
    List<Delivery> findDeliveriesByLotIdentifiers(final Collection<String> lotsIds);

    List<Delivery> findByIdentifierIn(List<String> identifiers);

    Long countByLot(Lot lot);

    @Query("select distinct d "
            + "from Delivery d "
            + "left join fetch d.lot l "
            + "left join fetch l.activeCheckConfiguration "
            + "left join fetch l.project p "
            + "left join fetch p.activeCheckConfiguration "
            + "where d.identifier = ?1")
    Delivery getWithActiveCheckConfiguration(String identifier);
    
    @Query("select distinct d "
            + "from Delivery d "
            + "left join fetch d.lot l "
            + "left join fetch l.project p "
            + "left join fetch p.library lib "
            + "left join lib.defaultRole "
            + "where d.identifier = ?1")
    Delivery getSimpleWithLot(String identifier);

    @Query("select distinct del "
           + "from Delivery del "
           + "join del.documents doc "
           + "join doc.digitalDocument dig "
           + "join dig.docUnit u "
           + "where u.identifier = ?1")
    List<Delivery> findAllByDocUnitIdentifier(String docUnitId);
    
}
