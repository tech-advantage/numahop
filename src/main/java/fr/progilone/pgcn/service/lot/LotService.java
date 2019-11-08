package fr.progilone.pgcn.service.lot;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.lot.SimpleLotDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.project.Project.ProjectStatus;
import fr.progilone.pgcn.domain.workflow.WorkflowModel;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnListValidationException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.lot.helper.LotSearchBuilder;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.es.EsLotService;
import fr.progilone.pgcn.service.exchange.ImportReportService;
import fr.progilone.pgcn.service.util.SortUtils;

/**
 * Service de gestion des lots
 *
 * @author jbrunet
 * Créé le 10 juil. 2017
 */
@Service
public class LotService {

    private static final Logger LOG = LoggerFactory.getLogger(LotService.class);
    
    private final DeliveryRepository deliveryRepository;
    private final DocUnitRepository docUnitRepository;
    private final EsLotService esLotService;
    private final ImportReportService importReportService;
    private final LotRepository lotRepository;
    private final LotValidationService lotValidationService;
    private final ProjectRepository projectRepository;
    private final ConditionReportService conditionReportService;
    

    @Autowired
    public LotService(final DeliveryRepository deliveryRepository,
                      final DocUnitRepository docUnitRepository,
                      final EsLotService esLotService,
                      final ImportReportService importReportService,
                      final LotRepository lotRepository,
                      final LotValidationService lotValidationService,
                      final ProjectRepository projectRepository,
                      final ConditionReportService conditionReportService) {
        this.deliveryRepository = deliveryRepository;
        this.docUnitRepository = docUnitRepository;
        this.esLotService = esLotService;
        this.importReportService = importReportService;
        this.lotRepository = lotRepository;
        this.lotValidationService = lotValidationService;
        this.projectRepository = projectRepository;
        this.conditionReportService = conditionReportService;
    }

    /**
     * Suppression d'un lot depuis son identifiant
     *
     * @param identifier
     *         l'identifiant de l'usager
     * @throws PgcnBusinessException
     *         si la suppression de l'usager échoue
     */
    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        // Validation de la suppression
        final Lot lot = lotRepository.findOne(identifier);
        final PgcnList<PgcnError> errors = validateDelete(lot);
        if (!errors.isEmpty()) {
            throw new PgcnValidationException(lot, errors);
        }
        // Clean Imports
        importReportService.setLotNull(Collections.singletonList(identifier));
        // Suppression
        lotRepository.delete(identifier);
        // Moteur de recherche
        esLotService.deleteAsync(lot);
    }

    /**
     * Suppression d'une liste de lots
     *
     * @param lots
     */
    @Transactional
    public void delete(final Collection<Lot> lots) throws PgcnListValidationException {
        lots.stream().map(AbstractDomainObject::getIdentifier).forEach(this::delete);
    }

    private PgcnList<PgcnError> validateDelete(final Lot lot) {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Livraison
        final Long lotCount = deliveryRepository.countByLot(lot);
        if (lotCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LOT_DEL_EXITS_DELIVERY).setAdditionalComplement(lotCount).build());
        }
        // Unités documentaires
        final Long docCount = docUnitRepository.countByLot(lot);
        if (docCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.LOT_DEL_EXITS_DOCUNIT).setAdditionalComplement(docCount).build());
        }
        // TODO: platform

        if (!errors.isEmpty()) {
            lot.setErrors(errors);
        }
        return errors;
    }

    @Transactional(readOnly = true)
    public List<Lot> findAll(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return lotRepository.findByIdentifierIn(ids, null);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByActive(final boolean active) {
        return lotRepository.findAllByActive(active);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByActiveForDelivery(final boolean active) {
        return lotRepository.findAllByActiveForDelivery(active);
    }
    
    @Transactional(readOnly = true)
    public List<Lot> findAllActiveForMultiLotsDelivery(final boolean active) {
        return lotRepository.findAllActiveForMultiLotsDelivery(active);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByActiveForDocUnit(final boolean active) {
        return lotRepository.findAllByActiveAndTypeAndStatus(active, Lot.Type.PHYSICAL, Lot.LotStatus.CREATED);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByProjectId(final String id) {
        return lotRepository.findAllByProjectIdentifier(id);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllForDocUnitByProjectId(final String id) {
        return lotRepository.findAllByActiveAndTypeAndStatusAndProjectIdentifier(true, Lot.Type.PHYSICAL, Lot.LotStatus.CREATED, id);
    }

    @Transactional(readOnly = true)
    public List<SimpleLotDTO> findAllByProjectIds(final List<String> projectIds) {
        return lotRepository.findAllIdentifiersInProjectIds(projectIds);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAll() {
        return lotRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Lot findByIdentifier(final String id) {
        return lotRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Lot findByIdentifierWithWorkflowModel(final String id) {
        return lotRepository.findOneWithWorkflowModel(id);
    }

    @Transactional(readOnly = true)
    public Lot findByDocUnitIdentifier(final String id) {
        return lotRepository.findOneByDocUnit(id);
    }

    @Transactional(readOnly = true)
    public Lot getOne(final String identifier) {
        return lotRepository.findOneWithDependencies(identifier);
    }
    
    @Transactional(readOnly = true)
    public Lot findOneWithDocsAndWorkflows(final String lotId) {
        return lotRepository.findOneWithDocsAndWorkflows(lotId);
    }

    @Transactional(readOnly = true)
    public Lot getOneWithConfigRules(final String identifier) {

        final Lot lot = lotRepository.findOneWithActiveCheckConfiguration(identifier);
        // retrouve la conf de controle dans la hierarchie
        CheckConfiguration conf = null;
        if (lot.getActiveCheckConfiguration() == null || lot.getActiveCheckConfiguration().getIdentifier() == null) {
            conf = lot.getProject().getActiveCheckConfiguration();
            if (conf == null) {
                conf = lot.getProject().getLibrary().getActiveCheckConfiguration();
            }
            lot.setActiveCheckConfiguration(conf);
        }
        // retrouve la conf de format d'images dans la hierarchie
        ViewsFormatConfiguration formatConf = null;
        if (lot.getActiveFormatConfiguration() == null || lot.getActiveFormatConfiguration().getIdentifier() == null) {
            formatConf = lot.getProject().getActiveFormatConfiguration();
            if (formatConf == null) {
                formatConf = lot.getProject().getLibrary().getActiveFormatConfiguration();
            }
            lot.setActiveFormatConfiguration(formatConf);
        }
        return lot;
    }

    @Transactional(readOnly = true)
    public List<Lot> findByIdentifierIn(final List<String> identifiers) {
        return lotRepository.findByIdentifierIn(identifiers);
    }

    @Transactional
    public Lot save(final Lot lot) throws PgcnValidationException, PgcnBusinessException {
        lotValidationService.validate(lot);

        Lot savedLot = lotRepository.save(lot);
        savedLot = getOne(savedLot.getIdentifier());

        return savedLot;
    }

    /**
     * Retrait du projet d'un lot
     * ?
     *
     * @param id
     */
    @Transactional
    public void unlinkProject(final String id) {
        final Lot lot = findByIdentifier(id);
        lot.setProject(null);
        save(lot);
    }

    @Transactional(readOnly = true)
    public Page<Lot> search(final String search,
                            final List<String> libraries,
                            final List<String> projects,
                            final boolean active,
                            final List<LotStatus> lotStatuses,
                            final List<String> providers,
                            final Integer docNumber,
                            final List<String> fileFormats,
                            final List<String> identifiers,
                            final Integer page,
                            final Integer size,
                            final List<String> sorts) {

        Sort sort = SortUtils.getSort(sorts);
        if (sort == null) {
            sort = new Sort("label");
        }
        final Pageable pageRequest = new PageRequest(page, size, sort);

        return lotRepository.search(new LotSearchBuilder().setSearch(search)
                                                          .setLibraries(libraries)
                                                          .setProjects(projects)
                                                          .setActive(active)
                                                          .setLotStatuses(lotStatuses)
                                                          .setProviders(providers)
                                                          .setDocNumber(docNumber)
                                                          .setFileFormats(fileFormats)
                                                          .setIdentifiers(identifiers), pageRequest);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByLibraryIn(final List<String> libraries) {
        return lotRepository.findAllByActiveAndProjectLibraryIdentifierIn(true, libraries);
    }

    @Transactional(readOnly = true)
    public List<Lot> findAllByProjectIn(final List<String> projects) {
        return lotRepository.findAllByActiveAndProjectIdentifierIn(true, projects);
    }

    /**
     * Récupération de la première configuration FTP active définie dans le lot, dans le projet, et dans la librarie du projet
     *
     * @param lot
     * @return
     */
    @Transactional(readOnly = true)
    public FTPConfiguration getActiveFTPConfiguration(final Lot lot) {
        final Lot lotWithFtpConf = lotRepository.findOneWithActiveFtpConfig(lot.getIdentifier());
        FTPConfiguration activeFTPConfiguration = null;
        if (lotWithFtpConf != null) {
            activeFTPConfiguration = lotWithFtpConf.getActiveFTPConfiguration();

            final Project project = lotWithFtpConf.getProject();
            if (activeFTPConfiguration == null && project != null) {
                activeFTPConfiguration = project.getActiveFTPConfiguration();

                if (activeFTPConfiguration == null) {
                    activeFTPConfiguration = project.getLibrary().getActiveFTPConfiguration();
                }
            }
        }
        return activeFTPConfiguration;
    }

    /**
     * Récupération de la première configuration de contrôle active définie dans le lot, dans le projet, et dans la librarie du projet
     *
     * @param lot
     * @return
     */
    @Transactional(readOnly = true)
    public CheckConfiguration getActiveCheckConfiguration(final Lot lot) {
        final Lot lotWithCheckConf = lotRepository.findOneWithActiveCheckConfiguration(lot.getIdentifier());
        CheckConfiguration activeCheckConfiguration = null;
        if (lotWithCheckConf != null) {
            activeCheckConfiguration = lotWithCheckConf.getActiveCheckConfiguration();

            final Project project = lotWithCheckConf.getProject();
            if (activeCheckConfiguration == null && project != null) {
                activeCheckConfiguration = project.getActiveCheckConfiguration();

                if (activeCheckConfiguration == null) {
                    activeCheckConfiguration = project.getLibrary().getActiveCheckConfiguration();
                }
            }
        }
        return activeCheckConfiguration;
    }

    /**
     * Récupération du model au niveau du lot ou du projet
     *
     * @param lot
     * @return
     */
    @Transactional(readOnly = true)
    public WorkflowModel getWorkflowModel(final Lot lot) {
        WorkflowModel model = null;
        if (lot != null) {
            model = lot.getWorkflowModel();

            final Project project = lot.getProject();
            if (model == null && project != null) {
                model = project.getWorkflowModel();
            }
        }
        return model;
    }

    @Transactional
    public void setProject(final List<String> lotIds, final String project) {
        final Project p = projectRepository.findOne(project);
        final List<Lot> lots = lotRepository.findByIdentifierIn(lotIds);

        for (final Lot lot : lots) {
            lot.setProject(p);
            lotRepository.save(lot);
        }
    }

    @Transactional(readOnly = true)
    public List<Lot> findLotsForWidget(final LocalDate fromDate,
                                       final List<String> libraries,
                                       final List<String> projects,
                                       final List<Lot.LotStatus> status) {

        return lotRepository.findLotsForWidget(fromDate, libraries, projects, status);
    }

    /**
     * Recherche les lots par bibliothèque, groupés par statut
     *
     * @param libraries
     * @param projects
     * @return liste de map avec 2 clés: statut et décompte
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getLotGroupByStatus(final List<String> libraries, final List<String> projects) {
        final List<Object[]> results = lotRepository.getLotGroupByStatus(libraries, projects); // status, count

        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("status", res[0]);
            resMap.put("count", res[1]);
            return resMap;
        }).collect(Collectors.toList());
    }

    /**
     * Valide un lot.
     * le status passe à {@link LotStatus#ONGOING}
     * le status du project passe à {@link ProjectStatus#ONGOING}
     *
     * @param id
     */
    @Transactional
    public Lot validate(final String id) {
        final Lot lot = findByIdentifierWithWorkflowModel(id);
        lot.setStatus(LotStatus.ONGOING);
        lot.getProject().setStatus(ProjectStatus.ONGOING);
        return save(lot);
    }

    @Transactional(readOnly = true)
    public void writeCondReportSlipPDF(final ServletOutputStream outputStream, final String id) throws PgcnTechnicalException {
        final Lot lot = getOne(id);
        final Set<DocUnit> docUnits = lot.getDocUnits();
        conditionReportService.writeSlipDocUnitsPDF(outputStream, docUnits, "Liste des unités documentaires");
    }

    @Transactional(readOnly = true)
    public void writeCondReportSlip(final ServletOutputStream outputStream, final String id, final String encoding, final char separator) throws
                                                                                                                                          IOException {
        final Lot lot = getOne(id);
        final Set<DocUnit> docUnits = lot.getDocUnits();
        conditionReportService.writeSlipDocUnitsCSV(outputStream, docUnits, encoding, separator);
    }
    
    /**
     * Cloture le lot et ses sous-éléments.
     *  
     * @param lot
     * @param endDate
     */
    @Transactional
    public void clotureLotAndCie(final Lot lot, final LocalDateTime endDate) {
        
        lot.setRealEndDate(endDate.toLocalDate());
        lot.setStatus(LotStatus.CLOSED);
        lot.setActive(false);
        save(lot);
        LOG.info("Workflow : Mise a jour du lot {} => lot status : CLOSED", lot.getLabel());
        
        lot.getDocUnits().forEach(du -> {    
            // cloture les docUnits associées
            du.setState(DocUnit.State.CLOSED);
            docUnitRepository.save(du);
            // cloture les livraisons associées
            final DigitalDocument dig = du.getDigitalDocuments().stream().findFirst().orElse(null);
            dig.getDeliveries().stream().forEach(delivered -> {
                final Delivery delivToArchive = delivered.getDelivery(); 
                delivToArchive.setStatus(DeliveryStatus.CLOSED);
                deliveryRepository.save(delivToArchive);
            });
            
        }); 
    }
 

    /**
     * Recherche les lots clôtures par librairie depuis un delai en jours.
     *
     * @param libraryIdentifier
     * @param delay
     */
    @Transactional(readOnly = true)
    public List<Lot> getClosedLotsByLibrary(final String libraryIdentifier, final int delay) {

        final LocalDate dateTo = LocalDate.now().minusDays(delay);
        final List<Lot> lots = lotRepository.getClosedLotsByLibrary(libraryIdentifier, dateTo);
        return lots;
    }

    @Transactional
    public void setFilesLotArchived(final String lotId) {
        final Lot lot = findByIdentifier(lotId);
        lot.setFilesArchived(true);
        save(lot);
    }
}
