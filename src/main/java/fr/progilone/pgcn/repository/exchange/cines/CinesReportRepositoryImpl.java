package fr.progilone.pgcn.repository.exchange.cines;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.QCinesReport;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class CinesReportRepositoryImpl implements CinesReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CinesReportRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<CinesReport> findAll(final List<String> libraries, final LocalDate fromDate, final boolean failures) {
        final QCinesReport qCinesReport = QCinesReport.cinesReport;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        // libraries
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression libFilter = qDocUnit.library.identifier.in(libraries);
            builder.and(libFilter);
        }
        // Date
        builder.and(qCinesReport.dateSent.goe(fromDate.atStartOfDay()));
        // failures
        builder.and(qCinesReport.status.eq(failures ? CinesReport.Status.FAILED
                                                    : CinesReport.Status.ARCHIVED));

        return queryFactory.select(qCinesReport)
                           .from(qCinesReport)
                           .innerJoin(qCinesReport.docUnit, qDocUnit)
                           .fetchJoin()
                           .where(builder)
                           .orderBy(qCinesReport.dateSent.desc())
                           .fetch();
    }
}
