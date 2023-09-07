package fr.progilone.pgcn.repository.workflow;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowHelper;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class DocUnitStateRepositoryImpl implements DocUnitStateRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DocUnitStateRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<DocUnitState> findDocUnitStates(final DocUnitWorkflowSearchBuilder searchBuilder, final Pageable pageable) {
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;

        JPAQuery<DocUnitState> query = queryFactory.select(qDocUnitState)
                                                   .from(qDocUnitState)
                                                   .innerJoin(qDocUnitState.workflow, qDocUnitWorkflow)
                                                   .innerJoin(qDocUnitWorkflow.docUnit, qDocUnit);
        query = DocUnitWorkflowHelper.getFindDocUnitWorkflowQuery(query, searchBuilder, qDocUnit, qDocUnitWorkflow, qDocUnitState);

        final long total = query.clone().select(qDocUnitState.countDistinct()).fetchOne();
        final List<DocUnitState> content = query.offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .orderBy(qDocUnit.library.name.asc(),
                                                         qDocUnit.project.name.asc(),
                                                         qDocUnit.lot.label.asc(),
                                                         qDocUnit.pgcnId.asc(),
                                                         qDocUnitState.endDate.asc())
                                                .fetch();
        return new PageImpl<>(content, pageable, total);
    }
}
