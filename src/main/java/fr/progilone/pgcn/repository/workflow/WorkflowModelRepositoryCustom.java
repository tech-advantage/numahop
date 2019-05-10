package fr.progilone.pgcn.repository.workflow;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.workflow.WorkflowModel;

/**
 * 
 * @author jbrunet
 * Créé le 19 juil. 2017
 */
public interface WorkflowModelRepositoryCustom {

    Page<WorkflowModel> search(String search,
            final String initiale,
         List<String> libraries,
         Pageable pageRequest);

}
