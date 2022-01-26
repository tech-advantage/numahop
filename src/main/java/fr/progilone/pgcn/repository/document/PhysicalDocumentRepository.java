package fr.progilone.pgcn.repository.document;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.document.PhysicalDocument;

public interface PhysicalDocumentRepository extends JpaRepository<PhysicalDocument, String> {

    @Query("select pd "
           + "from PhysicalDocument pd "
           + "left join fetch pd.train "
           + "left join fetch pd.docUnit "
           + "where pd.identifier = ?1")
    PhysicalDocument findByIdentifier(String identifier);

    @Query("select distinct pd from PhysicalDocument pd "
           + "left join pd.docUnit du "
           + "left join du.lot l "
           + "left join fetch pd.digitalDocuments dd "
           + "where pd.digitalId = ?1 "
           + "and l.identifier = ?2")
    Set<PhysicalDocument> getAllByDigitalIdAndLotIdentifier(String digitalId, String lotIdentifier);

    @Query("select distinct pd from PhysicalDocument pd "
           + "left join pd.docUnit du "
           + "left join du.lot l "
           + "where l.identifier = ?1 "
           + "and pd.digitalId IS NOT NULL")
    Set<PhysicalDocument> findAllByLotDigitalIdNotNull(String identifier);

    @Query("select distinct pd from PhysicalDocument pd "
           + "left join pd.docUnit du "
           + "left join du.lot l "
           + "where l.identifier = ?1 ")
    Set<PhysicalDocument> findAllByLot(String identifier);

    List<PhysicalDocument> findByTrainIdentifier(String identifier);
    
    List<PhysicalDocument> findByDocUnitIdentifierIn(List<String> identifiers);
}
