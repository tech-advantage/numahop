package fr.progilone.pgcn.service.exchange.internetarchive;

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
import fr.progilone.pgcn.domain.exchange.internetarchive.InternetArchiveReport;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.exchange.internetarchive.InternetArchiveReportRepository;
import fr.progilone.pgcn.web.websocket.WebsocketService;

/**
 * @author jbrunet
 * Créé le 2 mai 2017
 */
@Service
public class InternetArchiveReportService {

    private static final Logger LOG = LoggerFactory.getLogger(InternetArchiveReportService.class);

    private final InternetArchiveReportRepository iaReportRepository;
    private final WebsocketService websocketService;

    @Autowired
    public InternetArchiveReportService(final InternetArchiveReportRepository iaReportRepository, final WebsocketService websocketService) {
        this.iaReportRepository = iaReportRepository;
        this.websocketService = websocketService;
    }

    @PostConstruct
    @Transactional
    public void init() {
        // Mise à jour du statut des rapports en cours d'exécution au démarrage de l'application
        final List<InternetArchiveReport> interruptedImports =
            iaReportRepository.findByStatusIn(InternetArchiveReport.Status.EXPORTING, InternetArchiveReport.Status.SENDING);
        for (final InternetArchiveReport report : interruptedImports) {
            LOG.warn("L'export Internet Archive de {}, démarré le {}, a été interrompu au statut {}",
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
     * @param identifier
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InternetArchiveReport createInternetArchiveReport(final DocUnit docUnit, final String identifier) {
        final InternetArchiveReport report = new InternetArchiveReport();
        report.setDocUnit(docUnit);
        report.setInternetArchiveIdentifier(identifier);
        report.setNumber(0);
        report.setStatus(InternetArchiveReport.Status.EXPORTING);

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Les documents vont être envoyés sur le serveur de versement.
     * Le rapport passe au status SENDING.
     * Le nombre total de fichiers à envoyer
     *
     * @param report
     * @param total
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InternetArchiveReport setReportSending(final InternetArchiveReport report, final int total) {
        report.setStatus(InternetArchiveReport.Status.SENDING);
        report.setTotal(total);
        report.setDateSent(LocalDateTime.now());

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Met à jour le rapport en ajoutant le nombre de fichiers au total envoyé
     *
     * @param report
     * @param sent
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InternetArchiveReport updateReport(final InternetArchiveReport report, final int sent) {
        report.setNumber(report.getNumber() + sent);
        report.setDateSent(LocalDateTime.now());

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
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
    public InternetArchiveReport setReportSent(final InternetArchiveReport report) {
        report.setStatus(InternetArchiveReport.Status.SENT);
        report.setDateSent(LocalDateTime.now());

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    /**
     * Le serveur de versement a renvoyé le certificat d'archivage.
     * Le rapport passe au statut ARCHIVED.
     *
     * @param report
     * @param dateArchive
     * @param identifier
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public InternetArchiveReport setReportArchived(final InternetArchiveReport report, final LocalDateTime dateArchive, final String identifier) {
        report.setStatus(InternetArchiveReport.Status.ARCHIVED);
        report.setDateArchived(dateArchive);
        report.setInternetArchiveIdentifier(identifier);

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
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
    public InternetArchiveReport failReport(final InternetArchiveReport report, final String message) {
        report.setMessage("Arrêt imprévu du traitement au statut " + report.getStatus() + " avec l'erreur: " + message);
        report.setStatus(InternetArchiveReport.Status.FAILED);

        final InternetArchiveReport savedReport = iaReportRepository.save(report);
        sendUpdate(savedReport);
        return savedReport;
    }

    @Transactional(readOnly = true)
    public long countPendingByLibrary(final Library library) {
        return iaReportRepository.countByDocUnitLibraryAndStatusIn(library, InternetArchiveReport.Status.SENT);
    }

    @Transactional(readOnly = true)
    public InternetArchiveReport findByIdentifier(final String identifier) {
        return iaReportRepository.findByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public List<InternetArchiveReport> findByDocUnit(final String docUnitId) {
        return iaReportRepository.findByDocUnitIdentifierOrderByLastModifiedDateDesc(docUnitId);
    }

    @Transactional(readOnly = true)
    public List<InternetArchiveReport> findByDocUnits(final List<String> docUnitIds) {
        return iaReportRepository.findByDocUnitIdentifierIn(docUnitIds);
    }

    @Transactional(readOnly = true)
    public List<InternetArchiveReport> findAll(final List<String> libraries, final LocalDate fromDate, final boolean failures) {
        return iaReportRepository.findAll(libraries, fromDate, failures);
    }

    /**
     * Envoie la mise à jour du rapport par websocket
     *
     * @param report
     */
    private void sendUpdate(final InternetArchiveReport report) {
        websocketService.sendObject(report.getIdentifier(), getStatus(report));
    }

    /**
     * Construction d'une {@link Map} contenant les informations sur le statut de l'import
     *
     * @param report
     * @return
     */
    private Map<String, Object> getStatus(final InternetArchiveReport report) {
        final Map<String, Object> response = new HashMap<>();
        response.put("identifier", report.getIdentifier());
        response.put("status", report.getStatus());
        return response;
    }
}
