package fr.progilone.pgcn.repository.multilotsdelivery;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import fr.progilone.pgcn.domain.user.QUser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.QDelivery;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.domain.multilotsdelivery.QMultiLotsDelivery;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QRole;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;

public class MultiLotsDeliveryRepositoryImpl implements MultiLotsDeliveryRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


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
        final QLibrary qAssociatedLibrary = QLibrary.library;
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

        final JPQLQuery baseQuery = new JPAQuery(em);

        baseQuery.from(qMulti)
                 .leftJoin(qMulti.deliveries, qDelivery).fetch()
                 .leftJoin(qDelivery.lot, qLot).fetch()
                 .leftJoin(qLot.project, qProject).fetch()
                 .leftJoin(qProject.associatedUsers, qAssociatedUser)
                 .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                 .leftJoin(qProject.library, qLibrary).fetch()
                 .leftJoin(qLibrary.defaultRole, qRole)
                 .where(builder.getValue())
                 .distinct();

        // Décompte
        final long total = baseQuery.count();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }
        // Résultats
        final List<MultiLotsDelivery> result = baseQuery.orderBy(qMulti.label.asc()).list(qMulti);

        return new PageImpl<>(result, pageable, total);
    }

    /**
     * Construction d'une sous-requête testant l'existance d'ud vérifiant certains critères.
     *
     * @param docUnitPgcnId
     * @param docUnitStates
     * @param qDelivery
     * @return
     */
//    private Optional<Predicate> appendDocUnitFilter(final String docUnitPgcnId,
//                                                    final List<WorkflowStateKey> docUnitStates,
//                                                    final QDelivery qDelivery) {
//        final boolean filterPgcnId = StringUtils.isNotBlank(docUnitPgcnId);
//        final boolean filterDocStatus = CollectionUtils.isNotEmpty(docUnitStates);
//        Predicate existsFilter = null;
//
//        if (filterPgcnId || filterDocStatus) {
//            final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
//            final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
//            final QWorkflowModelState qWorkflowModelState = QWorkflowModelState.workflowModelState;
//            final QDocUnit qDocUnit = QDocUnit.docUnit;
//            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;
//            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
//
//            final BooleanBuilder subBuilder = new BooleanBuilder();
//
//            // UD
//            subBuilder.and(qDocUnit.isNotNull());
//
//            if (filterPgcnId) {
//                subBuilder.and(qDocUnit.pgcnId.like('%' + docUnitPgcnId + '%'));
//            }
//            // Étape de workflow en cours au moment de l'exécution de la requête
//            if (CollectionUtils.isNotEmpty(docUnitStates)) {
//                subBuilder.and(qWorkflowModelState.key.in(docUnitStates));
//                subBuilder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());
//            }
//            // Livraison
//            subBuilder.and(qDeliveredDocument.delivery.identifier.eq(qDelivery.identifier));
//
//            existsFilter = new JPASubQuery().from(qDocUnitWorkflow)
//                                            .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
//                                            .innerJoin(qDocUnit.digitalDocuments, qDigitalDocument)
//                                            .innerJoin(qDigitalDocument.deliveries, qDeliveredDocument)
//                                            .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
//                                            .leftJoin(qDocUnitState.modelState, qWorkflowModelState)
//                                            .where(subBuilder.getValue())
//                                            .exists();
//        }
//        return Optional.ofNullable(existsFilter);
//    }


}
