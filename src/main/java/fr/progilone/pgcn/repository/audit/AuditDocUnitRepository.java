package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.audit.AuditDocUnitRevisionDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import org.hibernate.Hibernate;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dépôt des table d'audit d'unités documentaires
 */
@Repository
public class AuditDocUnitRepository {

    private static final Logger LOG = LoggerFactory.getLogger(AuditDocUnitRepository.class);

    @PersistenceContext
    private EntityManager em;

    /**
     * Recherche une révision précise d'une UD
     *
     * @param id
     * @param rev
     * @return
     */
    public DocUnit getEntity(final String id, final Number rev) {
        final AuditReader auditReader = AuditReaderFactory.get(em);
        final DocUnit docUnit = auditReader.find(DocUnit.class, id, rev);
        try {
            Hibernate.initialize(docUnit.getLot());
        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return docUnit;
    }

    /**
     * Recherche la liste des révisions disponibles pour une UD
     *
     * @param id
     * @return
     */
    public List<AuditDocUnitRevisionDTO> getRevisions(final String id) {
        final List<?> revisions =
            AuditReaderFactory.get(em).createQuery().forRevisionsOfEntity(DocUnit.class, false, false).add(AuditEntity.id().eq(id)).getResultList();
        return revisions.stream()
                        // Construction des DTOs à partir des résultats de la recherche
                        .map(obj -> {
                            final DocUnit docUnit = (DocUnit) ((Object[]) obj)[0];
                            final AuditRevision rev = (AuditRevision) ((Object[]) obj)[1];

                            return getAuditDocUnitRevisionDTO(docUnit, rev);

                        }).sorted(Comparator.comparing(AuditDocUnitRevisionDTO::getTimestamp)).collect(Collectors.toList());
    }

    private AuditDocUnitRevisionDTO getAuditDocUnitRevisionDTO(final DocUnit docUnit, final AuditRevision rev) {
        final AuditDocUnitRevisionDTO dto = new AuditDocUnitRevisionDTO();
        dto.setRev(rev.getId());
        dto.setIdentifier(docUnit.getIdentifier());

        // Révision
        dto.setTimestamp(rev.getTimestamp());
        dto.setUsername(rev.getUsername());

        // DocUnit
        try {
            final Lot lot = docUnit.getLot();
            if (lot != null) {
                dto.setLotIdentifier(lot.getIdentifier());
                dto.setLotLabel(lot.getLabel());
            }

        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return dto;
    }
}
