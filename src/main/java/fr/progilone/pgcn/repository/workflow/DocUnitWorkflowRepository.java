package fr.progilone.pgcn.repository.workflow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;

public interface DocUnitWorkflowRepository extends JpaRepository<DocUnitWorkflow, String>, DocUnitWorkflowRepositoryCustom {

    List<DocUnitWorkflow> findByDocUnitIdentifierIn(List<String> docUnitIds);
    
    Long countByModel(WorkflowModel model);
}
