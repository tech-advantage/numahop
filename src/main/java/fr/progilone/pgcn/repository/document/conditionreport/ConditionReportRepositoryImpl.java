package fr.progilone.pgcn.repository.document.conditionreport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.NumberExpression;

import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.QConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.QDescription;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;

public class ConditionReportRepositoryImpl implements ConditionReportRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<String> search(final List<String> libraries,
                               final LocalDate from,
                               final LocalDate to,
                               final DimensionFilter dimensions,
                               final Map<String, List<String>> descriptions,
                               final List<String> docIdentifiers,
                               final boolean toValidateOnly,
                               final Pageable pageable) {

        // Initialisation des objets querydsl
        final QConditionReport qReport = QConditionReport.conditionReport;
        final QConditionReportDetail qDetail = QConditionReportDetail.conditionReportDetail;
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final Map<String, QDescription> qDescriptions = descriptions.keySet()
                                                                    .stream()
                                                                    .collect(Collectors.toMap(UnaryOperator.identity(),
                                                                                              property -> new QDescription("desc_"
                                                                                                                           + property.replaceAll(
                                                                                                  "[^a-zA-Z0-9]",
                                                                                                  "").toLowerCase())));

        final QDocUnitWorkflow qWorkflow = QDocUnitWorkflow.docUnitWorkflow;
        final QDocUnitState qState = QDocUnitState.docUnitState;
        
        // Construction du filtre
        final BooleanBuilder builder = new BooleanBuilder();

        // Bibliothèques
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression sitesFilter = qDocUnit.library.identifier.in(libraries);
            builder.and(sitesFilter);
        }

        // Dimensions
        if (dimensions != null && !dimensions.isEmpty()) {
            if (dimensions.getDim1() > 0) {
                builder.and(getDimensionExpression(qDetail.dim1, dimensions.getOp(), dimensions.getDim1()));
            }
            if (dimensions.getDim2() > 0) {
                builder.and(getDimensionExpression(qDetail.dim2, dimensions.getOp(), dimensions.getDim2()));
            }
            if (dimensions.getDim3() > 0) {
                builder.and(getDimensionExpression(qDetail.dim3, dimensions.getOp(), dimensions.getDim3()));
            }
        }

        // Reliures
        for (final Map.Entry<String, List<String>> entry : descriptions.entrySet()) {
            final String property = entry.getKey();
            final List<String> values = entry.getValue();
            final QDescription qDescription = qDescriptions.get(property);

            builder.and(new BooleanBuilder().and(qDescription.property.identifier.eq(property)).and(qDescription.value.identifier.in(values)));
        }

        // Dates
        if (from != null) {
            builder.and(qDetail.date.goe(from));
        }
        if (to != null) {
            builder.and(qDetail.date.loe(to));
        }

        // UD
        if (CollectionUtils.isNotEmpty(docIdentifiers)) {
            builder.and(qDocUnit.identifier.in(docIdentifiers));
        }
        
        // A valider
        if (toValidateOnly) { 
            builder.and(qState.status.eq(WorkflowStateStatus.PENDING))
                    .and(qState.discriminator.in(WorkflowStateKey.VALIDATION_CONSTAT_ETAT, WorkflowStateKey.CONSTAT_ETAT_AVANT_NUMERISATION, WorkflowStateKey.CONSTAT_ETAT_APRES_NUMERISATION));
        }

        // Requêtes
        final JPAQuery query = getBaseQuery(qReport, qDetail, qDocUnit, qDescriptions.values(), qWorkflow, qState, builder);
        final JPAQuery countQuery = getBaseQuery(qReport, qDetail, qDocUnit, qDescriptions.values(), qWorkflow, qState, builder);

        if (pageable != null) {
            query.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), query, qReport, qDetail);
        }

        final long total = countQuery.count();
        final List<String> identifiers = query.list(qReport.identifier);

        return new PageImpl<>(identifiers, pageable, total);
    }

    private JPAQuery getBaseQuery(final QConditionReport qReport,
                                  final QConditionReportDetail qDetail,
                                  final QDocUnit qDocUnit,
                                  final Collection<QDescription> qDescriptions,
                                  final QDocUnitWorkflow qWorkflow,
                                  final QDocUnitState qState,
                                  final BooleanBuilder filter) {
        final JPAQuery query = new JPAQuery(em).from(qReport).innerJoin(qReport.details, qDetail).innerJoin(qReport.docUnit, qDocUnit)
                                    .leftJoin(qDocUnit.workflow, qWorkflow).leftJoin(qWorkflow.states, qState);

        for (final QDescription qDescription : qDescriptions) {
            query.innerJoin(qDetail.descriptions, qDescription);
        }
        return query.where(filter.getValue()).distinct();
    }

    private <T extends Number & Comparable<?>> BooleanExpression getDimensionExpression(final NumberExpression<T> expression,
                                                                                        final DimensionFilter.Operator op,
                                                                                        final T value) {
        if (op != null) {
            switch (op) {
                case LTE:
                    return expression.loe(value);
                case GTE:
                    return expression.goe(value);
                case EQ:
                    return expression.eq(value);
            }
        }
        return expression.eq(value);
    }

    private JPAQuery applySorting(final Sort sort, final JPAQuery query, final QConditionReport qReport, final QConditionReportDetail qDetail) {

        final List<OrderSpecifier<String>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC : Order.DESC;

            switch (order.getProperty()) {
                case "pgcnId":
                case "docUnit.pgcnId":
                    orders.add(new OrderSpecifier<>(qOrder, qReport.docUnit.pgcnId));
                    break;
                case "docUnit.label":
                    orders.add(new OrderSpecifier<>(qOrder, qReport.docUnit.label));
                    break;
                case "docUnit.project.name":
                    orders.add(new OrderSpecifier<>(qOrder, qReport.docUnit.project.name));
                    break;
                case "docUnit.lot.label":
                    orders.add(new OrderSpecifier<>(qOrder, qReport.docUnit.lot.label));
                    break;
                case "docUnit.parent.pgcnId":
                    orders.add(new OrderSpecifier<>(qOrder, qReport.docUnit.parent.pgcnId));
                    break;
                default:
                    LOG.warn("Tri non implémenté: {}", order.getProperty());
                    break;
            }
        }
        OrderSpecifier<String> orderArray[] = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);
        return query.orderBy(orderArray);
    }
}
