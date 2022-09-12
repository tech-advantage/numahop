package fr.progilone.pgcn.repository.workflow;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;

public interface DocUnitWorkflowRepositoryCustom {

    /**
     * Recherche paginée
     *
     * @param libraries
     * @param projects
     * @param pgcnId
     * @param states
     * @param users
     * @param fromDate
     * @param toDate
     * @param pageable
     * @return
     */
    Page<DocUnitWorkflow> findDocUnitProgressStats(List<String> libraries,
                                                   List<String> projects,
                                                   boolean projetActive,
                                                   List<String> lots,
                                                   List<String> trains,
                                                   String pgcnId,
                                                   List<WorkflowStateKey> states,
                                                   final List<WorkflowStateStatus> status,
                                                   List<String> users,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Pageable pageable);

    /**
     * Recherche paginée
     *
     * @param libraries
     * @param projects
     * @param pgcnId
     * @param states
     * @param users
     * @param fromDate
     * @param toDate
     * @return
     */
    List<DocUnitWorkflow> findDocUnitProgressStatsPending(List<String> libraries,
                                                   List<String> projects,
                                                   boolean projetActive,
                                                   List<String> lots,
                                                   List<String> trains,
                                                   String pgcnId,
                                                   List<WorkflowStateKey> states,
                                                   final List<WorkflowStateStatus> status,
                                                   List<String> users,
                                                   LocalDate fromDate,
                                                   LocalDate toDate);

    /**
     * Recherche de DocUnitWorkflow
     *
     * @param searchBuilder
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflows(DocUnitWorkflowSearchBuilder searchBuilder);

    /**
     * Recherce de DocUnitWorkflow filtrés sur les étapes en cours
     *
     * @param libraries
     * @param projects
     * @param lots
     * @param deliveries
     * @param pendingStates
     * @param fromDate
     * @return
     */
    List<DocUnitWorkflow> findPendingDocUnitWorkflows(List<String> libraries,
                                                      List<String> projects,
                                                      List<String> lots,
                                                      List<String> deliveries,
                                                      List<WorkflowStateKey> pendingStates,
                                                      LocalDate fromDate);

    /**
     * Recherche de DocUnitWorkflows en attente de contrôle, ie une étape de livraison / relivraison est terminée,
     * mais suivie d'aucun étape de validation ok / ko terminée
     *
     * @param libraries
     * @param projects
     * @param lots
     * @param deliveries
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflowsInControl(List<String> libraries, List<String> projects, List<String> lots, List<String> deliveries);

    List<Object[]> getDocUnitsGroupByStatus(List<String> libraries, List<String> projects, List<String> lots);

    /**
     * Recherce de DocUnitWorkflow filtrés sur l'étape de controle qualite.
     *
     * @param libraries
     * @param projects
     * @param lots
     * @param states
     * @param fromDate
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflowsForStateControl(List<String> libraries,
                                                              List<String> projects,
                                                              List<String> lots,
                                                              List<DigitalDocumentStatus> states,
                                                              LocalDate fromDate);
    /**
     * Retrouve les docUnitWorkflow candidats pour diffusion vers Archive.
     * (diffusable et workflow en attente de diffusion)
     * ATTENTION : pas de controle d'acces - A reserver aux traitements automatiques
     *
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflowsForArchiveExport(String library);

    /**
     * Retrouve les docUnitWorkflow candidats pour Export local.
     * (workflow en attente)
     * ATTENTION : pas de controle d'acces - A reserver aux traitements automatiques
     *
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflowsForLocalExport(String library);

    /**
     * Retrouve les docUnitWorkflow candidats pour Export sur bibliothèque numérique.
     * (workflow en attente)
     * ATTENTION : pas de controle d'acces - A reserver aux traitements automatiques
     *
     * @return
     */
    List<DocUnitWorkflow> findDocUnitWorkflowsForDigitalLibraryExport(String library);
}
