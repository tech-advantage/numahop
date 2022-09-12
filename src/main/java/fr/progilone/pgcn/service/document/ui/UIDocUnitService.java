package fr.progilone.pgcn.service.document.ui;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import fr.progilone.pgcn.domain.administration.ExportFTPDeliveryFolder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import fr.progilone.pgcn.domain.administration.SftpConfiguration;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocSibling;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitDeletedReportDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitMassUpdateDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitUpdateErrorDTO;
import fr.progilone.pgcn.domain.dto.document.MinimalListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitWithLotDTO;
import fr.progilone.pgcn.domain.dto.exchange.InternetArchiveReportDTO;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnLockException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.service.LockService;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.mapper.DocUnitMapper;
import fr.progilone.pgcn.service.document.mapper.SimpleDocUnitMapper;
import fr.progilone.pgcn.service.document.mapper.UIDocUnitMapper;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.cines.CinesRequestHandlerService;
import fr.progilone.pgcn.service.exchange.cines.ExportMetsService;
import fr.progilone.pgcn.service.exchange.cines.ui.UICinesReportService;
import fr.progilone.pgcn.service.exchange.ead.ExportEadService;
import fr.progilone.pgcn.service.exchange.internetarchive.ui.UIInternetArchiveReportService;
import fr.progilone.pgcn.service.exchange.ssh.SftpService;
import fr.progilone.pgcn.service.exportftpconfiguration.ExportFTPConfigurationService;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLanguageMapper;
import fr.progilone.pgcn.service.storage.AltoService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.util.CryptoService;
import fr.progilone.pgcn.service.util.DateUtils;
import fr.progilone.pgcn.service.util.ImageUtils;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import fr.progilone.pgcn.service.workflow.WorkflowService;

/**
 * Service dédié à les gestion des vues des unités documentaires
 *
 * @author jbrunet
 */
@Service
public class UIDocUnitService {

    private static final Logger LOG = LoggerFactory.getLogger(UIDocUnitService.class);

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.ftpexport.cache}")
    private String ftpExportCacheDir;

    private final DocUnitService docUnitService;
    private final ExportEadService exportEadService;
    private final UIDocUnitMapper uiDocUnitMapper;
    private final UICinesReportService uiCinesReportService;
    private final UIInternetArchiveReportService uiIAReportService;
    private final ExportMetsService exportMetsService;
    private final BinaryStorageManager bm;
    private final UIBibliographicRecordService uiBibliographicRecordService;
    private final LockService lockService;
    private final WorkflowService workflowService;
    private final EsDocUnitService esDocUnitService;
    private final CinesRequestHandlerService cinesRequestHandlerService;
    private final SftpService sftpService;
    private final CryptoService cryptoService;
    private final LotRepository lotRepository;
    private final DeliveryService deliveryService;
    private final DigitalDocumentService digitalDocumentService;
    private final AltoService altoService;

    @Autowired
    public UIDocUnitService(final DocUnitService docUnitService,
                            final ExportEadService exportEadService,
                            final UIDocUnitMapper uiDocUnitMapper,
                            final UICinesReportService uiCinesReportService,
                            final UIInternetArchiveReportService uiIAReportService,
                            final ExportMetsService exportMetsService,
                            final BinaryStorageManager bm,
                            final UIBibliographicRecordService uiBibliographicRecordService,
                            final LockService lockService,
                            final WorkflowService workflowService,
                            final EsDocUnitService esDocUnitService,
                            final CinesRequestHandlerService cinesRequestHandlerService,
                            final ExportFTPConfigurationService exportFTPConfigurationService,
                            final SftpService sftpService,
                            final CryptoService cryptoService,
                            final LotRepository lotRepository,
                            final DeliveryService deliveryService,
                            final DigitalDocumentService digitalDocumentService,
                            final AltoService altoService) {
        this.docUnitService = docUnitService;
        this.exportEadService = exportEadService;
        this.uiDocUnitMapper = uiDocUnitMapper;
        this.uiCinesReportService = uiCinesReportService;
        this.uiIAReportService = uiIAReportService;
        this.exportMetsService = exportMetsService;
        this.bm = bm;
        this.uiBibliographicRecordService = uiBibliographicRecordService;
        this.lockService = lockService;
        this.workflowService = workflowService;
        this.esDocUnitService = esDocUnitService;
        this.cinesRequestHandlerService = cinesRequestHandlerService;
        this.sftpService = sftpService;
        this.cryptoService = cryptoService;
        this.lotRepository = lotRepository;
        this.deliveryService = deliveryService;
        this.digitalDocumentService = digitalDocumentService;
        this.altoService = altoService;
    }

    @PostConstruct
    public void initialize() {
        // 1 disk space per library
        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(ftpExportCacheDir, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });
    }

    /**
     * Création d'une unité documentaire
     *
     * @param request
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public DocUnitDTO create(final DocUnitDTO request) throws PgcnValidationException {
        validate(request);

        final DocUnit doc = docUnitService.initializeDocUnit();
        uiDocUnitMapper.mapInto(request, doc);

        if (request.getParentIdentifier() != null) {
            final DocUnit parent = docUnitService.findOneWithAllDependencies(request.getParentIdentifier());
            setParent(doc, parent, true);
        }

        final DocUnit savedDoc = docUnitService.save(doc);
        return findAndMapDocUnit(savedDoc.getIdentifier());
    }

    /**
     * Mise à jour d'une unité documentaire
     *
     * @param request
     *            un objet contenant les informations necessaires à l'enregistrement d'une unité doc
     * @return l'unité doc nouvellement créée ou mise à jour
     * @throws PgcnValidationException
     */
    @Transactional
    public DocUnitDTO update(final DocUnitDTO request) throws PgcnValidationException {
        validate(request);
        final DocUnit doc = docUnitService.findOneWithAllDependencies(request.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(doc, request);

        uiDocUnitMapper.mapInto(request, doc);

        // Le parent est mis à jour
        if (request.getParentIdentifier() != null && (doc.getParent() == null || !StringUtils.equals(request.getParentIdentifier(),
                                                                                                     doc.getParent().getIdentifier()))) {
            final DocUnit parent = docUnitService.findOneWithAllDependencies(request.getParentIdentifier());
            setParent(doc, parent, false);
        }

        final DocUnit savedDoc = docUnitService.save(doc);
        return findAndMapDocUnit(savedDoc.getIdentifier());
    }

    /**
     * Récupère une unité doc avec ses dépendences
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnitDTO getOne(final String id) {
        return findAndMapDocUnit(id);
    }

    /**
     * Gère la récupération du docUnit
     * Map dans un DTO
     * Récupère les infos supplémentaires et les ajoute au DTO
     *
     * @param identifier
     * @return
     */
    private DocUnitDTO findAndMapDocUnit(final String identifier) {
        final DocUnit doc = docUnitService.findOneForDisplay(identifier);
        if (doc == null) {
            return null;
        }

        final DocUnitDTO dto = mapIntoDTO(doc);
        // Ajout rapports
        dto.setCinesReports(uiCinesReportService.findAllByDocUnitIdentifier(identifier));
        final List<InternetArchiveReportDTO> iareports = uiIAReportService.findAllByDocUnitIdentifier(identifier);
        dto.setIaReports(iareports);
        // Recuperation url ark si besoin et non deja renseignee.
        if (StringUtils.isBlank(doc.getArkUrl()) && CollectionUtils.isNotEmpty(iareports)) {
            final InternetArchiveReportDTO lastModified = iareports.get(0);
            final LocalDateTime dateFrom = LocalDateTime.now().minusDays(7L);
            if (StringUtils.equals("ARCHIVED", lastModified.getStatus()) && lastModified.getDateArchived().isAfter(dateFrom)) {
                dto.setArkUrl(uiIAReportService.getIaArkUrl(lastModified.getInternetArchiveIdentifier()));
            }
        }

        dto.setEadExport(exportEadService.hasEadExport(identifier));
        // Ajout total de masters
        dto.setTotal(0);
        doc.getDigitalDocuments().forEach(digitalDoc -> {
            dto.setTotal(dto.getTotal() + digitalDoc.getNbPages());
        });
        return dto;
    }

    private DocUnitDTO mapIntoDTO(final DocUnit doc) {
        final DocUnitDTO result = DocUnitMapper.INSTANCE.docUnitToDocUnitDTO(doc);
        if (doc != null) {
            if (CollectionUtils.isNotEmpty(doc.getPhysicalDocuments())) {
                result.setDigitalId(doc.getPhysicalDocuments().iterator().next().getDigitalId());
            }
            if (result.getActiveOcrLanguage() == null) {
                if (doc.getLot() != null && doc.getLot().getActiveOcrLanguage() != null) {
                    result.setActiveOcrLanguage(OcrLanguageMapper.INSTANCE.objToDTO(doc.getLot().getActiveOcrLanguage()));
                }
            }
        }
        return result;
    }

    @Transactional
    public DocUnitDeletedReportDTO delete(final String id) throws PgcnBusinessException {
        final DocUnitDeletedReportDTO entityDeletedReport = Iterables.getOnlyElement(docUnitService.canDocUnitBeDeleted(Collections.singleton(id)));
        if (entityDeletedReport.getErrors() == null || entityDeletedReport.getErrors().isEmpty()) {
            docUnitService.delete(id);
        }
        return entityDeletedReport;
    }

    @Transactional
    public Collection<DocUnitDeletedReportDTO> delete(final List<String> ids) throws PgcnBusinessException {
        final Collection<DocUnitDeletedReportDTO> entityDeletedReports = docUnitService.canDocUnitBeDeleted(ids);
        final ImmutableCollection<String> toBeDeleted = difference(ids, entityDeletedReports);
        if (!toBeDeleted.isEmpty()) {
            docUnitService.delete(toBeDeleted);
        }
        return entityDeletedReports;
    }

    @Transactional
    public Collection<DocUnitDeletedReportDTO> deleteDocUnitsProject(final List<String> ids) throws PgcnBusinessException {
        final Collection<DocUnitDeletedReportDTO> entityDeletedReports = docUnitService.canDocUnitProjectBeDeleted(ids);
        final ImmutableCollection<String> toBeDeleted = difference(ids, entityDeletedReports);
        if (!toBeDeleted.isEmpty()) {
            docUnitService.deleteFromProject(toBeDeleted);
        }
        return entityDeletedReports;
    }

    @Transactional
    public Collection<DocUnitDeletedReportDTO> unlink(final List<String> ids) {
        final Collection<DocUnitDeletedReportDTO> entityUnlinkedReports = docUnitService.canDocUnitBeUnlinked(ids);
        final ImmutableCollection<String> toBeUnlinked = difference(ids, entityUnlinkedReports);
        if (!toBeUnlinked.isEmpty()) {
            docUnitService.unlink(ids);
        }
        return entityUnlinkedReports;
    }

    /**
     * Retourne les identifiants des entités qui ne sont pas présents dans la liste des rapports d'erreurs
     *
     * @param entityIds
     *            une liste d'identifiants d'entités
     * @param entityDeletedReports
     *            une liste de rapport d'erreurs
     * @return la différence entre les deux listes
     */
    private ImmutableCollection<String> difference(final List<String> entityIds, final Collection<DocUnitDeletedReportDTO> entityDeletedReports) {

        final ImmutableSet<String> entityIdsSet = ImmutableSet.copyOf(entityIds);
        final Set<String> deletedReportIds = entityDeletedReports.stream().map(report -> {
            if (report.getErrors() != null && !report.getErrors().isEmpty()) {
                return report.getIdentifier();
            }
            return null;
        }).collect(Collectors.toSet());

        return Sets.difference(entityIdsSet, deletedReportIds).immutableCopy();
    }

    public void createProjectFromDoc(final List<DocUnitDTO> docs) throws PgcnBusinessException {
        docs.forEach(doc -> {
            if (doc != null) {
                docUnitService.delete(doc.getIdentifier());
            }
        });
    }

    @Transactional
    public List<SimpleDocUnitDTO> findAllSimpleDTO() {
        final List<DocUnit> docs = docUnitService.findAll();
        return docs.stream().map(SimpleDocUnitMapper.INSTANCE::docUnitToSimpleDocUnitDTO).collect(Collectors.toList());
    }

    /**
     * Recherches
     *
     * @param search
     * @param hasDigitalDocuments
     * @param libraries
     * @param projects
     * @param lots
     * @param lastModifiedDateFrom
     * @param lastModifiedDateTo
     * @param createdDateFrom
     * @param createdDateTo
     * @param page
     * @param size
     * @param sorts
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SimpleDocUnitDTO> search(final String search,
                                         final boolean hasDigitalDocuments,
                                         final boolean active,
                                         final boolean archived,
                                         final boolean nonArchived,
                                         final boolean archivable,
                                         final boolean nonArchivable,
                                         final boolean distributed,
                                         final boolean nonDistributed,
                                         final boolean diffusable,
                                         final boolean nonDiffusable,
                                         final List<String> libraries,
                                         final List<String> projects,
                                         final List<String> lots,
                                         final List<String> statuses,
                                         final LocalDate lastModifiedDateFrom,
                                         final LocalDate lastModifiedDateTo,
                                         final LocalDate createdDateFrom,
                                         final LocalDate createdDateTo,
                                         final Integer page,
                                         final Integer size,
                                         final List<String> sorts) {
        final Page<DocUnit> docs = docUnitService.search(search,
                                                         hasDigitalDocuments,
                                                         active,
                                                         archived,
                                                         nonArchived,
                                                         archivable,
                                                         nonArchivable,
                                                         distributed,
                                                         nonDistributed,
                                                         diffusable,
                                                         nonDiffusable,
                                                         libraries,
                                                         projects,
                                                         lots,
                                                         Collections.emptyList(),
                                                         statuses,
                                                         lastModifiedDateFrom,
                                                         lastModifiedDateTo,
                                                         createdDateFrom,
                                                         createdDateTo,
                                                         null,
                                                         page,
                                                         size,
                                                         sorts);
        return docs.map(SimpleDocUnitMapper.INSTANCE::docUnitToSimpleDocUnitDTO);
    }

    @Transactional(readOnly = true)
    public Page<SimpleListDocUnitDTO> searchAsList(final String search,
                                                   final boolean hasDigitalDocuments,
                                                   final boolean active,
                                                   final boolean archived,
                                                   final boolean nonArchived,
                                                   final boolean archivable,
                                                   final boolean nonArchivable,
                                                   final boolean distributed,
                                                   final boolean nonDistributed,
                                                   final boolean diffusable,
                                                   final boolean nonDiffusable,
                                                   final List<String> libraries,
                                                   final List<String> projects,
                                                   final List<String> lots,
                                                   final List<String> trains,
                                                   final List<String> statuses,
                                                   final LocalDate lastModifiedDateFrom,
                                                   final LocalDate lastModifiedDateTo,
                                                   final LocalDate createdDateFrom,
                                                   final LocalDate createdDateTo,
                                                   final List<String> identifiers,
                                                   final Integer page,
                                                   final Integer size,
                                                   final List<String> sorts) {
        final Page<DocUnit> docs = docUnitService.search(search,
                                                         hasDigitalDocuments,
                                                         active,
                                                         archived,
                                                         nonArchived,
                                                         archivable,
                                                         nonArchivable,
                                                         distributed,
                                                         nonDistributed,
                                                         diffusable,
                                                         nonDiffusable,
                                                         libraries,
                                                         projects,
                                                         lots,
                                                         trains,
                                                         statuses,
                                                         lastModifiedDateFrom,
                                                         lastModifiedDateTo,
                                                         createdDateFrom,
                                                         createdDateTo,
                                                         identifiers,
                                                         page,
                                                         size,
                                                         sorts);
        return docs.map(SimpleDocUnitMapper.INSTANCE::docUnitToSimpleListDocUnitDTO);
    }

    @Transactional(readOnly = true)
    public List<MinimalListDocUnitDTO> searchAsMinList(final String search,
                                                       final List<String> libraries,
                                                       final List<String> projects,
                                                       final List<String> lots,
                                                       final List<String> trains,
                                                       final List<String> statuses) {

        final List<DocUnit> docs = docUnitService.searchMinList(search,
                                                                libraries,
                                                                projects,
                                                                lots,
                                                                trains,
                                                                statuses);

        return docs.stream().map(SimpleDocUnitMapper.INSTANCE::docUnitToMinimalListDocUnitDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SummaryDocUnitWithLotDTO> searchAllForProject(final String projectId, final Integer page, final Integer size) {
        final Page<DocUnit> docs = docUnitService.searchAllForProject(projectId, page, size);
        return docs.map(DocUnitMapper.INSTANCE::docUnitToDocUnitSummaryWithLotDTO);
    }

    @Transactional
    public void setProjectAndLot(final List<String> docs, final String project, final String lot, final String train) {

        final Lot l = StringUtils.isBlank(lot) ? null : lotRepository.findOne(lot);
        final Set<DocUnit> dus = docUnitService.findByIdentifierInWithDocs(docs);

        // En cas de lot renum, il faut s'assurer que les workflows des autres docs en cours ne restent pas bloqués...
        if (isLotRenum(dus, l)) {
            checkWorkflowStateforRenum(dus);
        }

        docUnitService.setProjectAndLot(dus, project, l, train);
    }

    /**
     * Retourne vrai si on cree un lot de renumerisation.
     *
     * @param dus
     * @param lot
     * @return
     */
    protected boolean isLotRenum(final Set<DocUnit> dus, final Lot lot) {

        return lot != null && dus.stream()
           .anyMatch(du -> du.getLot() != null
                         && !du.getLot().getIdentifier().equals(lot.getIdentifier()));
    }

    /**
     * Appelée uniquement à la création d'un train de renumérisation.
     *
     * @param docUnitIds
     */
    @Transactional
    public void checkWorkflowStateforTrainRenum(final List<String> docUnitIds) {
        checkWorkflowStateforRenum(docUnitService.findByIdentifierInWithDocs(docUnitIds));
    }

    /**
     * Appelée uniquement à la création d'un train/lot de renumérisation
     * pour vérifier que le changement de delivery des docs à renumeriser
     * ne puisse pas provoquer le blocage de workflows des autres docs de la livraison...
     * Si c'est le cas => on fait avancer les workflows concernés (processe l'envoi du rapport)
     *
     * @param dus
     */
    protected void checkWorkflowStateforRenum(final Set<DocUnit> dus) {

        final Set<String> renumDocUnitIds = dus.stream().map(d -> d.getIdentifier()).collect(Collectors.toSet());
        final Set<String> deliveriesToCheck = new HashSet<>();

        // recupere les livraisons concernées par ces mouvement de docUnits.
        dus.forEach(du -> {

            final DigitalDocument digDoc = du.getDigitalDocuments().stream().findFirst().orElse(null);
            // find delivery from last delivereddoc
            if(digDoc != null){
                final String lastDelivId = digDoc.getDeliveries()
                                                 .stream()
                                                 .filter(deliv -> deliv.getDelivery() != null)
                                                 .sorted(Collections.reverseOrder(Comparator.nullsLast(Comparator.comparing(DeliveredDocument::getCreatedDate))))
                                                 .map(deliv -> deliv.getDelivery().getIdentifier())
                                                 .findFirst()
                                                 .orElse(null);
                if(lastDelivId != null){
                    deliveriesToCheck.add(lastDelivId);
                }
            }
        });

        // Dans chacune de ces livraisons...
        deliveriesToCheck.forEach(deliveryId -> {

            final Set<String> possibleRunningStates = new HashSet<>();
            final Map<String, Optional<DeliveredDocument>> docUnitIdsToTreat = new HashMap<>();
            final Delivery delivery = deliveryService.findOneWithDep(deliveryId);

            for (final DeliveredDocument delivered : delivery.getDocuments()) {

                if (delivered.getDigitalDocument() == null
                    || delivered.getDigitalDocument().getDocUnit() == null) {
                    continue;
                }
                final String docUnitId = delivered.getDigitalDocument().getDocUnit().getIdentifier();
                if (renumDocUnitIds.contains(docUnitId)) {  // ne tient pas compte des docUnits qui sortent pour renum
                    continue;
                }
                docUnitIdsToTreat.put(docUnitId, Optional.of(delivered));

                // ... on verifie si au moins une etape susceptible de faire avancer le workflow est accessible....
                if (workflowService.areStatesRunning(docUnitId,
                                                     WorkflowStateKey.CONTROLE_QUALITE_EN_COURS,
                                                     WorkflowStateKey.PREREJET_DOCUMENT,
                                                     WorkflowStateKey.PREVALIDATION_DOCUMENT,
                                                     WorkflowStateKey.VALIDATION_DOCUMENT)) {
                    possibleRunningStates.add(docUnitId);
                    // ok, workflow non bloqué
                    break;
                }
            }

            // Sinon, le workflow ne pourra pas avancer, il faut le debloquer en déclenchant l'envoi du rapport...
            if (possibleRunningStates.isEmpty()) {
                docUnitIdsToTreat.forEach(digitalDocumentService::generateAndSendCheckSlip);
            }
        });

    }

    @Transactional
    public void setTrain(final List<String> docs, final String train) {
        docUnitService.setTrain(docs, train);
    }

    /**
     * Suppression d'une {@link DocUnit} d'un {@link Project}
     *
     * @param identifier
     */
    @Transactional
    public void removeFromProject(final String identifier) {
        docUnitService.removeFromProject(identifier);
    }

    @Transactional
    public void removeLot(final String identifier) {
        docUnitService.removeLot(identifier);
    }

    @Transactional
    public void removeTrain(final String identifier) {
        docUnitService.removeTrain(identifier);
    }

    @Transactional(readOnly = true)
    public List<SummaryDocUnitWithLotDTO> findAllForProject(final String projectId) {
        final List<DocUnit> docs = docUnitService.findAllByProjectId(projectId);
        return docs.stream().map(DocUnitMapper.INSTANCE::docUnitToDocUnitSummaryWithLotDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SummaryDocUnitDTO> findAllForLot(final String lotId) {
        return docUnitService.findAllSummaryByLotId(lotId);
    }

    /**
     * Validation des champs unique au niveau du DTO avant le merge
     */
    private PgcnList<PgcnError> validate(final DocUnitDTO dto) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();

        final PgcnError.Builder builder = new PgcnError.Builder();

        // PgcnId est unique
        if (StringUtils.isNotBlank(dto.getPgcnId())) {
            final DocUnit duplicate = docUnitService.findOneByPgcnIdAndState(dto.getPgcnId());
            if (duplicate != null && (dto.getIdentifier() == null || !duplicate.getIdentifier().equalsIgnoreCase(dto.getIdentifier()))) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_DUPLICATE_PGCN_ID).setField("pgcnId").build());
            }
        }

        // Retour
        if (!errors.isEmpty()) {
            dto.setErrors(errors);
            throw new PgcnValidationException(dto, errors);
        }
        return errors;
    }

    @Transactional
    public void removeAllFromLot(final List<String> ids) {
        docUnitService.removeAllFromLot(ids);
    }

    @Transactional
    public DocUnitDTO inactiveDocUnit(final DocUnitDTO docDto) {

        final DocUnit doc = docUnitService.findOne(docDto.getIdentifier());
        doc.setState(DocUnit.State.CANCELED);
        doc.setCancelingComment(docDto.getCancelingComment());
        workflowService.endWorkflowForDocUnit(docDto.getIdentifier());
        docUnitService.save(doc);
        return findAndMapDocUnit(doc.getIdentifier());
    }

    /**
     * Récupère les enfants d'une unité documentaire
     *
     * @param parentId
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnitDTO> getChildren(final String parentId) {
        return docUnitService.getChildren(parentId)
                             .stream()
                             .filter(ch -> ch.getState() == DocUnit.State.AVAILABLE)
                             .map(this::mapIntoDTO)
                             .collect(Collectors.toList());
    }

    @Transactional
    public void addChildren(final String parentId, final List<String> childrenIds) {
        final DocUnit parent = docUnitService.findOneWithAllDependencies(parentId);
        final Set<DocUnit> children = docUnitService.findAllById(childrenIds);
        children.forEach(ch -> ch.setParent(parent));
        docUnitService.saveWithoutValidation(children);
    }

    @Transactional
    public void removeChild(final String parentId, final String childId) {
        final DocUnit child = docUnitService.findOneWithAllDependencies(childId);
        if (child.getParent() != null && StringUtils.equals(child.getParent().getIdentifier(), parentId)) {
            child.setParent(null);
            docUnitService.saveWithoutValidation(Collections.singletonList(child));
        }
    }

    /**
     * Définit parent en tant que parent de doc, et recopie les relation de parent dans doc si copyRelations = true (library, project, lot)
     *
     * @param doc
     * @param parent
     * @param copyRelations
     */
    private void setParent(final DocUnit doc, final DocUnit parent, final boolean copyRelations) {
        if (parent != null) {
            doc.setParent(parent);

            if (copyRelations) {
                doc.setLibrary(parent.getLibrary());
                doc.setLot(parent.getLot());
                doc.setProject(parent.getProject());
            }
        }
    }

    /**
     * Récupère les soeurs d'une unité documentaire
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnitDTO> getSiblings(final String id) {
        return docUnitService.getSiblings(id)
                             .stream()
                             .filter(ch -> ch.getState() == DocUnit.State.AVAILABLE && !StringUtils.equals(ch.getIdentifier(), id))
                             .map(this::mapIntoDTO)
                             .collect(Collectors.toList());
    }

    @Transactional
    public void addSibling(final String id, final List<String> siblingIds) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(id);
        final Set<DocUnit> siblings = docUnitService.findAllById(siblingIds);

        // DocSibling de la notice éditée
        DocSibling docSibling = docUnit.getSibling();
        if (docSibling == null) {
            docSibling = new DocSibling();
            docUnit.setSibling(docSibling);

            final DocUnit savedUnit = docUnitService.saveWithoutValidation(docUnit);
            docSibling = savedUnit.getSibling();
        }
        // DocSibling sur les notices soeurs
        final DocSibling fDocSibling = docSibling;
        siblings.forEach(ch -> ch.setSibling(fDocSibling));
        docUnitService.saveWithoutValidation(siblings);
    }

    @Transactional
    public void removeSibling(final String id) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(id);
        if (docUnit.getSibling() != null) {
            docUnitService.clearSibling(docUnit);
        }
    }

    @Transactional(readOnly = true)
    public Page<String> getDistinctTypes(final String search, final Integer page, final Integer size) {
        return docUnitService.getDistinctTypes(search, page, size);
    }

    @Transactional
    public boolean
           massExportToFtp(final List<DocUnit> docUnits, final List<String> exportTypes, final Library lib) throws IOException {

        boolean exported = false;
        // Création du fichier zip global
        String zipName = DateUtils.formatDateToString(LocalDateTime.now(), "yyyy-MM-dd HH-mm-ss") + "_";
        if (!docUnits.isEmpty() && docUnits.size() == 1) {
            final DocUnit doc = docUnits.get(0);
            zipName += doc.getPgcnId() + ".zip";
        } else {
            zipName += "export.zip";
        }
        final File zipFile = new File(Paths.get(ftpExportCacheDir, lib.getIdentifier()).toFile(), zipName);
        try {
            if (!zipFile.createNewFile()) {
                LOG.warn("Probleme à la creation du zip : le fichier {} existe deja!", zipName);
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            return exported;
        }

        try (final FileOutputStream fos = new FileOutputStream(zipFile)) {
            massExport(fos, docUnits.stream().map(DocUnit::getIdentifier).collect(Collectors.toList()), exportTypes);
        } catch (final IOException e) {
            LOG.error("Erreur lors de la compression des fichiers d'export", e);
            return exported;
        }

        final Path zipPath = zipFile.toPath();

        // recup config export ftp
        List<DocUnit> docsInError = new ArrayList<>();
        docUnits.stream().forEach(docUnit -> {
            //Get conf FTP on Lot then, if null, on project or terminate process to this docUnit
            ExportFTPConfiguration confExport = docUnit.getLot().getActiveExportFTPConfiguration();
            if(confExport == null) {
                confExport = docUnit.getProject().getActiveExportFTPConfiguration();
            }

            ExportFTPDeliveryFolder deliveryFolder = docUnit.getLot().getActiveExportFTPDeliveryFolder();
            if(deliveryFolder == null) {
                deliveryFolder = docUnit.getProject().getActiveExportFTPDeliveryFolder();
            }

            if(confExport != null && deliveryFolder != null) {
                if (zipPath.toFile().exists()) {

                    final SftpConfiguration sftpConf = new SftpConfiguration();
                    sftpConf.setActive(true);
                    sftpConf.setHost(confExport.getStorageServer());
                    sftpConf.setPort(Integer.valueOf(confExport.getPort()));
                    sftpConf.setUsername(confExport.getLogin());
                    sftpConf.setTargetDir(confExport.getAddress().concat(deliveryFolder.getName()));

                    try {
                        sftpConf.setPassword(cryptoService.encrypt(confExport.getPassword()));
                        sftpService.sftpPut(sftpConf, zipPath);
                    } catch (final PgcnTechnicalException e) {
                        LOG.error("Erreur Export FTP", e);
                        docsInError.add(docUnit);
                    }
                }
            } else {
                docsInError.add(docUnit);
                LOG.error("Erreur Export FTP, export configuration is missing on " + docUnit.getLabel());
            }
        });

        exported = docsInError.isEmpty();

        if (exported) {
            // Suppression du zip si envoyé sur le ftp
            FileUtils.deleteQuietly(zipFile);
        }
        return exported;
    }

    @Transactional
    public boolean exportToFtp(List<String> docIdentifiers, List<String> exportTypes, Library library) throws IOException {
        return massExportToFtp(new ArrayList<>(docUnitService.findAllById(docIdentifiers)), exportTypes, library);
    }

    @Transactional
    public void massExport(final OutputStream out, final List<String> docUnitIdentifiers, final List<String> exportTypes) throws IOException {
        //mandatory to prevent lazy loading exceptions
        final Collection<DocUnit> docUnits = docUnitService.findAllById(docUnitIdentifiers);
        try (final ZipOutputStream zos = new ZipOutputStream(out)) {

            for (final DocUnit du : docUnits) {

                final String libraryId = du.getLibrary().getIdentifier();

                final String directory = du.getPgcnId() + "/";
                final List<CheckSummedStoredFile> cssfs = new ArrayList<>();
                zos.putNextEntry(new ZipEntry(directory));
                zos.closeEntry();

                // Export des images / format.
                if(exportTypes.contains("METS") || exportTypes.contains("MASTER") || exportTypes.contains("PDF") || exportTypes.contains("VIEW") || exportTypes.contains("THUMBNAIL")){
                    for (final DigitalDocument dd : du.getDigitalDocuments()) {
                        // PDF
                        if (exportTypes.contains("PDF")) {
                            DocPage pdfPage = digitalDocumentService.getPdfPage(dd.getIdentifier());
                            if (pdfPage != null && pdfPage.getMaster().isPresent()) {
                                final Optional<StoredFile> master = pdfPage.getMaster();
                                final StoredFile sf = master.get();
                                final File file = bm.getFileForStoredFile(sf, libraryId);
                                zos.putNextEntry(new ZipEntry(directory + "pdf/" + sf.getFilename()));

                                copyToZip(zos, file);
                            }
                        }

                        if(exportTypes.contains("METS") || exportTypes.contains("MASTER") ||  exportTypes.contains("VIEW") || exportTypes.contains("THUMBNAIL")) {

                            for (final DocPage dp : dd.getOrderedPages()) {

                                final Optional<StoredFile> master = dp.getMaster();
                                if (master.isPresent()) {
                                    final StoredFile sf = master.get();
                                    final File file = bm.getFileForStoredFile(sf, libraryId);

                                    if (exportTypes.contains("METS")) {
                                        final CheckSummedStoredFile cssf = exportMetsService.getCheckSummedStoredFile(sf, file);
                                        cssfs.add(cssf);
                                    }

                                    if (exportTypes.contains("MASTER") && dp.getNumber() != null) {
                                        zos.putNextEntry(new ZipEntry(directory + "master/" + sf.getFilename()));

                                        copyToZip(zos, file);
                                    }
                                }

                                if (exportTypes.contains("VIEW")) {
                                    final Optional<StoredFile> view = dp.getDerivedForFormat(ViewsFormatConfiguration.FileFormat.VIEW);
                                    if (view.isPresent()) {
                                        final StoredFile sf = view.get();
                                        final File file = bm.getFileForStoredFile(sf, libraryId);
                                        final String fileName = sf.getFilename().substring(0, sf.getFilename().lastIndexOf("."));
                                        zos.putNextEntry(new ZipEntry(directory.concat("view/").concat(fileName).concat(".").concat(ImageUtils.FORMAT_JPG)));

                                        copyToZip(zos, file);
                                    }
                                }

                                if (exportTypes.contains("THUMBNAIL")) {
                                    final Optional<StoredFile> thumb = dp.getDerivedForFormat(ViewsFormatConfiguration.FileFormat.THUMB);
                                    if (thumb.isPresent()) {
                                        final StoredFile sf = thumb.get();
                                        final File file = bm.getFileForStoredFile(sf, libraryId);
                                        final String fileName = sf.getFilename().substring(0, sf.getFilename().lastIndexOf("."));
                                        zos.putNextEntry(new ZipEntry(directory.concat("vignettes/").concat(fileName).concat(".").concat(ImageUtils.FORMAT_JPG)));

                                        copyToZip(zos, file);
                                    }
                                }
                            }
                        }
                    }
                }

                if (du.getRecords().isEmpty()) {
                    // pas de notice sur un doc => pas d'export METS / SIP / AIP.
                    continue;
                }

                final BibliographicRecord record = du.getRecords().iterator().next();
                final BibliographicRecordDcDTO noticeDto = uiBibliographicRecordService.getOneDc(record.getIdentifier());

                if (exportTypes.contains("METS")) {
                    zos.putNextEntry(new ZipEntry(directory.concat(MetaDatasCheckService.METS_XML_FILE)));
                    final boolean metaEad = exportTypes.contains("EAD");
                    try {
                        exportMetsService.writeMetadata(zos, du, noticeDto, metaEad, cssfs);
                    } catch (final JAXBException | SAXException e) {
                        LOG.error(e.getMessage(), e);
                    }
                    zos.closeEntry();
                }

                if (exportTypes.contains("AIP")) {
                    // aip.xml
                    final File aipFile = cinesRequestHandlerService.retrieveAip(du.getIdentifier());
                    if (aipFile != null && aipFile.exists()) {
                        zos.putNextEntry(new ZipEntry(directory.concat(CinesRequestHandlerService.AIP_XML_FILE)));

                        try (final FileInputStream fis = new FileInputStream(aipFile)) {
                            IOUtils.copy(fis, zos);
                            zos.closeEntry();
                        }
                    }
                    // sip.xml
                    final File sipFile = cinesRequestHandlerService.retrieveSip(du.getIdentifier(), false);
                    if (sipFile != null && sipFile.exists()) {
                        zos.putNextEntry(new ZipEntry(directory.concat(CinesRequestHandlerService.SIP_XML_FILE)));

                        copyToZip(zos, sipFile);
                    }
                }

                if (exportTypes.contains("ALTO")) {
                    // alto.xml
                    final Optional<PhysicalDocument> physicalDocument = du.getPhysicalDocuments().stream().findFirst();

                    if (physicalDocument.isPresent()) {
                        final List<File> altoTextFiles = altoService.retrieveAlto(physicalDocument.get().getDigitalId(), libraryId, true, true);
                        if (altoTextFiles != null) {
                            for (final File file : altoTextFiles) {
                                zos.putNextEntry(new ZipEntry(directory.concat(file.getName())));

                                copyToZip(zos, file);
                            }
                        }
                    }
                }
            }
        }
    }

    private void copyToZip(ZipOutputStream zos, File file) throws IOException {
        LOG.trace("copyToZip - start");
        try (Stream<Path> paths = Files.walk(Paths.get(file.getPath()))){
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    LOG.trace("copyToZip - walk - " + path.toString());
                    FileInputStream fileInputTest = new FileInputStream(path.toFile());
                    IOUtils.copy(fileInputTest, zos);
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            });
            zos.closeEntry();
        }
        LOG.trace("copyToZip - end");
    }

    /**
     * Modification de masse des UD sélectionnées.
     *
     * @param values
     * @param filteredDocUnits
     * @return
     */
    @Transactional
    public List<DocUnitUpdateErrorDTO> massUpdate(final DocUnitMassUpdateDTO values, final Collection<DocUnit> filteredDocUnits) {

        final List<DocUnitUpdateErrorDTO> errors = new ArrayList<>();

        for (final DocUnit docUnit : filteredDocUnits) {
            // verif lock
            try {
                lockService.checkLock(docUnit);
            } catch (final PgcnLockException le) {
                LOG.error("[{}] Unité documentaire verrouillée", docUnit.getIdentifier(), le);
                errors.add(new DocUnitUpdateErrorDTO(docUnit.getIdentifier(), docUnit.getLabel(), "Unité documentaire verrouillée"));
                continue;
            }
            // Verif / workflow (ko si notice validée ou si archivé ou diffusé)
            if (workflowService.isStateValidated(docUnit.getIdentifier(), WorkflowStateKey.VALIDATION_NOTICES)) {
                LOG.error("[{}] Unité documentaire - notice validée - MAJ impossible", docUnit.getIdentifier());
                errors.add(new DocUnitUpdateErrorDTO(docUnit.getIdentifier(), docUnit.getLabel(), "Notice déjà validée"));
                continue;
            } else if (workflowService.isStateDone(docUnit.getIdentifier(), WorkflowStateKey.ARCHIVAGE_DOCUMENT)) {
                LOG.error("[{}] Unité documentaire archivée - MAJ impossible", docUnit.getIdentifier());
                errors.add(new DocUnitUpdateErrorDTO(docUnit.getIdentifier(), docUnit.getLabel(), "Unité documentaire archivée au Cines"));
                continue;
            } else if (workflowService.isStateDone(docUnit.getIdentifier(), WorkflowStateKey.DIFFUSION_DOCUMENT)) {
                LOG.error("[{}] Unité documentaire diffusée - MAJ impossible", docUnit.getIdentifier());
                errors.add(new DocUnitUpdateErrorDTO(docUnit.getIdentifier(), docUnit.getLabel(), "Unité documentaire diffusée sur IA"));
                continue;
            }

            // Mise à jour
            final DocUnitDTO dto = findAndMapDocUnit(docUnit.getIdentifier());
            if (dto == null) {
                LOG.error("[{}] Unité documentaire non trouvée", docUnit.getIdentifier());
                errors.add(new DocUnitUpdateErrorDTO(docUnit.getIdentifier(), docUnit.getLabel(), "Unité documentaire non trouvée"));
                continue;
            }

            dto.setArchivable(values.isArchivable());
            dto.setDistributable(values.isDistributable());
            if (StringUtils.isBlank(values.getCondReportType())) {
                dto.setCondReportType(null);
            } else {
                dto.setCondReportType(DocUnit.CondReportType.valueOf(values.getCondReportType()));
            }

            update(dto);
            esDocUnitService.indexAsync(dto.getIdentifier()); // Moteur de recherche
        }
        return errors;
    }

}
