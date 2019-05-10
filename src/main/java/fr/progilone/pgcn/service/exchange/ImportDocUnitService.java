package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.ImportReport;
import fr.progilone.pgcn.domain.exchange.ImportedDocUnit;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.repository.exchange.ImportedDocUnitRepository;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.document.DocUnitValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static fr.progilone.pgcn.exception.message.PgcnErrorCode.*;

/**
 * Created by Sebastien on 08/12/2016.
 */
@Service
public class ImportDocUnitService {

    private static final Logger LOG = LoggerFactory.getLogger(ImportDocUnitService.class);

    private final DocUnitService docUnitService;
    private final DocUnitValidationService docUnitValidationService;
    private final ImportedDocUnitRepository importedDocUnitRepository;

    @Autowired
    public ImportDocUnitService(final DocUnitService docUnitService,
                                final DocUnitValidationService docUnitValidationService,
                                final ImportedDocUnitRepository importedDocUnitRepository) {
        this.docUnitService = docUnitService;
        this.docUnitValidationService = docUnitValidationService;
        this.importedDocUnitRepository = importedDocUnitRepository;
    }

    /**
     * Sauvegarde de importDocUnit, et de l'unité documentaire associée
     * La validation de l'UD n'est pas exécutée par cette fonction
     *
     * @param importDocUnit
     * @return
     */
    @Transactional
    public ImportedDocUnit save(final ImportedDocUnit importDocUnit) {
        final DocUnit docUnit = importDocUnit.getDocUnit();
        if (docUnit != null) {
            docUnitService.save(docUnit, false);    // Sauvegarde sans validation
        }
        return importedDocUnitRepository.save(importDocUnit);
    }

    /**
     * Sauvegarde de importDocUnit seul et sans validation
     *
     * @param importDocUnit
     * @return
     */
    @Transactional
    public ImportedDocUnit saveWithoutValidation(final ImportedDocUnit importDocUnit) {
        return importedDocUnitRepository.save(importDocUnit);
    }

    /**
     * Création de importDocUnit
     * A la différence de save, les erreurs de validations sont catchées, et gérées: dans ce cas un {@link ImportedDocUnit} est tout de même créé
     * pour remonter l'erreur à l'utilisateur, mais sans unité documentaire attachée.
     *
     * @param importDocUnit
     * @return
     * @throws PgcnValidationException
     */
    @Transactional(noRollbackFor = PgcnValidationException.class)
    public ImportedDocUnit create(final ImportedDocUnit importDocUnit) throws PgcnValidationException {
        final DocUnit docUnit = importDocUnit.getDocUnit();

        try {
            // Validation de l'UD
            if (docUnit != null) {
                docUnitValidationService.validate(docUnit); // nouvelle transaction
            }
            // Si Ok => on sauvegarde
            return save(importDocUnit);

        } catch (PgcnValidationException e) {
            final List<PgcnError> errors = e.getErrors();

            // Si Ko + doublon sur PGCN ID / Statut => on supprime le doublon et on sauvegarde de nouveau
            if (errors.size() == 1 && errors.get(0).getCode() == DOC_UNIT_DUPLICATE_PGCN_ID && docUnit != null) {
                LOG.debug("L'UD {} au statut \"non disponible\" existe déjà", docUnit.getPgcnId());
                docUnitService.deleteByPgcnIdAndState(docUnit.getPgcnId(), DocUnit.State.NOT_AVAILABLE); // nouvelle transaction
                return save(importDocUnit);
            }
            // Sinon => message d'erreur
            else {
                final ImportedDocUnit imp = new ImportedDocUnit();
                imp.setReport(importDocUnit.getReport());
                imp.setDocUnitLabel(importDocUnit.getDocUnitLabel());
                imp.setDocUnitPgcnId(importDocUnit.getDocUnitPgcnId());
                saveWithError(imp, e);

                throw e;
            }
        }
    }

    /**
     * Sauvegarde des erreurs dans imp
     *
     * @param imp
     * @param e
     */
    @Transactional
    public ImportedDocUnit saveWithError(final ImportedDocUnit imp, final PgcnException e) {
        for (PgcnError pgcnError : e.getErrors()) {
            ImportedDocUnit.Message msg = new ImportedDocUnit.Message();
            msg.setCode(pgcnError.getCode().name());
            pgcnError.getComplements().stream().reduce((a, b) -> a + ", " + b).ifPresent(msg::setComplement);
            imp.addMessages(msg);
        }
        return importedDocUnitRepository.save(imp);
    }

    @Transactional(readOnly = true)
    public ImportedDocUnit findByIdentifier(String identifier) {
        return importedDocUnitRepository.findByIdentifier(identifier);
    }

    /**
     * Recherche les unités documentaires importées
     *
     * @param report
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ImportedDocUnit> findByImportReport(final ImportReport report, final Pageable pageable) {
        // récupération de la plage de résultats
        final Page<String> pageOfIds = importedDocUnitRepository.findIdentifiersByImportReport(report, pageable);

        // Chargement des résultats et de leurs relations
        List<ImportedDocUnit> reports;
        if (pageOfIds.getNumberOfElements() > 0) {
            reports = importedDocUnitRepository.findByIdentifiersIn(pageOfIds.getContent(), pageable.getSort());
        } else {
            reports = Collections.emptyList();
        }
        // Page renvoyée
        return new PageImpl<>(reports, pageable, pageOfIds.getTotalElements());
    }

    /**
     * Recherche les unités documentaires importées
     *
     * @param report
     * @param page
     * @param size
     * @param states
     * @param withErrors
     * @param withDuplicates
     * @return
     */
    @Transactional(readOnly = true)
    public Page<ImportedDocUnit> findByImportReport(final ImportReport report,
                                                    final int page,
                                                    final int size,
                                                    final List<DocUnit.State> states,
                                                    boolean withErrors,
                                                    boolean withDuplicates) {
        // récupération de la plage de résultats
        final Pageable pageable = new PageRequest(page, size);
        final Page<String> pageOfIds = importedDocUnitRepository.findIdentifiersByImportReport(report, states, withErrors, withDuplicates, pageable);

        // Chargement des résultats et de leurs relations
        List<ImportedDocUnit> reports;
        if (pageOfIds.getNumberOfElements() > 0) {
            final Sort sort =
                JpaSort.unsafe("coalesce(i.groupCode, i.parentDocUnitPgcnId, i.docUnitPgcnId)", "groupCode", "parentDocUnitPgcnId", "docUnitPgcnId");
            reports = importedDocUnitRepository.findByIdentifiersIn(pageOfIds.getContent(), sort);
        } else {
            reports = Collections.emptyList();
        }
        // Page renvoyée
        return new PageImpl<>(reports, pageable, pageOfIds.getTotalElements());
    }

    /**
     * Recherche les unités documentaires importées par import et parentKeys
     *
     * @param reportId
     * @param parentKeys
     * @return
     */
    @Transactional(readOnly = true)
    public List<ImportedDocUnit> findByReportIdentifierAndParentKeyIn(final String reportId, final Collection<String> parentKeys) {
        return CollectionUtils.isNotEmpty(parentKeys) ?
               importedDocUnitRepository.findByReportIdentifierAndParentKeyIn(reportId, parentKeys) :
               Collections.emptyList();
    }

    /**
     * @param report
     * @param state
     * @param pageable
     * @return page d'identifiant des unités documentaires importées
     */
    @Transactional(readOnly = true)
    public Page<DocUnit> findDocUnitByImportReport(final ImportReport report, final DocUnit.State state, final Pageable pageable) {
        // Page de résultats
        final Page<String> pageOfIds = importedDocUnitRepository.findDocUnitIdentifiersByImportReport(report, state, pageable);
        // Chargement des résultats et de leurs relations
        final List<DocUnit> docUnits = docUnitService.findAllByIdWithRecords(pageOfIds.getContent());
        // Page renvoyée
        return new PageImpl<>(docUnits, pageable, pageOfIds.getTotalElements());
    }

    @Transactional
    public void updateProcess(final String identifier, final ImportedDocUnit.Process process) {
        importedDocUnitRepository.updateProcess(identifier, process);
    }
}
