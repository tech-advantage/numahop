package fr.progilone.pgcn.repository.exchange;

import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.QDocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.QImportedDocUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sébastien on 28/06/2017.
 */
public class ImportedDocUnitRepositoryImpl implements ImportedDocUnitRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<String> findIdentifiersByImportReport(final ImportReport report,
                                                      final List<DocUnit.State> states,
                                                      final boolean withErrors,
                                                      final boolean withDuplicates,
                                                      final Pageable pageable) {
        final QImportedDocUnit qImportedDocUnit = QImportedDocUnit.importedDocUnit;
        final QDocUnit qDocUnit = QDocUnit.docUnit;

        // Requête de comptage
        JPAQuery query = getQueryIdentifiersByImportReport(report, states, withErrors, withDuplicates, qImportedDocUnit, qDocUnit);
        final long count = query.distinct().count();

        // Recherche
        query = getQueryIdentifiersByImportReport(report, states, withErrors, withDuplicates, qImportedDocUnit, qDocUnit);
        // Tri
        query.orderBy(qImportedDocUnit.groupCode.coalesce(qImportedDocUnit.parentDocUnitPgcnId, qImportedDocUnit.docUnitPgcnId).asc(),
                      qImportedDocUnit.groupCode.asc(),
                      qImportedDocUnit.parentDocUnitPgcnId.asc(),
                      qImportedDocUnit.docUnitPgcnId.asc());
        // Page
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());
        // Call
        final List<Tuple> results = query.distinct().fetchAll().list(qImportedDocUnit.identifier, qDocUnit.label);
        final List<String> identifiers = results.stream().map(tuple -> tuple.get(qImportedDocUnit.identifier)).collect(Collectors.toList());
        // Result
        return new PageImpl<>(identifiers, pageable, count);
    }

    /**
     * Initialisation de la requête utilisée par findIdentifiersByImportReport pour générer une page de résultats
     *
     * @param report
     * @param states
     * @param withErrors
     * @param withDuplicates
     * @param qImportedDocUnit
     * @param qDocUnit
     * @return
     */
    private JPAQuery getQueryIdentifiersByImportReport(final ImportReport report,
                                                       final List<DocUnit.State> states,
                                                       final boolean withErrors,
                                                       final boolean withDuplicates,
                                                       final QImportedDocUnit qImportedDocUnit,
                                                       final QDocUnit qDocUnit) {
        final JPAQuery query =
            new JPAQuery(em).from(qImportedDocUnit).leftJoin(qImportedDocUnit.docUnit, qDocUnit).where(qImportedDocUnit.report.eq(report));

        // Filtrage par statut des entités importées
        if (CollectionUtils.isNotEmpty(states)) {
            final boolean filterDeletedUnits = states.contains(DocUnit.State.DELETED);
            final List<DocUnit.State> stateFilters = states.stream().filter(st -> st != DocUnit.State.DELETED).collect(Collectors.toList());

            List<BooleanExpression> expressions = new ArrayList<>();
            if (!stateFilters.isEmpty()) {
                expressions.add(qDocUnit.state.in(stateFilters));
            }
            if (filterDeletedUnits) {
                expressions.add(qDocUnit.isNull());
            }
            if (expressions.size() == 1) {
                query.where(expressions.get(0));
            } else {
                query.where(expressions.get(0).or(expressions.get(1)));
            }
        }
        // Filtrage sur les imports ayant des messages d'erreur
        if (withErrors) {
            query.where(qImportedDocUnit.messages.size().gt(0));
        }
        // Filtrage sur les imports ayant des doublons
        if (withDuplicates) {
            query.where(qImportedDocUnit.duplicatedUnits.isNotEmpty());
        }
        return query;
    }
}
