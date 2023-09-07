package fr.progilone.pgcn.repository.workflow;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.workflow.QWorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class WorkflowModelRepositoryImpl implements WorkflowModelRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public WorkflowModelRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<WorkflowModel> search(final String search, final String initiale, final List<String> libraries, final Pageable pageable) {

        final QWorkflowModel model = QWorkflowModel.workflowModel;

        final BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.isNotBlank(search)) {
            final BooleanExpression nameFilter = model.name.containsIgnoreCase(search);
            builder.andAnyOf(nameFilter);
        }

        if (libraries != null && !libraries.isEmpty()) {
            final BooleanExpression sitesFilter = model.library.identifier.in(libraries);
            builder.and(sitesFilter);
        }

        // Filter initiale
        QueryDSLBuilderUtils.addFilterForInitiale(builder, initiale, model.name);

        final JPAQuery<WorkflowModel> baseQuery = queryFactory.selectDistinct(model).from(model).where(builder).orderBy(model.name.asc());

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, model);
        }

        final long total = queryFactory.select(model.countDistinct()).from(model).where(builder).fetchOne();
        return new PageImpl<>(baseQuery.fetch(), pageable, total);
    }

    protected JPAQuery<WorkflowModel> applySorting(final Sort sort, final JPAQuery<WorkflowModel> query, final QWorkflowModel model) {

        final List<OrderSpecifier<?>> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (final Sort.Order order : sort) {
            final Order qOrder = order.isAscending() ? Order.ASC
                                                     : Order.DESC;
            if (order.getProperty().equals("name")) {
                orders.add(new OrderSpecifier<>(qOrder, model.name));
            }
        }
        OrderSpecifier<?>[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);

        return query.orderBy(orderArray);
    }
}
