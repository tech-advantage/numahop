package fr.progilone.pgcn.repository.workflow;

import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocUnitStateRepositoryCustom {

    /**
     * Recherche de DocUnitState
     *
     * @param searchBuilder
     * @param pageable
     * @return
     */
    Page<DocUnitState> findDocUnitStates(DocUnitWorkflowSearchBuilder searchBuilder, Pageable pageable);
}
