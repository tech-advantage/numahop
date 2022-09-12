package fr.progilone.pgcn.service.statistics;

import static fr.progilone.pgcn.domain.workflow.WorkflowStateKey.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import fr.progilone.pgcn.domain.dto.statistics.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.workflow.DocUnitState;
import fr.progilone.pgcn.domain.workflow.DocUnitWorkflow;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.domain.workflow.WorkflowStateStatus;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.delivery.helper.DeliverySearchBuilder;
import fr.progilone.pgcn.repository.workflow.DocUnitWorkflowRepository;
import fr.progilone.pgcn.repository.workflow.helper.DocUnitWorkflowSearchBuilder;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.statistics.mapper.StatisticsWorkflowMapper;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

@Service
public class StatisticsWorkflowService {

    private final DeliveryRepository deliveryRepository;
    private final DocUnitWorkflowRepository docUnitWorkflowRepository;
    private final UserService userService;
    private final ConditionReportService condReportService;
    private final ConditionReportDetailService condReportDetailService;
    private final StatisticsWorkflowMapper statisticsWorkflowMapper;
    private final WorkflowService workflowService;

    @Autowired
    public StatisticsWorkflowService(final DeliveryRepository deliveryRepository,
                                     final DocUnitWorkflowRepository docUnitWorkflowRepository,
                                     final UserService userService,
                                     final ConditionReportService condReportService,
                                     final ConditionReportDetailService condReportDetailService,
                                     final StatisticsWorkflowMapper statisticsWorkflowMapper,
                                     final WorkflowService workflowService) {
        this.deliveryRepository = deliveryRepository;
        this.docUnitWorkflowRepository = docUnitWorkflowRepository;
        this.userService = userService;
        this.condReportService = condReportService;
        this.condReportDetailService = condReportDetailService;
        this.statisticsWorkflowMapper = statisticsWorkflowMapper;
        this.workflowService = workflowService;
    }

    @Transactional(readOnly = true)
    public Page<WorkflowDocUnitProgressDTO> getDocUnitProgressReport(final List<String> libraries,
                                                                     final List<String> projects,
                                                                     final boolean projetActive,
                                                                     final List<String> lots,
                                                                     final List<String> trains,
                                                                     final String pgcnId,
                                                                     final List<WorkflowStateKey> states,
                                                                     final List<WorkflowStateStatus> status,
                                                                     final List<String> users,
                                                                     final LocalDate fromDate,
                                                                     final LocalDate toDate,
                                                                     final int page,
                                                                     final int size) {

        final String userLogin = CollectionUtils.isEmpty(users) ? null : users.get(0);

        return docUnitWorkflowRepository.findDocUnitProgressStats(libraries,
                                                                  projects,
                                                                  projetActive,
                                                                  lots,
                                                                  trains,
                                                                  pgcnId,
                                                                  states,
                                                                  status,
                                                                  users,
                                                                  fromDate,
                                                                  toDate,
                                                                  new PageRequest(page, size)).map(w -> getWorkflowDocUnitProgressDTO(w, null, userLogin));
    }

    @Transactional(readOnly = true)
    public List<WorkflowDocUnitProgressDTOPending> getDocUnitProgressReportPending(final List<String> libraries,
                                                                                   final List<String> projects,
                                                                                   final boolean projetActive,
                                                                                   final List<String> lots,
                                                                                   final List<String> trains,
                                                                                   final String pgcnId,
                                                                                   final List<WorkflowStateKey> states,
                                                                                   final List<WorkflowStateStatus> status,
                                                                                   final List<String> users,
                                                                                   final LocalDate fromDate,
                                                                                   final LocalDate toDate) {

        final String userLogin = CollectionUtils.isEmpty(users) ? null : users.get(0);

        return docUnitWorkflowRepository.findDocUnitProgressStatsPending(libraries,
            projects,
            projetActive,
            lots,
            trains,
            pgcnId,
            states,
            status,
            users,
            fromDate,
            toDate).stream().map(w -> getWorkflowDocUnitProgressDTOLight(w, null, userLogin))  //transform to DTO
                                                 .collect(Collectors.toList());

    }


    @Transactional(readOnly = true)
    public List<WorkflowDocUnitProgressDTO> getDocUnitCurrentReport(final List<String> libraries,
                                                                    final List<String> projects,
                                                                    final List<String> lots,
                                                                    final List<WorkflowStateKey> states,
                                                                    final LocalDate fromDate) {
        final Comparator<WorkflowDocUnitProgressDTO> workflowComparator = Comparator.comparing(dto -> dto.getWorkflow()
                                                                                                         .stream()
                                                                                                         .map(WorkflowDocUnitProgressDTO.WorkflowState::getStartDate)
                                                                                                         .filter(Objects::nonNull)
                                                                                                         .max(Comparator.naturalOrder())
                                                                                                         .orElse(LocalDateTime.MAX));

        final List<WorkflowStateStatus> statusesToExclude = Arrays.stream(WorkflowStateStatus.values())
                                                                  .filter(val -> val != WorkflowStateStatus.FINISHED
                                                                                 && val != WorkflowStateStatus.FAILED
                                                                                 && val != WorkflowStateStatus.PENDING)
                                                                  .collect(Collectors.toList());

        final List<DocUnitWorkflow> pendingDocs = docUnitWorkflowRepository.findPendingDocUnitWorkflows(libraries, projects, lots, null, states, fromDate);

        return pendingDocs.stream()
                   .map(w -> getWorkflowDocUnitProgressDTO(w, null, null))
                   // on ne veut remonter que les étapes en cours ou terminées (#2107)
                   .peek(dto -> dto.getWorkflow()
                                   .removeIf(state -> (CollectionUtils.isEmpty(states) && WorkflowStateKey.INITIALISATION_DOCUMENT == state.getKey()
                                                       || (WorkflowStateKey.NUMERISATION_EN_ATTENTE == state.getKey()
                                                           && WorkflowStateStatus.FINISHED == state.getStatus())
                                                       || statusesToExclude.contains(state.getStatus())
                                                       || state.getStartDate() == null
                                                       || (state.getEndDate() != null && state.getEndDate().isBefore(fromDate.atStartOfDay()))) || (
                                                          CollectionUtils.isNotEmpty(states)
                                                          && !states.contains(state.getKey()))))
                   .sorted(workflowComparator.reversed())
                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<WorkflowDeliveryProgressDTO> getDeliveryProgressReport(final List<String> libraries,
                                                                       final List<String> projects,
                                                                       final List<String> lots,
                                                                       final List<String> deliveries,
                                                                       final String pgcnId,
                                                                       final List<WorkflowStateKey> states,
                                                                       final LocalDate fromDate,
                                                                       final LocalDate toDate,
                                                                       final int page,
                                                                       final int size) {

        return deliveryRepository.search(new DeliverySearchBuilder().setLibraries(libraries)
                                                                    .setProjects(projects)
                                                                    .setLots(lots)
                                                                    .setDeliveries(deliveries)
                                                                    .setDocUnitPgcnId(pgcnId)
                                                                    .setDocUnitStates(states)
                                                                    .setDateFrom(fromDate)
                                                                    .setDateTo(toDate), new PageRequest(page, size))
                                 // chargement des workflows pour chaque livraison
                                 .map(delivery -> {
                                     // UD
                                     final List<String> docUnits = delivery.getDocuments()
                                                                           .stream()
                                                                           .map(deliveredDoc -> deliveredDoc.getDigitalDocument()
                                                                                                            .getDocUnit()
                                                                                                            .getIdentifier())
                                                                           .collect(Collectors.toList());
                                     // UD-Workflow
                                     final List<DocUnitWorkflow> workflows =
                                         docUnits.isEmpty() ? Collections.emptyList() : docUnitWorkflowRepository.findByDocUnitIdentifierIn(docUnits);

                                     return getWorkflowDeliveryProgressDTO(delivery, workflows);
                                 });
    }

    @Transactional(readOnly = true)
    public List<WorkflowDocUnitProgressDTO> getDocUnitForStateControl(final List<String> libraries,
                                                                      final List<String> projects,
                                                                      final List<String> lots,
                                                                      final List<DigitalDocumentStatus> states,
                                                                      final LocalDate fromDate) {

        return docUnitWorkflowRepository.findDocUnitWorkflowsForStateControl(libraries, projects, lots, states, fromDate)
                                        .stream()
                                        .filter(w -> w.getDocUnit() != null)
                                        .map(w -> getWorkflowDocUnitProgressDTO(w, CONTROLE_QUALITE_EN_COURS, null))
                                        // on ne garde que le controle qualite
                                        //.peek(dto -> dto.getWorkflow()
                                        //.removeIf(st -> WorkflowStateKey.CONTROLE_QUALITE_EN_COURS != st.getKey()))
                                        .collect(Collectors.toList());
    }

    /**
     * Recherche les ud groupées par statut
     *
     * @param libraries
     * @param projects
     * @param lots
     * @return liste de map avec 2 clés: statut et décompte
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDocUnitsGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {
        final List<Object[]> results = docUnitWorkflowRepository.getDocUnitsGroupByStatus(libraries, projects, lots);    // status, count

        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("status", res[0]);
            resMap.put("count", res[1]);
            return resMap;
        }).collect(Collectors.toList());
    }

    /**
     * Statistiques de durée des étapes de workflow
     *
     * @param libraries
     * @param workflows
     * @param states
     * @param fromDate
     * @param toDate
     * @return
     */
    @Transactional(readOnly = true)
    public List<WorkflowStateProgressDTO> getWorkflowStatesStatistics(final List<String> libraries,
                                                                      final List<String> workflows,
                                                                      final List<WorkflowStateKey> states,
                                                                      final LocalDate fromDate,
                                                                      final LocalDate toDate) {

        return docUnitWorkflowRepository.findDocUnitWorkflows(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                                .setWorkflows(workflows)
                                                                                                .setStates(states)
                                                                                                .setFromDate(fromDate)
                                                                                                .setToDate(toDate))
                                        .stream()
                                        .filter(w -> w.getDocUnit() != null && w.getDocUnit().getLibrary() != null && w.getModel() != null)
                                        // Regroupement des DocUnitWorkflow par bibliothèque + workflow
                                        .collect(Collectors.groupingBy(w -> Pair.of(w.getDocUnit().getLibrary(), w.getModel())))
                                        .entrySet()
                                        .stream()
                                        // bibliothèque + workflow -> liste d'étapes
                                        .flatMap(e -> {
                                            final Library library = e.getKey().getLeft();
                                            final WorkflowModel workflow = e.getKey().getRight();
                                            final List<DocUnitWorkflow> docWorkflows = e.getValue();

                                            // Regroupement par étape de workflow, achevée
                                            return docWorkflows.stream()
                                                               .flatMap(w -> w.getStates().stream())
                                                               // Filtrage des états
                                                               .filter(st -> filterState(st, states))
                                                               // Filtrage des étapes achevées
                                                               .filter(this::filterFinishedState)
                                                               .collect(Collectors.groupingBy(DocUnitState::getKey,
                                                                                              // la valeur mappée est égale à la durée moyenne des étapes identiques, en secondes
                                                                                              Collectors.mapping(st -> st.getStartDate()
                                                                                                                         .until(st.getEndDate(),
                                                                                                                                ChronoUnit.SECONDS),
                                                                                                                 Collectors.averagingLong(Long::longValue))))
                                                               .entrySet()
                                                               .stream()
                                                               // workflow state -> durée moyenne
                                                               .map(e2 -> {
                                                                   final WorkflowStateKey state = e2.getKey();
                                                                   final Double avgDuration = e2.getValue();

                                                                   // bibliothèque + workflow + étape -> WorkflowStepDurationDTO
                                                                   final WorkflowStateProgressDTO dto = new WorkflowStateProgressDTO();
                                                                   dto.setLibraryIdentifier(library.getIdentifier());
                                                                   dto.setLibraryName(library.getName());
                                                                   dto.setWorkflowModelIdentifier(workflow.getIdentifier());
                                                                   dto.setWorkflowModelName(workflow.getName());
                                                                   dto.setKey(state);
                                                                   dto.setAvgDuration(avgDuration != null ? avgDuration.longValue() : 0);

                                                                   return dto;
                                                               });
                                        })
                                        .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Collection<WorkflowUserProgressDTO> getWorkflowUsersStatistics(final List<String> libraries,
                                                                          final List<String> projects,
                                                                          final List<String> lots,
                                                                          final List<String> deliveries,
                                                                          final LocalDate fromDate,
                                                                          final LocalDate toDate) {

        // Recherche des workflow des UD
        return docUnitWorkflowRepository.findDocUnitWorkflows(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                                .setProjects(projects)
                                                                                                .setLots(lots)
                                                                                                .setDeliveries(deliveries)
                                                                                                .setFromDate(fromDate)
                                                                                                .setToDate(toDate)
                                                                                                .setWithFailedStatuses(true)
                                                                                                .addState(CONTROLE_QUALITE_EN_COURS))
                                        .stream()
                                        .filter(w -> w.getDocUnit() != null && w.getDocUnit().getLibrary() != null)
                                        // listes des [library + user + workflow ud + étapes de workflow "contrôle" terminées]
                                        .flatMap(w -> {
                                            final Library library = w.getDocUnit().getLibrary();
                                            // étapes de contrôle terminées
                                            return w.getStates()
                                                    .stream()
                                                    .filter(st -> st.getKey() == CONTROLE_QUALITE_EN_COURS)
                                                    .filter(this::filterFinishedState)
                                                    .map(st -> new WorkflowUserProgressRegroup(library.getIdentifier(), st.getUser(), w, st));
                                        })
                                        // regroupement par [library + user] identiques
                                        .collect(Collectors.groupingBy(WorkflowUserProgressRegroup::getKey,
                                                                       Collectors.collectingAndThen(Collectors.toList(), list -> {
                                                                           final Library library =
                                                                               list.get(0).getWorkflow().getDocUnit().getLibrary();
                                                                           final String userLogin = list.get(0).getUser();
                                                                           final User user = StringUtils.isNotBlank(userLogin) ?
                                                                                             userService.findByLogin(userLogin) :
                                                                                             null;

                                                                           // construction des DTOs
                                                                           final WorkflowUserProgressDTO dto = new WorkflowUserProgressDTO();
                                                                           // library
                                                                           dto.setLibraryIdentifier(library.getIdentifier());
                                                                           dto.setLibraryName(library.getName());
                                                                           // user
                                                                           dto.setUserLogin(userLogin);
                                                                           if (user != null) {
                                                                               dto.setUserIdentifier(user.getIdentifier());
                                                                               dto.setUserFullName(user.getFullName());
                                                                           }
                                                                           // calcul des stats
                                                                           setWorkflowStateProgressDTO(dto, list);

                                                                           return dto;
                                                                       })))
                                        .values();
    }

    @Transactional(readOnly = true)
    public List<WorkflowUserActivityDTO> getUsersActivityStatistics(final List<String> libraries,
                                                                    final List<String> projects,
                                                                    final List<String> lots,
                                                                    final List<WorkflowStateKey> states,
                                                                    final List<String> roles,
                                                                    final LocalDate fromDate,
                                                                    final LocalDate toDate) {
        // Chargement des DocUnitWorkflow
        final List<DocUnitWorkflow> docUnitWorkflows =
            docUnitWorkflowRepository.findDocUnitWorkflows(new DocUnitWorkflowSearchBuilder().setLibraries(libraries)
                                                                                             .setProjects(projects)
                                                                                             .setLots(lots)
                                                                                             .setStates(states)
                                                                                             .setRoles(roles)
                                                                                             .setFromDate(fromDate)
                                                                                             .setToDate(toDate));
        // Chargement des utilisateurs
        final List<String> logins = docUnitWorkflows.stream()
                                                    .flatMap(w -> w.getStates().stream())
                                                    .map(DocUnitState::getUser)
                                                    .filter(StringUtils::isNotBlank)
                                                    .map(StringUtils::lowerCase)
                                                    .distinct()
                                                    .collect(Collectors.toList());
        final List<User> users = userService.findByLoginIn(logins);
        if (CollectionUtils.isNotEmpty(roles)) {
            users.removeIf(u -> !roles.contains(u.getRole().getIdentifier()));
        }

        return docUnitWorkflows.stream()
                               .filter(docUnitWorkflow -> docUnitWorkflow.getDocUnit() != null)
                               .flatMap(docWorkflow ->
                                            // Workflow d'une UD
                                            docWorkflow.getStates()
                                                       .stream()
                                                       // filtrage des états, qui ont été tous chargés par hibernate
                                                       .filter(state -> filterState(state,
                                                                                    states))
                                                       .filter(state -> filterPeriod(state,
                                                                                     fromDate,
                                                                                     toDate))
                                                       // Étapes
                                                       .map(state -> users.stream()
                                                                          .filter(u -> StringUtils.equalsIgnoreCase(u.getLogin(),
                                                                                                                    state.getUser()))
                                                                          .findAny()
                                                                          .map(user -> {
                                                                              final WorkflowUserActivityDTO dto = new WorkflowUserActivityDTO();
                                                                              // docunit
                                                                              setWorkflowUserActivityDTO(dto,
                                                                                                         docWorkflow.getDocUnit());

                                                                              // user
                                                                              dto.setUserIdentifier(user.getIdentifier());
                                                                              dto.setUserLogin(user.getLogin());
                                                                              dto.setUserFullName(user.getFullName());

                                                                              final Role role = user.getRole();
                                                                              if (role
                                                                                  != null) {
                                                                                  dto.setRoleIdentifier(role.getIdentifier());
                                                                                  dto.setRoleLabel(role.getLabel());
                                                                              }

                                                                              // étape
                                                                              dto.setState(state.getKey());
                                                                              dto.setStartDate(state.getStartDate());
                                                                              dto.setEndDate(state.getEndDate());

                                                                              // Durée
                                                                              if (state.getStartDate()
                                                                                  != null
                                                                                  && state.getEndDate()
                                                                                     != null) {
                                                                                  final long duration = state.getStartDate()
                                                                                                             .until(state.getEndDate(),
                                                                                                                    ChronoUnit.SECONDS);
                                                                                  dto.setDuration(duration);
                                                                              }
                                                                              return dto;
                                                                          })))
                               .filter(Optional::isPresent)
                               .map(Optional::get)
                               .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<WorkflowProfileActivityDTO> getProfilesActivityStatistics(final List<String> libraries,
                                                                          final List<String> projects,
                                                                          final List<String> lots,
                                                                          final List<WorkflowStateKey> states,
                                                                          final List<String> roles,
                                                                          final LocalDate fromDate,
                                                                          final LocalDate toDate) {
        return getUsersActivityStatistics(libraries, projects, lots, states, roles, fromDate, toDate).stream()
                                                                                                     .map(statisticsWorkflowMapper::userToProfileDto)
                                                                                                     .collect(Collectors.toList());
    }

    /**
     * Conversion des {@link DocUnitWorkflow} en {@link WorkflowDocUnitProgressDTO}
     *
     * @param workflow
     * @return
     */
    private WorkflowDocUnitProgressDTO getWorkflowDocUnitProgressDTO(final DocUnitWorkflow workflow, final WorkflowStateKey stateToKeep, final String userLogin) {
        final WorkflowDocUnitProgressDTO dto = new WorkflowDocUnitProgressDTO();

        final DocUnit docUnit = workflow.getDocUnit();
        if (docUnit == null) {
            return dto;
        }

        final Library library = docUnit.getLibrary();
        final Project project = docUnit.getProject();
        final Lot lot = docUnit.getLot();

        final Optional<BibliographicRecord> record = docUnit.getRecords().stream().findFirst();

        final Set<PhysicalDocument> physicalDocuments = docUnit.getPhysicalDocuments();
        final Train train;
        if (CollectionUtils.isNotEmpty(physicalDocuments)) {
            train = physicalDocuments.iterator().next().getTrain();
        } else {
            train = null;
        }

        dto.setDocIdentifier(docUnit.getIdentifier());
        dto.setDocPgcnId(docUnit.getPgcnId());
        dto.setDocLabel(docUnit.getLabel());

        if (library != null) {
            dto.setLibraryIdentifier(library.getIdentifier());
            dto.setLibraryName(library.getName());
        }
        if (project != null) {
            dto.setProjectIdentifier(project.getIdentifier());
            dto.setProjectName(project.getName());
        }
        if (lot != null) {
            dto.setLotIdentifier(lot.getIdentifier());
            dto.setLotLabel(lot.getLabel());
        }
        if (train != null) {
            dto.setTrainIdentifier(train.getIdentifier());
            dto.setTrainLabel(train.getLabel());
        }

        final User user;
        if (userLogin != null) {
            user = userService.findByLogin(userLogin);
        } else {
            user = null;
        }

        workflow.getStates().stream().filter(state -> stateToKeep == null || stateToKeep == state.getKey())
                                    .filter(state -> user == null
                                            || (user != null && workflowService.canUserProcessTask(user.getIdentifier(), state)))
                // dto
                .map(state -> {
                    final WorkflowDocUnitProgressDTO.WorkflowState workflowState = new WorkflowDocUnitProgressDTO.WorkflowState();
                    workflowState.setKey(state.getKey());
                    workflowState.setStatus(state.getStatus());
                    workflowState.setStartDate(state.getStartDate());
                    workflowState.setEndDate(state.getEndDate());
                    return workflowState;
                }).forEach(dto::addWorkflow);

        return setInfosAndNumberPageToDTO(dto, docUnit, physicalDocuments, lot, record);
    }

    //FIXME: To factorise a lot of code, I need to use WorkflowDocUnitProgressDTO and transfer data to tu DTOPending
    //FIXME: Maybe find something more efficient
    private WorkflowDocUnitProgressDTOPending getWorkflowDocUnitProgressDTOLight(final DocUnitWorkflow workflow, final WorkflowStateKey stateToKeep, final String userLogin) {
        final WorkflowDocUnitProgressDTOPending pendingDTO = new WorkflowDocUnitProgressDTOPending();
        final DocUnit docUnit = workflow.getDocUnit();
        WorkflowDocUnitProgressDTO dto = new WorkflowDocUnitProgressDTO();

        if (docUnit == null) return pendingDTO;

        final Project project = docUnit.getProject();
        final Lot lot = docUnit.getLot();

        final Optional<BibliographicRecord> record = docUnit.getRecords().stream().findFirst();

        final Set<PhysicalDocument> physicalDocuments = docUnit.getPhysicalDocuments();

        pendingDTO.setDocIdentifier(docUnit.getIdentifier());
        pendingDTO.setDocPgcnId(docUnit.getPgcnId());
        pendingDTO.setDocLabel(docUnit.getLabel());

        if (project != null) {
            pendingDTO.setProjectIdentifier(project.getIdentifier());
            pendingDTO.setProjectName(project.getName());
        }
        if (lot != null) {
            pendingDTO.setLotIdentifier(lot.getIdentifier());
            pendingDTO.setLotLabel(lot.getLabel());
        }

        final User user;

        if (userLogin != null) {
            user = userService.findByLogin(userLogin);
        }
        else {
            user = null;
        }

        dto = setInfosAndNumberPageToDTO(dto, docUnit, physicalDocuments, lot, record);

        pendingDTO.setWorkflowStateKeys(workflow.getStates().stream().filter(state -> (stateToKeep == null || stateToKeep == state.getKey()) && (user != null && workflowService.canUserProcessTask(user.getIdentifier(), state)))
                                     .filter(state -> state.getStatus().equals(WorkflowStateStatus.PENDING))
                                     .map(state -> state.getKey().toString()).collect(Collectors.toList()));

        pendingDTO.setTotalPage(dto.getTotalPage());
        pendingDTO.setDocStatus(dto.getDocStatus());
        pendingDTO.setInfos(dto.getInfos());

        return pendingDTO;
    }

    private WorkflowDocUnitProgressDTO setInfosAndNumberPageToDTO(WorkflowDocUnitProgressDTO dto, DocUnit docUnit, Set<PhysicalDocument> physicalDocuments, Lot lot, Optional<BibliographicRecord> record) {
        final WorkflowDocUnitInfoDTO infos = new WorkflowDocUnitInfoDTO();
        final Optional<DigitalDocument> digitalDoc =
            docUnit.getDigitalDocuments().stream().filter(digDoc -> !StringUtils.isEmpty(digDoc.getDigitalId())).findFirst();
        if (digitalDoc.isPresent()) {
            infos.setRadical(digitalDoc.get().getDigitalId());
            // nb pages du doc (celui du doc physique n'est pas fiable)
            dto.setTotalPage(digitalDoc.get().getPageNumber());
            dto.setDocStatus(digitalDoc.get().getStatus().name());
        } else {
            // Nb pages du constat
            final ConditionReport report = condReportService.findByDocUnit(docUnit.getIdentifier());
            final Optional<ConditionReportDetail> detail;
            if (report != null) {
                detail = condReportDetailService.getLatest(report);
            } else {
                detail = Optional.empty();
            }
            if (detail.isPresent() && detail.get().getNbViewTotal() > 0) {
                dto.setTotalPage(detail.get().getNbViewTotal());
            } else {
                // et sinon nbpages du doc physique
                if (CollectionUtils.isNotEmpty(physicalDocuments)) {
                    final int totalPages =
                        physicalDocuments.stream().filter(ph -> ph.getTotalPage() != null).mapToInt(PhysicalDocument::getTotalPage).sum();
                    dto.setTotalPage(totalPages);
                }
            }
        }

        if (lot != null) {
            infos.setLot(lot.getIdentifier());
        }
        record.ifPresent(bibliographicRecord -> infos.setRecord(bibliographicRecord.getIdentifier()));

        dto.setInfos(infos);

        return dto;
    }


    /**
     * Conversion des {@link DocUnitWorkflow} en {@link WorkflowDeliveryProgressDTO}
     *
     * @param workflows
     * @return
     */
    private WorkflowDeliveryProgressDTO getWorkflowDeliveryProgressDTO(final Delivery delivery, final List<DocUnitWorkflow> workflows) {
        final WorkflowDeliveryProgressDTO dto = new WorkflowDeliveryProgressDTO();
        dto.setDeliveryIdentifier(delivery.getIdentifier());
        dto.setDeliveryLabel(delivery.getLabel());
        dto.setDocUnitNumber(String.valueOf(delivery.getDocuments().size()));

        // Lot
        final Lot lot = delivery.getLot();
        if (lot != null) {
            dto.setLotIdentifier(lot.getIdentifier());
            dto.setLotLabel(lot.getLabel());

            // Projet
            final Project project = lot.getProject();
            if (project != null) {
                dto.setProjectIdentifier(project.getIdentifier());
                dto.setProjectName(project.getName());

                // Bibliothèque
                final Library library = project.getLibrary();
                if (library != null) {
                    dto.setLibraryIdentifier(library.getIdentifier());
                    dto.setLibraryName(library.getName());
                }
            }
        }

        for (final DocUnitWorkflow workflow : workflows) {
            final DocUnit docUnit = workflow.getDocUnit();
            if (docUnit == null) {
                continue;
            }

            // Workflow
            final List<DocUnitState> docUnitStates = workflow.getStates().stream()
                                                             // étapes de workflow en cours
                                                             .filter(state -> state.getStartDate() != null && state.getEndDate() == null)
                                                             .collect(Collectors.toList());

            // Toutes les étapes sont terminées
            if(docUnitStates.size() == 0) {
                // dernière étape terminée
                // (souvent la diffusion et la cloture se termine en même temps, on trie par Key pour faire remonter la cloture avant)
                final Optional<DocUnitState> docUnitState = workflow.getStates().stream()
                                                                    .filter(state -> state.getEndDate() != null)
                                                                    .sorted((s1,s2)-> s2.getKey().compareTo(s1.getKey()))
                                                                    .max(Comparator.comparing(DocUnitState::getEndDate));

                if (docUnitState.isPresent()) {
                    final DocUnitState state = docUnitState.get();
                    incrementWorkflowStepDto(dto, state);
                }
            } else {
                // màj du dto
                docUnitStates.forEach(state -> {
                    incrementWorkflowStepDto(dto, state);
                });
            }
        }

        return dto;
    }

    /**
     *
     * @param dto WorkflowDeliveryProgressDTO
     * @param state DocUnitState
     */
    private void incrementWorkflowStepDto(final WorkflowDeliveryProgressDTO dto, final DocUnitState state) {
        final WorkflowDeliveryProgressDTO.WorkflowState dtoStep
            // recherche / création de l'étape de workflow en cours, dans le dto
            = dto.getWorkflow().stream()
                 // étape du workflow dans le dto
                 .filter(dtoWorkflow -> dtoWorkflow.getKey() == state.getKey()).findAny()
                 // ou création d'une nouvelle étape dans le dto
                 .orElseGet(() -> {
                     final WorkflowDeliveryProgressDTO.WorkflowState workflowState = new WorkflowDeliveryProgressDTO.WorkflowState();
                     workflowState.setKey(state.getKey());
                     dto.addWorkflow(workflowState);

                     return workflowState;
                 });

        dtoStep.incrementCount();
    }

    private void setWorkflowStateProgressDTO(final WorkflowUserProgressDTO dto, final List<WorkflowUserProgressRegroup> list) {
        // Nombre d'unités documentaires contrôlées
        dto.setNbDocUnit(list.stream().map(g -> g.getWorkflow().getDocUnit()).distinct().count());

        // rejetees / validees
        final long nbVal = list.stream()
                                .filter(g -> WorkflowStateKey.CONTROLE_QUALITE_EN_COURS == g.getState().getKey())
                                .filter(g -> WorkflowStateStatus.FINISHED == g.getState().getStatus())
                                .map(g -> g.getWorkflow().getDocUnit())
                                .distinct().count();
        dto.setNbRejectedDocUnit(dto.getNbDocUnit()-nbVal);
        dto.setNbValidatedDocUnit(nbVal);

        // Nombre moyen de pages
        list.stream()
            .map(g -> g.getWorkflow().getDocUnit())
            .distinct()
            .mapToInt(doc -> doc.getPhysicalDocuments()
                                .stream()
                                .filter(ph -> ph.getTotalPage() != null)
                                .mapToInt(PhysicalDocument::getTotalPage)
                                .sum())
            .average()
            .ifPresent(avg -> dto.setAvgTotalPages((int) avg));

        // Délai moyen de contrôle
        list.stream()
            .map(WorkflowUserProgressRegroup::getState)
            .mapToLong(st -> st.getStartDate().until(st.getEndDate(), ChronoUnit.SECONDS))
            .average()
            .ifPresent(avg -> dto.setAvgDuration((long) avg));
    }

    /**
     * Initialisation des champs d'un WorkflowUserActivityDTO
     *
     * @param dto
     * @param docUnit
     */
    private void setWorkflowUserActivityDTO(final WorkflowUserActivityDTO dto, final DocUnit docUnit) {
        dto.setDocIdentifier(docUnit.getIdentifier());
        dto.setDocPgcnId(docUnit.getPgcnId());

        final Lot lot = docUnit.getLot();
        if (lot != null) {
            dto.setLotIdentifier(lot.getIdentifier());
            dto.setLotLabel(lot.getLabel());

            final Project project = lot.getProject();
            if (project != null) {
                dto.setProjectIdentifier(project.getIdentifier());
                dto.setProjectName(project.getName());

                final Library library = project.getLibrary();
                if (library != null) {
                    dto.setLibraryIdentifier(library.getIdentifier());
                    dto.setLibraryName(library.getName());
                }
            }
        }
    }

    private boolean filterState(final DocUnitState state, final List<WorkflowStateKey> states) {
        return CollectionUtils.isEmpty(states) || states.contains(state.getKey());
    }

    private boolean filterFinishedState(final DocUnitState st) {

        return st.getStartDate() != null && st.getEndDate() != null;
    }

    private boolean filterPeriod(final DocUnitState state, final LocalDate fromDate, final LocalDate toDate) {
        return (fromDate == null || (state.getEndDate() != null && state.getEndDate().isAfter(fromDate.atStartOfDay())))
               //
               && (toDate == null || (state.getStartDate() != null && state.getStartDate().isBefore(toDate.plusDays(1).atStartOfDay())));
    }

    /**
     * Regroupement des stats de workflow par user
     */
    private static final class WorkflowUserProgressRegroup {

        final String library;
        final String user;
        final DocUnitWorkflow workflow;
        final DocUnitState state;

        private WorkflowUserProgressRegroup(final String library, final String user, final DocUnitWorkflow workflow, final DocUnitState state) {
            this.library = library;
            this.user = user;
            this.workflow = workflow;
            this.state = state;
        }

        public String getLibrary() {
            return library;
        }

        public String getUser() {
            return user;
        }

        public DocUnitWorkflow getWorkflow() {
            return workflow;
        }

        public DocUnitState getState() {
            return state;
        }

        public String getKey() {
            return library + "#" + user;
        }
    }
}
