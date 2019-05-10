package fr.progilone.pgcn.repository.document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.query.ListSubQuery;
import com.mysema.query.types.query.NumberSubQuery;

import fr.progilone.pgcn.domain.delivery.QDeliveredDocument;
import fr.progilone.pgcn.domain.delivery.QDelivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.QDigitalDocument;
import fr.progilone.pgcn.domain.document.QDocPage;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.QPhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.QDescription;
import fr.progilone.pgcn.domain.library.QLibrary;
import fr.progilone.pgcn.domain.lot.QLot;
import fr.progilone.pgcn.domain.project.QProject;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;

public class DigitalDocumentRepositoryImpl implements DigitalDocumentRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<DigitalDocument> search(final String search,
                                        final List<DigitalDocument.DigitalDocumentStatus> status,
                                        final List<String> libraries,
                                        final List<String> projects,
                                        final List<String> lots,
                                        final List<String> trains,
                                        final List<String> deliveries,
                                        final LocalDate dateFrom,
                                        final LocalDate dateTo,
                                        final LocalDate dateLimitFrom,
                                        final LocalDate dateLimitTo,
                                        final String searchPgcnId,
                                        final String searchTitre,
                                        final String searchRadical,
                                        final List<String> searchFileFormats,
                                        final List<String> searchMaxAngles,
                                        final Integer searchPageFrom,
                                        final Integer searchPageTo,
                                        final Integer searchPageCheckFrom,
                                        final Integer searchPageCheckTo,
                                        final Double searchMinSize,
                                        final Double searchMaxSize,
                                        final boolean validated,
                                        final Pageable pageable) {

        final QDigitalDocument doc = QDigitalDocument.digitalDocument;
        final QProject project = QProject.project;
        final QLibrary library = QLibrary.library;
        final QLot lot = QLot.lot;
        final QDocUnit docUnit = QDocUnit.docUnit;
        final QDelivery delivery = QDelivery.delivery;
        final QDeliveredDocument deliveredDocument = QDeliveredDocument.deliveredDocument; 

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = doc.digitalId.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }
        if (StringUtils.isNotBlank(searchPgcnId)) {
            final BooleanExpression pgcnFilter = doc.docUnit.pgcnId.containsIgnoreCase(searchPgcnId);
            builder.andAnyOf(pgcnFilter);
        }
        if (StringUtils.isNotBlank(searchTitre)) {
            final BooleanExpression titleFilter = doc.docUnit.label.containsIgnoreCase(searchTitre);
            builder.andAnyOf(titleFilter);
        }
        if (StringUtils.isNotBlank(searchRadical)) {
            final BooleanExpression radFilter = doc.digitalId.containsIgnoreCase(searchRadical);
            builder.andAnyOf(radFilter);
        }
        if (CollectionUtils.isNotEmpty(status)) {
            builder.and(doc.status.in(status));
        }
        if (!validated) {
            builder.and(doc.status.ne(DigitalDocument.DigitalDocumentStatus.VALIDATED));
        }

        // provider
        QueryDSLBuilderUtils.addAccessFilters(builder, library, lot, project, libraries, null);

        if (CollectionUtils.isNotEmpty(deliveries)) {
            final BooleanExpression delivFilter = (delivery.identifier.in(deliveries).and(delivery.lot.identifier.eq(doc.docUnit.lot.identifier)));
            builder.and(delivFilter);
        } else {
            if (CollectionUtils.isNotEmpty(projects)) {
                final BooleanExpression projectFilter = doc.docUnit.project.identifier.in(projects);
                builder.and(projectFilter);
            }
            if (CollectionUtils.isNotEmpty(lots)) {
                final BooleanExpression lotFilter = doc.docUnit.lot.identifier.in(lots);
                builder.and(lotFilter);
            }
        }
        if (CollectionUtils.isNotEmpty(trains)) {
            final QPhysicalDocument qpd = doc.docUnit.physicalDocuments.any();
            final BooleanExpression trainFilter = qpd.isNotNull().and(qpd.train.identifier.in(trains));
            builder.and(trainFilter);
        }
        if (CollectionUtils.isNotEmpty(searchFileFormats)) {
            final BooleanExpression fileFormatFilter = lot.requiredFormat.in(searchFileFormats);
            builder.and(fileFormatFilter);
        }

        if (CollectionUtils.isNotEmpty(searchMaxAngles)) {
            final QConditionReport report = QConditionReport.conditionReport;
            final QConditionReportDetail detail = QConditionReportDetail.conditionReportDetail;
            final QDescription desc = new QDescription("desc_max_angle");

            final JPASubQuery subQueryCondRep = new JPASubQuery();
            final ListSubQuery<String> subCondRep = subQueryCondRep.from(report)
                                                    .innerJoin(report.details, detail)
                                                    .innerJoin(detail.descriptions, desc)
                                                    .where(desc.property.identifier.eq("MAX_ANGLE")
                                                           .and(desc.value.identifier.in(searchMaxAngles)))
                                                    .list(report.docUnit.identifier);

            builder.and(new BooleanBuilder().and(docUnit.identifier.in(subCondRep)));
        }

        if (dateFrom != null) {
            final BooleanExpression dateFromFilter = doc.deliveryDate.after(dateFrom.minusDays(1));
            builder.and(dateFromFilter);
        }
        if (dateTo != null) {
            final BooleanExpression dateToFilter = doc.deliveryDate.before(dateTo.plusDays(1));
            builder.and(dateToFilter);
        }
        if (dateLimitFrom != null) {
            final BooleanExpression limitFromFilter = doc.docUnit.checkEndTime.after(dateLimitFrom.minusDays(1));
            builder.and(limitFromFilter);
        }
        if (dateLimitTo != null) {
            final BooleanExpression limitToFilter = doc.docUnit.checkEndTime.before(dateLimitTo.plusDays(1));
            builder.and(limitToFilter);
        }
        if (searchPageFrom > 0 ) {
            final BooleanExpression pgFromFilter = doc.docUnit.physicalDocuments.any().totalPage.gt((searchPageFrom));
            builder.and(pgFromFilter);
        }
        if (searchPageTo > 0 ) {
            final BooleanExpression pgToFilter = doc.docUnit.physicalDocuments.any().totalPage.lt(searchPageTo);
            builder.and(pgToFilter);
        }
        if (searchPageCheckFrom > 0 || searchPageCheckTo > 0) {
            final QDocPage docPage = QDocPage.docPage;
            final JPASubQuery subQueryNotChecked = new JPASubQuery();
            final NumberSubQuery<Long> subNotChecked = subQueryNotChecked.from(docPage)
                                                    .where(docPage.digitalDocument.eq(doc)
                                                           .and(docPage.status.isNull())
                                                           .and(docPage.number.isNotNull()))
                                                           .count();
            if (searchPageCheckFrom > 0 ) {
                builder.and(subNotChecked.intValue().goe(searchPageCheckFrom));
            }
            if (searchPageCheckTo > 0 ) {
                builder.and(subNotChecked.intValue().loe(searchPageCheckTo));
            }
        }
        if (searchMinSize > 0 ) {
           // poids total des masters
           final BooleanExpression lengthFromFilter = doc.totalLength.gt((searchMinSize*1024));
           builder.and(lengthFromFilter);
        }
        if (searchMaxSize > 0 ) {
           // poids total des masters
            final BooleanExpression lengthToFilter = doc.totalLength.lt((searchMaxSize*1024));
            builder.and(lengthToFilter);
        }

        final JPQLQuery baseQuery = new JPAQuery(em);
        if (pageable != null) {
            baseQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, doc, docUnit);
        }

        final List<DigitalDocument> result;
        if (CollectionUtils.isNotEmpty(deliveries)) {
            result = baseQuery.from(doc, delivery)
                    .leftJoin(doc.docUnit, docUnit)
                    .leftJoin(doc.docUnit.library, library)
                    .leftJoin(doc.docUnit.project, project)
                    .leftJoin(doc.deliveries, deliveredDocument)
                    .leftJoin(delivery.lot, lot)
                    .where(builder.getValue())
                    .orderBy(doc.digitalId.asc())
                    .distinct()
                    .list(doc);
        } else {
            result = baseQuery.from(doc)
                    .leftJoin(doc.docUnit, docUnit)
                    .leftJoin(doc.docUnit.library, library)
                    .leftJoin(doc.docUnit.project, project)
                    .leftJoin(doc.docUnit.lot, lot)
                    .leftJoin(doc.deliveries, deliveredDocument)
                    .where(builder.getValue())
                    .orderBy(doc.digitalId.asc())
                    .distinct()
                    .list(doc);
        }

        final long total = baseQuery.count();
        return new PageImpl<>(result, pageable, total);
    }

    protected JPQLQuery applySorting(final Sort sort, final JPQLQuery query, final QDigitalDocument doc, final QDocUnit docUnit) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                               : Order.DESC;
            if ("pgcnId".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, docUnit.pgcnId));
            }
            if ("label".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, docUnit.label));
            }
            if ("status".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, doc.status));
            }
            if ("totalDelivery".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, doc.totalDelivery));
            }
            if ("deliveryDate".equals(order.getProperty())) {
                orders.add(new OrderSpecifier<>(qOrder, doc.deliveryDate));
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);

        return query.orderBy(orderArray);
    }
}
