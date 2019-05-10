package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConditionReportAttachmentRepository extends JpaRepository<ConditionReportAttachment, String> {

    @Query("select d "
           + "from ConditionReportAttachment a "
           + "join a.report r "
           + "join r.docUnit d "
           + "where a.identifier = ?1")
    DocUnit findDocUnitByIdentifier(String identifier);

    List<ConditionReportAttachment> findByReportIdentifier(String reportId);

    @Query("select a "
           + "from ConditionReportAttachment a "
           + "join fetch a.report "
           + "where a.identifier = ?1")
    ConditionReportAttachment findByIdentifier(String identifier);
}
