package fr.progilone.pgcn.service.check;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;
import fr.progilone.pgcn.domain.dto.check.SplitFilename;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO;
import fr.progilone.pgcn.domain.dto.document.PreDeliveryDocumentFileDTO.FileRoleEnum;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataValue;
import fr.progilone.pgcn.domain.jaxb.mets.MdSecType;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.check.AutomaticCheckResultRepository;
import fr.progilone.pgcn.repository.check.AutomaticCheckTypeRepository;
import fr.progilone.pgcn.repository.imagemetadata.ImageMetadataValuesRepository;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckTypeMapper;
import fr.progilone.pgcn.service.delivery.PrefixedDocuments;
import fr.progilone.pgcn.service.document.DigitalDocumentService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import fr.progilone.pgcn.service.storage.BinaryStorageManager.Metadatas;
import fr.progilone.pgcn.service.util.AutomaticCheckResultConvertUtil;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des contrôles automatisés sur les entités
 *
 * @author jbrunet
 *         Créé le 9 mars 2017
 */
@Service
public class AutomaticCheckService {

    private static final Logger LOG = LoggerFactory.getLogger(AutomaticCheckService.class);

    private static final int BATCH_SIZE = 20;
    private static final String LINE_SEP = System.getProperty("line.separator");

    private final AutomaticCheckTypeRepository checkTypeRepository;
    private final AutomaticCheckResultRepository checkResultRepository;

    private final BinaryStorageManager bm;

    private final MetaDatasCheckService metaCheckService;
    private final DigitalDocumentService digitalDocumentService;
    private final WebsocketService websocketService;

    // Services automatisés
    private final FacileCinesService facileService;
    private final ImageMetadataValuesRepository imageMetadataValuesRepository;

    @Autowired
    public AutomaticCheckService(final AutomaticCheckTypeRepository checkTypeRepository,
                                 final AutomaticCheckResultRepository checkResultRepository,
                                 final FacileCinesService facileService,
                                 final MetaDatasCheckService metaCheckService,
                                 final DigitalDocumentService digitalDocumentService,
                                 final BinaryStorageManager bm,
                                 final WebsocketService websocketService,
                                 final ImageMetadataValuesRepository imageMetadataValuesRepository) {
        this.checkTypeRepository = checkTypeRepository;
        this.checkResultRepository = checkResultRepository;
        this.facileService = facileService;
        this.metaCheckService = metaCheckService;
        this.digitalDocumentService = digitalDocumentService;
        this.bm = bm;
        this.websocketService = websocketService;
        this.imageMetadataValuesRepository = imageMetadataValuesRepository;
    }

    @Transactional
    public AutomaticCheckType save(final AutomaticCheckType type) {
        validate(type);
        return checkTypeRepository.save(type);
    }

    @Transactional
    public AutomaticCheckResult save(final AutomaticCheckResult result) {
        return checkResultRepository.save(result);
    }

    @Transactional
    public List<AutomaticCheckResult> save(final List<AutomaticCheckResult> results) {
        if (results == null) {
            return new ArrayList<>();
        }
        return checkResultRepository.saveAll(results);
    }

    @Transactional
    public List<AutomaticCheckResult> saveFacileResults(final List<AutomaticCheckResult> results, final DocUnit doc) {
        if (results == null) {
            return new ArrayList<>();
        }
        final List<AutomaticCheckResult> resToPersist = new ArrayList<>();
        String digDocId = "";
        if (CollectionUtils.isNotEmpty(doc.getDigitalDocuments())) {
            final DigitalDocument dd = doc.getDigitalDocuments().iterator().next();
            digDocId = dd.getIdentifier();
        }
        // on reutilise les resultats si déjà presents.
        final Map<String, AutomaticCheckResult> oldRes = findByDocUnitAndDigitalDocument(doc.getIdentifier(), digDocId);
        if (CollectionUtils.isNotEmpty(oldRes.keySet())) {

            results.stream().filter(res -> res.getPage() != null).forEach(res -> {

                if (oldRes.get(res.getPage().getIdentifier()) != null) {
                    final AutomaticCheckResult old = oldRes.get(res.getPage().getIdentifier());
                    BeanUtils.copyProperties(res, old, "identifier", "createdBy", "createdDate", "lastModifiedBy", "version", "lastModifiedDate");
                    // res.setVersion(old.getVersion()+1);
                    old.setLastModifiedDate(LocalDateTime.now());
                    resToPersist.add(old);
                } else {
                    resToPersist.add(res);
                }
            });

        } else {
            resToPersist.addAll(results);
        }

        return checkResultRepository.saveAll(resToPersist);
    }

    @Transactional
    public void delete(final Set<AutomaticCheckResult> results) {
        checkResultRepository.deleteAll(results);
    }

    @Transactional
    public void delete(final AutomaticCheckResult result) {
        checkResultRepository.delete(result);
    }

    @Transactional(readOnly = true)
    public List<AutomaticCheckType> findCheckByType(final List<AutoCheckType> types) {
        return checkTypeRepository.findByTypeIn(types);
    }

    @Transactional(readOnly = true)
    public List<AutomaticCheckTypeDTO> findAllDto() {
        final List<AutomaticCheckTypeDTO> typesDto = new ArrayList<>();
        final AutomaticCheckTypeMapper mapper = Mappers.getMapper(AutomaticCheckTypeMapper.class);
        checkTypeRepository.findAllConfigurable().forEach(ct -> typesDto.add(mapper.objToDto(ct)));
        return typesDto;
    }

    public Map<String, AutomaticCheckResult> findByDocUnitAndDigitalDocument(final String docUnit, final String digitalDocument) {

        final List<AutomaticCheckResult> results = checkResultRepository.findAllByDocUnitAndDigitalDocumentAndCheckType(docUnit, digitalDocument, AutoCheckType.FACILE);
        return results.stream().map(r -> Pair.of(r.getPage().getIdentifier(), r)).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    /**
     * Lance un contrôle sur une unité documentaire de façon asynchrone
     * L'unité documentaire doit être complètement initialisée
     *
     * @param type
     * @param doc
     */
    @Async
    public void asyncUnitCheck(final AutoCheckType type, final DocUnit doc, final String libraryId) {
        if (type != null) {
            final List<AutomaticCheckType> types = findCheckByType(Collections.singletonList(type));
            asyncCheck(types, doc, libraryId);
        }
    }

    public void asyncCheck(final List<AutomaticCheckType> checkList, final DocUnit doc, final String libraryId) {
        check(checkList, doc, libraryId);
    }

    /**
     * Lance le traitement
     * Gère la sauvegarde
     *
     * @param checkList
     * @param doc
     * @return
     */
    public List<AutomaticCheckResult> check(final List<AutomaticCheckType> checkList, final DocUnit doc, final String libraryId) {
        final List<AutomaticCheckResult> results = new ArrayList<>();
        if (doc == null || CollectionUtils.isEmpty(checkList)) {
            return results;
        }
        checkList.forEach(check -> {
            switch (check.getType()) {
                case FACILE:
                    checkFacile(check, results, doc, libraryId);
                    break;
                default:
                    LOG.error("Type de contrôle automatisé non reconnu : {}", check.getType().name());
                    break;

            }
        });
        return results;
    }

    /**
     * Check against FACILE
     * Vérifie les masters des Documents Numérisés
     *
     * @param checkType
     * @param results
     * @param doc
     */
    private void checkFacile(final AutomaticCheckType checkType, final List<AutomaticCheckResult> results, final DocUnit doc, final String libraryId) {
        final Set<DigitalDocument> digitalDocs = doc.getDigitalDocuments();
        final List<AutomaticCheckResult> batchResults = new ArrayList<>();

        digitalDocs.forEach(digitalDoc -> {

            digitalDoc.getOrderedPages()
                      .stream()
                      .filter(page -> page.getNumber() != null) // elimine le pdf eventuel
                      .forEach(page -> {
                          page.getMaster().ifPresent(master -> {
                              final File file = bm.getFileForStoredFile(master, libraryId);
                              final AutomaticCheckResult result = AutomaticCheckResultConvertUtil.convert(facileService.checkFileAgainstFacile(file));

                              result.setCheck(checkType);
                              result.setDigitalDocument(digitalDoc);
                              result.setDocUnit(doc);
                              result.setPage(page);
                              batchResults.add(result);
                              results.add(result);

                              LOG.info("Page traitée : {}", page.getNumber());
                              if (batchResults.size() == BATCH_SIZE) {
                                  saveFacileResults(batchResults, doc);
                                  sendFacileDoneMessage(doc, batchResults);
                                  batchResults.clear();
                              }
                          });
                      });
        });
        // Sauvegarde des reliquats
        if (!batchResults.isEmpty()) {
            saveFacileResults(batchResults, doc);
            sendFacileDoneMessage(doc, batchResults);
        }
    }

    private void sendFacileDoneMessage(final DocUnit doc, final List<AutomaticCheckResult> results) {

        final Map<String, Object> statusMap = new HashMap<>();
        final List<AutomaticCheckResult> koRes = results.stream().filter(res -> AutoCheckResult.KO == res.getResult()).collect(Collectors.toList());
        statusMap.put("processed", results.size());
        statusMap.put("success", koRes.isEmpty());
        statusMap.put("nbErrors", koRes.size());
        websocketService.sendObject(doc.getIdentifier(), statusMap);
    }

    /**
     * Vérification automatique du format de fichier
     * Contrôle uniquement ceux qui ont le bon préfixe
     * Persiste le résultat
     *
     * @param files
     * @param format
     * @param fileNames
     *            la liste à remplir des fichiers qui correspondent
     * @param fileFormatRule
     * @return
     */
    @Transactional
    public AutomaticCheckResult checkFileNamesAgainstFormat(final AutomaticCheckResult result,
                                                            final Collection<File> files,
                                                            final String format,
                                                            final List<String> fileNames,
                                                            final AutomaticCheckRule fileFormatRule) {
        result.setResult(AutoCheckResult.OK);
        if (!files.isEmpty()) {
            fileNames.addAll(findMastersOnly(files, format));
            if (fileFormatRule.isActive() && fileNames.isEmpty()) {
                result.setResult(fileFormatRule.isBlocking() ? AutoCheckResult.KO
                                                             : AutoCheckResult.OTHER);
                result.setMessage("Aucun fichier ne correspond au format attendu : " + format);
            }
        } else {
            if (fileFormatRule.isActive()) {
                result.setResult(fileFormatRule.isBlocking() ? AutoCheckResult.KO
                                                             : AutoCheckResult.OTHER);
                result.setMessage("Aucun fichier trouvé pour contrôler le format");
            }
        }
        return save(result);
    }

    /**
     * Vérification de présence du préfixe de Bibliothèque.
     *
     * @param result
     * @param files
     * @param format
     * @param fileNames
     * @param bibPrefixRule
     * @param bibPrefix
     * @param seqSeparator
     * @return
     */
    public AutomaticCheckResult checkFileAgainstBibPrefix(final AutomaticCheckResult result,
                                                          final Collection<File> files,
                                                          final String format,
                                                          final List<String> fileNames,
                                                          final AutomaticCheckRule bibPrefixRule,
                                                          final String bibPrefix,
                                                          final String seqSeparator) {
        result.setResult(AutoCheckResult.OK);
        if (bibPrefixRule.isActive()) {
            fileNames.stream().filter(name -> !StringUtils.startsWithIgnoreCase(name, bibPrefix.concat(seqSeparator))).forEach(result::addErrorFile);
        }
        if (CollectionUtils.isNotEmpty(result.getErrorFiles())) {
            result.setResult(bibPrefixRule.isBlocking() ? AutoCheckResult.KO
                                                        : AutoCheckResult.OTHER);
            result.setMessage("Le préfixe de bibliothèque est absent [".concat(bibPrefix).concat("]"));
        }
        return save(result);
    }

    /**
     * Controle de la casse des noms de fichier.
     *
     * @param result
     * @param splitNames
     * @param caseRule
     * @param bibPrefixRule
     * @param bibPrefix
     * @param seqSeparator
     * @param prefix
     * @return
     */
    public AutomaticCheckResult checkFileCase(final AutomaticCheckResult result,
                                              final Map<String, Optional<SplitFilename>> splitNames,
                                              final AutomaticCheckRule caseRule,
                                              final AutomaticCheckRule bibPrefixRule,
                                              final String bibPrefix,
                                              final String seqSeparator,
                                              final String prefix) {
        result.setResult(AutoCheckResult.OK);
        if (caseRule.isActive()) {

            splitNames.keySet().stream().filter(name -> StringUtils.containsIgnoreCase(name, prefix)).forEach((name) -> {

                if (splitNames.get(name).isPresent()) { // à priori toujours vrai....

                    final SplitFilename split = splitNames.get(name).get();
                    final String toTest = prefix.startsWith(bibPrefix) ? split.getLibrary().concat(seqSeparator).concat(split.getPrefix())
                                                                       : split.getPrefix();

                    if (bibPrefixRule.isActive() && (!bibPrefix.equals(split.getLibrary()) || !toTest.contains(prefix))) {

                        LOG.debug("ERREUR DE CASSE DETECTEE : prefix={} - chaine testee:{}", bibPrefix, toTest);
                        result.addErrorFile(name);
                    }
                }

            });

        }
        if (CollectionUtils.isNotEmpty(result.getErrorFiles())) {
            result.setResult(caseRule.isBlocking() ? AutoCheckResult.KO
                                                   : AutoCheckResult.OTHER);
            result.setMessage("La casse n'est pas respectée [".concat(prefix).concat("]"));
        }
        return save(result);
    }

    /**
     * Vérification des métadonnées / aux prérequis :
     * - Type compression
     * - Taux compression
     * - Résolution
     * - Profil colorimétrique
     *
     * @param results
     * @param checkingRules
     * @param files
     * @param format
     * @return
     */
    public List<AutomaticCheckResult> checkMetadataOfFiles(final Map<AutoCheckType, AutomaticCheckResult> results,
                                                           final Map<AutoCheckType, AutomaticCheckRule> checkingRules,
                                                           final Collection<File> files,
                                                           final String format,
                                                           final Delivery delivery,
                                                           final Map<File, Optional<Metadatas>> fileMetadatasForCheck) {

        final List<AutomaticCheckResult> allResults = new ArrayList<>();
        final Lot lot = delivery.getLot();

        final String seqSeparator = lot.getActiveCheckConfiguration().getSeparators();
        final String compType = lot.getRequiredTypeCompression();
        final Integer compRate = lot.getRequiredTauxCompression() != null ? lot.getRequiredTauxCompression()
                                                                          : 0;
        int res = 0;
        if (StringUtils.isNotBlank(lot.getRequiredResolution())) {
            try {
                res = Integer.parseInt(lot.getRequiredResolution().replaceAll("[^0-9]", ""));
            } catch (final NumberFormatException e) {
                LOG.error("Erreur de format sur la resolution demandee : {}", lot.getRequiredResolution());
                // on fera sans...
                res = 0;
            }
        }
        final Integer resolution = res;
        final String colorspace = lot.getRequiredColorspace();
        final AutomaticCheckResult resultCompType = results.get(AutoCheckType.FILE_TYPE_COMPR);
        final AutomaticCheckResult resultCompRate = results.get(AutoCheckType.FILE_TAUX_COMPR);
        final AutomaticCheckResult resultResolution = results.get(AutoCheckType.FILE_RESOLUTION);
        final AutomaticCheckResult resultColorspace = results.get(AutoCheckType.FILE_COLORSPACE);
        final AutomaticCheckResult resultIntegrity = results.get(AutoCheckType.FILE_INTEGRITY);
        final AutomaticCheckResult resultDefinition = results.get(AutoCheckType.FILE_DEFINITION);
        final AutomaticCheckResult resultImageMetadata = results.get(AutoCheckType.FILE_IMAGE_METADATA);

        resultCompType.setResult(AutoCheckResult.OK);
        resultCompRate.setResult(AutoCheckResult.OK);
        resultResolution.setResult(AutoCheckResult.OK);
        resultColorspace.setResult(AutoCheckResult.OK);
        resultIntegrity.setResult(AutoCheckResult.OK);
        resultDefinition.setResult(AutoCheckResult.OK);
        resultImageMetadata.setResult(AutoCheckResult.OK);
        final String HEADER_LIST_FILES = LINE_SEP.concat("Fichiers concernés : ");
        final String ERR_TYPE_COMP_MSG = "Détection de types de compression ne correspondant pas au prérequis [".concat(compType).concat("]");
        final StringBuilder sbCompType = new StringBuilder(ERR_TYPE_COMP_MSG);
        final StringBuilder sbCompRate = new StringBuilder();
        final StringBuilder sbResolution = new StringBuilder();
        final StringBuilder sbDefinition = new StringBuilder();
        final String ERR_COLORSPACE_MSG = "Détection de profils colorimétriques différents du prérequis [".concat(colorspace != null ? colorspace
                                                                                                                                     : "Non renseigné").concat("]");
        final StringBuilder sbColorspace = new StringBuilder(ERR_COLORSPACE_MSG);
        final StringBuilder sbMetadatas = new StringBuilder("Détection de métadonnées différentes de celles attendues");
        final String corruptDetected = "Détection de fichiers probablement corrompus";
        final String emptyDetected = "Détection de fichiers vides";
        final StringBuilder sbIntegrity = new StringBuilder();

        /* Lourdingue, mais permet de n'effectuer qu'une seule passe ImageMagick */
        final Map<String, List<ImageMetadataValue>> cacheMetadataValuesByDocUnit = Collections.synchronizedMap(new HashMap<>());
        fileMetadatasForCheck.forEach((file, metasOpt) -> {

            // Integrity : metas present && File size control
            if (!metasOpt.isPresent() || (!StringUtils.equalsIgnoreCase(format, "PDF") && checkFileSizeError(metasOpt.get().getFilesize(), file.length()))) {
                LOG.info("{} is probably empty or corrupted", file.getName());
                if (AutoCheckResult.OK.equals(resultIntegrity.getResult())) {
                    resultIntegrity.setResult(AutoCheckResult.KO);
                    if (file.length() == 0L) {
                        sbIntegrity.append(emptyDetected);
                    } else {
                        sbIntegrity.append(corruptDetected);
                    }
                    sbIntegrity.append(HEADER_LIST_FILES);
                }
                resultIntegrity.addErrorFile(file.getName());

                final DigitalDocument dd = resultIntegrity.getDigitalDocument();
                dd.setStatus(DigitalDocument.DigitalDocumentStatus.REJECTED); // Trying to reject the document
                digitalDocumentService.save(dd);
            }
            // Type de compression
            final AutomaticCheckRule ruleTypeComp = checkingRules.get(AutoCheckType.FILE_TYPE_COMPR);
            if (ruleTypeComp != null && ruleTypeComp.isActive()) {
                metasOpt.ifPresent(metas -> {
                    final String compress = metas.getCompression();
                    if (checkCompressionTypeError(format, compress, compType)) {
                        if (AutoCheckResult.OK.equals(resultCompType.getResult())) {
                            resultCompType.setResult(ruleTypeComp.isBlocking() ? AutoCheckResult.KO
                                                                               : AutoCheckResult.OTHER);
                            sbCompType.append(HEADER_LIST_FILES);
                        }
                        resultCompType.addErrorFile(file.getName());
                    }
                });
            }
            // Taux de compression
            final AutomaticCheckRule ruleTauxComp = checkingRules.get(AutoCheckType.FILE_TAUX_COMPR);
            if (ruleTauxComp != null && ruleTauxComp.isActive()) {
                final Integer qual = metasOpt.map(metas -> {
                    if (StringUtils.isNotBlank(metas.getQuality())) {
                        try {
                            return Integer.valueOf(metas.getQuality());
                        } catch (final NumberFormatException e) {
                            LOG.error("Invalid compression rate for {}", file.getName());
                        }
                    }
                    return 0;
                }).orElse(0);

                if (compRate == 0 || qual == 0
                    || qual > compRate) {
                    if (AutoCheckResult.OK.equals(resultCompRate.getResult())) {
                        resultCompRate.setResult(ruleTauxComp.isBlocking() ? AutoCheckResult.KO
                                                                           : AutoCheckResult.OTHER);
                        if (compRate == 0) {
                            sbCompRate.append("Le taux de compression demandé n'est pas correctement renseigné.");
                        } else if (qual == 0) {
                            sbCompRate.append("Le taux de compression du master n'est pas renseigné.");
                        } else {
                            sbCompRate.append("Détection de valeurs de taux de compression supérieures au prérequis [").append(compRate).append("]");
                        }
                        sbCompRate.append(HEADER_LIST_FILES);
                    }
                    resultCompRate.addErrorFile(file.getName());
                }
            }
            // Resolution
            final AutomaticCheckRule ruleResolution = checkingRules.get(AutoCheckType.FILE_RESOLUTION);
            if (ruleResolution != null && ruleResolution.isActive()) {
                final String[] vals = metasOpt.map(metas -> StringUtils.split(StringUtils.trimToEmpty(metas.getResolution()), "x", 2)).orElse(null);

                long fileRes = 0L;
                if (vals != null && vals.length > 0
                    && StringUtils.isNotBlank(vals[0])) {
                    try {
                        fileRes = Math.round(Double.valueOf(vals[0]));
                    } catch (final NumberFormatException e) {
                        LOG.error("Invalid resolution for {}", file.getName());
                        fileRes = 0;
                    }
                }
                if (resolution == 0 || fileRes == 0
                    || resolution > fileRes) {
                    if (AutoCheckResult.OK.equals(resultResolution.getResult())) {
                        resultResolution.setResult(ruleResolution.isBlocking() ? AutoCheckResult.KO
                                                                               : AutoCheckResult.OTHER);
                        if (resolution == 0) {
                            sbResolution.append("La résolution demandée n'est pas correctement renseignée.");
                        } else if (fileRes == 0) {
                            sbResolution.append("La résolution du master n'est pas renseignée.");
                        } else {
                            sbResolution.append("Détection de valeurs de résolution inférieures au prérequis [").append(String.valueOf(resolution)).append("]");
                        }
                        sbResolution.append(HEADER_LIST_FILES);
                    }
                    resultResolution.addErrorFile(file.getName());
                }
            }
            // Définition
            final AutomaticCheckRule ruleDefinition = checkingRules.get(AutoCheckType.FILE_DEFINITION);
            if (ruleDefinition != null && ruleDefinition.isActive()) {

                final DigitalDocument dd = resultDefinition.getDigitalDocument();
                final DocUnit docUnit = dd.getDocUnit();

                metasOpt.ifPresent(metas -> {
                    if (docUnit.getImageHeight() != null && docUnit.getImageWidth() != null) {
                        final int height = Integer.parseInt(metas.getHeight());
                        final int width = Integer.parseInt(metas.getWidth());

                        final double defErrorRate = ruleDefinition.getCheckConfiguration().getDefinitionErrorRate();
                        final int threshold = height > width ? docUnit.getImageHeight()
                                                             : docUnit.getImageWidth();

                        if (!checkDefinitionError(defErrorRate, Math.max(height, width), threshold)) {
                            if (AutoCheckResult.OK.equals(resultDefinition.getResult())) {
                                resultDefinition.setResult(ruleDefinition.isBlocking() ? AutoCheckResult.KO
                                                                                       : AutoCheckResult.OTHER);
                                sbDefinition.append(HEADER_LIST_FILES);
                            }
                            resultDefinition.addErrorFile(file.getName());
                        }
                    } else {
                        LOG.error(docUnit.getPgcnId() + " n'a pas de definition attendue.");
                        if (AutoCheckResult.OK.equals(resultDefinition.getResult())) {
                            resultDefinition.setResult(ruleDefinition.isBlocking() ? AutoCheckResult.KO
                                                                                   : AutoCheckResult.OTHER);
                            sbDefinition.append("Valeurs de définition différentes de celles attendues");
                            sbDefinition.append(HEADER_LIST_FILES);
                        }
                        resultDefinition.addErrorFile(file.getName());
                    }
                });
            }
            // Colorspace
            final AutomaticCheckRule ruleColor = checkingRules.get(AutoCheckType.FILE_COLORSPACE);
            if (ruleColor != null && ruleColor.isActive()
                && (StringUtils.isBlank(colorspace) || (metasOpt.isPresent() && !StringUtils.equals(colorspace, metasOpt.get().getColorSpace())))) {
                if (AutoCheckResult.OK.equals(resultColorspace.getResult())) {
                    resultColorspace.setResult(ruleColor.isBlocking() ? AutoCheckResult.KO
                                                                      : AutoCheckResult.OTHER);
                    sbColorspace.append(HEADER_LIST_FILES);
                }
                resultColorspace.addErrorFile(file.getName());
            }
            // Métadonnées
            final AutomaticCheckRule ruleImageMetadata = checkingRules.get(AutoCheckType.FILE_IMAGE_METADATA);
            if (ruleImageMetadata != null && ruleImageMetadata.isActive()) {
                final List<ImageMetadataValue> metadataValues = cacheMetadataValuesByDocUnit.computeIfAbsent(resultImageMetadata.getDigitalDocument().getDocUnit().getIdentifier(),
                                                                                                             imageMetadataValuesRepository::findAllByDocUnitIdentifierWithDependencies);
                final boolean ok = (!metasOpt.isPresent() && metadataValues.isEmpty()) || (metasOpt.isPresent() && metadataValues.stream()
                                                                                                                                 .collect(Collectors.groupingBy(ImageMetadataValue::getMetadata))
                                                                                                                                 .entrySet()
                                                                                                                                 .stream()
                                                                                                                                 .allMatch(e -> isTagValuesValid(e.getKey()
                                                                                                                                                                  .getIptcTag(),
                                                                                                                                                                 e.getValue(),
                                                                                                                                                                 metasOpt.get())
                                                                                                                                                || isTagValuesValid(e.getKey()
                                                                                                                                                                     .getXmpTag(),
                                                                                                                                                                    e.getValue(),
                                                                                                                                                                    metasOpt.get())));
                if (!ok) {
                    if (AutoCheckResult.OK.equals(resultImageMetadata.getResult())) {
                        resultImageMetadata.setResult(ruleImageMetadata.isBlocking() ? AutoCheckResult.KO
                                                                                     : AutoCheckResult.OTHER);
                        sbMetadatas.append(HEADER_LIST_FILES);
                    }
                    resultImageMetadata.addErrorFile(file.getName());
                }
            }
        });
        allResults.add(finalizeResult(resultCompType, sbCompType, seqSeparator, format));
        allResults.add(finalizeResult(resultCompRate, sbCompRate, seqSeparator, format));
        allResults.add(finalizeResult(resultResolution, sbResolution, seqSeparator, format));
        allResults.add(finalizeResult(resultColorspace, sbColorspace, seqSeparator, format));
        allResults.add(finalizeResult(resultIntegrity, sbIntegrity, seqSeparator, format));
        allResults.add(finalizeResult(resultDefinition, sbDefinition, seqSeparator, format));
        allResults.add(finalizeResult(resultImageMetadata, sbMetadatas, seqSeparator, format));
        return allResults;
    }

    private boolean isTagValuesValid(final String tag, final List<ImageMetadataValue> values, final Metadatas metas) {
        return StringUtils.isNotBlank(tag) && metas.getTags().containsKey(tag)
               && metas.getTags().get(tag).size() == values.size()
               && metas.getTags().get(tag).stream().allMatch(t -> values.stream().map(ImageMetadataValue::getValue).anyMatch(value -> value.equalsIgnoreCase(t)));
    }

    /**
     * Vérifie la concordance du type de compression selon le format.
     *
     * @param format
     * @param compress
     * @param expected
     * @return
     */
    private boolean checkCompressionTypeError(final String format, final String compress, final String expected) {

        boolean isOk = false;
        switch (format) {
            case "TIFF":
            case "TIF":
                isOk = StringUtils.containsAny(compress, "None", "LZW", "CCITT", "JPEG");
                break;
            default:
                isOk = StringUtils.equalsIgnoreCase(expected, compress);
                break;
        }
        return !isOk;
    }

    /**
     * Vérifie la cohérence entre la taille du fichier et la taille des métadonnée.
     *
     * @param metadataSize
     * @param fileSize
     * @return
     */
    private boolean checkFileSizeError(final String metadataSize, final long fileSize) {

        if (fileSize == 0L) {
            return true;
        }
        if (StringUtils.isBlank(StringUtils.getDigits(metadataSize))) {
            // taille non renseignée => controle impossible.
            LOG.info("Taille de fichier non renseignée dans les metadonnees");
            return false;
        }

        Double calcSize = 0.0;
        long multFactor = 1L;
        try {
            if (StringUtils.endsWithIgnoreCase(metadataSize, "KiB")) {
                // KibiBytes => 1024 Bytes
                multFactor = 1024L;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "KiB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "MiB")) {
                // MebiBytes => 1024*1024 Bytes
                multFactor = 1024L * 1024;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "MiB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "GiB")) {
                // GibiBytes => 1024*1024*1024 Bytes
                multFactor = 1024L * 1024
                             * 1024;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "GiB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "KB")) {
                multFactor = 1000L;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "KB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "K")) {
                multFactor = 1000L;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "K")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "MB")) {
                multFactor = 1000L * 1000;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "MB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "M")) {
                multFactor = 1000L * 1000;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "M")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "GB")) {
                multFactor = 1000L * 1000
                             * 1000;
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "GB")) * multFactor;
            } else if (StringUtils.endsWithIgnoreCase(metadataSize, "B")) {
                calcSize = Double.valueOf(StringUtils.removeEnd(metadataSize, "B"));
            } else {
                LOG.warn("Unité inattendue à la récupération de Metadatas.Filesize ");
            }
        } catch (final NumberFormatException e) {
            LOG.info("erreur à la récupération de Metadatas.Filesize: {}", e.getLocalizedMessage());
            // taille mal renseignée => controle impossible.
            return false;
        }

        final long longCalc = Math.round(calcSize);
        final long margin = longCalc * 20
                            / 100;  // 20% de marge à tester...

        if (fileSize == longCalc || Math.abs(fileSize - longCalc) < margin
            || longCalc == 0) {
            // on se laisse une bonne marge car les ecarts sont consequents sur les gros fichiers...
            // taille 0 => controle impossible.
            return false;
        }
        return true;
    }

    /**
     * Vérifie que la hauteur ou largeur soit dans l'interval d'erreur renseigné.
     *
     * @param errorRate
     * @param value
     *            : height or width
     * @param threshold
     *            : threshold defined by the rule
     * @return
     */
    private boolean checkDefinitionError(final double errorRate, final int value, final int threshold) {
        final int percentValue = (int) (threshold * errorRate);
        final int minValue = threshold - percentValue;
        final int maxValue = threshold + percentValue;

        return minValue < value && value < maxValue;
    }

    /**
     * @param result
     * @param msg
     * @return
     */
    private AutomaticCheckResult finalizeResult(final AutomaticCheckResult result, final StringBuilder msg, final String seqSeparator, final String format) {
        if (!AutoCheckResult.OK.equals(result.getResult())) {
            final String digitalId = result.getDigitalDocument().getDigitalId();
            // Nom du fichier à rechercher dans les fichiers en erreur
            final String searchFile = StringUtils.equalsIgnoreCase(format, "PDF") ? digitalId
                                                                                  : digitalId.concat(seqSeparator);
            final int concernedFiles = result.getErrorFiles().stream().filter(f -> StringUtils.contains(f, searchFile)).collect(Collectors.toList()).size();
            if (concernedFiles > 0) {
                result.setMessage(msg.append(String.valueOf(concernedFiles)).toString());
            } else {
                result.setResult(AutoCheckResult.OK);
            }
        }
        return save(result);
    }

    /**
     * Vérification du format des fichiers de metadonnées.
     *
     * @param delivery
     * @param digitalIdDoc
     * @param metaDatasDTO
     * @param files
     * @param isTocBlocking
     * @param isPdfBlocking
     * @param extractedDmdSec
     * @return
     */
    @Transactional
    public List<AutomaticCheckResult> checkMetaDataFilesFormat(final Delivery delivery,
                                                               final String digitalIdDoc,
                                                               final Set<PreDeliveryDocumentFileDTO> metaDatasDTO,
                                                               final List<File> files,
                                                               final boolean isTocBlocking,
                                                               final boolean isPdfBlocking,
                                                               final Map<String, List<MdSecType>> extractedDmdSec) {

        final List<AutomaticCheckResult> allResults = new ArrayList<>();

        // Format de fichier metadonnee
        AutomaticCheckResult tocResult = initializeAutomaticCheckResult(AutoCheckType.METADATA_FILE);
        handleLinkResultMetaDatas(tocResult, delivery, digitalIdDoc);
        tocResult.setResult(AutoCheckResult.OK);
        // Idem pour pdf/a
        AutomaticCheckResult pdfResult = initializeAutomaticCheckResult(AutoCheckType.FILE_PDF_MULTI);
        handleLinkResultMetaDatas(pdfResult, delivery, digitalIdDoc);
        pdfResult.setResult(AutoCheckResult.OK);

        if (!metaDatasDTO.isEmpty()) {
            for (final PreDeliveryDocumentFileDTO dto : metaDatasDTO) {
                switch (dto.getRole()) {
                    case METS:
                        // validation xml METS
                        final Optional<File> metsToCheck = files.stream().filter(file -> StringUtils.equalsIgnoreCase(file.getName(), dto.getName())).findFirst();
                        tocResult = metaCheckService.checkMetaDataFileFormat(tocResult,
                                                                             metsToCheck,
                                                                             MetaDatasCheckService.METS_FILE_FORMAT,
                                                                             MetaDatasCheckService.METS_MIME_TYPE,
                                                                             FileRoleEnum.METS,
                                                                             extractedDmdSec);

                        break;
                    case EXCEL:
                        // validation table des matieres excel.
                        final Optional<File> excelToCheck = files.stream().filter(file -> StringUtils.equalsIgnoreCase(file.getName(), dto.getName())).findFirst();

                        if (excelToCheck.isPresent()) {
                            if (excelToCheck.get().getName().endsWith(".xlsx")) {
                                tocResult = metaCheckService.checkMetaDataFileFormat(tocResult,
                                                                                     excelToCheck,
                                                                                     MetaDatasCheckService.EXCEL_FILE_FORMAT,
                                                                                     MetaDatasCheckService.EXCEL_MIME_TYPE,
                                                                                     FileRoleEnum.EXCEL,
                                                                                     extractedDmdSec);
                            } else {
                                tocResult = metaCheckService.checkMetaDataFileFormat(tocResult,
                                                                                     excelToCheck,
                                                                                     MetaDatasCheckService.EXCEL_FILE_FORMAT2,
                                                                                     MetaDatasCheckService.EXCEL_MIME_TYPE2,
                                                                                     FileRoleEnum.EXCEL,
                                                                                     extractedDmdSec);
                            }
                        }

                        break;
                    case PDF_MULTI:
                        // Validation pdf/A ocr.
                        final Optional<File> pdfToCheck = files.stream().filter(file -> StringUtils.equalsIgnoreCase(file.getName(), dto.getName())).findFirst();
                        pdfResult = metaCheckService.checkMetaDataFileFormat(pdfResult,
                                                                             pdfToCheck,
                                                                             MetaDatasCheckService.PDF_FILE_FORMAT,
                                                                             MetaDatasCheckService.PDF_MIME_TYPE,
                                                                             FileRoleEnum.PDF_MULTI,
                                                                             extractedDmdSec);

                        break;
                    case OTHER:
                        // rien pour le moment....
                        break;
                    default:
                        // ?? COLOR, ?... Valider les elements de l'enum
                        break;
                }

                if (isTocBlocking && tocResult.getResult().compareTo(AutoCheckResult.OK) != 0) {
                    delivery.setTableOfContentsOK(false);
                }
                if (isPdfBlocking && pdfResult.getResult().compareTo(AutoCheckResult.OK) != 0) {
                    delivery.setPdfMultiOK(false);
                }

                allResults.add(save(tocResult));
                allResults.add(save(pdfResult));
            }
        } else {
            tocResult.setResult(AutoCheckResult.OTHER);
            tocResult.setMessage("Table des matières introuvable");
            pdfResult.setResult(AutoCheckResult.OTHER);
            pdfResult.setMessage("PDF multicouches introuvable");

            if (isTocBlocking && tocResult.getResult().compareTo(AutoCheckResult.OK) != 0) {
                delivery.setTableOfContentsOK(false);
            }
            if (isPdfBlocking && pdfResult.getResult().compareTo(AutoCheckResult.OK) != 0) {
                delivery.setPdfMultiOK(false);
            }
            allResults.add(save(tocResult));
            allResults.add(save(pdfResult));
        }
        return allResults;
    }

    /**
     * Retourne les fichiers qui ne sont pas des fichiers master
     *
     * @param files
     * @param format
     * @return
     */
    public List<String> findNonMaster(final Collection<File> files, final String format) {
        return files.stream().map(File::getName).filter(name -> !checkIfNameIsCorrect(name, format)).collect(Collectors.toList());
    }

    /**
     * Retourne uniquement les fichiers master
     *
     * @param files
     * @param format
     * @return
     */
    public List<String> findMastersOnly(final Collection<File> files, final String format) {
        return files.stream().map(File::getName).filter(name -> checkIfNameIsCorrect(name, format)).collect(Collectors.toList());
    }

    /**
     * Vérifie que le nom est correct pour un master
     * TODO FIXME : paramétrer la mire
     *
     * @param name
     * @param format
     * @return
     */
    public boolean checkIfNameIsCorrect(final String name, final String format) {
        return StringUtils.endsWithIgnoreCase(name, format) && !StringUtils.containsIgnoreCase(name, "color");
    }

    /**
     * Vérification automatique de la cohérence de la séquence des fichiers
     * Persiste le résultat
     *
     * @param result
     * @param fileNames
     * @param splitNames
     *            la map à remplir à l'aide de split
     * @param fileSeqRule
     * @param bibPrefixMandatory
     * @param seqSeparator
     * @return
     */
    @Transactional
    public AutomaticCheckResult checkSequenceNumber(final AutomaticCheckResult result,
                                                    final List<String> fileNames,
                                                    final Map<String, Optional<SplitFilename>> splitNames,
                                                    final AutomaticCheckRule fileSeqRule,
                                                    final boolean bibPrefixMandatory,
                                                    final String seqSeparator,
                                                    final boolean isPdfDelivery,
                                                    final boolean isJustOneEstampe,
                                                    final String prefix) {
        result.setResult(AutoCheckResult.OK);
        final Map<String, List<Integer>> filePiecesNumbers = new HashMap<>();
        final List<String> handledNames = new ArrayList<>();
        // on recupere les splitNames systematiquement.
        for (final String name : fileNames) {
            final SplitFilename splitName;
            try {
                splitName = SplitFilename.split(name, splitNames, bibPrefixMandatory, seqSeparator, isPdfDelivery, isJustOneEstampe, prefix);
            } catch (final PgcnTechnicalException e) {
                LOG.warn(e.getMessage()); // NOSONAR
                continue;
            }
            // ajout du numéro de séquence
            if (filePiecesNumbers.containsKey(splitName.getPiece())) {
                filePiecesNumbers.get(splitName.getPiece()).add(splitName.getNumber());
            } else {
                final List<Integer> numbers = new ArrayList<>(Collections.singletonList(splitName.getNumber()));
                filePiecesNumbers.put(splitName.getPiece(), numbers);
            }
            handledNames.add(name);
        }
        // par contre, on ne verifie la sequence que si le controle est actif et que ce n'est pas 1 estampe unique non sequencée.
        if (fileSeqRule.isActive() && !isJustOneEstampe) {
            // vérification numéros en séquence
            for (final Map.Entry<String, List<Integer>> entry : filePiecesNumbers.entrySet()) {
                final List<Integer> fileNumbers = entry.getValue();
                Collections.sort(fileNumbers);
                if (!fileNumbers.isEmpty()) {
                    // On vérifie que la séquence commence par 0 ou 1
                    if (fileNumbers.get(0) != 0 && fileNumbers.get(0) != 1) {
                        result.setResult(fileSeqRule.isBlocking() ? AutoCheckResult.KO
                                                                  : AutoCheckResult.OTHER);
                        result.setMessage("Séquence erronée : elle doit commencer par 0 ou 1" + " pour la pièce "
                                          + entry.getKey());
                    } else {
                        // Remise en ordre de la pagination au besoin
                        if (fileNumbers.get(0) == 0) {
                            handledNames.forEach(key -> {
                                if (splitNames.get(key).isPresent()) {
                                    splitNames.get(key).get().setNumber(splitNames.get(key).get().getNumber() + 1);
                                }
                            });
                        }
                        for (int i = 1; i < fileNumbers.size(); i++) {
                            if (fileNumbers.get(i) != fileNumbers.get(i - 1) + 1) {
                                result.setResult(fileSeqRule.isBlocking() ? AutoCheckResult.KO
                                                                          : AutoCheckResult.OTHER);
                                result.setMessage("Séquence erronée entre " + fileNumbers.get(i - 1)
                                                  + " et "
                                                  + fileNumbers.get(i)
                                                  + " pour la pièce "
                                                  + entry.getKey());
                                break;
                            }
                        }
                    }
                } else {
                    result.setResult(fileSeqRule.isBlocking() ? AutoCheckResult.KO
                                                              : AutoCheckResult.OTHER);
                    result.setMessage("Contrôle de séquence impossible : aucune séquence trouvée" + " pour la pièce "
                                      + entry.getKey());
                }
            }
        }
        return save(result);
    }

    /**
     * Vérification de la conformité entre le nombre de pages attendu (doc physique)
     * et le nombre de pages reçues
     */
    @Transactional
    public AutomaticCheckResult checkTotalFileNumber(final AutomaticCheckResult result,
                                                     final List<String> fileNames,
                                                     final PrefixedDocuments prefixedDoc,
                                                     final AutomaticCheckRule nbFilesRule) {
        result.setResult(AutoCheckResult.OK);
        if (prefixedDoc == null || prefixedDoc.getPhysicalDocuments().isEmpty()
            || prefixedDoc.getPhysicalDocuments().get(0).getTotalPage() == null) {
            result.setResult(AutoCheckResult.OTHER);
            result.setMessage("Nombre de pages manquant : impossible d'effectuer les contrôles automatiques");
        }
        // Il manque des pages (FIXME : cas du pdf qui contient toutes les pages ?)
        else if (Integer.valueOf(fileNames.size()).compareTo(prefixedDoc.getPhysicalDocuments().get(0).getTotalPage()) < 0) {
            result.setResult(nbFilesRule.isBlocking() ? AutoCheckResult.KO
                                                      : AutoCheckResult.OTHER);
            result.setMessage("Nombre de fichiers erroné : nombre de pages livrées inférieur au nombre attendu");
            // Il y a plus de pages livrées : acceptable
        } else if (Integer.valueOf(fileNames.size()).compareTo(prefixedDoc.getPhysicalDocuments().get(0).getTotalPage()) > 0) {
            result.setMessage("Nombre de pages livrées supérieur au nombre attendu");
        }
        return save(result);
    }

    public AutomaticCheckResult checkRadicalFile(final AutomaticCheckResult result, final Collection<File> files, final AutomaticCheckRule radicalRule, final String prefix) {
        result.setResult(AutoCheckResult.OK);
        if (radicalRule != null && radicalRule.isActive()) {
            if (files.isEmpty()) {
                if (radicalRule.isBlocking()) {
                    result.setResult(AutoCheckResult.KO);
                    result.setMessage("Aucun fichier ne correspond au préfix " + prefix);
                } else {
                    result.setResult(AutoCheckResult.OTHER);
                    result.setMessage("Aucun fichier ne correspond au préfix " + prefix);
                }
            }
        }
        return save(result);
    }

    /**
     * Crée un modèle vide de résultat de contrôle (Factory)
     * Pas de sauvegarde
     *
     * @param type
     * @return
     */
    public AutomaticCheckResult initializeAutomaticCheckResult(final AutoCheckType type) {
        final AutomaticCheckResult result = new AutomaticCheckResult();
        final AutomaticCheckType formatCheck = checkTypeRepository.getOneByType(type);
        if (formatCheck == null) {
            LOG.error("Impossible de trouver un contrôle auto de type {}", type);
        }
        result.setCheck(formatCheck);
        return result;
    }

    private PgcnList<PgcnError> validate(final AutomaticCheckType type) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();
        // le label est obligatoire
        if (StringUtils.isBlank(type.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.AUTO_CHECK_TYPE_LABEL_MANDATORY).setField("label").build());
        }
        /** Retour **/
        if (!errors.isEmpty()) {
            type.setErrors(errors);
            throw new PgcnValidationException(type, errors);
        }
        return errors;
    }

    /**
     * Remplit les champs pour chaque résultat (delivery et DigitalDocument)
     *
     * @param result
     * @param delivery
     * @param digitalIdDoc
     */
    private void handleLinkResultMetaDatas(final AutomaticCheckResult result, final Delivery delivery, final String digitalIdDoc) {
        result.setDelivery(delivery);
        final List<DigitalDocument> dds = digitalDocumentService.getAllByDigitalId(digitalIdDoc);
        if (CollectionUtils.isNotEmpty(dds)) {
            result.setDigitalDocument(dds.get(0));
        }
    }
}
