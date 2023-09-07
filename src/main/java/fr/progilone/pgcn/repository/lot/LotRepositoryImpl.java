package fr.progilone.pgcn.repository.lot;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.dto.lot.QSimpleLotDTO;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.repository.lot.helper.LotSearchBuilder;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LotRepositoryImpl implements LotRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public LotRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * récupère les lots attachés aux projets.
     */
    @Override
    public List<SimpleLotDTO> findAllIdentifiersInProjectIds(final Collection<String> projectIds) {
        final QLot lot = QLot.lot;
        final QProject project = QProject.project;
        return queryFactory.select(new QSimpleLotDTO(lot.identifier, lot.label)).from(lot).innerJoin(lot.project, project).where(project.identifier.in(projectIds)).fetch();
    }

    @Override
    public List<Object[]> getLotGroupByStatus(final List<String> libraries, final List<String> projects) {
        final QLibrary qLibrary = QLibrary.library;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qAssociatedLibrary = QLibrary.library;
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();
        // provider, library
        QueryDSLBuilderUtils.addAccessFilters(builder, qLibrary, qLot, qProject, qAssociatedLibrary, qAssociatedUser, libraries, null);

        // project
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }

        // query
        return queryFactory.select(qLot.status, qLot.identifier.countDistinct())
                           .from(qLot)
                           .leftJoin(qLot.project, qProject)
                           .leftJoin(qProject.library)
                           .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                           .leftJoin(qProject.associatedUsers, qAssociatedUser)
                           .where(builder)
                           .groupBy(qLot.status)
                           .stream()
                           .map(Tuple::toArray)
                           .collect(Collectors.toList());
    }

    @Override
    public Page<Lot> search(final LotSearchBuilder searchBuilder, final Pageable pageable) {

        final QLibrary qLibrary = QLibrary.library;
        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qAssociatedLibrary = QLibrary.library;
        final QUser qAssociatedUser = QUser.user;

        final BooleanBuilder builder = new BooleanBuilder();

        // Libellé du lot
        searchBuilder.getSearch().ifPresent(search -> {
            final BooleanExpression nameFilter = qLot.label.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        });

        // active
        if (searchBuilder.isActive()) {
            builder.and(qLot.active.eq(true));
        }
        // provider, library
        QueryDSLBuilderUtils.addAccessFilters(builder,
                                              qLibrary,
                                              qLot,
                                              qProject,
                                              qAssociatedLibrary,
                                              qAssociatedUser,
                                              searchBuilder.getLibraries().orElse(null),
                                              searchBuilder.getProviders().orElse(null));

        // project
        searchBuilder.getProjects().ifPresent(projects -> {
            builder.and(qProject.identifier.in(projects));
        });
        // status
        searchBuilder.getLotStatuses().ifPresent(lotStatuses -> {
            builder.and(qLot.status.in(lotStatuses));
        });
        // docNumber
        searchBuilder.getDocNumber().ifPresent(docNumber -> {
            builder.and(qLot.docUnits.size().eq(docNumber));
        });
        // fileFormat
        searchBuilder.getFileFormats().ifPresent(fileFormats -> {
            builder.and(qLot.requiredFormat.in(fileFormats));
        });
        // Date de la dernière livraison
        if (searchBuilder.getLastDlvFrom().isPresent() || searchBuilder.getLastDlvTo().isPresent()) {
            final QDeliveredDocument qDeliveredDocument = QDeliveredDocument.deliveredDocument;
            final QDigitalDocument qDigitalDocument = QDigitalDocument.digitalDocument;
            final QDocUnit qDocUnit = QDocUnit.docUnit;

            final BooleanBuilder subFilter = new BooleanBuilder().and(qDocUnit.lot.eq(qLot));
            searchBuilder.getLastDlvFrom().ifPresent(fromDate -> subFilter.and(qDeliveredDocument.deliveryDate.goe(fromDate)));
            searchBuilder.getLastDlvTo().ifPresent(toDate -> subFilter.and(qDeliveredDocument.deliveryDate.loe(toDate)));

            builder.and(JPAExpressions.select(qDeliveredDocument)
                                      .from(qDeliveredDocument)
                                      .innerJoin(qDeliveredDocument.digitalDocument, qDigitalDocument)
                                      .innerJoin(qDigitalDocument.docUnit, qDocUnit)
                                      .where(subFilter)
                                      .exists());
        }
        // identifiers
        searchBuilder.getIdentifiers().ifPresent(identifiers -> {
            builder.and(qLot.identifier.in(identifiers));
        });

        JPAQuery<Lot> baseQuery = queryFactory.selectDistinct(qLot)
                                              .from(qLot)
                                              .leftJoin(qLot.project, qProject)
                                              .leftJoin(qProject.associatedLibraries, qAssociatedLibrary)
                                              .leftJoin(qProject.associatedUsers, qAssociatedUser)
                                              .leftJoin(qProject.library)
                                              .where(builder.getValue());

        if (pageable != null) {
            final long total = baseQuery.clone().select(qLot.countDistinct()).fetchOne();
            baseQuery = baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            if (pageable.getSort() != null) {
                applySorting(pageable.getSort(), baseQuery, qLot, qProject);
            } else {
                baseQuery.orderBy(qLibrary.name.asc(), qProject.name.asc(), qLot.label.asc());
            }

            return new PageImpl<>(baseQuery.fetch(), pageable, total);

        } else {
            baseQuery.orderBy(qLibrary.name.asc(), qProject.name.asc(), qLot.label.asc());
            return new PageImpl<>(baseQuery.fetch());
        }
    }

    @Override
    public List<Lot> findLotsForWidget(final LocalDate fromDate, final List<String> libraries, final List<String> projects, final List<Lot.LotStatus> statuses) {

        final QLot qLot = QLot.lot;
        final QProject qProject = QProject.project;
        final QLibrary qLibrary = QLibrary.library;

        final BooleanBuilder builder = new BooleanBuilder();
        if (CollectionUtils.isNotEmpty(libraries)) {
            builder.and(qLibrary.identifier.in(libraries));
        }
        if (CollectionUtils.isNotEmpty(projects)) {
            builder.and(qProject.identifier.in(projects));
        }
        if (CollectionUtils.isNotEmpty(statuses)) {
            builder.and(qLot.status.in(statuses));
        }
        if (fromDate != null) {
            builder.and(qLot.createdDate.after(fromDate.atStartOfDay()));
        }

        return queryFactory.select(qLot).from(qLot).leftJoin(qLot.project, qProject).leftJoin(qProject.library, qLibrary).where(builder).fetch();
    }

    /**
     * Gère le tri
     *
     * @param sort
     *            Sort
     * @param query
     *            JPQLQuery
     * @param lot
     *            QLot
     * @return JPQLQuery
     */
    protected JPAQuery<Lot> applySorting(final Sort sort, final JPAQuery<Lot> query, final QLot lot, final QProject project) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                                     : Order.DESC;

            switch (order.getProperty()) {
                case "label":
                    orders.add(new OrderSpecifier<>(qOrder, lot.label));
                    break;
                case "project.name":
                    orders.add(new OrderSpecifier<>(qOrder, project.name));
                    break;
                case "status":
                    orders.add(new OrderSpecifier<>(qOrder, lot.status));
                    break;
                case "type":
                    orders.add(new OrderSpecifier<>(qOrder, lot.type));
                    break;
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);
        return query.orderBy(orderArray);
    }
}
