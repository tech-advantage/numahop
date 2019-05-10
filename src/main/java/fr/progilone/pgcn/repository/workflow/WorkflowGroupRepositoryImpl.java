package fr.progilone.pgcn.repository.workflow;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import fr.progilone.pgcn.domain.workflow.QWorkflowGroup;
import fr.progilone.pgcn.domain.workflow.WorkflowGroup;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;

public class WorkflowGroupRepositoryImpl implements WorkflowGroupRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<WorkflowGroup> search(String search,
            final String initiale,
                                        List<String> libraries,
                                        Pageable pageable) {

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

        JPQLQuery baseQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, group);
        }
        

        List<WorkflowGroup> result = baseQuery.from(group)
                                                .where(builder.getValue())
                                                .orderBy(group.name.asc())
                                                .distinct()
                                                .list(group);

        final long total = baseQuery.count();
        return new PageImpl<>(result, pageable, total);
    }
    
    protected JPQLQuery applySorting(Sort sort, JPQLQuery query, QWorkflowGroup group) {

        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (Sort.Order order : sort) {
            Order qOrder = order.isAscending() ? Order.ASC
                                               : Order.DESC;
            if (order.getProperty().equals("name")) {
                orders.add(new OrderSpecifier(qOrder, group.name));
            }
        }
        OrderSpecifier[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);

        return query.orderBy(orderArray);
    }
}
