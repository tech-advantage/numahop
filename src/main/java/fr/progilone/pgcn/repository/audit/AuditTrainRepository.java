package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.dto.audit.AuditTrainRevisionDTO;
import fr.progilone.pgcn.domain.train.Train;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.criteria.AuditCriterion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
public class AuditTrainRepository extends AbstractAuditRepository<Train> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditTrainRepository.class);

    @PersistenceContext
    private EntityManager em;

    public AuditTrainRepository() {
        super(Train.class);
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée
     *
     * @param fromDate
     * @param status
     * @return
     */
    public List<AuditTrainRevisionDTO> getRevisions(final LocalDate fromDate, final List<Train.TrainStatus> status) {
        if (CollectionUtils.isNotEmpty(status)) {
            final AuditCriterion filterStatus = AuditEntity.property("status").in(status);
            return super.getRevisions(fromDate, em, Collections.singletonList(filterStatus), this::getAuditTrainRevisionDTO);
        } else {
            return super.getRevisions(fromDate, em, this::getAuditTrainRevisionDTO);
        }
    }

    private AuditTrainRevisionDTO getAuditTrainRevisionDTO(final Train train, final AuditRevision rev) {
        final AuditTrainRevisionDTO dto = new AuditTrainRevisionDTO();
        dto.setRev(rev.getId());
        dto.setIdentifier(train.getIdentifier());

        // Révision
        dto.setTimestamp(rev.getTimestamp());
        dto.setUsername(rev.getUsername());

        // Train
        try {
            dto.setStatus(train.getStatus());

        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return dto;
    }

}
