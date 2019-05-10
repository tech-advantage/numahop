package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.AbstractDomainObject_;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.State;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit_;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.es.EsBibliographicRecordService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import fr.progilone.pgcn.web.websocket.WebsocketService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.TransactionStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Sébastien on 02/01/2017.
 */
public abstract class AbstractImportService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractImportService.class);
    protected static final int BULK_SIZE = 100;

    private final DeduplicationService deduplicationService;
    private final DocUnitService docUnitService;
    private final EsDocUnitService esDocUnitService;
    private final EsBibliographicRecordService esBibliographicRecordService;
    private final ImportDocUnitService importDocUnitService;
    private final ImportReportService importReportService;
    private final TransactionService transactionService;
    private final WebsocketService websocketService;

    @Value("${elasticsearch.bulk_size}")
    private Integer esBulkSize;

    protected AbstractImportService(final DeduplicationService deduplicationService,
                                    final DocUnitService docUnitService,
                                    final EsDocUnitService esDocUnitService,
                                    final EsBibliographicRecordService esBibliographicRecordService,
                                    final ImportDocUnitService importDocUnitService,
                                    final ImportReportService importReportService,
                                    final TransactionService transactionService,
                                    final WebsocketService websocketService) {
        this.deduplicationService = deduplicationService;
        this.docUnitService = docUnitService;
        this.esDocUnitService = esDocUnitService;
        this.esBibliographicRecordService = esBibliographicRecordService;
        this.importDocUnitService = importDocUnitService;
        this.importReportService = importReportService;
        this.transactionService = transactionService;
        this.websocketService = websocketService;
    }

    /**
     * Mise à disposition des notices pré-importées dans le catalogue
     * Les notices déjà importées ne sont pas modifiées
     *
     * @param report
     * @param defaultProcess
     *         gestion par défaut des unité documentaires pré-importées, sans doublon
     * @param defaultDedupProcess
     *         gestion par défaut des unité documentaires pré-importées, avec doublon
     * @return
     */
    @Async
    public ImportReport processPreimportedDocUnitsAsync(ImportReport report,
                                                        final ImportedDocUnit.Process defaultProcess,
                                                        final ImportedDocUnit.Process defaultDedupProcess) {
        try {
            LOG.info("Import des unités documentaires pré-importées à partir du fichier {}", report.getFilesAsString());

            Page<ImportedDocUnit> importedUnits = null;
            int nbProcessed = 0;
            final Set<DocUnit> deletedUnits = new HashSet<>();
            final Set<ImportedDocUnit> availableUnits = new HashSet<>();

            // Rechargement du report avec ses attributs initialisés
            TransactionStatus status = transactionService.startTransaction(true);
            report = importReportService.findByIdentifier(report.getIdentifier());
            transactionService.commitTransaction(status);

            do {
                status = transactionService.startTransaction(false);
                try {
                    final Pageable pageable = importedUnits == null ?
                                              new PageRequest(0, BULK_SIZE, new Sort(ImportedDocUnit_.identifier.getName())) :
                                              importedUnits.nextPageable();
                    importedUnits = importDocUnitService.findByImportReport(report, pageable);

                    for (final ImportedDocUnit imp : importedUnits) {
                        // L'unité documentaire a déjà été importée (ou pas du tout)
                        if (imp.getDocUnit() == null || imp.getDocUnit().getState() == State.AVAILABLE) {
                            continue;
                        }
                        // Gestion des doublons
                        final boolean hasDuplicates = !imp.getDuplicatedUnits().isEmpty();

                        // Process à effectuer: celui défini sur l'unité documentaire, ou celui par défaut
                        final ImportedDocUnit.Process process = imp.getProcess() != null
                                                                // Une action est définie sur l'unité importée
                                                                ? imp.getProcess()
                                                                // Sinon on effectue l'action par défaut avec / sans doublon
                                                                : hasDuplicates ? defaultDedupProcess : defaultProcess;
                        imp.setProcess(process);

                        final DocUnit importedUnit = imp.getDocUnit();
                        // L'unité documentaire n'est pas importée
                        if (process == ImportedDocUnit.Process.IGNORE) {
                            deletedUnits.add(importedUnit);
                            report.incrementNbImp(-1); // on décrémente le compteur quand on n'importe pas l'UD

                        } else {
                            set(importedUnit, report);

                            // Dédoublonnage
                            if (hasDuplicates) {
                                if (process == ImportedDocUnit.Process.ADD) {
                                    merge(imp, report, deletedUnits, false);
                                }
                                // Remplacement de la notice existante par celle importée, sans toucher à l'UD pour conserver les données liées (workflows, constats, ...)
                                else if (process == ImportedDocUnit.Process.REPLACE) {
                                    merge(imp, report, deletedUnits, true);
                                }
                            }

                            imp.setImportDate(LocalDateTime.now());
                            availableUnits.add(imp);
                        }

                        // Suppression du lien vers les unités documentaires doublons
                        imp.setDuplicatedUnits(null);

                        // Sauvegarde
                        importDocUnitService.save(imp);
                        nbProcessed++;
                    }

                    // Suppression des unités documentaires à remplacer
                    deletedUnits.stream().map(DocUnit::getIdentifier).forEach(id -> docUnitService.delete(id, false));
                    deletedUnits.clear();

                    transactionService.commitTransaction(status);

                } catch (final Exception e) {
                    transactionService.rollbackTransaction(status);
                    throw e;
                }

                // Mise à disposition des unités documentaires importées
                final int nbFailures = setAvailable(availableUnits);
                if (nbFailures > 0) {
                    report.incrementNbImp(-nbFailures);
                }

                final Map<String, Object> statusMap = importReportService.getStatus(report);
                statusMap.put("processed", nbProcessed);
                websocketService.sendObject(report.getIdentifier(), statusMap);

                LOG.debug("{} enregistrements traités avec succès", nbProcessed);
            } while (importedUnits.hasNext());


            /* Indexation des unités documentaires importées */
            report = importReportService.setReportStatus(report, ImportReport.Status.INDEXING);
            report = indexEntities(report);

            LOG.info("Fin de l'import de {} unités documentaires, pré-importées à partir du fichier {}", nbProcessed, report.getFilesAsString());
            return importReportService.endReport(report);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return importReportService.failReport(report, e);
        }
    }

    /**
     * Fusion de la notice importée dans l'existante
     *
     * @param udReport
     * @param report
     * @param deletedUnits
     * @param replace
     *         supprimer les notices existantes
     */
    public void merge(final ImportedDocUnit udReport, final ImportReport report, final Set<DocUnit> deletedUnits, final boolean replace) {
        final DocUnit importedDoc = udReport.getDocUnit();
        final DocUnit existingDoc = udReport.getDuplicatedUnits().iterator().next();
        if (importedDoc == null) {
            return;
        }

        // Suppression des notices existantes
        if (replace) {
            existingDoc.getRecords().forEach(rec -> rec.setDocUnit(null));
            existingDoc.setRecords(null);
        }

        // Ajout de la notice importée sur l'UD existante
        importedDoc.getRecords().forEach(existingDoc::addRecord);

        // Mise à jour des liens
        for (final DocUnit child : importedDoc.getChildren()) {
            child.setParent(existingDoc);
            set(child, report);
        }

        // Lien entre l'import et l'unité documentaire existante
        udReport.initDocUnitFields(existingDoc);

        // Suppression de l'unité documentaire traitée
        importedDoc.setRecords(null);
        deletedUnits.add(importedDoc);
    }

    /**
     * Mets à jour les infos de l'unité documentaire par rapport aux paramètres d'import (projet, lot)
     *
     * @param docUnit
     * @param report
     */
    public void set(final DocUnit docUnit, final ImportReport report) {
        if (report.getProject() != null) {
            docUnit.setProject(report.getProject());
        }
        if (report.getLot() != null) {
            docUnit.setLot(report.getLot());
        }
    }

    /**
     * Passe le statut des unités documentaires importées à "validé"
     * Logue les erreurs de validation
     *
     * @param importedDocUnits
     * @return nombre d'import en échec
     */
    private int setAvailable(final Collection<ImportedDocUnit> importedDocUnits) {
        if (CollectionUtils.isEmpty(importedDocUnits)) {
            return 0;
        }
        TransactionStatus status = transactionService.startTransaction(true);
        @SuppressWarnings("ConstantConditions")
        final Set<String> ids = importedDocUnits.stream()
                                                .filter(imp -> imp.getDocUnit() != null)
                                                .map(imp -> imp.getDocUnit().getIdentifier())
                                                .distinct()
                                                .collect(Collectors.toSet());
        final Set<DocUnit> docUnits = docUnitService.findAllById(ids);
        transactionService.commitTransaction(status);

        final Set<DocUnit> deletedUnits = new HashSet<>();

        for (DocUnit docUnit : docUnits) {
            try {
                status = transactionService.startTransaction(false);

                docUnit.setState(State.AVAILABLE);
                docUnitService.save(docUnit);
                transactionService.commitTransaction(status);

            } catch (PgcnValidationException e) {
                LOG.error(e.getMessage());
                transactionService.rollbackTransaction(status); // Annulation de la sauvegarde de l'unité documentaire

                status = transactionService.startTransaction(false); // nouvelle transaction pour mettre à jour le rapport d'import
                // rapport d'import
                importedDocUnits.stream()
                                .filter(imp -> imp.getDocUnit() != null && StringUtils.equals(imp.getDocUnit().getIdentifier(),
                                                                                              docUnit.getIdentifier()))
                                .findAny()
                                .ifPresent(imp -> {
                                    importDocUnitService.saveWithError(imp, e);
                                });
                // suppression de l'unité documentaire en erreur
                deletedUnits.add(docUnit);
                transactionService.commitTransaction(status);
            }
        }
        // on supprime les ud en erreur pour ne pas bloquer les ré-imports ultérieurs
        if (!deletedUnits.isEmpty()) {
            status = transactionService.startTransaction(false);
            deletedUnits.stream().map(DocUnit::getIdentifier).forEach(id -> docUnitService.delete(id, false));
            transactionService.commitTransaction(status);
        }
        return deletedUnits.size();
    }

    /**
     * Traitement de unité documentaires pré-importées: recherche de doublons, validation utilisateur, import
     *
     * @param report
     * @param parentReportId
     *         identifiant de l'import parent, pour gérer la création des relations hiérarchiques avec des parents provenant d'un autre import
     * @param stepValidation
     * @param stepDeduplication
     * @param defaultDedupProcess
     * @return
     */
    protected ImportReport importDocUnit(ImportReport report,
                                         final String parentReportId,
                                         final boolean stepValidation,
                                         final boolean stepDeduplication,
                                         final ImportedDocUnit.Process defaultDedupProcess) {
        /* Recherche de doublons */
        if (stepDeduplication) {
            report = importReportService.setReportStatus(report, ImportReport.Status.DEDUPLICATING);
            report = lookupDuplicates(report);
        }
        /* Rattachement des unités documentaires à leur parent */
        if (parentReportId != null) {
            linkToParent(report, parentReportId);
        }
        /* Validation des unités documentaires pré-importées par l'utilisateur */
        if (stepValidation) {
            LOG.debug("Pré-import des unités documentaires terminé; l'import débutera après validation par l'utilisateur");
            return importReportService.setReportStatus(report, ImportReport.Status.USER_VALIDATION);
        }
        /* Import + Indexation des unités documentaires */
        else {
            report = importReportService.setReportStatus(report, ImportReport.Status.IMPORTING);
            report = processPreimportedDocUnitsAsync(report, ImportedDocUnit.Process.ADD, defaultDedupProcess);
        }
        return importReportService.endReport(report);
    }

    /**
     * Recherche de doublons pour les unités documentaires pré-importées
     *
     * @param report
     * @return
     * @throws PgcnTechnicalException
     */
    protected ImportReport lookupDuplicates(ImportReport report) {
        LOG.info("Recherche de doublons pour les unités documentaires pré-importées à partir du fichier {}", report.getFilesAsString());
        Page<ImportedDocUnit> importedUnits = null;
        int nbProcessed = 0;
        do {
            final TransactionStatus status = transactionService.startTransaction(false);
            try {
                final Pageable pageable = importedUnits == null ?
                                          new PageRequest(0, BULK_SIZE, new Sort(ImportedDocUnit_.identifier.getName())) :
                                          importedUnits.nextPageable();
                importedUnits = importDocUnitService.findByImportReport(report, pageable);

                for (final ImportedDocUnit imp : importedUnits) {
                    // L'unité documentaire a déjà été importée
                    if (imp.getDocUnit() == null || imp.getDocUnit().getState() == State.AVAILABLE) {
                        continue;
                    }
                    // Recherche de doublon pour l'unité documentaire pré-importée
                    final Collection<DocUnit> duplicated = deduplicationService.lookupDuplicates(imp.getDocUnit());
                    if (!duplicated.isEmpty()) {
                        LOG.debug("{} doublon(s) trouvé(s) pour l'unité documentaire ({}) {}",
                                  duplicated.size(),
                                  imp.getDocUnit().getIdentifier(),
                                  imp.getDocUnit().getLabel());
                        imp.setDuplicatedUnits(duplicated);

                        importDocUnitService.save(imp);
                    }
                    nbProcessed++;
                }
                transactionService.commitTransaction(status);

            } catch (final Exception e) {
                transactionService.rollbackTransaction(status);
                throw e;
            }

            final Map<String, Object> statusMap = importReportService.getStatus(report);
            statusMap.put("processed", nbProcessed);
            websocketService.sendObject(report.getIdentifier(), statusMap);

            LOG.debug("{} recherche de doublons effectuées avec succès", nbProcessed);
        } while (importedUnits.hasNext());

        return report;
    }

    /**
     * Création des liens hiérarchiques parent / enfant, les UD parentes ayant été importées dans un autre import
     *
     * @param report
     * @param parentReportId
     *         identifiant de l'import des UD parentes
     * @return
     */
    protected ImportReport linkToParent(final ImportReport report, final String parentReportId) {
        LOG.info("Création des liens hiérarchiques pour les unités documentaires pré-importées à partir du fichier {}", report.getFilesAsString());

        Page<ImportedDocUnit> importedUnits = null;
        int nbProcessed = 0;
        do {
            final TransactionStatus status = transactionService.startTransaction(false);
            try {
                final Pageable pageable = importedUnits == null ?
                                          new PageRequest(0, BULK_SIZE, new Sort(ImportedDocUnit_.identifier.getName())) :
                                          importedUnits.nextPageable();
                importedUnits = importDocUnitService.findByImportReport(report, pageable);

                // clés de rapprochement imports parent / import enfant
                final Set<String> parentKeys = importedUnits.getContent()
                                                            .stream()
                                                            .map(ImportedDocUnit::getParentKey)
                                                            .filter(StringUtils::isNotBlank)
                                                            .collect(Collectors.toSet());
                // recherche des unités parentes
                final List<ImportedDocUnit> parentUnits = importDocUnitService.findByReportIdentifierAndParentKeyIn(parentReportId, parentKeys);

                // création des liens parent / enfant
                for (final ImportedDocUnit imp : importedUnits) {
                    final DocUnit docUnit = imp.getDocUnit();
                    // L'unité documentaire a déjà été importée, ou le lien parent/enfant n'existe pas
                    if (StringUtils.isEmpty(imp.getParentKey()) || docUnit == null || docUnit.getState() == State.AVAILABLE) {
                        continue;
                    }

                    parentUnits.stream().filter(u -> StringUtils.equals(u.getParentKey(), imp.getParentKey())).findAny().ifPresent(parent -> {
                        final DocUnit parentDocUnit = parent.getDocUnit();
                        docUnit.setParent(parentDocUnit);
                        docUnitService.saveWithoutValidation(docUnit);

                        // Recopie des infos du parent
                        imp.setParentDocUnit(parentDocUnit.getIdentifier());
                        imp.setParentDocUnitPgcnId(parentDocUnit.getPgcnId());
                        imp.setParentDocUnitLabel(parentDocUnit.getLabel());
                        importDocUnitService.saveWithoutValidation(imp);
                    });
                    nbProcessed++;
                }
                transactionService.commitTransaction(status);

            } catch (final Exception e) {
                transactionService.rollbackTransaction(status);
                throw e;
            }

            LOG.debug("{} liens hiérarchiques créés avec succès", nbProcessed);
        } while (importedUnits.hasNext());

        return report;
    }

    private ImportReport indexEntities(final ImportReport report) {
        LOG.info("Indexation des unités documentaires importées à partir du fichier {}", report.getFilesAsString());
        long nbDocUnits = 0, nbRecords = 0;
        Page<DocUnit> pageOfDocs = null;    // Chargement des objets par page de bulkSize éléments

        do {
            final TransactionStatus status = transactionService.startTransaction(true);

            // Chargement des objets
            final Pageable pageable = pageOfDocs == null ?
                                      new PageRequest(0, esBulkSize, Sort.Direction.ASC, AbstractDomainObject_.identifier.getName()) :
                                      pageOfDocs.nextPageable();
            pageOfDocs = importDocUnitService.findDocUnitByImportReport(report, State.AVAILABLE, pageable);
            final List<DocUnit> docUnits = pageOfDocs.getContent();
            final List<String> docUnitIds = docUnits.stream().map(DocUnit::getIdentifier).collect(Collectors.toList());
            final List<String> recordIds =
                docUnits.stream().flatMap(doc -> doc.getRecords().stream()).map(BibliographicRecord::getIdentifier).collect(Collectors.toList());

            // Traitement des unités documentaires
            esDocUnitService.extendDocUnits(docUnits);
            // Indexation des unités documentaires
            esDocUnitService.index(docUnitIds);
            // Indexation des notices biblio
            esBibliographicRecordService.index(recordIds);

            transactionService.commitTransaction(status);

            nbDocUnits += docUnitIds.size();
            nbRecords += recordIds.size();
            LOG.trace("{} / {} unités documentaires (et {} notices) indexées", nbDocUnits, pageOfDocs.getTotalElements(), nbRecords);

        } while (pageOfDocs.hasNext());

        LOG.info("Fin de l'indexation: {} unités documentaires, {} notices importées", nbDocUnits, nbRecords);
        return report;
    }
}
