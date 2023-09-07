package fr.progilone.pgcn.repository.exchange;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.QImportReport;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class ImportReportRepositoryImpl implements ImportReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ImportReportRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ImportReport> search(final String search, final List<String> users, final List<ImportReport.Status> status, final List<String> libraries, final Pageable pageable) {

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

        final JPAQuery<ImportReport> baseQuery = queryFactory.select(importReport).from(importReport).where(builder.getValue());
        final long total = baseQuery.clone().select(importReport.countDistinct()).fetchOne();

        if (pageable != null) {
            baseQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());
        }

        final List<ImportReport> result = baseQuery.orderBy(importReport.lastModifiedDate.desc()).distinct().fetch();

        return new PageImpl<>(result, pageable, total);
    }
}
