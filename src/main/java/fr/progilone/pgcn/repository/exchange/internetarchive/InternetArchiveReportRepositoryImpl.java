package fr.progilone.pgcn.repository.exchange.internetarchive;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.QInternetArchiveReport;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;

public class InternetArchiveReportRepositoryImpl implements InternetArchiveReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public InternetArchiveReportRepositoryImpl(final JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public List<InternetArchiveReport> findAll(final List<String> libraries, final LocalDate fromDate, final boolean failures) {
        final QInternetArchiveReport qIaReport = QInternetArchiveReport.internetArchiveReport;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        final BooleanBuilder builder = new BooleanBuilder();

        // libraries
        if (CollectionUtils.isNotEmpty(libraries)) {
            final BooleanExpression libFilter = qDocUnit.library.identifier.in(libraries);
            builder.and(libFilter);
        }
        // Date
        builder.and(qIaReport.dateSent.goe(fromDate.atStartOfDay()));
        // failures
        builder.and(qIaReport.status.eq(failures ? InternetArchiveReport.Status.FAILED
                                                 : InternetArchiveReport.Status.ARCHIVED));

        return queryFactory.select(qIaReport).from(qIaReport).innerJoin(qIaReport.docUnit, qDocUnit).fetchJoin().where(builder).orderBy(qIaReport.dateSent.desc()).fetch();
    }
}
