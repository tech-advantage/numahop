package fr.progilone.pgcn.repository.workflow;

import fr.progilone.pgcn.domain.workflow.WorkflowModelState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowModelStateRepository extends JpaRepository<WorkflowModelState, String>, WorkflowModelStateRepositoryCustom {

}
