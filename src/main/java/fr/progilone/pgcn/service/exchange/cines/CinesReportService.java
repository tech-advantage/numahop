package fr.progilone.pgcn.service.exchange.cines;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.workflow.WorkflowStateKey;
import fr.progilone.pgcn.repository.exchange.cines.CinesReportRepository;
import fr.progilone.pgcn.service.workflow.WorkflowService;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * Created by Sébastien on 02/01/2017.
 */
@Service
public class CinesReportService {

    private static final Logger LOG = LoggerFactory.getLogger(CinesReportService.class);

    private final CinesReportRepository cinesReportRepository;
    private final WebsocketService websocketService;
    private final WorkflowService workflowService;

    @Autowired
    public CinesReportService(final CinesReportRepository cinesReportRepository, final WebsocketService websocketService, final WorkflowService workflowService) {
        this.cinesReportRepository = cinesReportRepository;
        this.websocketService = websocketService;
        this.workflowService = workflowService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Mise à jour du statut des rapports en cours d'exécution au démarrage de l'application
        final List<CinesReport> interruptedImports = cinesReportRepository.findByStatusIn(CinesReport.Status.EXPORTING, CinesReport.Status.SENDING);
        for (final CinesReport report : interruptedImports) {
            LOG.warn("L'export CINES de {}, démarré le {}, a été interrompu au statut {}",
                     report.getIdentifier(),
                     report.getCreatedDate(),
                     report.getStatus());
            failReport(report, "L'import a été interrompu en cours d'exécution");
        }
    }

    /**
     * Les répertoires et fichiers d'export sont en cours de création.
     * Création du rapport d'export au statut EXPORTING.
     *
     * @param docUnit
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CinesReport createCinesReport(final DocUnit docUnit) {
        final CinesReport report = new CinesReport();
        report.setDocUnit(docUnit);
        report.setStatus(CinesReport.Status.EXPORTING);

        final CinesReport savedReport = cinesReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Les documents vont être envoyés sur le serveur de versement.
     * Le rapport passe au status SENDING.
     *
     * @param report
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CinesReport setReportSending(final CinesReport report) {
        report.setStatus(CinesReport.Status.SENDING);
        report.setDateSent(LocalDateTime.now());

        final CinesReport savedReport = cinesReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Les documents ont été envoyés au serveur de versement.
     * Le rapport passe au statut SENT.
     *
     * @param report
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CinesReport setReportSent(final CinesReport report) {
        report.setStatus(CinesReport.Status.SENT);
        report.setDateSent(LocalDateTime.now());

        final CinesReport savedReport = cinesReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Le serveur de versement a renvoyé un accusé réception de confirmation.
     * Le rapport passe au statut AR_RECEIVED.
     *
     * @param report
     * @param dateAr
     * @return
     */
    @Transactional
    public CinesReport setReportArReceived(final CinesReport report, final LocalDateTime dateAr) {
        report.setStatus(CinesReport.Status.AR_RECEIVED);
        report.setDateAr(dateAr);

        final CinesReport savedReport = cinesReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Le serveur de versement a renvoyé un accusé réception de rejet.
     * Le rapport passe au statut REJECTED.
     *
     * @param report
     * @param dateRejection
     * @param motive
     * @return
     */
    @Transactional
    public CinesReport setReportRejected(final CinesReport report, final LocalDateTime dateRejection, final String motive) {
        report.setStatus(CinesReport.Status.REJECTED);
        report.setDateRejection(dateRejection);
        report.setRejectionMotive(motive);
        // le cines a rejeté, on doit rester en 1er versement
        report.getDocUnit().setCinesVersion(null);

        final CinesReport savedReport = cinesReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Le serveur de versement a renvoyé le certificat d'archivage.
     * Le rapport passe au statut ARCHIVED.
     *
     * @param report
     * @param dateArchive
     * @param certificate
     * @return
     */
    @Transactional
    public CinesReport setReportArchived(final CinesReport report, final LocalDateTime dateArchive, final String certificate) {
        report.setStatus(CinesReport.Status.ARCHIVED);
        report.setDateArchived(dateArchive);
        report.setCertificate(certificate);
        final CinesReport savedReport = cinesReportRepository.save(report);
        
        if (CinesReport.Status.ARCHIVED == savedReport.getStatus()) {
            // on avance ds le workflow si necessaire 
            final String docUnitId = savedReport.getDocUnit().getIdentifier();
            if (workflowService.isStateRunning(docUnitId, WorkflowStateKey.ARCHIVAGE_DOCUMENT)) {
                workflowService.processAutomaticState(docUnitId, WorkflowStateKey.ARCHIVAGE_DOCUMENT);
            }
        }

        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Une erreur est survenue pendant l'export.
     * Le rapport passe au statut FAILED.
     *
     * @param report
     * @param message
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CinesReport failReport(final CinesReport report, final String message) {
    	
    	final CinesReport reloaded = cinesReportRepository.findByIdentifier(report.getIdentifier());

    	reloaded.setMessage("Arrêt imprévu du traitement au statut " + reloaded.getStatus() + " avec l'erreur: " + message);
    	reloaded.setStatus(CinesReport.Status.FAILED);
    	reloaded.setDateSent(LocalDateTime.now());

        final CinesReport savedReport = cinesReportRepository.save(reloaded);
        sendUpdate(savedReport);
        return savedReport;
    }

    @Transactional(readOnly = true)
    public long countPendingByLibrary(final Library library) {
        return cinesReportRepository.countByDocUnitLibraryAndStatusIn(library, CinesReport.Status.AR_RECEIVED, CinesReport.Status.SENT);
    }

    @Transactional(readOnly = true)
    public CinesReport findByIdVersement(final String idVersement) {
        return cinesReportRepository.findFirstByDocUnitPgcnIdOrderByDateSentDesc(idVersement);
    }

    @Transactional(readOnly = true)
    public List<CinesReport> findByDocUnit(final String docUnitId) {
        return cinesReportRepository.findByDocUnitIdentifierOrderByLastModifiedDateDesc(docUnitId);
    }

    @Transactional(readOnly = true)
    public List<CinesReport> findByDocUnits(final List<String> docUnitIds) {
        return cinesReportRepository.findByDocUnitIdentifierIn(docUnitIds);
    }

    @Transactional(readOnly = true)
    public List<CinesReport> findAll(final List<String> libraries, final LocalDate fromDate, final boolean failures) {
        return cinesReportRepository.findAll(libraries, fromDate, failures);
    }

    /**
     * Envoie la mise à jour du rapport par websocket
     *
     * @param report
     */
    private void sendUpdate(final CinesReport report) {
        websocketService.sendObject(report.getIdentifier(), getStatus(report));
    }

    /**
     * Construction d'une {@link Map} contenant les informations sur le statut de l'import
     *
     * @param report
     * @return
     */
    private Map<String, Object> getStatus(final CinesReport report) {
        final Map<String, Object> response = new HashMap<>();
        response.put("identifier", report.getIdentifier());
        response.put("status", report.getStatus());
        return response;
    }
}
