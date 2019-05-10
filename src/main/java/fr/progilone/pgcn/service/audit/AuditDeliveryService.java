package fr.progilone.pgcn.service.audit;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.dto.audit.AuditDeliveryRevisionDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.audit.AuditDeliveryRepository;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
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
public class AuditDeliveryService {

    private final AuditDeliveryRepository auditDeliveryRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public AuditDeliveryService(final AuditDeliveryRepository auditDeliveryRepository, final DeliveryRepository deliveryRepository) {
        this.auditDeliveryRepository = auditDeliveryRepository;
        this.deliveryRepository = deliveryRepository;
    }

    /**
     * Liste des modification apportées sur une livraison, à partir d'une date donnée
     *
     * @param fromDate
     * @param libraries
     * @param projects
     * @param lots
     * @param status
     * @return
     */
    @Transactional(readOnly = true)
    public List<AuditDeliveryRevisionDTO> getRevisions(final LocalDate fromDate,
                                                       final List<String> libraries,
                                                       final List<String> projects,
                                                       final List<String> lots,
                                                       final List<Delivery.DeliveryStatus> status) {
        List<AuditDeliveryRevisionDTO> revisions = auditDeliveryRepository.getRevisions(fromDate, status);
        revisions = updateRevisions(revisions, libraries, projects, lots);
        return revisions;
    }

    /**
     * Ajout des infos de la livraison (non auditées)
     *
     * @param revisions
     * @param libraries
     * @param projects
     * @param lots
     */
    private List<AuditDeliveryRevisionDTO> updateRevisions(final List<AuditDeliveryRevisionDTO> revisions,
                                                           final List<String> libraries,
                                                           final List<String> projects,
                                                           final List<String> lots) {
        final List<AuditDeliveryRevisionDTO> updatedRevs = new ArrayList<>();
        final List<String> deliveryIds = revisions.stream().map(AuditDeliveryRevisionDTO::getIdentifier).collect(Collectors.toList());
        final List<Delivery> deliverys = deliveryRepository.findAll(deliveryIds);

        for (final AuditDeliveryRevisionDTO revision : revisions) {
            deliverys.stream()
                     // Livraison correspondant à la révision
                     .filter(delivery -> StringUtils.equals(delivery.getIdentifier(), revision.getIdentifier()))
                     // Filtrage par bibliothèque, par projet et par lot
                     .filter(delivery -> {
                         final Lot lot = delivery.getLot();
                         final Project project = lot != null ? lot.getProject() : null;
                         final Library library = project != null ? project.getLibrary() : null;

                         // Lot
                         if (CollectionUtils.isNotEmpty(lots) && (lot == null || !lots.contains(lot.getIdentifier()))) {
                             return false;
                         }
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
                     .ifPresent(delivery -> {
                         revision.setLabel(delivery.getLabel());

                         // Lot
                         final Lot lot = delivery.getLot();
                         if (lot != null) {
                             revision.setLotIdentifier(lot.getIdentifier());
                             revision.setLotLabel(lot.getLabel());
                         }

                         updatedRevs.add(revision);
                     });
        }
        return updatedRevs;
    }
}
