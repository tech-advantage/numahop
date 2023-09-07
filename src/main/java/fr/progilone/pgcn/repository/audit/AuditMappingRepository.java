package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.hibernate.envers.query.internal.property.RevisionNumberPropertyName;
import org.hibernate.envers.query.internal.property.RevisionPropertyPropertyName;
import org.hibernate.envers.query.order.internal.PropertyAuditOrder;
import org.hibernate.envers.query.projection.internal.PropertyAuditProjection;
import org.springframework.stereotype.Repository;

/**
 * Dépôt des table d'audit de mapping
 * <p>
 * Created by Sébastien on 27/06/2017.
 */
@Repository
public class AuditMappingRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Recherche une révision précise d'un mapping
     *
     * @param id
     * @param rev
     * @return
     */
    public Mapping getEntity(final String id, final Number rev) {
        final AuditReader auditReader = AuditReaderFactory.get(em);

        // Recherche du mapping à la révision rev
        final Mapping mapping = auditReader.find(Mapping.class, id, rev);

        if (mapping != null) {
            // Initialisation des règles à la révision rev
            @SuppressWarnings("unchecked")
            final List<MappingRule> rules = auditReader.createQuery()
                                                       .forEntitiesAtRevision(MappingRule.class, rev)
                                                       .add(AuditEntity.property("mapping").eq(mapping))
                                                       .getResultList();
            mapping.setRules(rules);

            // Initialisation des données non versionnées
            Hibernate.initialize(mapping.getLibrary());
            rules.forEach(r -> Hibernate.initialize(r.getProperty()));
        }
        return mapping;
    }

    /**
     * Recherche la liste des révisions disponibles pour un mapping en tenant compte du mapping et de ses règles
     *
     * @param id
     * @return
     */
    public List<AuditRevision> getRevisions(final String id) {
        final List<AuditRevision> mappingResult = getRevisions(em, Mapping.class, AuditEntity.id().eq(id));

        final Mapping mapping = new Mapping();
        mapping.setIdentifier(id);
        final List<AuditRevision> ruleResult = getRevisions(em, MappingRule.class, AuditEntity.property("mapping").eq(mapping));

        return Stream.concat(mappingResult.stream(), ruleResult.stream()).sorted(Comparator.comparing(AuditRevision::getTimestamp)).distinct().collect(Collectors.toList());
    }

    /**
     * Récupère la révision la plus récente
     *
     * @return
     */
    public long getLatestRevision() {
        return Stream.of(Mapping.class, MappingRule.class).map(c -> getLatestRevision(em, c)).mapToLong(rev -> rev).max().orElse(0L);
    }

    /**
     * Recherche la liste des révisions disponibles pour la classe clazz, en appliquant le critère de filtrage auditFilter
     *
     * @param clazz
     * @param auditFilter
     * @return
     */
    private List<AuditRevision> getRevisions(final EntityManager em, final Class<?> clazz, final AuditCriterion auditFilter) {
        final AuditReader auditReader = AuditReaderFactory.get(em);
        final List<?> result = auditReader.createQuery().forRevisionsOfEntity(clazz, false, false).add(auditFilter).getResultList();
        return result.stream()
                     .map(obj -> ((Object[]) obj)[1])
                     .map(AuditRevision.class::cast)
                     .sorted(Comparator.comparing(AuditRevision::getTimestamp))
                     .collect(Collectors.toList());
    }

    /**
     * Récupère la révision la plus récente pour les entités de classe clazz
     */
    private long getLatestRevision(final EntityManager em, final Class<?> clazz) {
        try {
            return (long) AuditReaderFactory.get(em)
                                            .createQuery()
                                            // Révision des entités Mapping
                                            .forRevisionsOfEntity(clazz, false, false)
                                            // Champ timestamp de la révision
                                            .addProjection(new PropertyAuditProjection(null, new RevisionPropertyPropertyName("timestamp"), null, true))
                                            // Tri par n° de révision desc
                                            .addOrder(new PropertyAuditOrder(null, new RevisionNumberPropertyName(), false))
                                            // On ne conserve que le 1er résultat
                                            .setMaxResults(1)
                                            .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
}
