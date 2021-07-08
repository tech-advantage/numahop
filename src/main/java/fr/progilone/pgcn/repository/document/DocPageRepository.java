package fr.progilone.pgcn.repository.document;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import fr.progilone.pgcn.domain.document.DocPage;

public interface DocPageRepository extends JpaRepository<DocPage, String> {

    DocPage getOneByNumberAndDigitalDocumentIdentifier(int number, String digitalDocumentIdentifier);
    
    DocPage getOneByNumberAndSampleIdentifier(int number, String sampleIdentifier);

    @Query("select p from DocPage p "
           + "where p.number!=null and p.digitalDocument.identifier = ?1 ")
    List<DocPage> getAllByDigitalDocumentIdentifier(String digitalDocumentIdentifier);
    
    @Query("select p from DocPage p "
            + "where p.number is null and p.digitalDocument.identifier = ?1 ")
     DocPage getMasterPdfByDigitalDocumentIdentifier(String digitalDocumentIdentifier);
    
    @Query("select p from DocPage p "
            + "where p.number!=null and p.sample.identifier = ?1 ")
    List<DocPage> getAllBySampleIdentifier(String sampleIdentifier);

    @Query("select p from DocPage p "
           + "join p.checks c "
           + "where p.digitalDocument.identifier = ?1 ")
    List<DocPage> getAllByDigitalDocumentIdentifierWithErrors(String digitalDocumentIdentifier);

    @Query("select distinct p.number from DocPage p "
           + "join p.checks c "
           + "where p.digitalDocument.identifier = ?1 ")
    List<Integer> getPageNumbersByDigitalDocumentIdentifierWithErrors(String digitalDocumentIdentifier);
    
    @Query("select du.pgcnId, p from DocPage p "
            + "join p.digitalDocument dd "
            + "join dd.docUnit du "
            + "where du.project.identifier = ?1 ")
    List<Object[]> getPagesByProjectIdentifier(String projectIdentifier);
    
    @Query("select du.pgcnId, p from DocPage p "
            + "left join p.digitalDocument dd "
            + "left join dd.docUnit du "
            + "where du.lot.identifier = ?1 ")
    List<Object[]> getPagesByLotIdentifier(String lotIdentifier);

    @Query("select count(distinct p) from DocPage p "
           + "join p.checks c "
           + "where c.errorType = 'MINOR' and p.digitalDocument.identifier = ?1 ")
    int countDocPageWithMinorErrors(String digitalDocumentIdentifier);
    
    @Query("select count(distinct p) from DocPage p "
            + "join p.checks c "
            + "where c.errorType = 'MINOR' and p.sample.identifier = ?1 ")
     int countDocPageWithMinorErrorsForSample(String sampleIdentifier);
    
    @Query("select count(distinct p) from DocPage p "
            + "join p.checks c "
            + "where c.errorType = 'MAJOR' and p.digitalDocument.identifier = ?1 ")
    int countDocPageWithMajorErrors(String digitalDocumentIdentifier);
    
    @Query("select count(distinct p) from DocPage p "
            + "join p.checks c "
            + "where c.errorType = 'MAJOR' and p.sample.identifier = ?1 ")
    int countDocPageWithMajorErrorsForSample(String sampleIdentifier);

    int countDocPageByDigitalDocumentIdentifier(String digitalDocumentIdentifier);

    @Query("select distinct p.piece from DocPage p "
           + "where p.digitalDocument.identifier = ?1 ")
    Set<String> getAllPieceByDigitalDocumentIdentifier(String identifier);
}
