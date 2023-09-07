package fr.progilone.pgcn.repository.document;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import fr.progilone.pgcn.domain.user.QUser;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
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
import org.springframework.data.domain.Sort;

public class DigitalDocumentRepositoryImpl implements DigitalDocumentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DigitalDocumentRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

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
                                        final boolean relivraison,
                                        final String searchPgcnId,
                                        final String searchTitre,
                                        final String searchRadical,
                                        final List<String> searchFileFormats,
                                        final List<String> searchMaxAngles,
                                        final Integer searchPageFrom,
                                        final Integer searchPageTo,
                                        final Long searchPageCheckFrom,
                                        final Long searchPageCheckTo,
                                        final Double searchMinSize,
                                        final Double searchMaxSize,
                                        final boolean validated,
                                        final Pageable pageable) {

        final QDigitalDocument doc = QDigitalDocument.digitalDocument;
        final QProject project = QProject.project;
        final QLibrary library = QLibrary.library;
        final QLot lot = QLot.lot;
        final QDocUnit docUnit = QDocUnit.docUnit;
        final QDocUnitWorkflow workflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState docUnitState = QDocUnitState.docUnitState;
        final QDelivery delivery = QDelivery.delivery;
        final QDeliveredDocument deliveredDocument = QDeliveredDocument.deliveredDocument;
        final QLibrary qAssociatedLibrary = new QLibrary("associatedLibrary");
        final QUser qAssociatedUser = QUser.user;

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
        QueryDSLBuilderUtils.addAccessFilters(builder, library, lot, project, qAssociatedLibrary, qAssociatedUser, libraries, null);

        if (CollectionUtils.isNotEmpty(deliveries)) {
            builder.and(delivery.identifier.in(deliveries));
        } else {
            if (CollectionUtils.isNotEmpty(projects)) {
                final BooleanExpression projectFilter = doc.docUnit.project.identifier.in(projects);
                builder.and(projectFilter);
            }
            if (CollectionUtils.isNotEmpty(lots)) {
                final BooleanExpression lotFilter = lot.identifier.in(lots);
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

            final JPQLQuery<String> subCondRep = JPAExpressions.select(report.docUnit.identifier)
                                                               .from(report)
                                                               .innerJoin(report.details, detail)
                                                               .innerJoin(detail.descriptions, desc)
                                                               .where(desc.property.identifier.eq("MAX_ANGLE").and(desc.value.identifier.in(searchMaxAngles)));

            builder.and(new BooleanBuilder().and(docUnit.identifier.in(subCondRep)));
        }

        if (relivraison) {
            final BooleanExpression stateFilter = docUnitState.discriminator.eq(WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS);
            builder.and(stateFilter);
            builder.and(docUnitState.startDate.isNotNull());
            builder.and(docUnitState.endDate.isNull());
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
            final BooleanExpression limitFromFilter = docUnit.checkEndTime.after(dateLimitFrom.minusDays(1));
            builder.and(limitFromFilter);
        }
        if (dateLimitTo != null) {
            final BooleanExpression limitToFilter = docUnit.checkEndTime.before(dateLimitTo.plusDays(1));
            builder.and(limitToFilter);
        }
        if (searchPageFrom > 0) {
            final BooleanExpression pgFromFilter = docUnit.physicalDocuments.any().totalPage.gt((searchPageFrom));
            builder.and(pgFromFilter);
        }
        if (searchPageTo > 0) {
            final BooleanExpression pgToFilter = docUnit.physicalDocuments.any().totalPage.lt(searchPageTo);
            builder.and(pgToFilter);
        }
        if (searchPageCheckFrom > 0 || searchPageCheckTo > 0) {
            final QDocPage docPage = QDocPage.docPage;
            final JPQLQuery<Long> subNotChecked = JPAExpressions.select(docPage.count())
                                                                .where(docPage.digitalDocument.eq(doc).and(docPage.status.isNull()).and(docPage.number.isNotNull()));
            if (searchPageCheckFrom > 0) {
                builder.and(subNotChecked.goe(searchPageCheckFrom));
            }
            if (searchPageCheckTo > 0) {
                builder.and(subNotChecked.loe(searchPageCheckTo));
            }
        }
        if (searchMinSize > 0) {
            // poids total des masters
            final BooleanExpression lengthFromFilter = doc.totalLength.gt((searchMinSize * 1024));
            builder.and(lengthFromFilter);
        }
        if (searchMaxSize > 0) {
            // poids total des masters
            final BooleanExpression lengthToFilter = doc.totalLength.lt((searchMaxSize * 1024));
            builder.and(lengthToFilter);
        }

        final JPAQuery<DigitalDocument> baseQuery = queryFactory.selectDistinct(doc)
                                                                .from(doc)
                                                                .leftJoin(doc.docUnit, docUnit)
                                                                .leftJoin(docUnit.lot, lot)
                                                                .leftJoin(lot.deliveries, delivery)
                                                                .leftJoin(docUnit.library, library)
                                                                .leftJoin(docUnit.project, project)
                                                                .leftJoin(project.associatedLibraries, qAssociatedLibrary)
                                                                .leftJoin(project.associatedUsers, qAssociatedUser)
                                                                .leftJoin(docUnit.workflow, workflow)
                                                                .leftJoin(workflow.states, docUnitState)
                                                                .leftJoin(doc.deliveries, deliveredDocument)
                                                                .where(builder.getValue())
                                                                .orderBy(doc.digitalId.asc());

        final long total = baseQuery.clone().select(doc.countDistinct()).fetchOne();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, doc, docUnit);
        }

        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    protected JPAQuery<DigitalDocument> applySorting(final Sort sort, final JPAQuery<DigitalDocument> query, final QDigitalDocument doc, final QDocUnit docUnit) {

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
