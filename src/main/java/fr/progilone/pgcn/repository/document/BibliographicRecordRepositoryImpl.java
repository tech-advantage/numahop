package fr.progilone.pgcn.repository.document;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.QBibliographicRecord;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BibliographicRecordRepositoryImpl implements BibliographicRecordRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(BibliographicRecordRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<BibliographicRecord> search(final String search,
                                            final List<String> libraries,
                                            final List<String> projects,
                                            final List<String> lots,
                                            final LocalDate lastModifiedDateFrom,
                                            final LocalDate lastModifiedDateTo,
                                            final LocalDate createdDateFrom,
                                            final LocalDate createdDateTo,
                                            final Boolean orphan,
                                            final Pageable pageable) {

        final QBibliographicRecord qRecord = QBibliographicRecord.bibliographicRecord;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QProject qProject = QProject.project;
        final QLot qLot = QLot.lot;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = qRecord.title.containsIgnoreCase(search).or(qDocUnit.pgcnId.containsIgnoreCase(search));
            builder.andAnyOf(nameFilter);
        }

        // Bibliothèques
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression sitesFilter = qRecord.library.identifier.in(libraries);
            builder.and(sitesFilter);
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
        builder.and(qDocUnit.state.isNull().or(qDocUnit.state.eq(DocUnit.State.AVAILABLE)));

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, qRecord, qDocUnit, qProject, qLot);
        }

        final long total = countQuery.from(qRecord).leftJoin(qRecord.docUnit, qDocUnit).where(builder.getValue()).distinct().count();

        final List<BibliographicRecord> result = baseQuery.from(qRecord)
                                                          .leftJoin(qRecord.docUnit, qDocUnit)
                                                          .fetch()
                                                          .leftJoin(qDocUnit.project, qProject)
                                                          .leftJoin(qDocUnit.lot, qLot)
                                                          .where(builder.getValue())
                                                          .orderBy(qRecord.title.asc())
                                                          .distinct()
                                                          .list(qRecord);
        return new PageImpl<>(result, pageable, total);
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
    protected JPQLQuery applySorting(Sort sort, JPQLQuery query, QBibliographicRecord bib, QDocUnit doc, QProject project, QLot lot) {

        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (Sort.Order order : sort) {
            Order qOrder = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "docUnit.pgcnId":
                    orders.add(new OrderSpecifier(qOrder, doc.pgcnId));
                    break;
                case "title":
                    orders.add(new OrderSpecifier(qOrder, bib.title));
                    break;
                case "project.name":
                    orders.add(new OrderSpecifier(qOrder, project.name));
                    break;
                case "lot.label":
                    orders.add(new OrderSpecifier(qOrder, lot.label));
                    break;
                default:
                    LOG.warn("Tri non implémenté: {}", order.getProperty());
                    break;
            }
        }
        OrderSpecifier[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);
        return query.orderBy(orderArray);
    }
}
