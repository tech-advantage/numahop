package fr.progilone.pgcn.repository.document;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.QBibliographicRecord;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.QPhysicalDocument;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class BibliographicRecordRepositoryImpl implements BibliographicRecordRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(BibliographicRecordRepositoryImpl.class);

    private final JPAQueryFactory queryFactory;

    public BibliographicRecordRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<BibliographicRecord> search(final String search,
                                            final List<String> libraries,
                                            final List<String> projects,
                                            final List<String> lots,
                                            final List<String> statuses,
                                            final List<String> trains,
                                            final LocalDate lastModifiedDateFrom,
                                            final LocalDate lastModifiedDateTo,
                                            final LocalDate createdDateFrom,
                                            final LocalDate createdDateTo,
                                            final Boolean orphan,
                                            final Pageable pageable) {

        final QBibliographicRecord qRecord = QBibliographicRecord.bibliographicRecord;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QLibrary qLibrary = QLibrary.library;
        final QProject qProject = QProject.project;
        final QLot qLot = QLot.lot;
        final QDocUnitWorkflow qWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qState = QDocUnitState.docUnitState;
        final QLibrary qAssociatedLibrary = new QLibrary("associatedLibrary");
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = qRecord.title.containsIgnoreCase(search).or(qDocUnit.pgcnId.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }

        // Projets
        if (CollectionUtils.isNotEmpty(projects)) {
            final BooleanExpression projectsFilter = qDocUnit.project.identifier.in(projects);
            builder.and(projectsFilter);
        }
        // Lots
        if (CollectionUtils.isNotEmpty(lots)) {
            final BooleanExpression lotsFilter = qDocUnit.lot.identifier.in(lots);
            builder.and(lotsFilter);
        }
        // Statuts de workflow
        if (CollectionUtils.isNotEmpty(statuses)) {
            final List<WorkflowStateKey> wkfStates = statuses.stream().map(WorkflowStateKey::valueOf).collect(Collectors.toList());

            final BooleanBuilder statusBuilder = new BooleanBuilder();
            if (statuses.contains(WorkflowStateKey.CLOTURE_DOCUMENT.name())) {
                statuses.remove(WorkflowStateKey.CLOTURE_DOCUMENT.name());
                statusBuilder.and(qState.discriminator.eq(WorkflowStateKey.CLOTURE_DOCUMENT)).and(qState.status.in(WorkflowStateStatus.FINISHED));
                if (CollectionUtils.isNotEmpty(statuses)) {
                    wkfStates.remove(WorkflowStateKey.CLOTURE_DOCUMENT);
                    statusBuilder.or(qState.discriminator.in(wkfStates).and(qState.startDate.isNotNull().and(qState.endDate.isNull())));
                }
            } else {
                final BooleanExpression stateFilter = qState.discriminator.in(wkfStates);
                final BooleanExpression statusFilter = qState.startDate.isNotNull().and(qState.endDate.isNull());
                statusBuilder.and(stateFilter).and(statusFilter);
            }

            builder.and(statusBuilder);
        }
        if (CollectionUtils.isNotEmpty(trains)) {
            final QPhysicalDocument qpd = qDocUnit.physicalDocuments.any();
            final BooleanExpression trainFilter = qpd.isNotNull().and(qpd.train.identifier.in(trains));
            builder.and(trainFilter);
        }
        if (lastModifiedDateFrom != null) {
            final BooleanExpression lastModifiedDateFromFilter = qRecord.lastModifiedDate.after(lastModifiedDateFrom.atStartOfDay());
            builder.and(lastModifiedDateFromFilter);
        }

        if (lastModifiedDateTo != null) {
            final BooleanExpression lastModifiedDateToFilter = qRecord.lastModifiedDate.before(lastModifiedDateTo.atTime(LocalTime.MAX));
            builder.and(lastModifiedDateToFilter);
        }

        if (createdDateFrom != null) {
            final BooleanExpression createdDateFromFilter = qRecord.createdDate.after(createdDateFrom.atStartOfDay());
            builder.and(createdDateFromFilter);
        }

        if (createdDateTo != null) {
            final BooleanExpression createdDateToFilter = qRecord.createdDate.before(createdDateTo.atTime(LocalTime.MAX));
            builder.and(createdDateToFilter);
        }

        // Orphans
        if (orphan != null && orphan) {
            builder.and(qDocUnit.isNull());
        }
        // UD Disponibles ou notices non rattachées
        builder.and(qDocUnit.isNull().or(qDocUnit.state.eq(DocUnit.State.AVAILABLE)));

        // provider
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qLot, qProject, qAssociatedLibrary, qAssociatedUser, libraries, null);

        final long total = queryFactory.select(qRecord.identifier.countDistinct())
                                       .from(qRecord)
                                       .leftJoin(qRecord.docUnit, qDocUnit)
                                       .leftJoin(qRecord.library, qLibrary)
                                       .leftJoin(qDocUnit.project, qProject)
                                       .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                       .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                       .leftJoin(qDocUnit.lot, qLot)
                                       .leftJoin(qDocUnit.workflow, qWorkflow)
                                       .leftJoin(qWorkflow.states, qState)
                                       .where(builder.getValue())
                                       .fetchOne();

        final JPAQuery<BibliographicRecord> baseQuery = queryFactory.selectDistinct(qRecord)
                                                                    .from(qRecord)
                                                                    .leftJoin(qRecord.docUnit, qDocUnit)
                                                                    .fetchJoin()
                                                                    .leftJoin(qRecord.library, qLibrary)
                                                                    .leftJoin(qDocUnit.project, qProject)
                                                                    .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                                                    .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                                                    .leftJoin(qDocUnit.lot, qLot)
                                                                    .leftJoin(qDocUnit.workflow, qWorkflow)
                                                                    .leftJoin(qWorkflow.states, qState)
                                                                    .where(builder.getValue());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, qRecord, qDocUnit, qProject, qLot);
        }

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    /**
     * Gère le tri
     *
     * @param sort
     * @param query
     * @param bib
     * @param doc
     * @param project
     * @param lot
     * @return
     */
    protected JPAQuery<BibliographicRecord> applySorting(final Sort sort,
                                                         final JPAQuery<BibliographicRecord> query,
                                                         final QBibliographicRecord bib,
                                                         final QDocUnit doc,
                                                         final QProject project,
                                                         final QLot lot) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                                     : Order.DESC;

            switch (order.getProperty()) {
                case "docUnit.pgcnId":
                    orders.add(new OrderSpecifier<>(qOrder, doc.pgcnId));
                    break;
                case "docUnit.label":
                    orders.add(new OrderSpecifier<>(qOrder, doc.label));
                    break;
                case "title":
                    orders.add(new OrderSpecifier<>(qOrder, bib.title));
                    break;
                case "project.name":
                    orders.add(new OrderSpecifier<>(qOrder, project.name));
                    break;
                case "lot.label":
                    orders.add(new OrderSpecifier<>(qOrder, lot.label));
                    break;
                default:
                    LOG.warn("Tri non implémenté: {}", order.getProperty());
                    break;
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);
        return query.orderBy(orderArray);
    }
}
