package fr.progilone.pgcn.service.delivery;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckResult.AutoCheckResult;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.service.MailService;
import fr.progilone.pgcn.service.storage.FileStorageManager;



/**
 * Service gerant les rapports de livraison.
 *
 * @author Emmanuel RIZET
 *
 */
@Service
public class DeliveryReportingService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryReportingService.class);

    private final String LINE_SEP = System.lineSeparator();

    private final String REPORT_NAME = "deliv_report.txt";

    private final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss", Locale.FRENCH);

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Value("${services.deliveryreporting.path}")
    private String deliveryReportingDirectory;

    private final FileStorageManager fm;
    private final DeliveryService deliveryService;
    private final MailService mailService;

    @Autowired
    public DeliveryReportingService(final FileStorageManager fm,
            final DeliveryService deliveryService,
            final MailService mailService) {
        this.fm = fm;
        this.deliveryService = deliveryService;
        this.mailService = mailService;
    }
    
    @PostConstruct
    public void initialize() {
        
        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(deliveryReportingDirectory, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });

    }

    

    /**
     * Construction du rapport de livraison.
     *
     * @param delivery
     * @param startDeliv
     * @param deliveryRefused
     * @param results
     */
    @Transactional
    public void createReport(final Delivery delivery, final LocalDateTime startDeliv, final boolean deliveryRefused, final Map<String, PrefixedDocuments> documentsToTreat,
                             final int nbRefus, final List<AutomaticCheckResult> results, final Map<AutoCheckType, AutomaticCheckRule> rules,
                             final Map<String, Integer> expectedPagesByPrefix, final String seqSeparator, final String libraryId) {


        final StringBuilder report = new StringBuilder(2048);
        report.append("*** RAPPORT DE LIVRAISON ***")
                .append(LINE_SEP).append(LINE_SEP)
                .append(delivery.getLabel());
        if (deliveryRefused) {
            report.append(" => LIVRAISON REJETEE (")
                    .append(String.valueOf(nbRefus)).append(" document(s) rejeté(s) )");
        }
        report.append(LINE_SEP).append(LINE_SEP)
                .append("[").append(startDeliv.format(dtFormat))
                .append("] DEBUT LIVRAISON")
                .append(LINE_SEP).append(LINE_SEP)
                .append("DOCUMENTS: ").append(LINE_SEP);
        
        // Documents concernés.
        final Set<DeliveredDocument> documents = deliveryService.getDigitalDocumentsByDelivery(delivery.getIdentifier());
        documents.stream()
                .filter(doc->doc.getDigitalDocument()!=null && doc.getDigitalDocument().getDocUnit()!=null)
                .forEach(doc -> {

                    final DigitalDocument digDoc = doc.getDigitalDocument();
                    final String digitalId = digDoc.getDigitalId();
                    report.append(digDoc.getDocUnit().getPgcnId());

                    final int nbPages = expectedPagesByPrefix.get(digitalId) != null ? expectedPagesByPrefix.get(digitalId) : 0;

                    if (nbPages > 0
                            && documentsToTreat.keySet().contains(digitalId)) {
                        report.append(" (")
                                .append(nbPages)
                                .append(" pages livrées")
                                .append(")");
                    }
                    report.append(LINE_SEP);
                });

        report.append(LINE_SEP)
                .append("[").append(startDeliv.format(dtFormat))
                .append("] DEBUT CONTROLES AUTOMATIQUES")
                .append(LINE_SEP);

        // Detail controles automatiques
        documents.stream()
                .filter(doc->doc.getDigitalDocument().getDocUnit()!=null)
                .forEach(doc -> {

                    final DigitalDocument dd = doc.getDigitalDocument();
                    final String digitalId = dd.getDigitalId();

                    final int expectedPages = expectedPagesByPrefix.get(digitalId) != null ? expectedPagesByPrefix.get(digitalId) : 0;

                    report.append(LINE_SEP).append("+++ DOCUMENT ")
                                .append(dd.getDocUnit().getPgcnId())
                                .append(" +++")
                                .append(LINE_SEP).append(LINE_SEP);

                    results.stream()
                            .filter(res->res.getDigitalDocument() != null && res.getDigitalDocument().equals(dd))
                            .forEach(res-> {
                                report.append(" - ")
                                        .append(res.getCheck().getLabel()).append(" : ");

                                final AutomaticCheckRule rule = rules.get(res.getType());
                                if (rule != null && !rule.isActive() ) {
                                    report.append("CONTROLE NON ACTIVE").append(LINE_SEP);
                                } else {
                                    if (AutoCheckResult.OTHER.equals(res.getResult())) {
                                        report.append("ERREURS NON BLOQUANTES DETECTEES").append(LINE_SEP);
                                    } else {
                                        report.append(res.getResult()).append(LINE_SEP);
                                    }
                                    if (!AutoCheckResult.OK.equals(res.getResult())) {
                                        report.append("    ++ Message: ").append(res.getMessage())
                                                .append(LINE_SEP);

                                        final List<String> filteredErrorFiles =
                                                res.getErrorFiles().stream()
                                                        .filter(f->StringUtils.contains(f, digitalId.concat(seqSeparator)))
                                                        .collect(Collectors.toList());

                                        if (filteredErrorFiles != null && expectedPages == filteredErrorFiles.size()) {
                                            report.append(" [Tous les fichiers masters]").append(LINE_SEP);
                                        } else {
                                            res.getErrorFiles().stream()
                                            .filter(f->StringUtils.contains(f, digitalId.concat(seqSeparator))) // filtre sur le radical
                                            .forEach(f -> report.append("  ").append(f).append(LINE_SEP));
                                        }
                                    }
                                }
                                report.append(LINE_SEP);
                    });

            });

        final LocalDateTime endCheck = LocalDateTime.now();
        report.append("[").append(endCheck.format(dtFormat))
                .append("] FIN CONTROLES AUTOMATIQUES - ")
                .append("Durée: ")
                .append(calculateDuration(startDeliv, endCheck));
        report.append(LINE_SEP).append(LINE_SEP);

        handleDeliveryReportFile(delivery, new ByteArrayInputStream(report.toString().getBytes(StandardCharsets.UTF_8)), libraryId);
    }

    /**
     * Ajout de lignes supplémentaires au rapport.
     *
     * @param delivery
     * @param startDeliv
     * @param dtHr
     * @param text
     */
    public void updateReport(final Delivery delivery, final Optional<LocalDateTime> start, 
                             final Optional<LocalDateTime> dtHr, final String text,
                             final String libraryId) {

        final Path root = Paths.get(deliveryReportingDirectory, libraryId, delivery.getIdentifier());

        final List<String> lines = new ArrayList<>();
        lines.add("");
        if (dtHr.isPresent()) {
            lines.add("[".concat(dtHr.get().format(dtFormat)).concat("] ")
                      .concat(text));
            start.ifPresent(deb->lines.add("Durée: ".concat(calculateDuration(deb, dtHr.get()))) );
        } else {
            lines.add(text);
        }
        fm.appendFile(lines, root.toFile(), REPORT_NAME);
    }


    /**
     * Retourne la duree au format 'hh:mm:ss'.
     *
     * @param start
     * @param end
     * @return
     */
    private String calculateDuration(final LocalDateTime start, final LocalDateTime end) {

        final long ms = Duration.between(start, end).toMillis();
        return String.format("%02dh%02dm%02ds",
                      TimeUnit.MILLISECONDS.toHours(ms),
                      TimeUnit.MILLISECONDS.toMinutes(ms) -
                      TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                      TimeUnit.MILLISECONDS.toSeconds(ms) -
                      TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
    }

    /**
     * Assure le stockage physique du rapport de la livraison.
     *
     * @param delivery
     * @param report
     */
    private void handleDeliveryReportFile(final Delivery delivery, final InputStream reportInput, final String libraryId) {

        final Path root = Paths.get(deliveryReportingDirectory, libraryId);
        try (InputStream input = new BufferedInputStream(reportInput)) {
            fm.copyInputStreamToFileWithOtherDirs(input, root.toFile(), Arrays.asList(delivery.getIdentifier()), REPORT_NAME, true, false);
        } catch (final IOException e) {
            LOG.error("Erreur {} lors de la génération du rapport de livraison {}.", e.getLocalizedMessage(), delivery.getLabel());
        }
    }

    /**
     * Récupération du fichier de rapport de contrôle.
     *
     * @param deliveryId
     * @return
     */
    public File getDeliveryReportForDelivery(final String deliveryId) {
 
        final Path root = Paths.get(deliveryReportingDirectory, fm.getUserLibraryId(), deliveryId, REPORT_NAME);
        if(root != null) {
            return root.toFile();
        }
        return null;
    }
}
