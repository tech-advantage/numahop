package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.dto.audit.AuditLotRevisionDTO;
import fr.progilone.pgcn.domain.lot.Lot;
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
public class AuditLotRepository extends AbstractAuditRepository<Lot> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditLotRepository.class);

    @PersistenceContext
    private EntityManager em;

    public AuditLotRepository() {
        super(Lot.class);
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée
     *
     * @param fromDate
     * @param status
     * @return
     */
    public List<AuditLotRevisionDTO> getRevisions(final LocalDate fromDate, final List<Lot.LotStatus> status) {
        if (CollectionUtils.isNotEmpty(status)) {
            final AuditCriterion filterStatus = AuditEntity.property("status").in(status);
            return super.getRevisions(fromDate, em, Collections.singletonList(filterStatus), this::getAuditLotRevisionDTO);
        } else {
            return super.getRevisions(fromDate, em, this::getAuditLotRevisionDTO);
        }
    }

    private AuditLotRevisionDTO getAuditLotRevisionDTO(final Lot lot, final AuditRevision rev) {
        final AuditLotRevisionDTO dto = new AuditLotRevisionDTO();
        dto.setRev(rev.getId());
        dto.setIdentifier(lot.getIdentifier());

        // Révision
        dto.setTimestamp(rev.getTimestamp());
        dto.setUsername(rev.getUsername());

        // Lot
        try {
            dto.setStatus(lot.getStatus());

        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return dto;
    }

}
