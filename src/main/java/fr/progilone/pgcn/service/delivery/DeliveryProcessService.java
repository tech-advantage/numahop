package fr.progilone.pgcn.service.delivery;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.DELIVERY_NOT_ENOUGH_AVAILABLE_SPACE;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.DELIVERY_NO_MASTER_FOUND;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.DELIVERY_NO_MATCHING_PREFIX;
import static fr.progilone.pgcn.exception.message.PgcnErrorCode.DELIVERY_WRONG_FOLDER;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Iterables;

import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.delivery.DeliverySlip;
import fr.progilone.pgcn.domain.delivery.DeliverySlipLine;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import fr.progilone.pgcn.domain.dto.check.SplitFilename;
import fr.progilone.pgcn.domain.dto.delivery.PreDeliveryDTO;
import fr.progilone.pgcn.domain.dto.document.DigitalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO.FileRoleEnum;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.PhysicalDocumentRepository;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.checkconfiguration.AutomaticCheckRuleService;
import fr.progilone.pgcn.service.delivery.mapper.PrefixedDocumentsMapper;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.lot.LotService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.web.util.WorkflowAccessHelper;

/**
 * Service de traitement des livraisons : effectue les pré-livraisons et les livraisons
 *
 * @author jbrunet
 * Créé le 16 mai 2017
 */
@Service
public class DeliveryProcessService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryProcessService.class);

    private static final String EXTENSION_FORMAT_PDF = "PDF";

    private static final String[] AUTHORIZED_COMPLEMENTS = {"TDC", "TDM", "TOC"};

    private final PhysicalDocumentRepository physicalDocumentRepository;
    private final DigitalDocumentService digitalDocumentService;
    private final AutomaticCheckService autoCheckService;
    private final DeliveryService deliveryService;
    private final AutomaticCheckRuleService autoCheckRuleService;
    private final PrefixedDocumentsMapper mapper;
    private final LotService lotService;
    private final DocUnitService docUnitService;
    private final WorkflowAccessHelper workflowAccessHelper;
    private final WorkflowService workflowService;
    private final ConditionReportService conditionReportService;
    private final BinaryStorageManager bm;
    
    @Autowired
    public DeliveryProcessService(final PhysicalDocumentRepository physicalDocumentRepository,
                                  final DigitalDocumentService digitalDocumentService,
                                  final AutomaticCheckService autoCheckService,
                                  final DeliveryService deliveryService,
                                  final AutomaticCheckRuleService autoCheckRuleService,
                                  final PrefixedDocumentsMapper mapper,
                                  final LotService lotService,
                                  final DocUnitService docUnitService,
                                  final WorkflowAccessHelper workflowAccessHelper,
                                  final WorkflowService workflowService,
                                  final ConditionReportService conditionReportService,
                                  final BinaryStorageManager bm) {
        this.physicalDocumentRepository = physicalDocumentRepository;
        this.digitalDocumentService = digitalDocumentService;
        this.autoCheckService = autoCheckService;
        this.deliveryService = deliveryService;
        this.autoCheckRuleService = autoCheckRuleService;
        this.mapper = mapper;
        this.lotService = lotService;
        this.docUnitService = docUnitService;
        this.workflowAccessHelper = workflowAccessHelper;
        this.workflowService = workflowService;
        this.conditionReportService = conditionReportService;
        this.bm = bm;
    }

    /**
     * Fait l'inventaire des dossiers de livraison.
     *
     * @param delivery
     * @param createDocs
     *         true: création d'ud par rapport aux fichiers trouvés; false: livraison des fichiers trouvés par rapport aux UD existantes
     * @return Un objet PreDeliveryDTO contenant, pour chaque sous-dossier correspondant à un document de la livraison,
     * le nombre de fichiers image et la liste des fichiers de métadonnées.
     */
    @Transactional
    public PreDeliveryDTO predeliver(final Delivery delivery, final boolean createDocs) throws PgcnValidationException {
        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();
        final Set<PreDeliveryDocumentDTO> pddtos = new HashSet<>();
        final PreDeliveryDTO preDeliveryDTO = new PreDeliveryDTO();
        final String format = delivery.getLot().getRequiredFormat();

        // Separateur de sequence et prefixe de bibliotheque
        final Lot lot = lotService.getOneWithConfigRules(delivery.getLot().getIdentifier());
        final String seqSeparator = lot.getActiveCheckConfiguration().getSeparators();
        final String bibPrefix = lot.getProject().getLibrary().getPrefix();
        final AutomaticCheckRule bibPrefixRule = getCheckingRulesConfig(lot).get(AutoCheckType.FILE_BIB_PREFIX);
        final boolean bibPrefixMandatory = bibPrefixRule != null && bibPrefixRule.isActive() && bibPrefixRule.isBlocking();

        final List<File> subDirectories = getSubDirectories(delivery, preDeliveryDTO);
        final Map<File, String> prefixes = new HashMap<>();

        if (!createDocs) {
            // Fichiers associés à un préfixe
            final Map<String, PrefixedDocuments> documentsForPrefix = getPrefixedDocuments(delivery, preDeliveryDTO);
            // Alimente la liste de préfixes, et élimine les sous-répertoires sans préfixe
            subDirectories.removeIf(directory -> {
                final String prefixForDirectory = getPrefixForDirectory(directory, documentsForPrefix, seqSeparator);
                prefixes.put(directory, prefixForDirectory);

                return prefixForDirectory == null;
            });
        }

        final DeliverySlip deliverySlip = new DeliverySlip();
        delivery.setDeliverySlip(deliverySlip);
        deliverySlip.setDelivery(delivery);
        
        long totalMastersSize = 0L;

        for (final File directory : subDirectories) {
            final String prefix = prefixes.getOrDefault(directory, directory.getName());
            final Collection<File> filesToHandle = FileUtils.listFiles(directory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
            
            try {
                totalMastersSize += Files.walk(directory.toPath())
                                        .filter(p -> p.toFile().isFile())
                                        .filter(p -> autoCheckService.checkIfNameIsCorrect(p.toFile().getName(), format))
                                        .mapToLong(p -> p.toFile().length())
                                        .sum();
            } catch(final IOException e) {
                LOG.error("Erreur evaluation taille directory {}, erreur {}", directory.getName(), e.getMessage());
            }
            
            // Traitement des fichiers au format attendu (FIXME : master et non master au même format)
            final List<String> fileNames = autoCheckService.findMastersOnly(filesToHandle, format);
            int pageCount = 0;
            final Set<String> pieces = new HashSet<>();

            // #4993 - Cas des estampes : 1 seul fichier master, pas forcément besoin de sequence..
            final boolean isJustOneFileEstampe = fileNames.size() == 1
                                                 && StringUtils.equalsIgnoreCase(fileNames.get(0), (prefix + "." + format));

            for (final String name : fileNames) {
                final SplitFilename splitFileName;
                try {

                    splitFileName = SplitFilename.split(name,
                                                        splitNames,
                                                        bibPrefixMandatory,
                                                        seqSeparator,
                                                        EXTENSION_FORMAT_PDF.equalsIgnoreCase(format),
                                                        isJustOneFileEstampe,
                                                        prefix);
                } catch (final PgcnTechnicalException e) {
                    LOG.warn(e.getMessage()); // NOSONAR
                    continue;
                }
                pageCount++;
                pieces.add(splitFileName.getPiece());
            }

            final PreDeliveryDocumentDTO pddto = new PreDeliveryDocumentDTO(prefix, 0, pieces, new HashSet<>());

            // Traitement des fichiers non masters.
            final List<String> fileNamesOthers = autoCheckService.findNonMaster(filesToHandle, format);
            fileNamesOthers.forEach(fileName -> {
                // Pré-remplissage des roles selon l'extension.
                final String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
                final String nameBeforeExt = fileName.substring(0, fileName.lastIndexOf("."));
                FileRoleEnum role = null;

                if (nameIsAuthorized(prefix, bibPrefix, seqSeparator, nameBeforeExt, ext)) {
                    if (MetaDatasCheckService.PDF_FILE_FORMAT.equalsIgnoreCase(ext)) {
                        role = FileRoleEnum.PDF_MULTI;
                    } else if (MetaDatasCheckService.METS_FILE_FORMAT.equalsIgnoreCase(ext)) {
                        role = FileRoleEnum.METS;
                    }
                    if (MetaDatasCheckService.EXCEL_FILE_FORMAT.equalsIgnoreCase(ext)
                        || MetaDatasCheckService.EXCEL_FILE_FORMAT2.equalsIgnoreCase(ext)) {
                        role = FileRoleEnum.EXCEL;
                    }
                } else {
                    if (MetaDatasCheckService.METS_XML_FILE.equalsIgnoreCase(fileName)) {
                        role = FileRoleEnum.METS;
                    }
                }
                final PreDeliveryDocumentFileDTO fileDTO = new PreDeliveryDocumentFileDTO(fileName, role);
                pddto.getMetaDataFiles().add(fileDTO);
            });

            final DeliverySlipLine line = new DeliverySlipLine();
            line.setDate(delivery.getReceptionDate().toString());
            line.setLot(delivery.getLot().getLabel());
            line.setNbPages(String.valueOf(pageCount));
            line.setRadical(prefix);

            final DocUnit du = docUnitService.findOneByLotIdAndDigitalId(delivery.getLot().getIdentifier(), prefix);

            if (du != null) {
                final PhysicalDocument pd = du.getPhysicalDocuments().iterator().next();
                if (pd.getTrain() != null) {
                    line.setTrain(pd.getTrain().getLabel());
                }
                line.setPgcnId(du.getPgcnId());
                line.setTitle(du.getLabel());
            }

            line.setSlip(deliverySlip);
            deliverySlip.addSlipLine(line);

            pddto.setPageNumber(pageCount);
            pddtos.add(pddto);
        }

        // Si il n'y a aucun fichier à livrer et qu'on attend des masters => on bloque le process.
        final Map<AutoCheckType, AutomaticCheckRule> checkingRules = getCheckingRulesConfig(delivery.getLot());
        final AutomaticCheckRule masterRule = checkingRules.get(AutoCheckType.WITH_MASTER);
        if (masterRule.isActive() && splitNames.isEmpty()) {
            // aucun master trouvé, on empeche la livraison
            LOG.warn("Aucun fichier à livrer => livraison stoppée");
            preDeliveryDTO.addError(buildError(DELIVERY_NO_MASTER_FOUND));
        }
        // On effectue le contrôle après : les dossiers non correspondants sont simplement ignorés
        if (pddtos.isEmpty()) {
            preDeliveryDTO.addError(buildError(DELIVERY_NO_MATCHING_PREFIX));
        }
        // Controle espace disque / taille totale des masters. 
        if (!checkDiskAvailableSpace(lot.getProject().getLibrary(), totalMastersSize)) {
            LOG.error("Manque de place sur le disque  => livraison stoppée");
            preDeliveryDTO.addError(buildError(DELIVERY_NOT_ENOUGH_AVAILABLE_SPACE)); 
        }
        
        preDeliveryDTO.setDocuments(pddtos);

        deliveryService.save(delivery);
        return preDeliveryDTO;
    }

    /**
     * Verifie la concordance du nom de fichier / au prefixe et ses variantes possibles.
     *
     * @param prefix
     * @param bibPrefix
     * @param seqSeparator
     * @param nameBeforeExt
     * @param ext
     * @return
     */
    private boolean nameIsAuthorized(final String prefix,
                                     final String bibPrefix,
                                     final String seqSeparator,
                                     final String nameBeforeExt,
                                     final String ext) {

        boolean isAuthorized = false;
        if (prefix.equalsIgnoreCase(nameBeforeExt) || bibPrefix.concat(seqSeparator).concat(prefix).equalsIgnoreCase(nameBeforeExt)) {
            isAuthorized = true;
        } else if (MetaDatasCheckService.EXCEL_FILE_FORMAT.equalsIgnoreCase(ext) || MetaDatasCheckService.EXCEL_FILE_FORMAT2.equalsIgnoreCase(ext)) {

            final String prefixSep = prefix.concat(seqSeparator);
            final String bibPrefixSep = bibPrefix.concat(seqSeparator).concat(prefix).concat(seqSeparator);
            for (final String compl : AUTHORIZED_COMPLEMENTS) {
                if (prefixSep.concat(compl).equalsIgnoreCase(nameBeforeExt) || bibPrefixSep.concat(compl).equalsIgnoreCase(nameBeforeExt)) {
                    isAuthorized = true;
                    break;
                }
            }
        }
        return isAuthorized;
    }

    private List<File> getSubDirectories(final Delivery delivery, final PreDeliveryDTO preDeliveryDTO) {
        final String deliveryPath = getFolderPath(delivery).toString();
        if (deliveryPath == null) {
            final PgcnError error = buildError(DELIVERY_WRONG_FOLDER);
            preDeliveryDTO.addError(error);
            throw new PgcnValidationException(preDeliveryDTO, error);
        }
        LOG.debug("Recherche de fichiers dans le dossier : {}", deliveryPath);

        final File[] subDirectories = new File(deliveryPath).listFiles(File::isDirectory);
        if (subDirectories == null) {
            final PgcnError error = buildError(DELIVERY_WRONG_FOLDER);
            preDeliveryDTO.addError(error);
            throw new PgcnValidationException(preDeliveryDTO, error);
        }
        return new ArrayList<>(Arrays.asList(subDirectories));
    }

    private Map<String, PrefixedDocuments> getPrefixedDocuments(final Delivery delivery, final PreDeliveryDTO preDeliveryDTO) {
        final Map<String, PrefixedDocuments> documentsForPrefix = new HashMap<>();
        final Set<PhysicalDocument> physicalDocuments = physicalDocumentRepository.findAllByLot(delivery.getLot().getIdentifier());
        physicalDocuments.forEach(physicalDoc -> {
            // Vérification de la possibilité de livrer le document
            if (workflowAccessHelper.canDocUnitBeDelivered(physicalDoc.getDocUnit().getIdentifier())) {
                if (physicalDoc.getDigitalId().isEmpty()) {
                    LOG.info("Le document ayant pour PGCN id {} ne peut pas être livré (aucun radical n'est configuré)",
                             physicalDoc.getDocUnit().getPgcnId());
                } else {
                    final PrefixedDocuments prefixedDocs = new PrefixedDocuments();
                    prefixedDocs.addPhysicalDocument(physicalDoc);
                    documentsForPrefix.put(physicalDoc.getDigitalId(), prefixedDocs);
                }
            } else {
                LOG.info("Le document ayant pour préfixe {} ne peut pas être livré (workflow)", physicalDoc.getDigitalId());
            }
        });

        // Vérification de l'éventuel traitement en cours sur les documents
        final Set<String> prefixFromLockedDocsToRemove = new HashSet<>();
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final List<DigitalDocument> ddForLotAndId =
                digitalDocumentService.getAllByDigitalIdAndLotIdentifier(prefix, delivery.getLot().getIdentifier());
            ddForLotAndId.forEach(dd -> {
                final Optional<DigitalDocumentDTO> ddtoOpt =
                    // Récupération du doc livré, s'il existe (sans création s'il n'existe pas)
                    digitalDocumentService.getDeliveredDocumentIfExists(dd, delivery)
                                          // status CREATING ou DELIVERING
                                          .filter(deliveredDocument -> deliveredDocument.getStatus() != null && (deliveredDocument.getStatus()
                                                                                                                 == DigitalDocumentStatus.CREATING
                                                                                                                 || deliveredDocument.getStatus()
                                                                                                                    == DigitalDocumentStatus.DELIVERING))
                                          // Création d'un DigitalDocumentDTO
                                          .map(deliveredDocument -> {
                                              final DigitalDocumentDTO ddto = new DigitalDocumentDTO();
                                              ddto.setIdentifier(dd.getIdentifier());
                                              ddto.setDigitalId(dd.getDigitalId());
                                              ddto.setStatus(deliveredDocument.getStatus().name());
                                              return ddto;
                                          });
                // On a un DigitalDocumentDTO => doc en cours de traitement
                if (ddtoOpt.isPresent()) {
                    preDeliveryDTO.addLockedDigitalDocument(ddtoOpt.get());
                    prefixFromLockedDocsToRemove.add(prefix);
                }
                // Sinon on ajoute le doc pour traitement ultérieur
                else {
                    prefixedDoc.addDigitalDocument(dd);
                }
            });
        });
        // Confirmation et suppression des lockeds
        prefixFromLockedDocsToRemove.forEach(prefixLocked -> {
            if (documentsForPrefix.get(prefixLocked).getDigitalDocuments().isEmpty()) {
                documentsForPrefix.remove(prefixLocked);
            }
        });
        return documentsForPrefix;
    }

    /**
     * Création des UD à partir de la pré-livraison
     *
     * @param deliveryId
     * @param pddtos
     */
    @Transactional
    public List<DocUnit> createDocs(final String deliveryId, final Collection<PreDeliveryDocumentDTO> pddtos, final List<String> prefixToExclude) {
        final Delivery delivery = deliveryService.getOne(deliveryId);
        final Lot lot = delivery.getLot();
        if (lot == null) {
            return null;
        }
        final Project project = lot.getProject();
        final Library library = project != null ? project.getLibrary() : null;

        return pddtos.stream().filter(dto -> prefixToExclude == null || !prefixToExclude.contains(dto.getDigitalId()))
                     // Création des unités documentaires
                     .map(dto -> {
                         
                         // La docUnit existe en cas de relivraison
                         final DocUnit alreadyHere = docUnitService.findOneByPgcnIdAndState(dto.getDigitalId());
                         final DocUnit docUnit = alreadyHere == null ? new DocUnit() : alreadyHere;
                         docUnit.setPgcnId(dto.getDigitalId());
                         docUnit.setLibrary(library);
                         docUnit.setLot(lot);
                         docUnit.setProject(project);

                         // Champs obligatoires
                         docUnit.setLabel(dto.getDigitalId());
                         docUnit.setRights(DocUnit.RightsEnum.TO_CHECK);
                         docUnit.setType(lot.getRequiredFormat());

                         final PhysicalDocument physDoc = new PhysicalDocument();
                         physDoc.setDigitalId(dto.getDigitalId());
                         physDoc.setTotalPage(dto.getMetaDataFiles().size());
                         docUnit.addPhysicalDocument(physDoc);

                         return docUnit;
                     })
                     // Sauvegarde
                     .map(docUnitService::save)
                     // Résultat
                     .collect(Collectors.toList());
    }

    /**
     * Réalise les vérifications et livre les fichiers image.
     * Ne traite que les cas 1 document physique <=> 1 document numérique.
     * <p>
     * Les lockedDocs ne doivent pas être traités
     * <p>
     * <br />
     * Retourne les éléments calculés nécessaires à la suite du traitement
     * (réalisé en asynchrone)
     *
     * @param identifier
     * @param lockedDocs
     * @return
     * @throws PgcnTechnicalException
     */
    @Transactional
    public DeliveryProcessResults deliver(final String identifier,
                                          final List<String> lockedDocs,
                                          final List<String> prefixToExclude,
                                          final List<PreDeliveryDocumentDTO> metaDatas,
                                          final String libraryId) throws PgcnTechnicalException {

        final Delivery delivery = deliveryService.getOne(identifier);
        final Map<String, Optional<SplitFilename>> splitNames = new HashMap<>();

        // Fichiers associés à un préfixe
        final Map<String, PrefixedDocuments> documentsForPrefix = new HashMap<>();
        final Set<PhysicalDocument> physicalDocuments = physicalDocumentRepository.findAllByLot(delivery.getLot().getIdentifier());        
        final List<String> digitalIdstoDeliver = metaDatas.stream().map(PreDeliveryDocumentDTO::getDigitalId).collect(Collectors.toList());
        // les docs marqués à exclure ne seront pas livrés.
        physicalDocuments.stream()
                         .filter(physicalDoc -> prefixToExclude == null || !prefixToExclude.contains(physicalDoc.getDigitalId()))
                         .filter(physicalDoc -> digitalIdstoDeliver.contains(physicalDoc.getDigitalId()))
                         .forEach(physicalDoc -> {
                             // Vérification de la possibilité de livrer le document
                             if (workflowAccessHelper.canDocUnitBeDelivered(physicalDoc.getDocUnit().getIdentifier())) {
                                 final PrefixedDocuments prefixedDocs = new PrefixedDocuments();
                                 prefixedDocs.addPhysicalDocument(physicalDoc);
                                 documentsForPrefix.put(physicalDoc.getDigitalId(), prefixedDocs);
                             } else {
                                 LOG.info("Le document ayant pour préfixe {} ne peut pas être livré (workflow)", physicalDoc.getDigitalId());
                             }
                         });

        // Retrait des lockedDocs & verrouillage des fichiers
        final Set<String> prefixFromLockedDocsToRemove = new HashSet<>();
        final List<String> errors = new ArrayList<>();
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final List<DigitalDocument> ddForLotAndId =
                digitalDocumentService.getAllByDigitalIdAndLotIdentifier(prefix, delivery.getLot().getIdentifier());
            ddForLotAndId.forEach(dd -> {
                final DeliveredDocument deliveredDocument = digitalDocumentService.getDeliveredDocument(dd, delivery);

                // Si le document est locked il faudra le retirer
                if (lockedDocs.contains(dd.getIdentifier())) {
                    LOG.warn("Le document {} est verrouillé, il ne sera pas livré {id : {}}", dd.getDigitalId(), dd.getIdentifier());
                    prefixFromLockedDocsToRemove.add(prefix);
                } else if (DigitalDocumentStatus.CREATING.equals(deliveredDocument.getStatus()) || DigitalDocumentStatus.DELIVERING.equals(
                    deliveredDocument.getStatus())) {
                    // Si le document n'est pas locké mais qu'une livraison est en cours, on arrête le traitement
                    LOG.error("Le document {} a été verrouillé pendant le lancement du traitement : annulation de la livraison", dd.getDigitalId());
                    errors.add(dd.getIdentifier());
                } else {
                    prefixedDoc.addDigitalDocument(dd);
                }
            });
        });
        // Vérification de l'absence d'erreurs
        if (!errors.isEmpty()) {
            throw new PgcnTechnicalException("Changement d'état des documents de livraison au cours de l'initialisation");
        }
        // Confirmation et suppression des lockeds
        prefixFromLockedDocsToRemove.forEach(prefixLocked -> {
            if (documentsForPrefix.get(prefixLocked).getDigitalDocuments().isEmpty()) {
                documentsForPrefix.remove(prefixLocked);
            } else {
                LOG.warn("Le prefix {} sera livré, alors qu'un de ses documents est verrouillé.", prefixLocked);
            }
        });

        // Il ne reste plus que les fichiers qui sont à livrer, on s'en occupe maintenant
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final DigitalDocument digitalDoc;
            // Première livraison
            if (prefixedDoc.getDigitalDocuments().isEmpty()) {
                digitalDoc = new DigitalDocument();
                digitalDoc.setDigitalId(prefix);
                digitalDoc.setStatus(DigitalDocument.DigitalDocumentStatus.CREATING);
                digitalDoc.setTotalDelivery(0);

                final PhysicalDocument phDoc = Iterables.getOnlyElement(prefixedDoc.getPhysicalDocuments());
                digitalDoc.setDocUnit(phDoc.getDocUnit());

                // recherche du nbre de pages attendu (Constat d'etat - #2023)
                final ConditionReport condReport = conditionReportService.findByDocUnit(phDoc.getDocUnit().getIdentifier());
                if (condReport != null) {
                    final Optional<ConditionReportDetail> reportDetail =
                        condReport.getDetails().stream().filter(detail -> Type.LIBRARY_LEAVING.equals(detail.getType())).findFirst();
                    if (reportDetail.isPresent()) {
                        // on passe par ce champ qui ne sert plus autrement ...
                        phDoc.setTotalPage(reportDetail.get().getNbViewTotal());
                    }
                }

                // Création d'un nouveau DeliveredDocument
                final DeliveredDocument deliveredDocument = new DeliveredDocument();
                deliveredDocument.setDelivery(delivery);
                deliveredDocument.setStatus(digitalDoc.getStatus());
                digitalDoc.addDelivery(deliveredDocument);
                deliveredDocument.setDigitalDocument(digitalDoc);

                prefixedDoc.addDigitalDocument(digitalDoc);
            } else {
                digitalDoc = Iterables.getOnlyElement(prefixedDoc.getDigitalDocuments());
                digitalDoc.setStatus(DigitalDocument.DigitalDocumentStatus.DELIVERING);
                // Recherche du DeliveredDocument correspondant à la livraison en cours
                digitalDocumentService.getDeliveredDocument(digitalDoc, delivery).setStatus(digitalDoc.getStatus());
            }

            // Mise à jour du workflow du document (livraison ou RE-livraison)
            if (workflowService.isWorkflowRunning(digitalDoc.getDocUnit().getIdentifier()) ) {
                if (workflowService.isStateRunning(digitalDoc.getDocUnit(), WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS)) {
                    workflowService.processState(digitalDoc.getDocUnit().getIdentifier(),
                                                 WorkflowStateKey.LIVRAISON_DOCUMENT_EN_COURS,
                                                 SecurityUtils.getCurrentUser().getIdentifier());
                } else {
                    workflowService.processState(digitalDoc.getDocUnit().getIdentifier(),
                                                 WorkflowStateKey.RELIVRAISON_DOCUMENT_EN_COURS,
                                                 SecurityUtils.getCurrentUser().getIdentifier());
                } 
            }
            
            digitalDocumentService.save(digitalDoc);
        });

        // Màj livraison
        delivery.setStatus(DeliveryStatus.DELIVERING);
        deliveryService.save(delivery);

        // Mapping stateless
        final Map<String, PrefixedDocumentsDTO> documentsDTOForPrefix = new HashMap<>();
        documentsForPrefix.forEach((prefix, prefixedDoc) -> {
            final PrefixedDocumentsDTO dto = mapper.mapToDTO(prefixedDoc);
            documentsDTOForPrefix.put(prefix, dto);
        });

        // Metadatas Files - On ne conserve que les fichiers marques à livrer et dont le role est renseigne.
        final Map<String, Set<PreDeliveryDocumentFileDTO>> metadatasDTOForPrefix = new HashMap<>();
        metaDatas.stream().filter(meta -> prefixToExclude == null || !prefixToExclude.contains(meta.getDigitalId())).forEach(meta -> {
            final Set<PreDeliveryDocumentFileDTO> metaToKeep = new HashSet<>();
            meta.getMetaDataFiles().forEach((mdf) -> {
                if (mdf.getRole() != null && !mdf.getRole().equals(PreDeliveryDocumentFileDTO.FileRoleEnum.NO_ROLE)) {
                    metaToKeep.add(mdf);
                }
            });
            metadatasDTOForPrefix.put(meta.getDigitalId(), metaToKeep);
        });

        final DeliveryProcessResults processElements = new DeliveryProcessResults();
        processElements.setDocumentsDTOForPrefix(documentsDTOForPrefix);
        processElements.setSplitNames(splitNames);
        processElements.setMetadatasDTOForPrefix(metadatasDTOForPrefix);
        processElements.setCheckingRules(getCheckingRulesConfig(delivery.getLot()));
        final CheckConfiguration cc = getActiveCheckConfiguration(delivery.getLot());
        if (cc != null) {
            processElements.setSamplingMode(cc.getSampleMode());
            processElements.setSamplingRate(cc.getSampleRate());
            processElements.setLibraryId(cc.getLibrary().getIdentifier());
        } else {
            processElements.setLibraryId(libraryId);
        }
        return processElements;
    }

    /**
     * Retourne les regles applicables pour chaque controle.
     *
     * @param lot
     * @return
     */
    private Map<AutoCheckType, AutomaticCheckRule> getCheckingRulesConfig(final Lot lot) {
        String configId = null;
        if (lot.getActiveCheckConfiguration() == null) {
            final CheckConfiguration checkConf = lotService.getActiveCheckConfiguration(lot);
            configId = checkConf.getIdentifier();
        } else {
            configId = lot.getActiveCheckConfiguration().getIdentifier();
        }
        if (StringUtils.isNotBlank(configId)) {
            return autoCheckRuleService.findRulesByConfigId(configId);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Retourne la configuration de contrôle active.
     *
     * @param lot
     * @return
     */
    private CheckConfiguration getActiveCheckConfiguration(final Lot lot) {
        CheckConfiguration cc = null;
        if (lot.getActiveCheckConfiguration() == null) {
            cc = lotService.getActiveCheckConfiguration(lot);
        } else {
            cc = lot.getActiveCheckConfiguration();
        }
        return cc;
    }

    /**
     * Retrouve le radical des fichiers à livrer dans le dossier
     * Le radical est issu des documents physiques et est retourné s'il est contenu dans le nom du dossier
     *
     * @param directory
     *         Sous-dossier de livraison
     * @param documentsForPrefix
     *         Un set de préfixes possibles pour le lot livré
     * @return un préfixe ou null si aucun match dans le dossier
     */
    private String getPrefixForDirectory(final File directory, final Map<String, PrefixedDocuments> documentsForPrefix, final String seqSeparator) {
        if (directory.isDirectory()) {
            for (final String prefix : documentsForPrefix.keySet()) {
                // #2522 : bazar ds les prefixes
                if (StringUtils.equals(directory.getName(), prefix) || StringUtils.contains(directory.getName(), prefix.concat(seqSeparator))) {
                    return prefix;
                }
            }
        }
        return null;
    }

    /**
     * Retourne le chemin complet correspondant à la livraison
     *
     * @param delivery
     * @return
     */
    private Path getFolderPath(final Delivery delivery) {
        final FTPConfiguration activeFTPConfiguration = lotService.getActiveFTPConfiguration(delivery.getLot());
        if (activeFTPConfiguration == null) {
            LOG.error("Aucune configuration FTP active n'a été trouvée pour la livraison du lot {}", delivery.getLot());
            return null;
        }
        return Paths.get(activeFTPConfiguration.getDeliveryFolder(), delivery.getFolderPath());
    }
    
    /**
     * Controle si espace disque suffisant pour la livraison à venir.
     *
     * @param totalMastersSize
     * @return
     */
    private boolean checkDiskAvailableSpace(final Library library, final long totalMastersSize) {
        final File tmpdir = bm.getTmpDir(library.getIdentifier());
        return tmpdir.getUsableSpace() > (3 * totalMastersSize);        
    }
    
    /**
     * Retourne infos sur l'espace disque de la bibliothèque (widget Espace disque disponible).
     * 
     * @param libId
     * @return
     */
    public Map<String, Long> getDiskInfos(final String libId) {

        final File tmpdir = bm.getTmpDir(libId);
        final Map<String, Long> infos = new HashMap<>();
        if (tmpdir != null) {
            infos.put("occupe", tmpdir.getTotalSpace() - tmpdir.getUsableSpace());
            infos.put("disponible", tmpdir.getUsableSpace());
            LOG.debug("Espace disque - Occupé {} - Disponible {}", infos.get("occupe"), infos.get("disponible"));
        }
        return infos;   
    }

    private PgcnError buildError(final PgcnErrorCode pgcnErrorCode) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        switch (pgcnErrorCode) {
            case DELIVERY_NO_MATCHING_PREFIX:
            case DELIVERY_NO_MASTER_FOUND:
                builder.setCode(pgcnErrorCode);
                break;
            case DELIVERY_WRONG_FOLDER:
            case DELIVERY_NOT_ENOUGH_AVAILABLE_SPACE:
                builder.setCode(pgcnErrorCode);
                break;
            default:
                break;
        }
        return builder.build();
    }
}
