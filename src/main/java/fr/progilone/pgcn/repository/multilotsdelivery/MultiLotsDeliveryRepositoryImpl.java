package fr.progilone.pgcn.repository.multilotsdelivery;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.QDelivery;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.domain.multilotsdelivery.QMultiLotsDelivery;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QRole;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class MultiLotsDeliveryRepositoryImpl implements MultiLotsDeliveryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MultiLotsDeliveryRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<MultiLotsDelivery> search(final String search,
                                          final List<String> libraries,
                                          final List<String> projects,
                                          final List<String> lots,
                                          final List<String> deliveries,
                                          final List<String> providers,
                                          final List<Delivery.DeliveryStatus> status,
                                          final LocalDate dateFrom,
                                          final LocalDate dateTo,
                                          final List<WorkflowStateKey> docUnitStates,
                                          final Pageable pageable) {

        final QMultiLotsDelivery qMulti = QMultiLotsDelivery.multiLotsDelivery;
        final QDelivery qDelivery = QDelivery.delivery;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final QLot qLot = QLot.lot;
        final QRole qRole = QRole.role;
        final QLibrary qAssociatedLibrary = new QLibrary("associatedLibrary");
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        // Prestataires
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qLot, qProject, qAssociatedLibrary, qAssociatedUser, libraries, providers);

        // Libellé
        if (StringUtils.isNotBlank(search)) {
            builder.and(qDelivery.label.containsIgnoreCase(search));
        }
        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qLot.identifier.in(lots));
        }
        // Livraisons
        if (CollectionUtils.isNotEmpty(deliveries)) {
            builder.and(qDelivery.identifier.in(deliveries));
        }
        // Statut
        if (status != null) {
            builder.and(qDelivery.status.in(status));
        } else {
            final List<Delivery.DeliveryStatus> statusClosed = new ArrayList<>();
            statusClosed.add(Delivery.DeliveryStatus.CLOSED);
            builder.and(qMulti.status.notIn(statusClosed));
        }
        // Dates
        if (dateFrom != null) {
            builder.and(qDelivery.receptionDate.after(dateFrom.minusDays(1)));
        }
        if (dateTo != null) {
            builder.and(qDelivery.receptionDate.before(dateTo.plusDays(1)));
        }

        // UD
        // appendDocUnitFilter(docUnitPgcnId, docUnitStates, qDelivery).ifPresent(builder::and);

        final JPAQuery<MultiLotsDelivery> baseQuery = queryFactory.selectDistinct(qMulti)
                                                                  .from(qMulti)
                                                                  .leftJoin(qMulti.deliveries, qDelivery)
                                                                  .fetchJoin()
                                                                  .leftJoin(qDelivery.lot, qLot)
                                                                  .fetchJoin()
                                                                  .leftJoin(qLot.project, qProject)
                                                                  .fetchJoin()
                                                                  .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                                                  .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                                                  .leftJoin(qProject.library, qLibrary)
                                                                  .fetchJoin()
                                                                  .leftJoin(qLibrary.defaultRole, qRole)
                                                                  .where(builder)
                                                                  .orderBy(qMulti.label.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final long total = queryFactory.select(qMulti.countDistinct())
                                       .from(qMulti)
                                       .leftJoin(qMulti.deliveries, qDelivery)
                                       .leftJoin(qDelivery.lot, qLot)
                                       .leftJoin(qLot.project, qProject)
                                       .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                       .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                       .leftJoin(qProject.library, qLibrary)
                                       .leftJoin(qLibrary.defaultRole, qRole)
                                       .where(builder)
                                       .fetchOne();

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    /**
     * Construction d'une sous-requête testant l'existance d'ud vérifiant certains critères.
     *
     * @param docUnitPgcnId
     * @param docUnitStates
     * @param qDelivery
     * @return
     */
    // private Optional<Predicate> appendDocUnitFilter(final String docUnitPgcnId,
    // final List<WorkflowStateKey> docUnitStates,
    // final QDelivery qDelivery) {
    // final boolean filterPgcnId = StringUtils.isNotBlank(docUnitPgcnId);
    // final boolean filterDocStatus = CollectionUtils.isNotEmpty(docUnitStates);
    // Predicate existsFilter = null;
    //
    // if (filterPgcnId || filterDocStatus) {
    // final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
    // final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
    // final QWorkflowModelState qWorkflowModelState = QWorkflowModelState.workflowModelState;
    // final QDocUnit qDocUnit = QDocUnit.docUnit;
    // final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;
    // final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
    //
    // final BooleanBuilder subBuilder = new BooleanBuilder();
    //
    // // UD
    // subBuilder.and(qDocUnit.isNotNull());
    //
    // if (filterPgcnId) {
    // subBuilder.and(qDocUnit.pgcnId.like('%' + docUnitPgcnId + '%'));
    // }
    // // Étape de workflow en cours au moment de l'exécution de la requête
    // if (CollectionUtils.isNotEmpty(docUnitStates)) {
    // subBuilder.and(qWorkflowModelState.key.in(docUnitStates));
    // subBuilder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());
    // }
    // // Livraison
    // subBuilder.and(qDeliveredDocument.delivery.identifier.eq(qDelivery.identifier));
    //
    // existsFilter = new JPASubQuery().from(qDocUnitWorkflow)
    // .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
    // .innerJoin(qDocUnit.digitalDocuments, qDigitalDocument)
    // .innerJoin(qDigitalDocument.deliveries, qDeliveredDocument)
    // .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
    // .leftJoin(qDocUnitState.modelState, qWorkflowModelState)
    // .where(subBuilder.getValue())
    // .exists();
    // }
    // return Optional.ofNullable(existsFilter);
    // }

}
