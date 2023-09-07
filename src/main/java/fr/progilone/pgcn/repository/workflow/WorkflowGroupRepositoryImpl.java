package fr.progilone.pgcn.repository.workflow;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.workflow.QWorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class WorkflowGroupRepositoryImpl implements WorkflowGroupRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WorkflowGroupRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<WorkflowGroup> search(final String search, final String initiale, final List<String> libraries, final Pageable pageable) {

        final QWorkflowGroup group = QWorkflowGroup.workflowGroup;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = group.name.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }

        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression sitesFilter = group.library.identifier.in(libraries);
            builder.and(sitesFilter);
        }

        // Filter initiale
        QueryDSLBuilderUtils.addFilterForInitiale(builder, initiale, group.name);

        final JPAQuery<WorkflowGroup> baseQuery = queryFactory.selectDistinct(group).from(group).where(builder.getValue());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, group);
        }

        final long total = baseQuery.clone().select(group.countDistinct()).fetchOne();
        return new PageImpl<>(baseQuery.orderBy(group.name.asc()).fetch(), pageable, total);
    }

    protected JPAQuery<WorkflowGroup> applySorting(final Sort sort, final JPAQuery<WorkflowGroup> query, final QWorkflowGroup group) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                                     : Order.DESC;
            if (order.getProperty().equals("name")) {
                orders.add(new OrderSpecifier<>(qOrder, group.name));
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);

        return query.orderBy(orderArray);
    }
}
