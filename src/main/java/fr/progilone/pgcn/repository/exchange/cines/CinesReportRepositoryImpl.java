package fr.progilone.pgcn.repository.exchange.cines;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.cines.QCinesReport;
import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;

public class CinesReportRepositoryImpl implements CinesReportRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

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
        builder.and(qCinesReport.status.eq(failures ? CinesReport.Status.FAILED : CinesReport.Status.ARCHIVED));

        // Query
        final JPQLQuery query = new JPAQuery(em);

        return query.from(qCinesReport)
                    .innerJoin(qCinesReport.docUnit, qDocUnit)
                    .fetch()
                    .where(builder)
                    .orderBy(qCinesReport.dateSent.desc())
                    .list(qCinesReport);
    }
}
