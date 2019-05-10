package fr.progilone.pgcn.service.audit;

import fr.progilone.pgcn.domain.dto.audit.AuditLotRevisionDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.audit.AuditLotRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLotService {

    private final AuditLotRepository auditLotRepository;
    private final LotRepository lotRepository;

    @Autowired
    public AuditLotService(final AuditLotRepository auditLotRepository, final LotRepository lotRepository) {
        this.auditLotRepository = auditLotRepository;
        this.lotRepository = lotRepository;
    }

    /**
     * Liste des modification apportées sur un lot, à partir d'une date donnée
     *
     * @param fromDate
     * @param libraries
     * @param projects
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<AuditLotRevisionDTO> getRevisions(final LocalDate fromDate,
                                                  final List<String> libraries,
                                                  final List<String> projects,
                                                  final List<Lot.LotStatus> status) {
        List<AuditLotRevisionDTO> revisions = auditLotRepository.getRevisions(fromDate, status);
        revisions = updateRevisions(revisions, libraries, projects);
        return revisions;
    }

    /**
     * Ajout des infos du lot (non auditées)
     *
     * @param revisions
     * @param libraries
     *         filtrage par bibliothèque
     * @param projects
     *         filtrage ar projet
     */
    private List<AuditLotRevisionDTO> updateRevisions(final List<AuditLotRevisionDTO> revisions,
                                                      final List<String> libraries,
                                                      final List<String> projects) {
        final List<AuditLotRevisionDTO> updatedRevs = new ArrayList<>();
        final List<String> lotIds = revisions.stream().map(AuditLotRevisionDTO::getIdentifier).collect(Collectors.toList());
        final List<Lot> lots = lotRepository.findAll(lotIds);

        for (final AuditLotRevisionDTO revision : revisions) {
            lots.stream()
                // Lot correspondant la révision
                .filter(lot -> StringUtils.equals(lot.getIdentifier(), revision.getIdentifier()))
                // Filtrage par bibliothèque et par projet
                .filter(lot -> {
                    final Project project = lot.getProject();
                    final Library library = project != null ? project.getLibrary() : null;

                    // Projet
                    if (CollectionUtils.isNotEmpty(projects) && (project == null || !projects.contains(project.getIdentifier()))) {
                        return false;
                    }
                    // Bibliothèque
                    if (CollectionUtils.isNotEmpty(libraries) && (library == null || !libraries.contains(library.getIdentifier()))) {
                        return false;
                    }
                    return true;

                }).findAny()
                // alimentation liste résultats
                .ifPresent(lot -> {
                    revision.setLabel(lot.getLabel());
                    updatedRevs.add(revision);
                });
        }
        return updatedRevs;
    }
}
