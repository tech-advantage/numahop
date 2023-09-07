package fr.progilone.pgcn.repository.delivery;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.delivery.QDelivery;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.sample.QSample;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.QWorkflowModelState;
import fr.progilone.pgcn.repository.delivery.helper.DeliverySearchBuilder;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class DeliveryRepositoryImpl implements DeliveryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DeliveryRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<Object[]> getDeliveryGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {
        final QDelivery qDelivery = QDelivery.delivery;
        final QLibrary qLibrary = QLibrary.library;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qAssociatedLibrary = QLibrary.library;
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        // provider
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qLot, qProject, qAssociatedLibrary, qAssociatedUser, libraries, null);

        // project
        if (CollectionUtils.isNotEmpty(projects)) {
            final BooleanExpression projectFilter = qProject.identifier.in(projects);
            builder.and(projectFilter);
        }

        // lot
        if (CollectionUtils.isNotEmpty(lots)) {
            final BooleanExpression lotFilter = qLot.identifier.in(lots);
            builder.and(lotFilter);
        }

        // query
        return queryFactory.select(qDelivery.status, qDelivery.identifier.countDistinct())
                           .from(qDelivery)
                           .leftJoin(qDelivery.lot, qLot)
                           .leftJoin(qLot.project, qProject)
                           .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                           .leftJoin(qProject.associatedUsers, qAssociatedUser)
                           .leftJoin(qProject.library)
                           .where(builder)
                           .groupBy(qDelivery.status)
                           .stream()
                           .map(Tuple::toArray)
                           .collect(Collectors.toList());
    }

    @Override
    public Page<Delivery> search(final DeliverySearchBuilder searchBuilder, final Pageable pageable) {

        final QDelivery qDelivery = QDelivery.delivery;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final QLot qLot = QLot.lot;
        final QLibrary qAssociatedLibrary = QLibrary.library;
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        // Prestataires
        QueryDSLBuilderUtils.addAccessFilters(builder,
                                              qLibrary,
                                              qLot,
                                              qProject,
                                              qAssociatedLibrary,
                                              qAssociatedUser,
                                              searchBuilder.getLibraries().orElse(null),
                                              searchBuilder.getProviders().orElse(null));

        // Libellé
        searchBuilder.getSearch().ifPresent(search -> {
            builder.and(qDelivery.label.containsIgnoreCase(search));
        });
        // Projets
        searchBuilder.getProjects().ifPresent(projects -> {
            builder.and(qProject.identifier.in(projects));
        });
        // Lots
        searchBuilder.getLots().ifPresent(lots -> {
            builder.and(qLot.identifier.in(lots));
        });
        // Livraisons
        searchBuilder.getDeliveries().ifPresent(deliveries -> {
            builder.and(qDelivery.identifier.in(deliveries));
        });
        // Statut
        if (searchBuilder.getStatus().isPresent()) {
            builder.and(qDelivery.status.in(searchBuilder.getStatus().get()));
        } else {
            final List<DeliveryStatus> statuses = new ArrayList<>();
            statuses.add(DeliveryStatus.CLOSED);
            builder.and(qDelivery.status.notIn(statuses));
        }

        // Dates
        searchBuilder.getDateFrom().ifPresent(dateFrom -> {
            builder.and(qDelivery.receptionDate.after(dateFrom.minusDays(1)));
        });
        searchBuilder.getDateTo().ifPresent(dateTo -> {
            builder.and(qDelivery.receptionDate.before(dateTo.plusDays(1)));
        });
        // UD
        appendDocUnitFilter(searchBuilder, qDelivery).ifPresent(builder::and);

        final JPAQuery<Delivery> baseQuery = queryFactory.selectDistinct(qDelivery)
                                                         .from(qDelivery)
                                                         .innerJoin(qDelivery.lot, qLot)
                                                         .innerJoin(qLot.project, qProject)
                                                         .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                                         .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                                         .innerJoin(qProject.library)
                                                         .where(builder.getValue());

        // Décompte
        final long total = baseQuery.clone().select(qDelivery.countDistinct()).fetchOne();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }
        // Résultats
        final List<Delivery> result = baseQuery.orderBy(qDelivery.label.asc()).fetch();

        return new PageImpl<>(result, pageable, total);
    }

    /**
     * Construction d'une sous-requête testant l'existance d'ud vérifiant certains critères.
     *
     * @param searchBuilder
     * @param qDelivery
     * @return
     */
    private Optional<Predicate> appendDocUnitFilter(final DeliverySearchBuilder searchBuilder, final QDelivery qDelivery) {
        Predicate existsFilter = null;

        if (searchBuilder.getDocUnitPgcnId().isPresent() || searchBuilder.getDocUnitStates().isPresent()) {
            final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;
            final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
            final QWorkflowModelState qWorkflowModelState = QWorkflowModelState.workflowModelState;
            final QDocUnit qDocUnit = QDocUnit.docUnit;
            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;
            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;

            final BooleanBuilder subBuilder = new BooleanBuilder();

            // UD
            subBuilder.and(qDocUnit.isNotNull());

            searchBuilder.getDocUnitPgcnId().ifPresent(docUnitPgcnId -> {
                subBuilder.and(qDocUnit.pgcnId.like('%' + docUnitPgcnId
                                                    + '%'));
            });
            // Étape de workflow en cours au moment de l'exécution de la requête
            searchBuilder.getDocUnitStates().ifPresent(docUnitStates -> {
                subBuilder.and(qWorkflowModelState.key.in(docUnitStates));
                subBuilder.and(qDocUnitState.startDate.isNotNull()).and(qDocUnitState.endDate.isNull());
            });
            // Livraison
            subBuilder.and(qDeliveredDocument.delivery.identifier.eq(qDelivery.identifier));

            existsFilter = JPAExpressions.select(qDocUnitWorkflow)
                                         .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit)
                                         .innerJoin(qDocUnit.digitalDocuments, qDigitalDocument)
                                         .innerJoin(qDigitalDocument.deliveries, qDeliveredDocument)
                                         .leftJoin(qDocUnitWorkflow.states, qDocUnitState)
                                         .leftJoin(qDocUnitState.modelState, qWorkflowModelState)
                                         .where(subBuilder.getValue())
                                         .exists();
        }
        return Optional.ofNullable(existsFilter);
    }

    @Override
    public List<Delivery> findByProjectsAndLots(final List<String> projectIds, final List<String> lotIds) {
        final QDelivery qDelivery = QDelivery.delivery;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;

        final boolean filterProj = CollectionUtils.isNotEmpty(projectIds);
        final boolean filterLot = CollectionUtils.isNotEmpty(lotIds);

        final JPAQuery<Delivery> baseQuery = queryFactory.select(qDelivery).from(qDelivery);

        if (filterProj || filterLot) {
            baseQuery.innerJoin(qDelivery.lot, qLot);
        }
        if (filterProj) {
            baseQuery.innerJoin(qLot.project, qProject);
            baseQuery.where(qProject.identifier.in(projectIds));
        }
        if (filterLot) {
            baseQuery.where(qLot.identifier.in(lotIds));
        }

        return baseQuery.fetch();
    }

    @Override
    public List<Delivery> findDeliveriesForWidget(final LocalDate fromDate,
                                                  final List<String> libraries,
                                                  final List<String> projects,
                                                  final List<String> lots,
                                                  final List<Delivery.DeliveryStatus> statuses,
                                                  final boolean sampled) {

        final QDelivery qDelivery = QDelivery.delivery;
        final QSample qSample = QSample.sample;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qLibrary = QLibrary.library;

        final BooleanBuilder builder = new BooleanBuilder();

        // Bibliothèques
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qLibrary.identifier.in(libraries));
        }
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }
        if (CollectionUtils.isNotEmpty(lots)) {
            builder.and(qLot.identifier.in(lots));
        }
        // Statuts
        if (CollectionUtils.isNotEmpty(statuses)) {
            builder.and(qDelivery.status.in(statuses));
        }
        if (fromDate != null) {
            builder.and(qDelivery.depositDate.after(fromDate));
        }

        // Requête
        if (sampled) {
            builder.and(qSample.isNotNull());

            final List<Delivery> test = queryFactory.select(qDelivery)
                                                    .from(qSample)
                                                    .leftJoin(qSample.delivery, qDelivery)
                                                    .leftJoin(qDelivery.lot, qLot)
                                                    .leftJoin(qLot.project, qProject)
                                                    .leftJoin(qProject.library, qLibrary)
                                                    .where(builder.getValue())
                                                    .fetch();
            return test;
        } else {
            return queryFactory.select(qDelivery)
                               .from(qDelivery)
                               .leftJoin(qDelivery.lot, qLot)
                               .leftJoin(qLot.project, qProject)
                               .leftJoin(qProject.library, qLibrary)
                               .where(builder.getValue())
                               .fetch();
        }
    }

    @Override
    public List<Delivery> findByProviders(final List<String> libraries,
                                          final List<String> providers,
                                          final List<DeliveryStatus> statuses,
                                          final LocalDate fromDate,
                                          final LocalDate toDate) {
        final QDelivery qDelivery = QDelivery.delivery;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qLibrary = QLibrary.library;

        final BooleanBuilder builder = new BooleanBuilder();

        // Bibliothèques
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qLibrary.identifier.in(libraries));
        }
        // Providers
        if (CollectionUtils.isNotEmpty(providers)) {
            builder.and(qLot.provider.identifier.coalesce(qProject.provider.identifier).in(providers));
        }
        // Statuts
        if (CollectionUtils.isNotEmpty(statuses)) {
            builder.and(qDelivery.status.in(statuses));
        }
        // Dates
        if (fromDate != null) {
            builder.and(new BooleanBuilder().or(qDelivery.receptionDate.isNull()).or(qDelivery.receptionDate.after(fromDate)));
        }
        if (toDate != null) {
            builder.and(new BooleanBuilder().or(qDelivery.receptionDate.isNull()).or(qDelivery.receptionDate.before(toDate)));
        }
        // Requête
        return queryFactory.select(qDelivery)
                           .from(qDelivery)
                           .leftJoin(qDelivery.lot, qLot)
                           .leftJoin(qLot.project, qProject)
                           .leftJoin(qProject.library, qLibrary)
                           .where(builder.getValue())
                           .fetch();
    }
}
