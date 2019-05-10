package fr.progilone.pgcn.service.statistics;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProgressDTO;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.lot.helper.LotSearchBuilder;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.exchange.cines.CinesReportService;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveReportService;
import fr.progilone.pgcn.service.project.ProjectService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StatisticsProgressService {

    private final CinesReportService cinesReportService;
    private final DeliveryService deliveryService;
    private final DocUnitWorkflowRepository docUnitWorkflowRepository;
    private final InternetArchiveReportService iaReportService;
    private final ProjectService projectService;
    private final LotRepository lotRepository;

    public StatisticsProgressService(final CinesReportService cinesReportService,
                                     final DeliveryService deliveryService,
                                     final DocUnitWorkflowRepository docUnitWorkflowRepository,
                                     final InternetArchiveReportService iaReportService,
                                     final ProjectService projectService,
                                     final LotRepository lotRepository) {
        this.cinesReportService = cinesReportService;
        this.deliveryService = deliveryService;
        this.docUnitWorkflowRepository = docUnitWorkflowRepository;
        this.iaReportService = iaReportService;
        this.projectService = projectService;
        this.lotRepository = lotRepository;
    }

    /**
     * #186: Tableau de bord des bibliothèques et institutions
     * par projet
     *
     * @param libraries
     * @param projects
     * @param fromDate
     * @param toDate
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<StatisticsProgressDTO> getProjectProgress(final List<String> libraries,
                                                          final List<String> projects,
                                                          final LocalDate fromDate,
                                                          final LocalDate toDate,
                                                          final Integer page,
                                                          final Integer size) {
        // Projets
        final Page<Project> pageOfProjects = projectService.findAllForStatistics(null, libraries, projects, fromDate, toDate, page, size, null);
        final Page<StatisticsProgressDTO> pageOfDtos = pageOfProjects.map(this::initDto);

        final List<String> docUnitIds = pageOfProjects.getContent()
                                                      .stream()
                                                      .flatMap(project -> project.getDocUnits().stream())
                                                      .map(AbstractDomainObject::getIdentifier)
                                                      .collect(Collectors.toList());
        // Livraisons
        setDeliveryCounts(pageOfDtos, projects, null);
        // Workflow
        setWorkflowCounts(pageOfDtos, libraries, projects, null);
        // Doc archivés
        setCinesCounts(pageOfDtos, docUnitIds);
        // Docs diffusés
        setIaCounts(pageOfDtos, docUnitIds);
        // Pct
        setDocUnitsPct(pageOfDtos, pageOfProjects.getContent().stream().flatMap(pj -> pj.getDocUnits().stream()).collect(Collectors.toList()));

        return pageOfDtos;
    }

    /**
     * #186: Tableau de bord des bibliothèques et institutions
     * par lot
     *
     * @param libraries
     * @param projects
     * @param fromDate
     * @param toDate
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<StatisticsProgressDTO> getLotProgress(final List<String> libraries,
                                                      final List<String> projects,
                                                      final LocalDate fromDate,
                                                      final LocalDate toDate,
                                                      final Integer page,
                                                      final Integer size) {
        // Lots
        final Page<Lot> pageOfLots =
            lotRepository.search(new LotSearchBuilder().setLibraries(libraries).setProjects(projects).setLastDlvFrom(fromDate).setLastDlvTo(toDate),
                                 new PageRequest(page, size));
        final Page<StatisticsProgressDTO> pageOfDtos = pageOfLots.map(this::initDto);

        final List<String> lotIds = pageOfLots.getContent().stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        final List<String> docUnitIds = pageOfLots.getContent()
                                                  .stream()
                                                  .flatMap(lot -> lot.getDocUnits().stream())
                                                  .map(AbstractDomainObject::getIdentifier)
                                                  .collect(Collectors.toList());
        // Livraisons
        setDeliveryCounts(pageOfDtos, projects, lotIds);
        // Workflow
        setWorkflowCounts(pageOfDtos, libraries, projects, lotIds);
        // Doc archivés
        setCinesCounts(pageOfDtos, docUnitIds);
        // Docs diffusés
        setIaCounts(pageOfDtos, docUnitIds);
        // Pct
        setDocUnitsPct(pageOfDtos, pageOfLots.getContent().stream().flatMap(lot -> lot.getDocUnits().stream()).collect(Collectors.toList()));

        return pageOfDtos;
    }

    /**
     * Initialisation d'un DTO à partir d'un projet
     *
     * @param project
     * @return
     */
    private StatisticsProgressDTO initDto(final Project project) {
        final StatisticsProgressDTO dto = new StatisticsProgressDTO();

        dto.setProjectIdentifier(project.getIdentifier());
        dto.setProjectName(project.getName());
        dto.setProjectStatus(project.getStatus());
        dto.setNbLots(project.getLots().size());

        final Library library = project.getLibrary();
        if (library != null) {
            dto.setLibraryIdentifier(library.getIdentifier());
            dto.setLibraryName(library.getName());
        }

        setDocUnitCounts(dto, project.getDocUnits());

        return dto;
    }

    /**
     * Initialisation d'un DTO à partir d'un lot
     *
     * @param lot
     * @return
     */
    private StatisticsProgressDTO initDto(final Lot lot) {
        final StatisticsProgressDTO dto = new StatisticsProgressDTO();

        dto.setLotIdentifier(lot.getIdentifier());
        dto.setLotLabel(lot.getLabel());

        final Project project = lot.getProject();
        if (project != null) {
            dto.setProjectIdentifier(project.getIdentifier());
            dto.setProjectName(project.getName());
            dto.setProjectStatus(project.getStatus());

            final Library library = project.getLibrary();
            if (library != null) {
                dto.setLibraryIdentifier(library.getIdentifier());
                dto.setLibraryName(library.getName());
            }
        }

        setDocUnitCounts(dto, lot.getDocUnits());

        return dto;
    }

    /**
     * Stats sur les unités documentaires
     *
     * @param dto
     * @param docUnits
     */
    private void setDocUnitCounts(final StatisticsProgressDTO dto, final Set<DocUnit> docUnits) {
        dto.setNbDocUnits(docUnits.size());
        dto.setNbDocDistributable(docUnits.stream().filter(DocUnit::isDistributable).count());
        dto.setNbDocArchivable(docUnits.stream().filter(DocUnit::isArchivable).count());

        setDlvCounts(dto, docUnits);

        final double avgDocDlv = docUnits.stream()
                                         .flatMap(ud -> ud.getDigitalDocuments().stream())
                                         .collect(Collectors.groupingBy(AbstractDomainObject::getIdentifier,
                                                                        Collectors.collectingAndThen(Collectors.toList(), List::size)))
                                         .values()
                                         .stream()
                                         .mapToInt(i -> i)
                                         .average()
                                         .orElse(0);
        dto.setAvgDocDlv(avgDocDlv);
    }

    /**
     * Nombres de documents par statut de numérisation
     *
     * @param dto
     * @param docUnits
     */
    private void setDlvCounts(final StatisticsProgressDTO dto, final Set<DocUnit> docUnits) {
        final Map<DigitalDocumentStatus, Integer> countByStatus = docUnits.stream()
                                                                          // Documents numérisés
                                                                          .flatMap(ud -> ud.getDigitalDocuments().stream())
                                                                          // Dernière livraison
                                                                          .map(digitalDocument -> digitalDocument.getDeliveries()
                                                                                                                 .stream()
                                                                                                                 .min(Collections.reverseOrder(
                                                                                                                     Comparator.nullsLast(Comparator.comparing(
                                                                                                                         DeliveredDocument::getCreatedDate)))))
                                                                          .filter(Optional::isPresent)
                                                                          .map(Optional::get)
                                                                          // Regroupement du nombre de documents livrés par statut de la livraison
                                                                          .collect(Collectors.groupingBy(DeliveredDocument::getStatus,
                                                                                                         Collectors.collectingAndThen(Collectors.toList(),
                                                                                                                                      List::size)));

        final int nbDlv = countByStatus.values().stream().mapToInt(i -> i).sum();
        final int nbDlvControlled = Arrays.stream(DigitalDocumentStatus.values())
                                          .filter(s -> s.compareTo(DigitalDocumentStatus.CHECKING) > 0)
                                          .filter(countByStatus::containsKey)
                                          .mapToInt(countByStatus::get)
                                          .sum();
        final int nbDlvRejected =
            countByStatus.getOrDefault(DigitalDocumentStatus.PRE_REJECTED, 0) + countByStatus.getOrDefault(DigitalDocumentStatus.REJECTED, 0);
        final int nbDlvValidated = countByStatus.getOrDefault(DigitalDocumentStatus.VALIDATED, 0);

        dto.setNbDigitalDocs(nbDlv);
        dto.setNbDlvControlled(nbDlvControlled);
        dto.setNbDlvRejected(nbDlvRejected);
        dto.setNbDlvValidated(nbDlvValidated);

        if (nbDlv > 0) {
            dto.setPctDlvControlled((double) nbDlvControlled / (double) nbDlv);
            dto.setPctDlvRejected((double) nbDlvRejected / (double) nbDlv);
            dto.setPctDlvValidated((double) nbDlvValidated / (double) nbDlv);
        }
    }

    /**
     * Stats sur les livraisons
     *
     * @param pageOfDtos
     * @param projects
     * @param lotIds
     */
    private void setDeliveryCounts(final Page<StatisticsProgressDTO> pageOfDtos, final List<String> projects, final List<String> lotIds) {
        final List<Delivery> deliveries = deliveryService.findByProjectsAndLots(projects, lotIds);

        pageOfDtos.forEach(dto -> {
            final long nbDlv = deliveries.stream().filter(d -> match(dto, d.getLotId(), d.getLot().getProjectId())).count();
            dto.setNbDlv(nbDlv);
        });
    }

    /**
     * Stats sur les workflows
     *
     * @param pageOfDtos
     * @param libraries
     * @param projects
     * @param lotIds
     */
    private void setWorkflowCounts(final Page<StatisticsProgressDTO> pageOfDtos,
                                   final List<String> libraries,
                                   final List<String> projects,
                                   final List<String> lotIds) {
        final List<DocUnitWorkflow> validatedUdw =
            docUnitWorkflowRepository.findDocUnitWorkflows(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                             .setProjects(projects)
                                                                                             .setLots(lotIds));
        pageOfDtos.forEach(dto -> {
            final long nbWorkflowValidated = validatedUdw.stream()
                                                         .filter(d -> match(dto, d.getDocUnit().getLotId(), d.getDocUnit().getProjectId()))
                                                         .map(d -> d.getDocUnit().getIdentifier())
                                                         .distinct()
                                                         .count();
            dto.setNbWorkflowValidated(nbWorkflowValidated);
        });
    }

    /**
     * Stats Cines (Archivage)
     *
     * @param pageOfDtos
     * @param docUnitIds
     */
    private void setCinesCounts(final Page<StatisticsProgressDTO> pageOfDtos, final List<String> docUnitIds) {
        final List<CinesReport> cinesReports = cinesReportService.findByDocUnits(docUnitIds);

        pageOfDtos.forEach(dto -> {
            final long nbDocArchived = cinesReports.stream()
                                                   .filter(report -> match(dto, report.getDocUnit().getLotId(), report.getDocUnit().getProjectId()))
                                                   .map(d -> d.getDocUnit().getIdentifier())
                                                   .distinct()
                                                   .count();
            dto.setNbDocArchived(nbDocArchived);
        });
    }

    /**
     * Stats Internet Archive (Diffusion)
     *
     * @param pageOfDtos
     * @param docUnitIds
     */
    private void setIaCounts(final Page<StatisticsProgressDTO> pageOfDtos, final List<String> docUnitIds) {
        final List<InternetArchiveReport> iaReports = iaReportService.findByDocUnits(docUnitIds);

        pageOfDtos.forEach(dto -> {
            final long nbDocDistributed = iaReports.stream()
                                                   .filter(report -> match(dto, report.getDocUnit().getLotId(), report.getDocUnit().getProjectId()))
                                                   .map(d -> d.getDocUnit().getIdentifier())
                                                   .distinct()
                                                   .count();
            dto.setNbDocDistributed(nbDocDistributed);
        });
    }

    /**
     * Stats: pourcentages UD
     *
     * @param pageOfDtos
     * @param docUnits
     */
    private void setDocUnitsPct(final Page<StatisticsProgressDTO> pageOfDtos, final List<DocUnit> docUnits) {
        pageOfDtos.forEach(dto -> {
            if (dto.getNbDocUnits() > 0) {
                dto.setPctDocDistributable((double) dto.getNbDocDistributable() / (double) dto.getNbDocUnits());
                dto.setPctDocArchivable((double) dto.getNbDocArchivable() / (double) dto.getNbDocUnits());

                final long nbDocUnitsDigitalized = docUnits.stream()
                                                           .filter(ud -> match(dto, ud.getLotId(), ud.getProjectId()))
                                                           .filter(ud -> !ud.getDigitalDocuments().isEmpty())
                                                           .count();
                dto.setPctDigitalDocs((double) nbDocUnitsDigitalized / (double) dto.getNbDocUnits());

                dto.setPctWorkflowValidated((double) dto.getNbWorkflowValidated() / (double) dto.getNbDocUnits());
                dto.setPctDocArchived((double) dto.getNbDocArchived() / (double) dto.getNbDocUnits());
                dto.setPctDocDistributed((double) dto.getNbDocDistributed() / (double) dto.getNbDocUnits());
            }
        });
    }

    private boolean match(final StatisticsProgressDTO dto, final String lotId, final String projectId) {
        if (dto.getLotIdentifier() != null) {
            return StringUtils.equals(dto.getLotIdentifier(), lotId);
        }
        if (dto.getProjectIdentifier() != null) {
            return dto.getLotIdentifier() == null && StringUtils.equals(dto.getProjectIdentifier(), projectId);
        }
        return false;
    }
}
