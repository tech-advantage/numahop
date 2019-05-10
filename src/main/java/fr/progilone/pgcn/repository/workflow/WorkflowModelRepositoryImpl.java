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

import fr.progilone.pgcn.domain.workflow.QWorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.repository.util.QueryDSLBuilderUtils;

public class WorkflowModelRepositoryImpl implements WorkflowModelRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<WorkflowModel> search(String search,
            final String initiale,
                                        List<String> libraries,
                                        Pageable pageable) {

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

        JPQLQuery baseQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset())
                     .limit(pageable.getPageSize());
            applySorting(pageable.getSort(), baseQuery, model);
        }
        

        List<WorkflowModel> result = baseQuery.from(model)
                                                .where(builder.getValue())
                                                .orderBy(model.name.asc())
                                                .distinct()
                                                .list(model);

        final long total = baseQuery.count();
        return new PageImpl<>(result, pageable, total);
    }
    
    protected JPQLQuery applySorting(Sort sort, JPQLQuery query, QWorkflowModel model) {

        List<OrderSpecifier> orders = new ArrayList<>();
        if (sort == null) {
            return query;
        }

        for (Sort.Order order : sort) {
            Order qOrder = order.isAscending() ? Order.ASC
                                               : Order.DESC;
            if (order.getProperty().equals("name")) {
                orders.add(new OrderSpecifier(qOrder, model.name));
            }
        }
        OrderSpecifier[] orderArray = new OrderSpecifier[orders.size()];
        orderArray = orders.toArray(orderArray);

        return query.orderBy(orderArray);
    }
}
