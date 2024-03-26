package fr.progilone.pgcn.repository.workflow;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.QPhysicalDocument;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class DocUnitWorkflowRepositoryImpl implements DocUnitWorkflowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DocUnitWorkflowRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<DocUnitWorkflow> findDocUnitProgressStats(final List<String> libraries,
                                                          final List<String> projects,
                                                          final boolean projetActive,
                                                          final List<String> lots,
                                                          final List<String> trains,
                                                          final String pgcnId,
                                                          final List<WorkflowStateKey> states,
                                                          final List<WorkflowStateStatus> status,
                                                          final List<String> users,
                                                          final LocalDate fromDate,
                                                          final LocalDate toDate,
                                                          final Pageable pageable) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;

        final JPAQuery<DocUnitWorkflow> baseQuery = createQueryToFindDocUnitWorkFlows(qDocUnitWorkflow,
                                                                                      libraries,
                                                                                      projects,
                                                                                      projetActive,
                                                                                      lots,
                                                                                      trains,
                                                                                      pgcnId,
                                                                                      states,
                                                                                      status,
                                                                                      users,
                                                                                      fromDate,
                                                                                      toDate);

        // Nombre de résultats
        final long total = baseQuery.clone().select(qDocUnitWorkflow.countDistinct()).fetchOne();

        // Pagination
        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        // Résultats
        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    private JPAQuery<DocUnitWorkflow> createQueryToFindDocUnitWorkFlows(final QDocUnitWorkflow qDocUnitWorkflow,
                                                                        final List<String> libraries,
                                                                        final List<String> projects,
                                                                        final boolean projetActive,
                                                                        final List<String> lots,
                                                                        final List<String> trains,
                                                                        final String pgcnId,
                                                                        final List<WorkflowStateKey> states,
                                                                        final List<WorkflowStateStatus> status,
                                                                        final List<String> users,
                                                                        final LocalDate fromDate,
                                                                        final LocalDate toDate) {
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QWorkflowModelState qWorkflowModelState = QWorkflowModelState.workflowModelState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QLibrary qAssociatedLibrary = new QLibrary("associatedLibrary");
        final QLibrary qLibrary = QLibrary.library;
        final QUser qAssociatedUser = QUser.user;
        final QProject qProject = QProject.project;
        final QLot qLot = QLot.lot;
        final QPhysicalDocument qPhysicalDocument = QPhysicalDocument.physicalDocument;
        final BooleanBuilder builder = new BooleanBuilder();

        // UD
        if (StringUtils.isNotBlank(pgcnId)) {
            builder.and(qDocUnit.pgcnId.like('%' + pgcnId
                                             + '%'));
        }
        // Projets
        if (projetActive) {
            builder.and(qProject.active.eq(true));
        }
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qLot.identifier.in(lots));
        }
        // Trains
        if (CollectionUtils.isNotEmpty(trains)) {
            builder.and(qPhysicalDocument.train.identifier.in(trains));
        }
        // Étape de workflow
        if (CollectionUtils.isNotEmpty(states)) {
            builder.and(qDocUnitState.discriminator.in(states));
        }
        // Étape de workflow, démarrée, appartenant à l'intervalle [fromDate; toDate]
        if (fromDate != null) {
            builder.and(qDocUnitState.status.ne(WorkflowStateStatus.NOT_STARTED));
            builder.and(qDocUnitState.endDate.isNull()).or(qDocUnitState.endDate.after(fromDate.atStartOfDay()));
        }
        if (toDate != null) {
            builder.and(qDocUnitState.status.ne(WorkflowStateStatus.NOT_STARTED));
            builder.and(qDocUnitState.startDate.before(toDate.plusDays(1).atStartOfDay()));
        }
        if (CollectionUtils.isNotEmpty(status)) {
            builder.and(qDocUnitState.status.in(status));
        }

        // provider, library
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qLot, qProject, qAssociatedLibrary, qAssociatedUser, libraries, null);

        // Utilisateurs
        if (CollectionUtils.isNotEmpty(users)) {
            final QWorkflowGroup qWorkflowGroup = QWorkflowGroup.workflowGroup;
            final QUser qUser = QUser.user;
            // les utilisateurs recherchés font partie des groupes de workflow des étapes
            builder.and(JPAExpressions.select(qUser)
                                      .from(qUser)
                                      .innerJoin(qUser.groups, qWorkflowGroup)
                                      .where(qUser.login.in(users).and(qWorkflowGroup.eq(qWorkflowModelState.group)))
                                      .exists());
        }

        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .leftJoin(qDocUnitState.modelState, qWorkflowModelState)
                           .leftJoin(qDocUnit.project, qProject)
                           .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                           .leftJoin(qProject.associatedUsers, qAssociatedUser)
                           .leftJoin(qDocUnit.lot, qLot)
                           .leftJoin(qDocUnit.library, qLibrary)
                           .leftJoin(qDocUnit.physicalDocuments, qPhysicalDocument)
                           .where(builder)
                           .orderBy(qDocUnit.pgcnId.asc());
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflows(final DocUnitWorkflowSearchBuilder searchBuilder) {
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;

        final JPAQuery<DocUnitWorkflow> query = queryFactory.select(qDocUnitWorkflow)
                                                            .from(qDocUnitWorkflow)
                                                            .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                                                            .innerJoin(qDocUnitWorkflow.states, qDocUnitState);

        return DocUnitWorkflowHelper.getFindDocUnitWorkflowQuery(query, searchBuilder, qDocUnit, qDocUnitWorkflow, qDocUnitState).fetch();
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
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
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
            final Predicate subQuery = JPAExpressions.select(qDeliveredDocument)
                                                     .from(qDeliveredDocument)
                                                     .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                                     .where(subBuilder)
                                                     .exists();
            builder.and(subQuery);
        }
        // Étapes en cours
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
        builder.and(JPAExpressions.select(qDocUnitState).from(qDocUnitState).where(stateBuilder).exists());

        // Requête
        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .fetchJoin()
                           .leftJoin(qDocUnit.library, qLibrary)
                           .leftJoin(qDocUnit.project, qProject)
                           .where(builder)
                           .fetch();
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsInControl(final List<String> libraries, final List<String> projects, final List<String> lots, final List<String> deliveries) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
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
            final Predicate subQuery = JPAExpressions.select(qDeliveredDocument)
                                                     .from(qDeliveredDocument)
                                                     .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                                     .leftJoin(qDocUnit.library, qLibrary)
                                                     .leftJoin(qDocUnit.project, qProject)
                                                     .where(subBuilder)
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
        builderDelivery.and(JPAExpressions.select(qDocUnitStateValidation)
                                          .from(qDocUnitStateValidation)
                                          .where(builderValidation.and(qDocUnitStateValidation.workflow.eq(qDocUnitStateDelivery.workflow))
                                                                  .and(qDocUnitStateValidation.startDate.goe(qDocUnitStateDelivery.endDate)))
                                          .notExists());

        /*
         * Workflow d 'UD ayant une (Re)Livraison terminée, non suivie d' une validation terminée
         * La double sous-requête donne:
         * select *
         * from doc_workflow w
         * inner join doc_workflow_state s on s.workflow = w.identifier
         * where s.start_date is not null
         * and s.end_date is not null
         * and s.`key` in ('LIVRAISON_DOCUMENT_EN_COURS', 'RELIVRAISON_DOCUMENT_EN_COURS')
         * and not exists(
         * select s2.identifier
         * from doc_workflow w2
         * inner join doc_workflow_state s2 on s2.workflow = w2.identifier
         * where s2.start_date is not null
         * and s2.end_date is not null
         * and s2.`key` in ('VALIDATION_DOCUMENT')
         * and s2.start_date >= s.end_date
         * and w2.identifier = w.identifier
         * )
         */
        builder.and(JPAExpressions.select(qDocUnitStateDelivery)
                                  .from(qDocUnitStateDelivery)
                                  .where(builderDelivery.and(qDocUnitStateDelivery.workflow.eq(qDocUnitWorkflow)))
                                  .exists());

        // Requête
        return queryFactory.selectDistinct(qDocUnitWorkflow).from(qDocUnitWorkflow).innerJoin(qDocUnitWorkflow.docUnit, qDocUnit).where(builder).fetch();
    }

    @Override
    public List<Object[]> getDocUnitsGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final BooleanBuilder builder = new BooleanBuilder();

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qDocUnit.lot.identifier.in(lots));
        }
        // Workflow en cours
        builder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());

        // Requête
        return queryFactory.select(qDocUnitState.discriminator, qDocUnit.identifier.countDistinct())
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .innerJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .leftJoin(qDocUnit.library, qLibrary)
                           .leftJoin(qDocUnit.project, qProject)
                           .where(builder)
                           .groupBy(qDocUnitState.discriminator)
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
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;

        final BooleanBuilder builder = new BooleanBuilder();

        final List<WorkflowStateKey> wkfStates = new ArrayList<>();
        wkfStates.add(WorkflowStateKey.CONTROLE_QUALITE_EN_COURS);

        // Droits d'accès
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qProject, libraries, null);

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
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
        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .leftJoin(qDocUnit.library, qLibrary)
                           .leftJoin(qDocUnit.project, qProject)
                           .where(builder)
                           .fetch();
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsForArchiveExport(final String library) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        final List<WorkflowStateKey> wkfStates = new ArrayList<>();
        wkfStates.add(WorkflowStateKey.DIFFUSION_DOCUMENT);

        // etape controle qualite en cours
        builder.and(qDocUnitState.discriminator.in(wkfStates)).and(qDocUnitState.status.eq(WorkflowStateStatus.PENDING)).and(qDocUnitState.endDate.isNull());

        // docUnit diffusable
        builder.and(qDocUnit.distributable.isTrue()).and(qDocUnitWorkflow.endDate.isNull());

        builder.and(qDocUnit.library.identifier.eq(library));

        // Requête
        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .fetchJoin()
                           .where(builder)
                           .fetch();
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsForLocalExport(final String library) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        final List<WorkflowStateKey> wkfStates = new ArrayList<>();
        wkfStates.add(WorkflowStateKey.DIFFUSION_DOCUMENT_LOCALE);

        // etape diffusion locale
        builder.and(qDocUnitState.discriminator.in(wkfStates)).and(qDocUnitState.status.eq(WorkflowStateStatus.PENDING)).and(qDocUnitState.endDate.isNull());

        builder.and(qDocUnit.library.identifier.eq(library));

        // Requête
        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .where(builder)
                           .fetch();
    }

    @Override
    public List<DocUnitWorkflow> findDocUnitWorkflowsForDigitalLibraryExport(final String library) {

        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        final List<WorkflowStateKey> wkfStates = new ArrayList<>();
        wkfStates.add(WorkflowStateKey.DIFFUSION_DOCUMENT_DIGITAL_LIBRARY);

        // etape diffusion sur bibliothèqure numérique
        builder.and(qDocUnitState.discriminator.in(wkfStates)).and(qDocUnitState.status.eq(WorkflowStateStatus.PENDING)).and(qDocUnitState.endDate.isNull());

        builder.and(qDocUnit.library.identifier.eq(library));
        builder.and(qDocUnit.digLibExportStatus.ne(DocUnit.ExportStatus.IN_PROGRESS).or(qDocUnit.digLibExportStatus.isNull()));

        // Requête
        return queryFactory.selectDistinct(qDocUnitWorkflow)
                           .from(qDocUnitWorkflow)
                           .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                           .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                           .fetchJoin()
                           .where(builder)
                           .fetch();
    }

}
