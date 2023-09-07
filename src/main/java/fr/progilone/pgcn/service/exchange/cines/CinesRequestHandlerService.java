package fr.progilone.pgcn.service.exchange.cines;

import fr.progilone.pgcn.domain.administration.MailboxConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.cines.CinesReport;
import fr.progilone.pgcn.domain.jaxb.aip.PacType;
import fr.progilone.pgcn.domain.jaxb.avis.PacAvisType;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.administration.MailboxConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.mail.MailboxService;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.DateUtils;
import fr.progilone.pgcn.service.util.transaction.TransactionService;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.PostConstruct;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service gérant l'évolution des éléments exportés vers le(s) serveur(s) CINES
 * <p>
 * Created by Sébastien on 04/01/2017.
 */
@Service
public class CinesRequestHandlerService {

    public static final String SIP_XML_FILE = "sip.xml";
    public static final String AIP_XML_FILE = "aip.xml";
    public static final String METS_XML_FILE = "mets.xml";

    public static final String CINES_SUBJECT_ARCHIVAGE = "AVIS_PAC";

    private static final Logger LOG = LoggerFactory.getLogger(CinesRequestHandlerService.class);

    // Id des réponses pac_avis
    private static final String REP_ACCUSE_RECEPTION_DE_VERSEMENT = "ACCUSE_RECEPTION_DE_VERSEMENT";
    private static final String REP_CERTIFICAT_ARCHIVAGE = "CERTIFICAT_ARCHIVAGE";
    private static final String REP_REJET_VERSEMENT = "REJET_VERSEMENT";

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.cines.updating.cronenabled}")
    private boolean cinesUpdatingEnabled;

    // Stockage des AIP
    @Value("${services.cines.aip}")
    private String workingDir;

    // Stockage temp des SIP
    @Value("${services.cines.cache}")
    private String cacheDir;

    private final CinesReportService cinesReportService;
    private final ExportCinesService exportCinesService;
    private final MailboxService mailboxService;
    private final MailboxConfigurationService mailboxConfigurationService;
    private final FileStorageManager fm;
    private final DocUnitService docUnitService;
    private final TransactionService transactionService;

    @Autowired
    public CinesRequestHandlerService(final ExportCinesService exportCinesService,
                                      final CinesReportService cinesReportService,
                                      final MailboxService mailboxService,
                                      final MailboxConfigurationService mailboxConfigurationService,
                                      final FileStorageManager fm,
                                      final DocUnitService docUnitService,
                                      final TransactionService transactionService) {
        this.cinesReportService = cinesReportService;
        this.exportCinesService = exportCinesService;
        this.mailboxService = mailboxService;
        this.mailboxConfigurationService = mailboxConfigurationService;
        this.fm = fm;
        this.docUnitService = docUnitService;
        this.transactionService = transactionService;
    }

    @PostConstruct
    public void initialize() {

        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(workingDir, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });

    }

    @Scheduled(cron = "${cron.cinesExport}")
    public void cinesExportCron() {

        LOG.info("Lancement du cronjob cinesExport ...");
        final List<String> docsToExport = exportCinesService.findDocUnitsReadyForCinesExport();
        docsToExport.forEach(id -> {
            LOG.info("Debut export CINES - DocUnit[{}]", id);
            final CinesReport report = exportCinesService.exportDocToCines(id, true);
            LOG.info("Fin export CINES - DocUnit[{}] - Message: {}", id, report.getMessage());
        });
    }

    @Scheduled(cron = "${cron.cinesUpdateStatus}")
    public void updateExportedDocUnitsCron() {

        if (cinesUpdatingEnabled) {
            LOG.info("Lancement du cronjob updateExportedDocUnitsCron...");
            final Set<MailboxConfiguration> confs = mailboxConfigurationService.findAll(true);
            try {
                updateExportedDocUnits(confs);

            } catch (final PgcnTechnicalException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Vérification des boite mails à la recherche de messages concernant les exports en attente de réponse de la part de CINES
     *
     * @param mailboxConfigurations
     * @throws PgcnTechnicalException
     */
    public void updateExportedDocUnits(final Collection<MailboxConfiguration> mailboxConfigurations) throws PgcnTechnicalException {

        for (final MailboxConfiguration conf : mailboxConfigurations) {

            final long nbPending = cinesReportService.countPendingByLibrary(conf.getLibrary());
            // Si des exports CINES sont en attente de réponse, on vérifie la boite mail
            if (nbPending > 0) {

                mailboxService.readMailbox(conf, messages -> {

                    if (messages.length > 0) {

                        transactionService.executeInNewTransaction(() -> {

                            for (final Message message : messages) {

                                try {
                                    final String subject = message.getSubject();
                                    // On ne traite pas les messages autres genre Ticket etc..
                                    if (subject == null || !subject.contains(CINES_SUBJECT_ARCHIVAGE)) {
                                        message.setFlag(Flags.Flag.SEEN, true);
                                    } else {
                                        // les messages qui nous interessent
                                        final CinesResponse response = parseMessage(message);
                                        if (updateExport(response, conf.getLibrary())) {
                                            message.setFlag(Flags.Flag.SEEN, true);
                                        } else {
                                            message.setFlag(Flags.Flag.SEEN, false);
                                        }
                                    }
                                } catch (final JAXBException | IOException | MessagingException e) {
                                    LOG.warn("Vérification des boite mails CINES - Problem: {}", e.getMessage(), e);
                                    // on continue pour laisser leur chance aux msg suivants....
                                }
                            }
                        });
                    }
                });

            } else {
                LOG.debug("CINES - Aucun export en attente de reponse pour la bibliothèque {}", conf.getLibrary().getIdentifier());
            }

        }
    }

    /**
     * Parse le message à la recherche d'un corps au format avis.xsd et/ou d'une pièce jointe au format aip.xsd
     *
     * @param message
     * @return
     * @throws MessagingException
     * @throws IOException
     * @throws JAXBException
     */
    private CinesResponse parseMessage(final Message message) throws MessagingException, IOException, JAXBException {

        final Date sentDate = message.getSentDate();
        LOG.debug("Lecture du message {} du {}", message.getSubject(), sentDate);
        final CinesResponse response = new CinesResponse();
        response.setMsgDate(sentDate != null ? DateUtils.convertToLocalDateTime(sentDate)
                                             : LocalDateTime.now());

        final Multipart multipart;
        if (message.isMimeType(MediaType.TEXT_HTML_VALUE) || message.isMimeType(MediaType.TEXT_XML_VALUE)) {
            // ert: on reconstitue un pseudo multipart...
            // correction pour msg 'vides' non multipart avec du texte attaché qui ne passent pas.
            final DataSource ds = new ByteArrayDataSource(message.getInputStream(), MediaType.TEXT_PLAIN_VALUE);
            final MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setDataHandler(new DataHandler(ds));
            multipart = new MimeMultipart("multipart/alternative");
            multipart.addBodyPart(bodyPart);
        } else {
            multipart = (Multipart) message.getContent();
        }

        // Parcours des différentes parties du mail
        for (int i = 0, n = multipart.getCount(); i < n; i++) {
            final Part part = multipart.getBodyPart(i);
            final String disposition = part.getDisposition();
            final String fileName = part.getFileName();

            Optional<?> parseResult = Optional.empty();

            // Corps du mail
            if (disposition == null) {
                // Corps du mail au format text/plain
                if (part.isMimeType(MediaType.TEXT_PLAIN_VALUE)) {
                    parseResult = parsePart(part, fr.progilone.pgcn.domain.jaxb.avis.ObjectFactory.class);
                    parseResult.map(elt -> ((JAXBElement<PacAvisType>) elt).getValue()).ifPresent(response::setAvis);
                }
                // Corps du mail au format multipart/alternative
                else if (part.isMimeType("multipart/alternative")) {
                    final Multipart subMultipart = (Multipart) part.getContent();

                    // Parcours des sous-parties du corps du mail
                    for (int j = 0, n2 = subMultipart.getCount(); j < n2; j++) {
                        final Part subPart = subMultipart.getBodyPart(i);

                        // Sous-partie au format text/plain
                        if (subPart.isMimeType(MediaType.TEXT_PLAIN_VALUE)) {

                            parseResult = parsePart(subPart, fr.progilone.pgcn.domain.jaxb.avis.ObjectFactory.class);
                            parseResult.map(elt -> ((JAXBElement<PacAvisType>) elt).getValue()).ifPresent(response::setAvis);
                            break;
                        }
                    }
                }
                if (!parseResult.isPresent()) {
                    response.setErrorMessage("Réponse ".concat(message.getSubject())
                                                       .concat(" - ")
                                                       .concat(response.getMsgDate().toString())
                                                       .concat(": le contenu de format texte n'a pu être interprété."));
                }

            }
            // Pièce jointe xml
            else if (StringUtils.equals(disposition, Part.ATTACHMENT) && (part.isMimeType(MediaType.TEXT_XML_VALUE) || part.isMimeType(MediaType.APPLICATION_XML_VALUE))) {

                parseResult = parsePart(part, fr.progilone.pgcn.domain.jaxb.aip.ObjectFactory.class);

                // aip.xml
                if (StringUtils.equals(fileName, AIP_XML_FILE)) {
                    if (parseResult.isPresent()) {
                        parseResult.map(elt -> ((JAXBElement<PacType>) elt).getValue()).ifPresent(response::setAip);
                    } else {
                        response.setErrorMessage("Réponse ".concat(message.getSubject())
                                                           .concat(" - ")
                                                           .concat(response.getMsgDate().toString())
                                                           .concat(": le fichier AIP n'a pu être interprété."));
                    }

                    if (response.getAip() != null) {

                        final PacType pac = response.getAip();

                        // stocke le fichier aip.xml
                        handleAip(response.getAip(), part);

                        // il n'y a plus d'avis dans ce cas => on cree un avis + un certificat de toutes pieces...
                        final String certif = "Archivé le " + pac.getDocMeta().getDateArchivage()
                                              + " - Identifiant versement : "
                                              + pac.getDocMeta().getIdentifiantVersement()
                                              + " - Identifiant docPac : "
                                              + pac.getDocMeta().getIdentifiantDocPac();

                        final fr.progilone.pgcn.domain.jaxb.avis.ObjectFactory avisFactory = new fr.progilone.pgcn.domain.jaxb.avis.ObjectFactory();
                        final PacAvisType avis = avisFactory.createPacAvisType();

                        final ZonedDateTime resultDtArchiv = ZonedDateTime.parse(pac.getDocMeta().getDateArchivage(), DateTimeFormatter.ISO_DATE_TIME);
                        avis.setDateArchivage(resultDtArchiv.toLocalDateTime());
                        avis.setIdVersement(pac.getDocMeta().getIdentifiantVersement());
                        avis.setIdentifiantDocPac(pac.getDocMeta().getIdentifiantDocPac());
                        avis.setId(REP_CERTIFICAT_ARCHIVAGE);
                        response.setAvis(avis);
                        response.setCertificate(certif.getBytes("UTF-8"));
                    }

                    // AVIS .xml
                } else {

                    if (parseResult.isPresent()) {
                        parseResult.map(elt -> ((JAXBElement<PacAvisType>) elt).getValue()).ifPresent(response::setAvis);
                    } else {
                        response.setErrorMessage("Réponse ".concat(message.getSubject())
                                                           .concat(" - ")
                                                           .concat(response.getMsgDate().toString())
                                                           .concat(": le fichier Avis n'a pu être interprété."));
                    }

                }
            }
        }
        return response;
    }

    /**
     * Parse part à partir des classes jaxb jaxbBoundedClasses
     *
     * @param part
     * @param jaxbBoundedClasses
     * @return
     * @throws JAXBException
     * @throws IOException
     * @throws MessagingException
     */
    private Optional<?> parsePart(final Part part, final Class<?>... jaxbBoundedClasses) throws JAXBException, IOException, MessagingException {
        try (final Reader reader = new InputStreamReader(part.getInputStream(), "UTF-8")) {
            final JAXBContext context = JAXBContext.newInstance(jaxbBoundedClasses);
            final Unmarshaller unmarshaller = context.createUnmarshaller();

            return Optional.of(unmarshaller.unmarshal(reader));

        } catch (final UnmarshalException e) {
            final String strFile;
            if (IOUtils.toString(part.getInputStream(), "UTF-8").length() > 500) {
                strFile = IOUtils.toString(part.getInputStream(), "UTF-8").substring(0, 500);
            } else {
                strFile = IOUtils.toString(part.getInputStream(), "UTF-8");
            }

            LOG.error("Le message {} n'a pas pu être parsé à l'aide des classes {}",
                      strFile,
                      Arrays.stream(jaxbBoundedClasses)
                            .map(Class::getName)
                            .reduce((a, b) -> a + ", "
                                              + b)
                            .orElse("[Aucune classe fournie]"));
            return Optional.empty();
        }
    }

    /**
     * Mise à jour du rapport d'export à partir de la réponse extraite du mail.
     *
     * @param response
     * @return treated : Vrai si msg lu et rapprt mis à jour.
     */
    private boolean updateExport(final CinesResponse response, final Library library) {

        boolean treated = false;

        if (response.getAvis() == null) {
            return treated; // Le message n'a pas pu être parsé
        }
        // Recherche de l'export CINES correspondant à l'identifiant de versement précisé dans la réponse
        // cet id correspond au pgcnId de la docUnit
        final String idVersement;
        if (response.getAvis() != null) {
            idVersement = response.getAvis().getIdVersement();
        } else {
            idVersement = response.getAip().getDocMeta().getIdentifiantVersement();
        }
        final CinesReport report = cinesReportService.findByIdVersement(idVersement);
        if (report == null) {
            LOG.error("L'export CINES avec l'identifiant de versement {} n'a pas été trouvé", idVersement);
            return treated;
        }
        if (report.getDocUnit() == null || report.getDocUnit().getLibrary() == null
            || !StringUtils.equals(library.getIdentifier(), report.getDocUnit().getLibrary().getIdentifier())) {
            LOG.info("L'export CINES avec l'identifiant de versement {} ne depend pas de cette librairie...", idVersement);
            return treated;
        }

        // AR de versement
        if (response.hasId(REP_ACCUSE_RECEPTION_DE_VERSEMENT)) {
            if (report.getStatus() == CinesReport.Status.SENT) {
                LOG.debug("L'export {} passe au statut {}", idVersement, CinesReport.Status.AR_RECEIVED);
                cinesReportService.setReportArReceived(report, response.getMsgDate());
                treated = true;
            }
        }
        // Rejet du versement
        else if (response.hasId(REP_REJET_VERSEMENT)) {
            if (report.getStatus() == CinesReport.Status.SENT || report.getStatus() == CinesReport.Status.AR_RECEIVED) {
                String motive = response.getAvis().getCommentaire();
                if (StringUtils.isBlank(motive)) {
                    motive = response.getAvis().getErreurValidation();
                }
                LOG.debug("L'export {} passe au statut {} - motif : {}", idVersement, CinesReport.Status.REJECTED, motive);
                cinesReportService.setReportRejected(report, response.getMsgDate(), motive);
                treated = true;
            }
        }
        // Certificat d'archivage
        else if (response.hasId(REP_CERTIFICAT_ARCHIVAGE)) {
            if (report.getStatus() == CinesReport.Status.SENT || report.getStatus() == CinesReport.Status.AR_RECEIVED) {
                LOG.debug("L'export {} passe au statut {}", idVersement, CinesReport.Status.ARCHIVED);
                cinesReportService.setReportArchived(report, response.getMsgDate(), response.getCertificateAsString());
                treated = true;
            }
        }
        // Non géré
        else {
            LOG.warn("Le type de réponse {} n'est pas géré", response.getAvis().getId());
            treated = false;
        }
        return treated;
    }

    /**
     * Assure le stockage du fichier aip.xml
     * Unique par docUnit
     *
     * @param aip
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    private void handleAip(final PacType aip, final Part part) throws IOException, MessagingException {
        if (aip != null) {
            final String idVersement = aip.getDocMeta() != null ? aip.getDocMeta().getIdentifiantDocProducteur()
                                                                : null;
            if (idVersement != null) {
                final DocUnit availableDoc = docUnitService.findOneByPgcnIdAndState(idVersement, DocUnit.State.AVAILABLE);
                final DocUnit closedDoc = docUnitService.findOneByPgcnIdAndState(idVersement, DocUnit.State.CLOSED);
                if (availableDoc != null || closedDoc != null) {
                    final DocUnit foundDoc = availableDoc != null ? availableDoc
                                                                  : closedDoc;
                    final Path root = Paths.get(workingDir, exportCinesService.getDocLibraryId(foundDoc.getIdentifier()), foundDoc.getIdentifier());
                    if (root != null) {
                        fm.copyInputStreamToFile(part.getInputStream(), root.toFile(), AIP_XML_FILE, true, false);
                    }
                } else {
                    LOG.error("DocUnit non trouve - pgcnID = {}", idVersement);
                }
            }
        }
    }

    /**
     * Récupération du fichier aip.xml stocké
     * pour un docUnit donné
     *
     * @param docUnit
     * @return
     */
    public File retrieveAip(final String docUnit) {
        if (docUnit != null) {
            final Path root = Paths.get(workingDir, exportCinesService.getDocLibraryId(docUnit), docUnit);
            if (root != null) {
                return fm.retrieveFile(root.toFile(), AIP_XML_FILE);
            }
        }
        return null;
    }

    /**
     * Récupération du fichier sip.xml le + recent stocké en cache
     * ou backuppé pour un docUnit donné.
     *
     * @param docUnitId
     * @return
     */
    public File retrieveSip(final String docUnitId, final boolean exportError) {

        if (docUnitId != null) {
            if (exportError) {
                final DocUnit docUnit = docUnitService.findOne(docUnitId);
                final Path root = Paths.get(cacheDir, exportCinesService.getDocLibraryId(docUnitId), docUnit.getPgcnId());
                if (root != null) {
                    return fm.retrieveFile(root.toFile(), SIP_XML_FILE);
                }
            } else {

                final Path root = Paths.get(workingDir, exportCinesService.getDocLibraryId(docUnitId), docUnitId);
                if (root != null) {
                    return fm.retrieveFile(root.toFile(), SIP_XML_FILE);
                }
            }
        }
        return null;
    }

    /**
     * Récupération du fichier mets.xml le + recent stocké en cache
     * ou backuppé pour un docUnit donné.
     *
     * @param docUnitId
     * @return
     */
    public File retrieveMets(final String docUnitId, final boolean exportError) {

        if (docUnitId != null) {
            if (exportError) {
                final DocUnit docUnit = docUnitService.findOne(docUnitId);
                final Path root = Paths.get(cacheDir, exportCinesService.getDocLibraryId(docUnitId), docUnit.getPgcnId());
                if (root != null) {
                    return fm.retrieveFile(root.toFile(), METS_XML_FILE);
                }
            } else {

                final Path root = Paths.get(workingDir, exportCinesService.getDocLibraryId(docUnitId), docUnitId);
                if (root != null) {
                    return fm.retrieveFile(root.toFile(), METS_XML_FILE);
                }
            }
        }
        return null;
    }

    /**
     * Classe représentant une réponse de la part du CINES
     */
    private static class CinesResponse {

        private LocalDateTime msgDate;

        /**
         * Corps du mail parsé suivant le schéma avis.xsd
         */
        private PacAvisType avis;

        /**
         * Fichier aip.xml parsé suivant le schéma aip.xsd
         */
        private PacType aip;

        /**
         * Contenu brut du certificat issu de l'avis correspondant
         */
        private byte[] certificate;

        /**
         * Message d'erreur eventuel.
         */
        private String errorMessage;

        /**
         * La réponse correspond-t-elle au type passé en paramètre ?
         *
         * @param repId
         * @return
         */
        public boolean hasId(final String repId) {
            return avis != null && StringUtils.equals(avis.getId(), repId);
        }

        public LocalDateTime getMsgDate() {
            return msgDate;
        }

        public void setMsgDate(final LocalDateTime msgDate) {
            this.msgDate = msgDate;
        }

        public PacAvisType getAvis() {
            return avis;
        }

        public void setAvis(final PacAvisType avis) {
            this.avis = avis;
        }

        public PacType getAip() {
            return aip;
        }

        public void setAip(final PacType aip) {
            this.aip = aip;
        }

        public String getCertificateAsString() {
            return getCertificateAsString(StandardCharsets.UTF_8);
        }

        public String getCertificateAsString(final Charset charEncoding) {
            return new String(this.certificate, charEncoding);
        }

        public void setCertificate(final byte[] certificate) {
            this.certificate = certificate;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(final String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
