package fr.progilone.pgcn.service.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.SummaryDocUnitWithLotDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocSibling;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit.State;
import fr.progilone.pgcn.domain.document.DocUnit_;
import fr.progilone.pgcn.domain.document.PhysicalDocument;
import fr.progilone.pgcn.domain.dto.document.DocUnitDeletedReportDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.domain.lot.Lot.LotStatus;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.domain.train.Train;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.repository.document.BibliographicRecordRepository;
import fr.progilone.pgcn.repository.document.DocSiblingRepository;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.exchange.ImportedDocUnitRepository;
import fr.progilone.pgcn.repository.exchange.cines.CinesReportRepository;
import fr.progilone.pgcn.repository.exchange.internetarchive.InternetArchiveReportRepository;
import fr.progilone.pgcn.repository.lot.LotRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.repository.train.TrainRepository;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.service.exchange.internetarchive.ui.UIInternetArchiveReportService;
import fr.progilone.pgcn.service.util.SortUtils;

@Service
public class DocUnitService {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitService.class);

    private final BibliographicRecordRepository bibliographicRecordRepository;
    private final CinesReportRepository cinesReportRepository;
    private final ConditionReportService conditionReportService;
    private final DocUnitRepository docUnitRepository;
    private final DocSiblingRepository docSiblingRepository;
    private final DocUnitValidationService docUnitValidationService;
    private final EsDocUnitService esDocUnitService;
    private final ImportedDocUnitRepository importedDocUnitRepository;
    private final InternetArchiveReportRepository internetArchiveReportRepository;
    private final UIInternetArchiveReportService uiIAReportService;
    private final PhysicalDocumentService physicalDocumentService;
    private final ProjectRepository projectRepository;
    private final TrainRepository trainRepository;
    

    @Autowired
    public DocUnitService(final BibliographicRecordRepository bibliographicRecordRepository,
                          final CinesReportRepository cinesReportRepository,
                          final ConditionReportService conditionReportService,
                          final DocUnitRepository docUnitRepository,
                          final DocSiblingRepository docSiblingRepository,
                          final DocUnitValidationService docUnitValidationService,
                          final EsDocUnitService esDocUnitService,
                          final ImportedDocUnitRepository importedDocUnitRepository,
                          final InternetArchiveReportRepository internetArchiveReportRepository,
                          final PhysicalDocumentService physicalDocumentService,
                          final ProjectRepository projectRepository,
                          final LotRepository lotRepository,
                          final TrainRepository trainRepository,
                          final DeliveryService deliveryService,
                          final UIInternetArchiveReportService uiIAReportService) {
        this.bibliographicRecordRepository = bibliographicRecordRepository;
        this.cinesReportRepository = cinesReportRepository;
        this.conditionReportService = conditionReportService;
        this.docUnitRepository = docUnitRepository;
        this.docSiblingRepository = docSiblingRepository;
        this.docUnitValidationService = docUnitValidationService;
        this.esDocUnitService = esDocUnitService;
        this.importedDocUnitRepository = importedDocUnitRepository;
        this.internetArchiveReportRepository = internetArchiveReportRepository;
        this.physicalDocumentService = physicalDocumentService;
        this.projectRepository = projectRepository;
        this.trainRepository = trainRepository;
        this.uiIAReportService = uiIAReportService;
    }

    /**
     * Crée un docUnit correctement initialisé
     * Crée le document physique associé
     *
     * @return
     */
    public DocUnit initializeDocUnit() {
        final DocUnit doc = new DocUnit();
        final PhysicalDocument physDoc = new PhysicalDocument();
        doc.getPhysicalDocuments().add(physDoc);
        physDoc.setDocUnit(doc);
        physDoc.setTotalPage(0);
        return doc;
    }

    /**
     * Sauvegarde et valide (optionnel) une unité doc
     *
     * @param docUnit
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(noRollbackFor = PgcnValidationException.class)
    public DocUnit save(final DocUnit docUnit, final boolean validate) throws PgcnValidationException {
        if (validate) {
            docUnitValidationService.validate(docUnit);
        }

        handlePhysicalDocument(docUnit);

        final DocUnit savedDocUnit = docUnitRepository.save(docUnit);

        for (final BibliographicRecord record : savedDocUnit.getRecords()) {
            bibliographicRecordRepository.save(record);
        }
        for (final PhysicalDocument pd : savedDocUnit.getPhysicalDocuments()) {
            physicalDocumentService.save(pd);
        }
        return savedDocUnit;
    }

    /**
     * Sauvegarde et valide une unité doc
     *
     * @param docUnit
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(noRollbackFor = PgcnValidationException.class)
    public DocUnit save(final DocUnit docUnit) throws PgcnValidationException {
        return save(docUnit, true);
    }

    @Transactional
    public void incrementDocUnitVersion(final DocUnit docUnit) {
        if (docUnit.getCinesVersion() != null) {
            docUnit.setCinesVersion(docUnit.getCinesVersion() + 1);
        } else {
            docUnit.setCinesVersion(0);
        }
        save(docUnit);
    }

    /**
     * Sauvegarde d'une unité documentaire sans validation, ni sauvegarde des BibliographicRecord et des PhysicalDocument
     *
     * @param docUnit
     * @return
     */
    @Transactional
    public DocUnit saveWithoutValidation(final DocUnit docUnit) {
        return docUnitRepository.save(docUnit);
    }

    /**
     * Sauvegarde d'une liste d'unités documentaire sans validation, ni sauvegarde des BibliographicRecord et des PhysicalDocument
     *
     * @param docUnits
     * @return
     */
    @Transactional
    public List<DocUnit> saveWithoutValidation(final Iterable<DocUnit> docUnits) {
        return docUnitRepository.save(docUnits);
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findAll() {
        return docUnitRepository.findAllByState(DocUnit.State.AVAILABLE);
    }

    @Transactional(readOnly = true)
    public DocUnit findOne(final String identifier) {
        return docUnitRepository.findOne(identifier);
    }

    /**
     * Retourne une unité doc avec toutes les dépendences sauf les StoredFilesF:
     * (StoredFile, Page, DigitalDoc, PhysicalDoc, Project, Records..)
     * ne pas utiliser dans une boucle, à utiliser unitairement
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneWithAllDependencies(final String identifier) {
        return docUnitRepository.findOneWithAllDependencies(identifier, false);
    }

    /**
     * Retourne une unité doc avec toutes les dépendences y-compris storedFiles si initFiles == true:
     * (StoredFile (ou pas), Page, DigitalDoc, PhysicalDoc, Project, Records..)
     * ne pas utiliser dans une boucle, à utiliser unitairement
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneWithAllDependencies(final String identifier, final boolean initFiles) {
        return docUnitRepository.findOneWithAllDependencies(identifier, initFiles);
    }
    
    /**
     * Retrouve une unité documentaire avec les dépendances liées au workflow
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneWithAllDependenciesForWorkflow(final String identifier) {
        return docUnitRepository.findOneWithAllDependenciesForWorkflow(identifier);
    }

    /**
     * Retourne une unité doc avec seulement les dépendences nécessaires
     * pour l'affichage (Pas de pages)
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneForDisplay(final String identifier) {
        return docUnitRepository.findOneForDisplay(identifier);
    }

    /**
     * Retourne les enfants de l'unité documentaire parentId
     *
     * @param parentId
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnit> getChildren(final String parentId) {
        return docUnitRepository.findByParentIdentifier(parentId);
    }

    /**
     * Retourne les soeurs de l'unité documentaire id
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<DocUnit> getSiblings(final String id) {
        return docUnitRepository.findSiblingsByIdentifier(id);
    }

    /**
     * Détermine si une liste d'unité documentaire peuvent être supprimées
     *
     * @param identifiers
     *         une liste des identifiants des unité doc
     * @return une liste des entités en erreurs
     */
    @Transactional(readOnly = true)
    public Collection<DocUnitDeletedReportDTO> canDocUnitBeDeleted(final Collection<String> identifiers) {
        final Collection<DocUnitDeletedReportDTO> entityDeletedReports = new ArrayList<>();
        if (!identifiers.isEmpty()) {
            final Map<String, DocUnitDeletedReportDTO> entityDeletedReportsMap = new HashMap<>();
            //Vérification présence dans un projet
            final Collection<DocUnit> docs = docUnitRepository.findAll(identifiers);
            final PgcnError.Builder builder = new PgcnError.Builder();
            docs.forEach(doc -> {
                if (doc.getProject() != null) {
                    final PgcnError error = builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_IN_PROJECT).build();
                    buildAndAddReportFromError(error, entityDeletedReportsMap, doc.getIdentifier(), doc.getLabel());
                } else {
                    buildAndAddReportFromError(null, entityDeletedReportsMap, doc.getIdentifier(), doc.getLabel());
                }
            });
            entityDeletedReports.addAll(entityDeletedReportsMap.values());
        }
        return entityDeletedReports;
    }

    /**
     * Verifie si les UD peuvent etre detachees des projet/lot/train.
     *
     * @param identifiers
     * @return une liste des entités en erreurs
     */
    @Transactional(readOnly = true)
    public Collection<DocUnitDeletedReportDTO> canDocUnitBeUnlinked(final Collection<String> identifiers) {
        final Collection<DocUnitDeletedReportDTO> entityUnlinkedReports = new ArrayList<>();
        if (!identifiers.isEmpty()) {
            final Map<String, DocUnitDeletedReportDTO> entityUnlinkedReportsMap = new HashMap<>();
            // Vérification du statut du lot
            // todo verif statut train - tjs created pour le moment....
            final Collection<DocUnit> docs = docUnitRepository.findByIdentifierInWithProj(identifiers);
            final PgcnError.Builder builder = new PgcnError.Builder();
            docs.forEach(doc -> {
                // forbidden si lot en cours
                if (doc.getLot() != null && LotStatus.ONGOING == doc.getLot().getStatus()) {
                    final PgcnError error = builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_IN_ONGOING_LOT).build();
                    buildAndAddReportFromError(error, entityUnlinkedReportsMap, doc.getIdentifier(), doc.getLabel());
                } else {
                    buildAndAddReportFromError(null, entityUnlinkedReportsMap, doc.getIdentifier(), doc.getLabel());
                }
            });
            entityUnlinkedReports.addAll(entityUnlinkedReportsMap.values());
        }
        return entityUnlinkedReports;
    }

    /**
     * Détermine si une liste d'unité documentaire peuvent être supprimées
     *
     * @return une liste des entités en erreurs
     */
    @Transactional(readOnly = true)
    public Collection<DocUnitDeletedReportDTO> canDocUnitProjectBeDeleted(final Collection<String> Ids) {
        final Collection<DocUnitDeletedReportDTO> entityDeletedReports = new ArrayList<>();
        if (!Ids.isEmpty()) {
            final Map<String, DocUnitDeletedReportDTO> entityDeletedReportsMap = new HashMap<>();
            //Vérification présence dans un projet
            final Collection<DocUnit> docs = docUnitRepository.findAll(Ids);
            final PgcnError.Builder builder = new PgcnError.Builder();
            docs.forEach(doc -> {
                if (doc.getProject() == null) {
                    final PgcnError error = builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_IN_PROJECT).build();
                    buildAndAddReportFromError(error, entityDeletedReportsMap, doc.getIdentifier(), doc.getLabel());
                } else {
                    buildAndAddReportFromError(null, entityDeletedReportsMap, doc.getIdentifier(), doc.getLabel());
                }
            });
            entityDeletedReports.addAll(entityDeletedReportsMap.values());
        }
        return entityDeletedReports;
    }

    @Transactional(readOnly = true)
    public List<DocUnit> canDocUnitsBeAdded(final List<String> idDocs) {
        final List<DocUnit> docUnits = docUnitRepository.findAll(idDocs);
        final List<DocUnit> docUnitList = new ArrayList<>();
        docUnits.forEach(docUnit -> {
            if (docUnit.getProject() == null) {
                docUnitList.add(docUnit);
            }
        });
        return docUnitList;
    }

    /**
     * Construit un rapport d'erreur ou met à jour un rapport d'erreur pour une unité doc
     * Si le rapport d'erreur existe déjà, l'erreur sera simplement rajoutée au rapport
     *
     * @param error
     *         l'erreur pour laquelle on souhaite construire le rapport
     * @param entityDeletedReportsMap
     *         une map contenant des rappports d'erreurs
     * @param entityId
     *         l'entité pour laquelle on souhaite construire le rapport
     * @param label
     *         la template expression de l'entité
     */
    private void buildAndAddReportFromError(final PgcnError error,
                                            final Map<String, DocUnitDeletedReportDTO> entityDeletedReportsMap,
                                            final String entityId,
                                            final String label) {
        final DocUnitDeletedReportDTO entityDeletedReport;

        if (entityDeletedReportsMap.containsKey(entityId)) {
            entityDeletedReport = entityDeletedReportsMap.get(entityId);
            if (error != null) {
                entityDeletedReport.addError(error);
            }
        } else {
            entityDeletedReport = new DocUnitDeletedReportDTO(entityId, label);
            if (error != null) {
                entityDeletedReport.addError(error);
            }
            entityDeletedReportsMap.put(entityDeletedReport.getIdentifier(), entityDeletedReport);

        }
    }

    private void handlePhysicalDocument(final DocUnit docUnit) {
        if (docUnit.getPhysicalDocuments().isEmpty()) {
            final PhysicalDocument pd = new PhysicalDocument();
            docUnit.addPhysicalDocument(pd);
        }
    }

    @Transactional(readOnly = true)
    public Page<DocUnit> search(final String search,
                                final boolean hasDigitalDocuments,
                                final boolean active,
                                final boolean archived,
                                final boolean nonArchived,
                                final boolean archivable,
                                final boolean nonArchivable,
                                final boolean distributed,
                                final boolean nonDistributed,
                                final boolean distributable,
                                final boolean nonDistributable,
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
        Sort sort = SortUtils.getSort(sorts);
        if (sort == null) {
            sort = new Sort(DocUnit_.pgcnId.getName());
        }
        final Pageable pageRequest = new PageRequest(page, size, sort);        
        return docUnitRepository.search(search,
                                        hasDigitalDocuments,
                                        active,
                                        archived,
                                        nonArchived,
                                        archivable,
                                        nonArchivable,
                                        distributed,
                                        nonDistributed,
                                        distributable,
                                        nonDistributable,
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
                                        pageRequest);
    }
    
    @Transactional(readOnly = true)
    public List<DocUnit> searchMinList(final String search,
                                final List<String> libraries,
                                final List<String> projects,
                                final List<String> lots,
                                final List<String> trains,
                                final List<String> statuses) {
       
        return docUnitRepository.minSearch(search,
                                        libraries,
                                        projects,
                                        lots,
                                        trains,
                                        statuses);
    }

    @Transactional(readOnly = true)
    public Page<DocUnit> searchAllForProject(final String projectId, final Integer page, final Integer size){
        Sort sort = new Sort(DocUnit_.pgcnId.getName());
        
        final Pageable pageRequest = new PageRequest(page, size, sort);
        return docUnitRepository.searchAllForProject(projectId, pageRequest);
    }

    @Transactional
    public void delete(final String identifier) {
        delete(identifier, true);
    }

    @Transactional
    public void delete(final String identifier, final boolean deleteImpDocUnits) {
        final DocUnit docUnit = docUnitRepository.findOne(identifier);

        // Suppression des références à l'unité documentaire dans les tables d'import
        if (deleteImpDocUnits) {
            importedDocUnitRepository.deleteByDocUnitIdentifier(identifier);
            importedDocUnitRepository.deleteDuplicatedUnitsByDocUnitIdentifier(identifier);

            // internet archive
            internetArchiveReportRepository.deleteByDocUnitIdentifier(identifier);
        }
        // Sinon on casse juste le lien entre DocUnit et ImportedDocUnit, en conservant ce dernier
        else {
            importedDocUnitRepository.setDocUnitNullByDocUnitIdIn(Collections.singletonList(identifier));
            importedDocUnitRepository.deleteDuplicatedUnitsByDocUnitIdentifier(identifier);

            // internet archive
            internetArchiveReportRepository.deleteByDocUnitIdentifier(identifier);
        }

        // CinesReport
        cinesReportRepository.deleteByDocUnitIdentifier(identifier);
        // Constats d'état (db + elasticsearch)
        conditionReportService.deleteByDocUnitIdentifier(identifier);
        // Lien vers les UD filles
        docUnitRepository.setParentNullByParentIdIn(Collections.singletonList(identifier));

        final DocSibling sib = docSiblingRepository.findByDocUnitsIdentifier(identifier);
        // Suppression UD
        docUnitRepository.delete(identifier);
        // Liens vers les UD soeurs
        if (sib != null) {
            Hibernate.initialize(sib.getDocUnits());
            if (sib.getDocUnits().size() == 1) {
                docSiblingRepository.delete(sib);
            }
        }
        // Moteur de recherche
        esDocUnitService.deleteAsync(docUnit);
    }

    @Transactional
    public void delete(final Collection<String> identifiers) {
        identifiers.forEach(this::delete);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteByPgcnIdAndState(final String pgcnId, final State state) {
        final DocUnit docUnit = docUnitRepository.getOneByPgcnIdAndState(pgcnId, state);
        if (docUnit != null) {
            delete(docUnit.getIdentifier(), false);
        }
    }

    @Transactional
    public void deleteFromProject(final Collection<String> identifiers) {
        final Collection<DocUnit> docs = docUnitRepository.findAll(identifiers);
        docs.forEach(doc -> {
            doc.setProject(null);
            doc.setLot(null);
            docUnitRepository.save(doc);
        });
    }

    /**
     * Retourne les {@link DocUnit} de type {@link State#AVAILABLE} en fonction de leur PGCN ID
     *
     * @param pgcnId
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneByPgcnIdAndState(final String pgcnId) {
        if (pgcnId == null) {
            return null;
        }
        return docUnitRepository.getOneByPgcnIdAndState(pgcnId, State.AVAILABLE);
    }
    
    /**
     * Retourne les {@link DocUnit} de type {@link State#AVAILABLE} en fonction de leur PGCN ID
     *
     * @param pgcnId
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneByPgcnId(final String pgcnId) {
        if (pgcnId == null) {
            return null;
        }
        return docUnitRepository.getOneByPgcnId(pgcnId);
    }

    /**
     * Retourne les {@link DocUnit} en fonction de leur PGCN ID et de leur type parmi :
     * <br/>
     * <ul><li>{@link State#AVAILABLE}</li><li>{@link State#NOT_AVAILABLE}</li></ul>
     *
     * @param pgcnId
     * @param state
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findOneByPgcnIdAndState(final String pgcnId, final State state) {
        if (pgcnId == null || state == null) {
            return null;
        }
        return docUnitRepository.getOneByPgcnIdAndState(pgcnId, state);
    }
    
    @Transactional(readOnly = true)
    public DocUnit findOneWithLibrary(final String docUnitId) {
        return docUnitRepository.findOneWithLibrary(docUnitId);
    }

    @Transactional
    public void setProjectAndLot(final Set<DocUnit> dus, final String project, final Lot l, final String train) {
        
        final Project p = projectRepository.findOne(project);
        final Train t = train != null ? trainRepository.findOne(train) : null;
        
        for (final DocUnit du : dus) {
            du.setProject(p);

            if(l != null){
                du.setLot(l);
            }

            if (du.getPhysicalDocuments().iterator().hasNext() && t != null) {
                du.getPhysicalDocuments().iterator().next().setTrain(t);
            }
            docUnitRepository.save(du);
        }
    }
    
    @Transactional(readOnly = true)
    public Set<DocUnit> findByIdentifierInWithDocs(final List<String> docs) {
        return docUnitRepository.findByIdentifierInWithDocs(docs);
    }
    
    @Transactional
    public void setTrain(final List<String> docs, final String train) {
        final Train t = train != null ? trainRepository.findOne(train) : null;
        final Set<DocUnit> dus = docUnitRepository.findByIdentifierInWithDocs(docs);

        for (final DocUnit du : dus) {
            if (du.getPhysicalDocuments().iterator().hasNext()) {
                du.getPhysicalDocuments().iterator().next().setTrain(t);
            }
            docUnitRepository.save(du);
        }
    }

    /**
     * Retourne l'addresse mail du prestataire en charge du document
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public String getProviderMail(final String identifier) {
        final DocUnit doc = docUnitRepository.findOneWithAllDependenciesForInheritance(identifier);
        if (doc.getLot() != null && doc.getLot().getProvider() != null) {
            return doc.getLot().getProvider().getEmail();
        } else if (doc.getProject() != null && doc.getProject().getProvider() != null) {
            return doc.getProject().getProvider().getEmail();
        }
        return null;
    }

    /**
     * Retourne l'addresse mail du chef de projet en charge du document
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public String getResponsibleMail(final String identifier) {
        final DocUnit doc = docUnitRepository.findOneWithAllDependenciesForInheritance(identifier);
        if (doc.getLot() != null && doc.getLot().getProvider() != null) {
            return doc.getLot().getProvider().getEmail();
        } else if (doc.getProject() != null && doc.getProject().getProvider() != null) {
            return doc.getProject().getProvider().getEmail();
        }
        return null;
    }
    
    /**
     * Récupération du langage OCR dans le docUnit, ou dans le lot.
     *
     * @param doc DocUnit
     * @return
     */
    @Transactional(readOnly = true)
    public OcrLanguage getActiveOcrLanguage(final DocUnit doc) {
        
        final DocUnit docWithOcrLang = docUnitRepository.findOneWithOcrLanguage(doc.getIdentifier());
        OcrLanguage activeOcrLanguage = null;
        if (docWithOcrLang != null) {
            activeOcrLanguage = docWithOcrLang.getActiveOcrLanguage();
            final Lot lot = docWithOcrLang.getLot();
            if (activeOcrLanguage == null && lot != null) {
                activeOcrLanguage = lot.getActiveOcrLanguage();
            }
        }
        return activeOcrLanguage;
    }

    /**
     * Retrait d'un {@link DocUnit} d'un {@link Project} et d'un {@link Lot} au besoin.
     * TODO : condition de possibilité de retrait (dépendant du workflow)
     *
     * @param identifier
     */
    @Transactional
    public void removeFromProject(final String identifier) {
        final DocUnit du = docUnitRepository.findOne(identifier);
        du.setProject(null);
        du.setLot(null);
        saveWithoutValidation(du);
        removeTrain(identifier);
    }

    @Transactional
    public void removeLot(final String identifier) {
        final DocUnit du = docUnitRepository.findOne(identifier);
        du.setLot(null);
        saveWithoutValidation(du);
    }

    @Transactional
    public void removeAllFromLot(final List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            final Set<DocUnit> dus = docUnitRepository.findByIdentifierInWithDocs(ids);
            for (final DocUnit du : dus) {
                du.setLot(null);
                saveWithoutValidation(du);
            }
        }
    }

    @Transactional
    public void removeTrain(final String identifier) {
        final DocUnit du = docUnitRepository.findOneWithDependencies(identifier);
        if (du.getPhysicalDocuments().iterator().hasNext()) {
            final PhysicalDocument pd = du.getPhysicalDocuments().iterator().next();
            pd.setTrain(null);
            physicalDocumentService.save(pd);
        }
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findAllByProjectId(final String projectId) {
        return docUnitRepository.findAllByProjectIdentifier(projectId);
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findByLibraryWithCinesExportDep(final String libraryId) {
        return docUnitRepository.findByLibraryWithCinesExportDep(libraryId);
    }
    
    @Transactional(readOnly = true)
    public List<DocUnit> findByLibraryWithOmekaExportDep(final String libraryId) {
        return docUnitRepository.findByLibraryWithOmekaExportDep(libraryId);
    }
    
    @Transactional(readOnly = true)
    public List<DocUnit> findDocUnitWithArchiveExportDepIn(final List<String> archivableDocIds) {
        return docUnitRepository.findByLibraryWithArchiveExportDep(archivableDocIds);
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findAllByLotId(final String lotId) {
        return docUnitRepository.findAllByLotIdentifier(lotId);
    }
    
    @Transactional(readOnly = true)
    public List<SummaryDocUnitDTO> findAllSummaryByLotId(final String lotId) {
        return docUnitRepository.findAllSummaryByLotId(lotId);
    }

    @Transactional(readOnly = true)
    public DocUnit findOneByLotIdAndDigitalId(final String lotId, final String digitalId) {
        return docUnitRepository.findOneByLotIdentifierAndDigitalId(lotId, digitalId);
    }

    @Transactional(readOnly = true)
    public Set<DocUnit> findAllById(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        return docUnitRepository.findByIdentifierInWithDocs(ids);
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findAllByIdWithRecords(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return docUnitRepository.findByIdentifierInWithRecords(ids);
    }

    @Transactional(readOnly = true)
    public List<DocUnit> findAllByIdWithDependencies(final Iterable<String> ids) {
        if (IterableUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return docUnitRepository.findByIdentifierInWithDependencies(ids);
    }

    @Transactional
    public List<DocUnit> clearSibling(final DocUnit docUnit) {
        docUnit.setSibling(null);
        return saveWithoutValidation(Collections.singletonList(docUnit));
    }

    @Transactional
    public void unlink(final List<String> ids) {
        final Set<DocUnit> docUnits = findAllById(ids);
        for (final DocUnit du : docUnits) {
            if (!du.getPhysicalDocuments().isEmpty()) {
                du.getPhysicalDocuments().iterator().next().setTrain(null);
            }
            du.setLot(null);
            du.setProject(null);
            save(du);
        }
    }

    /**
     * Permet de s'assurer que les données d'archivage (Cines) sont déjà existantes
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isDocumentReadyForArchivage(final String identifier) {
        final DocUnit doc = docUnitRepository.findOneWithExportDependencies(identifier);
        return doc.getExportData() != null && !doc.getExportData().getProperties().isEmpty();
    }

    /**
     * Permet de s'assurer que les données d'export vers Internet Archive sont déjà existantes
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isDocumentReadyForDiffusion(final String identifier) {
        final DocUnit doc = docUnitRepository.findOneWithExportDependencies(identifier);
        return doc.getArchiveItem() != null && doc.getArchiveItem().getIdentifier() != null;
    }

    /**
     * Permet de s'assurer que les données d'export vers Omeka sont déjà existantes
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isDocumentReadyForDiffusionOmeka(final String identifier) {
        final DocUnit doc = docUnitRepository.findOne(identifier);
        return doc.getOmekaCollection() != null && doc.getOmekaItem() != null;
    }

    @Transactional(readOnly = true)
    public Page<String> getDistinctTypes(final String search, final Integer page, final Integer size) {
        if (StringUtils.isEmpty(search)) {
            return docUnitRepository.findDistinctTypes(new PageRequest(page, size));
        } else {
            return docUnitRepository.findDistinctTypes(search, new PageRequest(page, size));
        }
    }

    /**
     * tentative de fix pb saut de ligne 
     * notamment pour qq caracteres arabes entre crochets !!
     * 
     * @param value
     * @return
     */
    public String deleteUnwantedCrLf(final String value) {
        return (value.replace("\n", " ")).replace("\r", "");
    }

    /**
     * Mise à jour des ark s'il n'a été mis à jour après l'export (diffusion datant de moins d'une semaine)
     */
    @Scheduled(cron = "${cron.docUnitUpdateArk}")
    @Transactional
    public void docUnitUpdateArk() {
        LOG.info("Lancement du Job docUnitUpdateArk...");
        final LocalDateTime dateFrom = LocalDateTime.now().minusDays(7L);
        internetArchiveReportRepository.findAllByStatusArchivedAndEmptyDocUnitArk(dateFrom).forEach(report -> {
            final DocUnit docUnit = report.getDocUnit();
            docUnit.setArkUrl(uiIAReportService.getIaArkUrl(report.getInternetArchiveIdentifier()));
            save(docUnit);
        });
    }

}
