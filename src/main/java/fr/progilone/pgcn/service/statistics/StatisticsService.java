package fr.progilone.pgcn.service.statistics;

import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitAverageDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitCheckDelayDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitCountDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsDocUnitStatusRatioDTO;
import fr.progilone.pgcn.domain.dto.statistics.StatisticsProjectDTO;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.cines.CinesReportService;
import fr.progilone.pgcn.service.exchange.internetarchive.InternetArchiveReportService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.project.mapper.ProjectMapper;
import fr.progilone.pgcn.service.train.TrainService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.workflow.DocUnitWorkflowService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à l'affichage des statistiques
 */
@Service
public class StatisticsService {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private LotService lotService;
    @Autowired
    private TrainService trainService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private DocUnitService docUnitService;
    @Autowired
    private DocUnitWorkflowService docUnitWorkflowService;
    @Autowired
    private InternetArchiveReportService internetArchiveReportService;
    @Autowired
    private CinesReportService cinesReportService;
    @Autowired
    private UserService userService;

    @Transactional(readOnly = true)
    public Page<StatisticsProjectDTO> searchProject(final String search,
                                                    final List<String> libraries,
                                                    final List<String> projects,
                                                    final LocalDate fromDate,
                                                    final LocalDate toDate,
                                                    final Integer page,
                                                    final Integer size,
                                                    final List<String> sorts) {
        final Page<Project> results = projectService.findAllForStatistics(search, libraries, projects, fromDate, toDate, page, size, sorts);
        return results.map(ProjectMapper.INSTANCE::projectToStatProjectDTO);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectGroupByStatus(final List<String> libraries) {
        return projectService.getProjectGroupByStatus(libraries);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLotGroupByStatus(final List<String> libraries, final List<String> projects) {
        return lotService.getLotGroupByStatus(libraries, projects);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTrainGroupByStatus(final List<String> libraries, final List<String> projects) {
        return trainService.getTrainGroupByStatus(libraries, projects);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDeliveryGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {
        return deliveryService.getDeliveryGroupByStatus(libraries, projects, lots);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUsersGroupByLibrary(final List<String> libraries) {
        return userService.getUsersGroupByLibrary(libraries);
    }

    @Transactional(readOnly = true)
    public Page<StatisticsDocUnitCountDTO> getDocUnits(final List<String> libraries, final List<String> projects, final List<String> lots, final Integer page, final Integer size) {

        final Page<DocUnit> pageOfDocs = docUnitService.search(null,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               libraries,
                                                               projects,
                                                               lots,
                                                               Collections.emptyList(),
                                                               Collections.emptyList(),
                                                               null,
                                                               null,
                                                               null,
                                                               null,
                                                               null,
                                                               page,
                                                               size,
                                                               null);
        final List<String> docUnitIds = pageOfDocs.getContent().stream().map(DocUnit::getIdentifier).collect(Collectors.toList());

        final List<InternetArchiveReport> iaReports;
        final List<CinesReport> cinesReports;

        if (!docUnitIds.isEmpty()) {
            iaReports = internetArchiveReportService.findByDocUnits(docUnitIds);
            cinesReports = cinesReportService.findByDocUnits(docUnitIds);
        } else {
            iaReports = Collections.emptyList();
            cinesReports = Collections.emptyList();
        }

        return pageOfDocs.map(doc -> {
            final StatisticsDocUnitCountDTO dto = new StatisticsDocUnitCountDTO();
            dto.setIdentifier(doc.getIdentifier());
            dto.setPgcnId(doc.getPgcnId());

            final boolean isIa = iaReports.stream()
                                          .anyMatch(report -> StringUtils.equals(report.getDocUnit().getIdentifier(), doc.getIdentifier()) && report.getStatus()
                                                                                                                                              == InternetArchiveReport.Status.ARCHIVED);
            dto.setIa(isIa);

            final boolean isCines = cinesReports.stream()
                                                .anyMatch(report -> StringUtils.equals(report.getDocUnit().getIdentifier(), doc.getIdentifier()) && report.getStatus()
                                                                                                                                                    == CinesReport.Status.ARCHIVED);
            dto.setCines(isCines);

            final int totalPages = doc.getPhysicalDocuments().stream().filter(ph -> ph.getTotalPage() != null).mapToInt(PhysicalDocument::getTotalPage).sum();
            dto.setTotalPage(totalPages);

            final long totalLength = doc.getDigitalDocuments().stream().filter(dg -> dg.getTotalLength() != null).mapToLong(DigitalDocument::getTotalLength).sum();
            dto.setTotalLength(totalLength);

            return dto;
        });
    }

    @Transactional(readOnly = true)
    public List<StatisticsDocUnitAverageDTO> getDocUnitAverages(final List<String> libraries,
                                                                final List<String> projects,
                                                                final List<String> lots,
                                                                final List<String> deliveries,
                                                                final LocalDate fromDate,
                                                                final LocalDate toDate,
                                                                final GroupBy groupBy) {

        final List<DocUnitWorkflow> workflows = docUnitWorkflowService.findAll(libraries, projects, lots, deliveries, fromDate, toDate);

        switch (groupBy) {
            case PROJECT:
                return groupDocUnitWorkflowByProject(workflows).entrySet()
                                                               .stream()
                                                               // Pour chaque projet + liste de workflow, calcul d'un StatisticsDocUnitAverageDTO
                                                               .map(e -> {
                                                                   final Project project = e.getKey();
                                                                   final List<DocUnitWorkflow> projectWorkflows = e.getValue();

                                                                   final StatisticsDocUnitAverageDTO dto = new StatisticsDocUnitAverageDTO();
                                                                   dto.setProjectIdentifier(project.getIdentifier());
                                                                   dto.setProjectName(project.getName());
                                                                   setDocUnitAverages(dto, projectWorkflows);
                                                                   return dto;

                                                               })
                                                               .collect(Collectors.toList());
            case LOT:
                return groupDocUnitWorkflowByLot(workflows).entrySet()
                                                           .stream()
                                                           // Pour chaque lot + liste de workflow, calcul d'un StatisticsDocUnitAverageDTO
                                                           .map(e -> {
                                                               final Lot lot = e.getKey();
                                                               final List<DocUnitWorkflow> lotWorkflows = e.getValue();

                                                               final StatisticsDocUnitAverageDTO dto = new StatisticsDocUnitAverageDTO();
                                                               dto.setLotIdentifier(lot.getIdentifier());
                                                               dto.setLotLabel(lot.getLabel());

                                                               final Project project = lot.getProject();
                                                               if (project != null) {
                                                                   dto.setProjectIdentifier(project.getIdentifier());
                                                                   dto.setProjectName(project.getName());
                                                               }

                                                               setDocUnitAverages(dto, lotWorkflows);
                                                               return dto;

                                                           })
                                                           .collect(Collectors.toList());
            case DELIVERY:
                return groupDocUnitWorkflowByDelivery(workflows, deliveries).entrySet()
                                                                            .stream()
                                                                            // Pour chaque livraison + liste de workflow, calcul d'un
                                                                            // StatisticsDocUnitAverageDTO
                                                                            .map(e -> {
                                                                                final Delivery delivery = e.getKey();
                                                                                final List<DocUnitWorkflow> deliveryWorkflows = e.getValue();

                                                                                final StatisticsDocUnitAverageDTO dto = new StatisticsDocUnitAverageDTO();
                                                                                dto.setDeliveryIdentifier(delivery.getIdentifier());
                                                                                dto.setDeliveryLabel(delivery.getLabel());

                                                                                final Lot lot = delivery.getLot();
                                                                                if (lot != null) {
                                                                                    dto.setLotIdentifier(lot.getIdentifier());
                                                                                    dto.setLotLabel(lot.getLabel());

                                                                                    final Project project = lot.getProject();
                                                                                    if (project != null) {
                                                                                        dto.setProjectIdentifier(project.getIdentifier());
                                                                                        dto.setProjectName(project.getName());
                                                                                    }
                                                                                }

                                                                                setDocUnitAverages(dto, deliveryWorkflows);
                                                                                return dto;

                                                                            })
                                                                            .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public List<StatisticsDocUnitCheckDelayDTO> getCheckDelayStatisitics(final List<String> libraries,
                                                                         final List<String> projects,
                                                                         final List<String> lots,
                                                                         final List<String> deliveries,
                                                                         final GroupBy groupBy) {

        final List<DocUnitWorkflow> workflows = docUnitWorkflowService.findDocUnitWorkflowsInControl(libraries, projects, lots, deliveries);

        switch (groupBy) {
            case PROJECT:
                return groupDocUnitWorkflowByProject(workflows).entrySet()
                                                               .stream()
                                                               // Pour chaque projet + liste de workflow, calcul d'un StatisticsDocUnitAverageDTO
                                                               .map(e -> {
                                                                   final Project project = e.getKey();
                                                                   final List<DocUnitWorkflow> projectWorkflows = e.getValue();

                                                                   final StatisticsDocUnitCheckDelayDTO dto = new StatisticsDocUnitCheckDelayDTO();
                                                                   dto.setProjectIdentifier(project.getIdentifier());
                                                                   dto.setProjectName(project.getName());
                                                                   setDocUnitCheckDelay(dto, projectWorkflows);
                                                                   return dto;

                                                               })
                                                               .collect(Collectors.toList());
            case LOT:
                return groupDocUnitWorkflowByLot(workflows).entrySet()
                                                           .stream()
                                                           // Pour chaque lot + liste de workflow, calcul d'un StatisticsDocUnitAverageDTO
                                                           .map(e -> {
                                                               final Lot lot = e.getKey();
                                                               final List<DocUnitWorkflow> lotWorkflows = e.getValue();

                                                               final StatisticsDocUnitCheckDelayDTO dto = new StatisticsDocUnitCheckDelayDTO();
                                                               dto.setLotIdentifier(lot.getIdentifier());
                                                               dto.setLotLabel(lot.getLabel());

                                                               final Project project = lot.getProject();
                                                               if (project != null) {
                                                                   dto.setProjectIdentifier(project.getIdentifier());
                                                                   dto.setProjectName(project.getName());
                                                               }

                                                               setDocUnitCheckDelay(dto, lotWorkflows);
                                                               return dto;

                                                           })
                                                           .collect(Collectors.toList());
            case DELIVERY:
                return groupDocUnitWorkflowByDelivery(workflows, deliveries).entrySet()
                                                                            .stream()
                                                                            // Pour chaque livraison + liste de workflow, calcul d'un
                                                                            // StatisticsDocUnitAverageDTO
                                                                            .map(e -> {
                                                                                final Delivery delivery = e.getKey();
                                                                                final List<DocUnitWorkflow> deliveryWorkflows = e.getValue();

                                                                                final StatisticsDocUnitCheckDelayDTO dto = new StatisticsDocUnitCheckDelayDTO();
                                                                                dto.setDeliveryIdentifier(delivery.getIdentifier());
                                                                                dto.setDeliveryLabel(delivery.getLabel());

                                                                                final Lot lot = delivery.getLot();
                                                                                if (lot != null) {
                                                                                    dto.setLotIdentifier(lot.getIdentifier());
                                                                                    dto.setLotLabel(lot.getLabel());

                                                                                    final Project project = lot.getProject();
                                                                                    if (project != null) {
                                                                                        dto.setProjectIdentifier(project.getIdentifier());
                                                                                        dto.setProjectName(project.getName());
                                                                                    }
                                                                                }

                                                                                setDocUnitCheckDelay(dto, deliveryWorkflows);
                                                                                return dto;

                                                                            })
                                                                            .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Transactional(readOnly = true)
    public StatisticsDocUnitStatusRatioDTO getDocUnitStatusRatio(final List<String> libraries, final String project, final String lot, final WorkflowStateKey state) {
        final Page<DocUnit> pageOfDocs = docUnitService.search(null,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               false,
                                                               libraries,
                                                               project != null ? Collections.singletonList(project)
                                                                               : null,
                                                               lot != null ? Collections.singletonList(lot)
                                                                           : null,
                                                               Collections.emptyList(),
                                                               Collections.emptyList(),
                                                               null,
                                                               null,
                                                               null,
                                                               null,
                                                               null,
                                                               0,
                                                               Integer.MAX_VALUE,
                                                               null);
        final List<DocUnit> docUnits = pageOfDocs.getContent();

        final StatisticsDocUnitStatusRatioDTO dto = new StatisticsDocUnitStatusRatioDTO();
        if (!docUnits.isEmpty()) {
            // Projet
            if (StringUtils.isNotBlank(project)) {
                final Lot docUnitLot = docUnits.get(0).getLot();

                if (docUnitLot != null) {
                    final Project docProject = docUnitLot.getProject();
                    dto.setProjectIdentifier(docProject.getIdentifier());
                    dto.setProjectName(docProject.getName());
                }
            }
            // Lot
            else if (StringUtils.isNotBlank(lot)) {
                final Lot docLot = docUnits.get(0).getLot();
                dto.setLotIdentifier(docLot.getIdentifier());
                dto.setLotLabel(docLot.getLabel());
            }
            dto.setNbDoc(docUnits.size());
        }
        // Workflow
        final List<DocUnitWorkflow> docWorkflows = docUnitWorkflowService.findAll(libraries,
                                                                                  project != null ? Collections.singletonList(project)
                                                                                                  : null,
                                                                                  lot != null ? Collections.singletonList(lot)
                                                                                              : null,
                                                                                  null,
                                                                                  state != null ? Collections.singletonList(state)
                                                                                                : null);
        dto.setState(state);
        dto.setNbDocOnState(docWorkflows.size());

        return dto;
    }

    /**
     * Regroupement d'une liste de DocUnitWorkflow par projet
     *
     * @param workflows
     * @return
     */
    private Map<Project, List<DocUnitWorkflow>> groupDocUnitWorkflowByProject(final List<DocUnitWorkflow> workflows) {
        return workflows.stream().filter(w -> w.getDocUnit() != null && w.getDocUnit().getProject() != null).collect(Collectors.groupingBy(w -> w.getDocUnit().getProject()));
    }

    /**
     * Regroupement d'une liste de DocUnitWorkflow par lot
     *
     * @param workflows
     * @return
     */
    private Map<Lot, List<DocUnitWorkflow>> groupDocUnitWorkflowByLot(final List<DocUnitWorkflow> workflows) {
        return workflows.stream().filter(w -> w.getDocUnit() != null && w.getDocUnit().getLot() != null).collect(Collectors.groupingBy(w -> w.getDocUnit().getLot()));
    }

    /**
     * Regroupement d'une liste de DocUnitWorkflow par livraison
     *
     * @param workflows
     * @param deliveries
     *            filtrer par livraison
     * @return
     */
    private Map<Delivery, List<DocUnitWorkflow>> groupDocUnitWorkflowByDelivery(final List<DocUnitWorkflow> workflows, final List<String> deliveries) {
        return workflows.stream()
                        .filter(w -> w.getDocUnit() != null && w.getDocUnit().getDigitalDocuments() != null)
                        // on accède aux livraisons de l'UD via les docs numériques et le doc livré
                        .flatMap(w -> w.getDocUnit()
                                       .getDigitalDocuments()
                                       .stream()
                                       .filter(dg -> dg.getDeliveries() != null)
                                       .flatMap(dg -> dg.getDeliveries().stream())
                                       .map(DeliveredDocument::getDelivery)
                                       // Si filtrage par livraison, on l'applique
                                       .filter(delivery -> delivery != null && (CollectionUtils.isEmpty(deliveries) || deliveries.contains(delivery.getIdentifier())))
                                       // on renvoie la livraison + le doc workflow pour construire le regroupement
                                       .map(delivery -> Pair.of(delivery, w)))
                        .collect(Collectors.groupingBy(Pair::getLeft, Collectors.mapping(Pair::getRight, Collectors.toList())));
    }

    /**
     * Calcul des statistiques d'un {@link StatisticsDocUnitAverageDTO}
     *
     * @param dto
     * @param workflows
     */
    private void setDocUnitAverages(final StatisticsDocUnitAverageDTO dto, final List<DocUnitWorkflow> workflows) {
        if (CollectionUtils.isEmpty(workflows)) {
            return;
        }
        // Regroupement de workflows par unité documentaire
        final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit = workflows.stream().collect(Collectors.groupingBy(DocUnitWorkflow::getDocUnit));

        dto.setNbDocs(byDocUnit.size());
        // Nombre total de pages
        setAvgTotalPages(dto, byDocUnit);
        // Taux de rejet
        setRejectRatio(dto, byDocUnit);
        // Poids total des documents
        setTotalLengthDocs(dto, byDocUnit);
        // Temps moyen de contrôle
        setAvgDurControl(dto, byDocUnit);
        // Temps moyen de livraison et de relivraison
        setAvgDurDelivery(dto, byDocUnit);
        // Durée moyenne d'un workflow
        setAvgDurWorkflow(dto, byDocUnit);
    }

    /**
     * Calcul des statistiques d'un StatisticsDocUnitCheckDelayDTO
     *
     * @param dto
     * @param workflows
     */
    private void setDocUnitCheckDelay(final StatisticsDocUnitCheckDelayDTO dto, final List<DocUnitWorkflow> workflows) {
        if (CollectionUtils.isEmpty(workflows)) {
            return;
        }
        // Regroupement de workflows par unité documentaire
        final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit = workflows.stream().collect(Collectors.groupingBy(DocUnitWorkflow::getDocUnit));

        // durée restante
        byDocUnit.entrySet()
                 .stream()
                 // calcul de la durée restante
                 .map(e -> {
                     final DocUnit docUnit = e.getKey();
                     final List<DocUnitWorkflow> docUnitWorkflows = e.getValue();

                     final Integer checkDelay = docUnit.getCheckDelay();
                     if (checkDelay == null) {
                         return Optional.<Long> empty();
                     }

                     return docUnitWorkflows.stream()
                                            .flatMap(w -> w.getStates().stream())
                                            // étape de livraison / relivraison
                                            .filter(state -> state.getKey() == WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS || state.getKey()
                                                                                                                               == WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS)
                                            // étape terminée
                                            .filter(state -> state.getEndDate() != null)
                                            // (re)livraison la plus récente
                                            .min(Comparator.comparing(DocUnitState::getEndDate))
                                            // nombre de jours restant jusque: date de (re)livraison + délai de contrôle
                                            .map(state -> LocalDate.now().until(state.getEndDate().plusDays(checkDelay), ChronoUnit.DAYS))
                                            .filter(remaning -> remaning > 0);
                 })
                 //
                 .filter(Optional::isPresent)
                 .mapToLong(Optional::get)
                 // durée restante minimale
                 .min()
                 .ifPresent(dto::setMinRemainingCheckDelay);

        // délai avant contrôle
        byDocUnit.keySet()
                 .stream()
                 // délais
                 .mapToInt(docUnit -> docUnit.getCheckDelay() != null ? docUnit.getCheckDelay()
                                                                      : 0)
                 // délai maximal
                 .max()
                 .ifPresent(dto::setMaxCheckDelay);
    }

    /**
     * Calcul du nombre total de pages.
     *
     * @param dto
     * @param byDocUnit
     */
    private void setAvgTotalPages(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {
        final int totalPages = byDocUnit.keySet()
                                        .stream()
                                        .flatMapToInt(doc -> doc.getDigitalDocuments().stream().filter(dig -> dig != null).mapToInt(DigitalDocument::getNbPages))
                                        .sum();
        dto.setAvgTotalPages(totalPages);
    }

    /**
     * Calcul du taux de rejet de documents.
     *
     * @param dto
     * @param byDocUnit
     */
    private void setRejectRatio(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {

        final List<DigitalDocument> digs = byDocUnit.keySet()
                                                    .stream()
                                                    .map(doc -> doc.getDigitalDocuments().stream().filter(dig -> dig != null).findFirst().orElse(null))
                                                    .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(digs)) {
            dto.setRejectRatio(0);
        } else {

            final long nbRejected = digs.stream()
                                        .filter(dd -> dd != null)
                                        .filter(dd -> (dd.getDeliveries().size() == 1 && dd.getDeliveries().stream().findFirst().get().getStatus()
                                                                                         == DigitalDocumentStatus.REJECTED) || (dd.getDeliveries().size() > 1 && dd.getDeliveries()
                                                                                                                                                                   .stream()
                                                                                                                                                                   .noneMatch(deliv -> deliv.getStatus()
                                                                                                                                                                                       == DigitalDocumentStatus.REJECTED)))
                                        .count();
            final BigDecimal total = BigDecimal.valueOf(digs.size());
            final BigDecimal ratio = BigDecimal.valueOf(nbRejected).divide(total, 2, RoundingMode.HALF_UP);
            dto.setRejectRatio(ratio.doubleValue());
        }

    }

    /**
     * Calcul du poids total des documents.
     *
     * @param dto
     * @param byDocUnit
     */
    private void setTotalLengthDocs(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {

        final long totalLength = byDocUnit.keySet()
                                          .stream()
                                          .filter(doc -> doc.getDigitalDocuments() != null)
                                          .flatMapToLong(doc -> doc.getDigitalDocuments()
                                                                   .stream()
                                                                   .filter(dig -> dig != null && dig.getTotalLength() != null)
                                                                   .mapToLong(DigitalDocument::getTotalLength))
                                          .sum();
        dto.setLengthDocs(totalLength);
    }

    /**
     * Calcul du temps moyen de contrôle
     *
     * @param dto
     * @param byDocUnit
     */
    private void setAvgDurControl(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {
        byDocUnit.values()
                 .stream()
                 // workflows
                 .flatMap(Collection::stream)
                 // calcul de la durée des étapes de contrôles (achevées) pour chaque workflow d'unité documentaire
                 .map(workflow -> {
                     // étapes de contrôles, terminées
                     final List<DocUnitState> states = workflow.getStates()
                                                               .stream()
                                                               .filter(state -> state.getKey() == WorkflowStateKey.CONTROLE_QUALITE_EN_COURS)
                                                               .filter(state -> state.getStartDate() != null && state.getEndDate() != null)
                                                               .collect(Collectors.toList());
                     if (!states.isEmpty()) {
                         return states.stream().mapToLong(state -> state.getStartDate().until(state.getEndDate(), ChronoUnit.SECONDS)).sum();
                     }
                     // pas d'étapes => on n'en tient pas compte dans le calcul de la durée moyenne
                     return null;

                 })
                 .filter(Objects::nonNull)
                 .mapToLong(Long::longValue)
                 .average()
                 // set value
                 .ifPresent(avg -> dto.setAvgDurControl((long) avg));
    }

    /**
     * Calcul du temps moyen de livraison et de relivraison
     *
     * @param dto
     * @param byDocUnit
     */
    private void setAvgDurDelivery(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {
        byDocUnit.values()
                 .stream()
                 // workflow
                 .flatMap(Collection::stream)
                 // étapes
                 .flatMap(workflow -> workflow.getStates().stream())
                 // étapes de livraison et de relivraison
                 .filter(state -> state.getKey() == WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS || state.getKey() == WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS)
                 // étapes terminées
                 .filter(state -> state.getStartDate() != null && state.getEndDate() != null)
                 // durée de l'étape
                 .mapToLong(state -> state.getStartDate().until(state.getEndDate(), ChronoUnit.SECONDS))
                 // moyenne
                 .average()
                 // set value
                 .ifPresent(avg -> dto.setAvgDurDelivery((long) avg));
    }

    /**
     * Calcul de la durée moyenne d'un workflow
     *
     * @param dto
     * @param byDocUnit
     */
    private void setAvgDurWorkflow(final StatisticsDocUnitAverageDTO dto, final Map<DocUnit, List<DocUnitWorkflow>> byDocUnit) {
        byDocUnit.values()
                 .stream()
                 // workflows
                 .flatMap(Collection::stream)
                 .filter(workflow -> workflow.getStartDate() != null && workflow.getEndDate() != null)
                 // calcul de la durée du workflow, ie entre le début de l'initialisation et la fin de la clôture
                 .map(workflow -> workflow.getStartDate().until(workflow.getEndDate(), ChronoUnit.SECONDS))
                 .mapToLong(Long::longValue)
                 .average()
                 // set value
                 .ifPresent(avg -> dto.setAvgDurWorkflow((long) avg));
    }

    /**
     * Niveau de regroupement
     */
    public enum GroupBy {
        PROJECT,
        LOT,
        DELIVERY
    }
}
