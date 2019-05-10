package fr.progilone.pgcn.repository.workflow;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.workflow.WorkflowModelState;

public interface WorkflowModelStateRepository extends JpaRepository<WorkflowModelState, String>, WorkflowModelStateRepositoryCustom {

}
