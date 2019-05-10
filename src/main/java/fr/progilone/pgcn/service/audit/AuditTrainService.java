package fr.progilone.pgcn.service.audit;

import fr.progilone.pgcn.domain.dto.audit.AuditTrainRevisionDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.repository.audit.AuditTrainRepository;
import fr.progilone.pgcn.repository.train.TrainRepository;
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
public class AuditTrainService {

    private final AuditTrainRepository auditTrainRepository;
    private final TrainRepository trainRepository;

    @Autowired
    public AuditTrainService(final AuditTrainRepository auditTrainRepository, final TrainRepository trainRepository) {
        this.auditTrainRepository = auditTrainRepository;
        this.trainRepository = trainRepository;
    }

    /**
     * Liste des modification apportées sur un train, à partir d'une date donnée
     *
     * @param fromDate
     * @param libraries
     * @param projects
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<AuditTrainRevisionDTO> getRevisions(final LocalDate fromDate,
                                                    final List<String> libraries,
                                                    final List<String> projects,
                                                    final List<Train.TrainStatus> status) {
        List<AuditTrainRevisionDTO> revisions = auditTrainRepository.getRevisions(fromDate, status);
        revisions = updateRevisions(revisions, libraries, projects);
        return revisions;
    }

    /**
     * Ajout des infos du train (non auditées)
     *
     * @param revisions
     * @param libraries
     * @param projects
     */
    private List<AuditTrainRevisionDTO> updateRevisions(final List<AuditTrainRevisionDTO> revisions,
                                                        final List<String> libraries,
                                                        final List<String> projects) {
        final List<AuditTrainRevisionDTO> updatedRevs = new ArrayList<>();
        final List<String> trainIds = revisions.stream().map(AuditTrainRevisionDTO::getIdentifier).collect(Collectors.toList());
        final List<Train> trains = trainRepository.findAll(trainIds);

        for (final AuditTrainRevisionDTO revision : revisions) {
            trains.stream()
                  // Train correspondant à la révision
                  .filter(train -> StringUtils.equals(train.getIdentifier(), revision.getIdentifier()))
                  // Filtrage par bibliothèque et par projet
                  .filter(train -> {
                      final Project project = train.getProject();
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
                  .ifPresent(train -> {
                      revision.setLabel(train.getLabel());
                      updatedRevs.add(revision);
                  });
        }
        return updatedRevs;
    }
}
