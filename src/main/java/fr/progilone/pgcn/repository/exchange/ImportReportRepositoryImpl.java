package fr.progilone.pgcn.repository.exchange;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.QImportReport;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class ImportReportRepositoryImpl implements ImportReportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<ImportReport> search(String search, List<String> users, List<ImportReport.Status> status, List<String> libraries, Pageable pageable) {

        final QImportReport importReport = QImportReport.importReport;

        final BooleanBuilder builder = new BooleanBuilder();

        // users
        if (CollectionUtils.isNotEmpty(users)) {
            final BooleanExpression userFilter = importReport.runBy.in(users);
            builder.and(userFilter);
        }

        // status
        if (CollectionUtils.isNotEmpty(status)) {
            final BooleanExpression statusFilter = importReport.status.in(status);
            builder.and(statusFilter);
        }

        // libraries
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression libFilter = importReport.library.identifier.in(libraries);
            builder.and(libFilter);
        }

        final JPQLQuery baseQuery = new JPAQuery(em);
        final JPQLQuery countQuery = new JPAQuery(em);

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<String> deliveriesIdentifiers =
            countQuery.from(importReport).groupBy(importReport.identifier).where(builder.getValue()).distinct().list(importReport.identifier);
        final long total = deliveriesIdentifiers.size();

        final List<ImportReport> result =
            baseQuery.from(importReport).where(builder.getValue()).orderBy(importReport.lastModifiedDate.desc()).distinct().list(importReport);

        return new PageImpl<>(result, pageable, total);
    }
}
