package fr.progilone.pgcn.repository.audit;

import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.dto.audit.AuditProjectRevisionDTO;
import fr.progilone.pgcn.domain.project.Project;
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
public class AuditProjectRepository extends AbstractAuditRepository<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditProjectRepository.class);

    @PersistenceContext
    private EntityManager em;

    public AuditProjectRepository() {
        super(Project.class);
    }

    /**
     * Recherche la liste des dernières révisions depuis une date donnée, pour une liste de statuts donnés
     *
     * @param fromDate
     * @param status
     * @return
     */
    public List<AuditProjectRevisionDTO> getRevisions(final LocalDate fromDate, final List<Project.ProjectStatus> status) {
        if (CollectionUtils.isNotEmpty(status)) {
            final AuditCriterion filterStatus = AuditEntity.property("status").in(status);
            return super.getRevisions(fromDate, em, Collections.singletonList(filterStatus), this::getAuditProjectRevisionDTO);
        } else {
            return super.getRevisions(fromDate, em, this::getAuditProjectRevisionDTO);
        }
    }

    private AuditProjectRevisionDTO getAuditProjectRevisionDTO(final Project project, final AuditRevision rev) {
        final AuditProjectRevisionDTO dto = new AuditProjectRevisionDTO();
        dto.setRev(rev.getId());
        dto.setIdentifier(project.getIdentifier());

        // Révision
        dto.setTimestamp(rev.getTimestamp());
        dto.setUsername(rev.getUsername());

        // Project
        try {
            dto.setStatus(project.getStatus());

        } catch (final EntityNotFoundException e) {
            LOG.warn(e.getMessage());
        }
        return dto;
    }

}
