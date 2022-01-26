package fr.progilone.pgcn.repository.workflow;

import java.util.List;

import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import org.springframework.data.jpa.repository.JpaRepository;

import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import org.springframework.data.jpa.repository.Query;

public interface DocUnitWorkflowRepository extends JpaRepository<DocUnitWorkflow, String>, DocUnitWorkflowRepositoryCustom {

    List<DocUnitWorkflow> findByDocUnitIdentifierIn(List<String> docUnitIds);
    
    Long countByModel(WorkflowModel model);
    
    @Query("SELECT s from DocUnitState s " +
            "LEFT JOIN s.workflow w " +
            "WHERE s.discriminator in ?2 " +
            "AND w.docUnit.identifier = ?1")
    List<DocUnitState> findDocUnitStatesByKey(final String docUnit, final WorkflowStateKey... stateKeys);
}
