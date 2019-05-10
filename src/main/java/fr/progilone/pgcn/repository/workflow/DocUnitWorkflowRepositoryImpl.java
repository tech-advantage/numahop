package fr.progilone.pgcn.repository.workflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;

import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.QWorkflowGroup;
import fr.progilone.pgcn.domain.workflow.QWorkflowModelState;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowHelper;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;

public class DocUnitWorkflowRepositoryImpl implements DocUnitWorkflowRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<DocUnitWorkflow> findDocUnitProgressStats(final List<String> libraries,
                                                          final List<String> projects,
                                                          final List<String> lots,
                                                          final String pgcnId,
                                                          final List<WorkflowStateKey> states,
                                                          final List<String> users,
                                                          final LocalDate fromDate,
                                                          final LocalDate toDate,
                                                          final Pageable pageable) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QWorkflowModelState qWorkflowModelState = QWorkflowModelState.workflowModelState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, libraries, null);

        // UD
        if (StringUtils.isNotBlank(pgcnId)) {
            builder.and(qDocUnit.pgcnId.like('%' + pgcnId + '%'));
        }
        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocUnit.project.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // Étape de workflow en cours au moment de l'exécution de la requête
        if (CollectionUtils.isNotEmpty(states)) {
            builder.and(qDocUnitState.discriminator.in(states));
            builder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());
        }
        // Étape de workflow, démarrée, appartenant à l'intervalle [fromDate; toDate]
        if (fromDate != null) {
            builder.and(qDocUnitState.status.ne(WorkflowStateStatus.NOT_STARTED));
            builder.and(new BooleanBuilder().or(qDocUnitState.endDate.isNull()).or(qDocUnitState.endDate.after(fromDate.atStartOfDay())));
        }
        if (toDate != null) {
            builder.and(qDocUnitState.status.ne(WorkflowStateStatus.NOT_STARTED));
            builder.and(new BooleanBuilder().or(qDocUnitState.startDate.before(toDate.plusDays(1).atStartOfDay())));
        }
        // Utilisateurs
        if (CollectionUtils.isNotEmpty(users)) {
            final QWorkflowGroup qWorkflowGroup = QWorkflowGroup.workflowGroup;
            final QUser qUser = QUser.user;
            // les utilisateurs recherchés font partie des groupes de workflow des étapes                
            builder.and(new JPASubQuery().from(qUser)
                                         .innerJoin(qUser.groups, qWorkflowGroup)
                                         .where(new BooleanBuilder().and(qUser.login.in(users)).and(qWorkflowGroup.eq(qWorkflowModelState.group)))
                                         .exists());
        }

        final JPQLQuery baseQuery = new JPAQuery(em);
        baseQuery.from(qDocUnitWorkflow)
                 .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                 .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                 .leftJoin(qDocUnitState.modelState, qWorkflowModelState)
                 .leftJoin(qDocUnit.records)
                 .fetchAll()
                 .where(builder.getValue());

        // Nombre de résultats
        final long total = baseQuery.distinct().count();

        // Pagination
        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }
        // Tri
        baseQuery.orderBy(qDocUnitWorkflow.docUnit.pgcnId.asc());
        // Résultats
        final List<DocUnitWorkflow> results = baseQuery.distinct().list(qDocUnitWorkflow);

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflows(final DocUnitWorkflowSearchBuilder searchBuilder) {
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;

        final JPAQuery query =
            new JPAQuery(em).from(qDocUnitWorkflow).innerJoin(qDocUnitWorkflow.docUnit, qDocUnit).innerJoin(qDocUnitWorkflow.states, qDocUnitState);

        return DocUnitWorkflowHelper.getFindDocUnitWorkflowQuery(query, searchBuilder, qDocUnit, qDocUnitWorkflow, qDocUnitState)
                                    .list(qDocUnitWorkflow);
    }

    @Override
    public List<DocUnitWorkflow> findPendingDocUnitWorkflows(final List<String> libraries,
                                                             final List<String> projects,
                                                             final List<String> lots,
                                                             final List<String> deliveries,
                                                             final List<WorkflowStateKey> pendingStates,
                                                             final LocalDate fromDate) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocUnit.project.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // Livraisons
        if (CollectionUtils.isNotEmpty(deliveries)) {
            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;

            final BooleanBuilder subBuilder = new BooleanBuilder().and(qDeliveredDocument.delivery.identifier.in(deliveries))
                                                                  .and(qDigitalDocument.docUnit.identifier.eq(qDocUnit.identifier));
            final Predicate subQuery = new JPASubQuery().from(qDeliveredDocument)
                                                        .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                                        .where(subBuilder.getValue())
                                                        .exists();
            builder.and(subQuery);
        }
        // Étapes en cours
        final JPASubQuery stateSubQuery = new JPASubQuery().from(qDocUnitState);
        final BooleanBuilder stateBuilder = new BooleanBuilder().and(qDocUnitWorkflow.states.contains(qDocUnitState))
                                                                .and(qDocUnitState.startDate.isNotNull())
                                                                .and(qDocUnitState.endDate.isNull());
        // Êtats de l'étape
        if (CollectionUtils.isNotEmpty(pendingStates)) {
            stateBuilder.and(qDocUnitState.discriminator.in(pendingStates));
        }
        // Date de début de l'étape
        if (fromDate != null) {
            stateBuilder.and(qDocUnitState.startDate.goe(fromDate.atStartOfDay()));
        }
        builder.and(stateSubQuery.where(stateBuilder).exists());

        // Requête
        return new JPAQuery(em).from(qDocUnitWorkflow)
                               .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                               .fetchAll()
                               .where(builder.getValue())
                               .distinct()
                               .list(qDocUnitWorkflow);
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsInControl(final List<String> libraries,
                                                               final List<String> projects,
                                                               final List<String> lots,
                                                               final List<String> deliveries) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocUnit.project.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // Livraisons
        if (CollectionUtils.isNotEmpty(deliveries)) {
            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;

            final BooleanBuilder subBuilder = new BooleanBuilder().and(qDeliveredDocument.delivery.identifier.in(deliveries))
                                                                  .and(qDigitalDocument.docUnit.identifier.eq(qDocUnit.identifier));
            final Predicate subQuery = new JPASubQuery().from(qDeliveredDocument)
                                                        .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                                        .where(subBuilder.getValue())
                                                        .exists();
            builder.and(subQuery);
        }
        // Étape de livraison / relivraison terminée, non suivie d'une étape de validation ok / ko terminée
        final QDocUnitState qDocUnitStateDelivery = new QDocUnitState("stateDelivery");
        final QDocUnitState qDocUnitStateValidation = new QDocUnitState("stateValidation");

        // Livraison ou relivraison terminée
        final BooleanBuilder builderDelivery = new BooleanBuilder().and(qDocUnitStateDelivery.startDate.isNotNull())
                                                                   .and(qDocUnitStateDelivery.endDate.isNotNull())
                                                                   .and(qDocUnitStateDelivery.discriminator.in(WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS,
                                                                                                               WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS));
        // Validation ok / ko terminée
        final BooleanBuilder builderValidation = new BooleanBuilder().and(qDocUnitStateValidation.startDate.isNotNull())
                                                                     .and(qDocUnitStateValidation.endDate.isNotNull())
                                                                     .and(qDocUnitStateValidation.discriminator.in(WorkflowStateKey.VALIDATION_DOCUMENT));
        // (Re)Livraison terminée, non suivie d'une validation terminée
        builderDelivery.and(new JPASubQuery().from(qDocUnitStateValidation)
                                             .where(builderValidation.and(qDocUnitStateValidation.workflow.eq(qDocUnitStateDelivery.workflow))
                                                                     .and(qDocUnitStateValidation.startDate.goe(qDocUnitStateDelivery.endDate))
                                                                     .getValue())
                                             .notExists());

        /*
            Workflow d 'UD ayant une (Re)Livraison terminée, non suivie d' une validation terminée

            La double sous-requête donne:
                select *
                from doc_workflow w
                    inner join doc_workflow_state s on s.workflow = w.identifier
                where s.start_date is not null
                and s.end_date is not null
                and s.`key` in ('LIVRAISON_DOCUMENT_EN_COURS', 'RELIVRAISON_DOCUMENT_EN_COURS')
                and not exists(
                    select s2.identifier
                    from doc_workflow w2
                        inner join doc_workflow_state s2 on s2.workflow = w2.identifier
                    where s2.start_date is not null
                    and s2.end_date is not null
                    and s2.`key` in ('VALIDATION_DOCUMENT')
                    and s2.start_date >= s.end_date
                    and w2.identifier = w.identifier
                )
         */
        builder.and(new JPASubQuery().from(qDocUnitStateDelivery)
                                     .where(builderDelivery.and(qDocUnitStateDelivery.workflow.eq(qDocUnitWorkflow)).getValue())
                                     .exists());

        // Requête
        return new JPAQuery(em).from(qDocUnitWorkflow)
                               .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                               .fetchAll()
                               .where(builder.getValue())
                               .distinct()
                               .list(qDocUnitWorkflow);
    }

    @Override
    public List<Object[]> getDocUnitsGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocUnit.project.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // Workflow en cours
        builder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());

        // Requête
        return new JPAQuery(em).from(qDocUnitWorkflow)
                               .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                               .innerJoin(qDocUnitWorkflow.states, qDocUnitState)
                               .where(builder.getValue())
                               .groupBy(qDocUnitState.discriminator)
                               .list(qDocUnitState.discriminator, qDocUnit.identifier.countDistinct())
                               .stream()
                               .map(Tuple::toArray)
                               .collect(Collectors.toList());
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsForStateControl(final List<String> libraries,
                                                                     final List<String> projects,
                                                                     final List<String> lots,
                                                                     final List<DigitalDocumentStatus> states,
                                                                     final LocalDate fromDate) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        final List<WorkflowStateKey> wkfStates = new ArrayList<>();
        wkfStates.add(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qDocUnit.library, qDocUnit.project, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qDocUnit.project.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // etape controle qualite en cours
        builder.and(qDocUnitState.discriminator.in(wkfStates)).and(qDocUnitState.endDate.isNull());
        // Date de début de l'étape
        if (fromDate != null) {
            builder.and(qDocUnitState.startDate.goe(fromDate.atStartOfDay()));
        }
        if (CollectionUtils.isNotEmpty(states)) {
            builder.and(qDocUnit.digitalDocuments.any().status.in(states));
        }

        // Requête
        return new JPAQuery(em).from(qDocUnitWorkflow)
                               .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                               .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                               .fetchAll()
                               .where(builder.getValue())
                               .distinct()
                               .list(qDocUnitWorkflow);
    }
}

