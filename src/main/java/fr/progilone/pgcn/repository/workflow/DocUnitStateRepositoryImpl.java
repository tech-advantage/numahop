package fr.progilone.pgcn.repository.workflow;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.mysema.query.jpa.impl.JPAQuery;

import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitState;
import fr.progilone.pgcn.domain.workflow.QDocUnitWorkflow;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowHelper;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;

public class DocUnitStateRepositoryImpl implements DocUnitStateRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<DocUnitState> findDocUnitStates(final DocUnitWorkflowSearchBuilder searchBuilder, final Pageable pageable) {
        final QDocUnit qDocUnit = QDocUnit.docUnit;
        final QDocUnitState qDocUnitState = QDocUnitState.docUnitState;
        final QDocUnitWorkflow qDocUnitWorkflow = QDocUnitWorkflow.docUnitWorkflow;

        JPAQuery query =
            new JPAQuery(em).from(qDocUnitState).innerJoin(qDocUnitState.workflow, qDocUnitWorkflow).innerJoin(qDocUnitWorkflow.docUnit, qDocUnit);
        query = DocUnitWorkflowHelper.getFindDocUnitWorkflowQuery(query, searchBuilder, qDocUnit, qDocUnitWorkflow, qDocUnitState);

        final long total = query.count();
        final List<DocUnitState> content = query.offset(pageable.getOffset())
                                                .limit(pageable.getPageSize())
                                                .orderBy(qDocUnit.library.name.asc(),
                                                         qDocUnit.project.name.asc(),
                                                         qDocUnit.lot.label.asc(),
                                                         qDocUnit.pgcnId.asc(),
                                                         qDocUnitState.endDate.asc())
                                                .list(qDocUnitState);
        return new PageImpl<>(content, pageable, total);
    }
}
