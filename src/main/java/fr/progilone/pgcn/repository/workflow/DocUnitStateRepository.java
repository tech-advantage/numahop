package fr.progilone.pgcn.repository.workflow;

import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DocUnitStateRepository extends JpaRepository<DocUnitState, String>, DocUnitStateRepositoryCustom {

    @Query("select distinct d " + "from DocUnitState d "
           + "left join fetch d.modelState m "
           + "where d.status in ?1 "
           + "and m.group = ?2")
    List<DocUnitState> findAllStateInStatusForGroupIdentifier(List<WorkflowStateStatus> statuses, WorkflowGroup group);
}
