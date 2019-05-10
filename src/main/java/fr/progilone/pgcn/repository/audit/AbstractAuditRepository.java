package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.audit.AuditRevision;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.internal.property.RevisionNumberPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class AbstractAuditRepository<T extends AbstractDomainObject> {

    private final Class<T> clazz;

    public AbstractAuditRepository(final Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée
     *
     * @param fromDate
     * @param em
     * @param resultFn
     * @param <U>
     * @return
     */
    protected <U> List<U> getRevisions(final LocalDate fromDate, final EntityManager em, final BiFunction<T, AuditRevision, U> resultFn) {
        return getRevisions(fromDate, em, null, resultFn);
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée
     *
     * @param fromDate
     * @param em
     * @param criterion
     * @param resultFn
     * @param <U>
     * @return
     */
    protected <U> List<U> getRevisions(final LocalDate fromDate,
                                       final EntityManager em,
                                       final List<AuditCriterion> criterion,
                                       final BiFunction<T, AuditRevision, U> resultFn) {

        final AuditQuery auditQuery = AuditReaderFactory.get(em).createQuery().forRevisionsOfEntity(clazz, false, false)
                                                        // Tri par n° de révision desc
                                                        .addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), false));
        // Filtre sur le timestamp de la révision
        if (fromDate != null) {
            auditQuery.add(AuditEntity.revisionProperty("timestamp")
                                      .ge(fromDate.atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000));
        }
        // Filtres additionnels
        if (CollectionUtils.isNotEmpty(criterion)) {
            criterion.forEach(auditQuery::add);
        }
        // Résultat
        final List<?> revisions = auditQuery.getResultList();
        final Set<String> distinctEntities = new HashSet<>();

        return revisions.stream()
                        // distinct par entité
                        .filter(obj -> {
                            final T entity = (T) ((Object[]) obj)[0];
                            boolean found = distinctEntities.contains(entity.getIdentifier());

                            if (!found) {
                                distinctEntities.add(entity.getIdentifier());
                            }
                            return !found;
                        })
                        // Construction des DTOs à partir des résultats de la recherche
                        .map(obj -> {
                            final T entity = (T) ((Object[]) obj)[0];
                            final AuditRevision rev = (AuditRevision) ((Object[]) obj)[1];

                            return resultFn.apply(entity, rev);

                        }).collect(Collectors.toList());
    }
}
