package fr.progilone.pgcn.service.audit;

import fr.progilone.pgcn.domain.dto.audit.AuditProjectRevisionDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.audit.AuditProjectRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditProjectService {

    private final AuditProjectRepository auditProjectRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public AuditProjectService(final AuditProjectRepository auditProjectRepository, final ProjectRepository projectRepository) {
        this.auditProjectRepository = auditProjectRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Liste des modification apportées sur un projet, à partir d'une date donnée
     *
     * @param fromDate
     * @param libraries
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<AuditProjectRevisionDTO> getRevisions(final LocalDate fromDate, final List<String> libraries, final List<Project.ProjectStatus> status) {
        List<AuditProjectRevisionDTO> revisions = auditProjectRepository.getRevisions(fromDate, status);
        revisions = updateRevisions(revisions, libraries);
        return revisions;
    }

    /**
     * Ajout des infos du projet (non auditées)
     *
     * @param revisions
     * @param libraries
     *            filtrage par bibliothèque
     */
    private List<AuditProjectRevisionDTO> updateRevisions(final List<AuditProjectRevisionDTO> revisions, final List<String> libraries) {
        final List<AuditProjectRevisionDTO> updatedRevs = new ArrayList<>();
        final List<String> projectIds = revisions.stream().map(AuditProjectRevisionDTO::getIdentifier).collect(Collectors.toList());
        final List<Project> projects = projectRepository.findAllById(projectIds);

        for (final AuditProjectRevisionDTO revision : revisions) {
            projects.stream()
                    // Projet correspondant à la révision
                    .filter(pj -> StringUtils.equals(pj.getIdentifier(), revision.getIdentifier()))
                    // Filtrage par bibliothèque
                    .filter(pj -> CollectionUtils.isEmpty(libraries) || (pj.getLibrary() != null && libraries.contains(pj.getLibrary().getIdentifier())))
                    .findAny()
                    .ifPresent(pj -> {
                        revision.setName(pj.getName());
                        updatedRevs.add(revision);
                    });
        }
        return updatedRevs;
    }
}
