package fr.progilone.pgcn.repository.document.conditionreport;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConditionReportDetailRepository extends JpaRepository<ConditionReportDetail, String> {

    @Query("select r " + "from ConditionReportDetail c "
           + "join c.report r "
           + "where c.identifier = ?1")
    ConditionReport findParentByIdentifier(String identifier);

    @Query("select d " + "from ConditionReportDetail c "
           + "join c.report r "
           + "join r.docUnit d "
           + "where c.identifier = ?1")
    DocUnit findDocUnitByIdentifier(String identifier);

    @Query("select c " + "from ConditionReportDetail c "
           + "left join fetch c.descriptions d "
           + "left join fetch d.property "
           + "left join fetch d.value "
           + "where c.identifier = ?1")
    ConditionReportDetail findByIdentifier(String identifier);

    @Query("select d  " + "from ConditionReportDetail d "
           + "join d.report r "
           + "join r.docUnit du "
           + "where du.identifier = ?1")
    List<ConditionReportDetail> findByDocUnitId(String docUnitId);

    @Query("select c " + "from ConditionReportDetail c "
           + "where c.report.identifier = ?1 "
           + "order by c.position asc")
    List<ConditionReportDetail> findByConditionReportIdentifier(String reportId);

    @Query("select c " + "from ConditionReportDetail c "
           + "left join fetch c.descriptions d "
           + "left join fetch d.property "
           + "left join fetch d.value "
           + "where c.report.identifier = ?1 "
           + "order by c.position asc")
    List<ConditionReportDetail> findWithDescriptionsByCondReportIdentifier(String reportId);

    List<ConditionReportDetail> findByIdentifierIn(List<String> ids);

    @Query("select max(c.position) " + "from ConditionReportDetail c "
           + "where c.report.identifier = ?1")
    Integer getMaxPositionByConditionReportIdentifier(String reportId);
}
