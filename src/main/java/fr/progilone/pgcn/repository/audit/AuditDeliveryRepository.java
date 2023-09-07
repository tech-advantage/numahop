package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.audit.AuditDeliveryRevisionDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AuditDeliveryRepository extends AbstractAuditRepository<Delivery> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditDeliveryRepository.class);

    @PersistenceContext
    private EntityManager em;

    public AuditDeliveryRepository() {
        super(Delivery.class);
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée
     *
     * @param fromDate
     * @param status
     * @return
     */
    public List<AuditDeliveryRevisionDTO> getRevisions(final LocalDate fromDate, final List<Delivery.DeliveryStatus> status) {
        if (CollectionUtils.isNotEmpty(status)) {
            final AuditCriterion filterStatus = AuditEntity.property("status").in(status);
            return super.getRevisions(fromDate, em, Collections.singletonList(filterStatus), this::getAuditDeliveryRevisionDTO);
        } else {
            return super.getRevisions(fromDate, em, this::getAuditDeliveryRevisionDTO);
        }
    }

    private AuditDeliveryRevisionDTO getAuditDeliveryRevisionDTO(final Delivery delivery, final AuditRevision rev) {
        final AuditDeliveryRevisionDTO dto = new AuditDeliveryRevisionDTO();
        dto.setRev(rev.getId());
        dto.setIdentifier(delivery.getIdentifier());

        // Révision
        dto.setTimestamp(rev.getTimestamp());
        dto.setUsername(rev.getUsername());

        // Livraison
        try {
            dto.setStatus(delivery.getStatus());

        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return dto;
    }

}
