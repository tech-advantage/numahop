package fr.progilone.pgcn.repository.exchange.internetarchive;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.QInternetArchiveReport;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

public class InternetArchiveReportRepositoryImpl implements InternetArchiveReportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

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
        builder.and(qIaReport.status.eq(failures ? InternetArchiveReport.Status.FAILED : InternetArchiveReport.Status.SENT));

        // Query
        final JPQLQuery query = new JPAQuery(em);

        return query.from(qIaReport)
                    .innerJoin(qIaReport.docUnit, qDocUnit).fetch()
                    .where(builder)
                    .orderBy(qIaReport.dateSent.desc())
                    .list(qIaReport);
    }
}
