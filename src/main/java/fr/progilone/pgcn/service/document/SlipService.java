package fr.progilone.pgcn.service.document;

import static com.opencsv.CSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static com.opencsv.CSVWriter.RFC4180_LINE_END;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVWriter;

import fr.progilone.pgcn.domain.check.AutomaticCheckResult;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.delivery.Delivery;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.Check.ErrorLabel;
import fr.progilone.pgcn.domain.document.CheckSlip;
import fr.progilone.pgcn.domain.document.CheckSlipConfiguration;
import fr.progilone.pgcn.domain.document.CheckSlipLine;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument.DigitalDocumentStatus;
import fr.progilone.pgcn.domain.document.GlobalCheck;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.CheckSlipRepository;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.DateUtils;

/**
 * Séparation de la gestion des bordereau
 * @author jbrunet
 * Créé le 8 févr. 2018
 */
@Service
public class SlipService {

    private static final Logger LOG = LoggerFactory.getLogger(SlipService.class);

    private final DeliveryService deliveryService;
    private final LibraryService libraryService;
    private final CheckSlipConfigurationService checkSlipConfigurationService;
    private final JasperReportsService jasperReportService;
    private final CheckSlipRepository checkSlipRepository;

    @Autowired
    public SlipService(final DeliveryService deliveryService,
                        final LibraryService libraryService,
                        final CheckSlipConfigurationService checkSlipConfigurationService,
                        final JasperReportsService jasperReportService,
                        final CheckSlipRepository checkSlipRepository) {
        this.deliveryService = deliveryService;
        this.libraryService = libraryService;
        this.checkSlipConfigurationService = checkSlipConfigurationService;
        this.jasperReportService = jasperReportService;
        this.checkSlipRepository = checkSlipRepository;
    }

    /**
     * Vrai si au moins 1 doc de la livraison n'est pas controlé.
     *
     * @param delivery
     * @return
     */
    private boolean isDeliveryChecksUncompleted(final Delivery delivery) {
        return delivery.getDocuments().stream()
                       .anyMatch(delivDoc -> DigitalDocumentStatus.REJECTED != delivDoc.getStatus()
                                && DigitalDocumentStatus.PRE_REJECTED != delivDoc.getStatus()
                                && DigitalDocumentStatus.VALIDATED != delivDoc.getStatus());
    }

    /**
     * Faux si si bordereau de controle a deja ete cree.
     *
     * @param delivery
     * @return
     */
    private boolean isCheckSlipAbsent(final Delivery delivery) {

        return delivery.getDocuments().stream()
                       .anyMatch(delivDoc -> delivDoc.getCheckSlip() == null);
    }

    /**
     * Edition du bordereau de controle CSV.
     */
    @Transactional
    public void writeSlip(final OutputStream out, final String deliveryId, final String encoding, final char separator) throws IOException {

        final Delivery delivery = deliveryService.findOneWithDep(deliveryId);
        if (delivery.getLot() != null && delivery.getLot().getProject() != null) {

            final Library library = delivery.getLot().getProject().getLibrary();
            // Recup config bordereau de controle
            final Optional<CheckSlipConfiguration> config =
                    checkSlipConfigurationService.getOneByLibrary(library.getIdentifier());
            // Si 1ere fois ou si livraison pas completement controlee, on construit le bordereau.
            if (isCheckSlipAbsent(delivery) || isDeliveryChecksUncompleted(delivery)) {
                createCheckSlip(deliveryId);
            }

            writeCsv(out, getSlipParams(delivery, config, false, null), encoding, separator, config);
        } else {
            LOG.warn("Aucune bibliothèque rattachée à cette livraison: le bordereau ne peut pas être généré");
            return;
        }
    }

    public void writeCsv(final OutputStream out, final Map<String, Object> params, final String encoding,
                          final char separator, final Optional<CheckSlipConfiguration> slipConfig) throws IOException {

        // Alimentation du CSV
        try (final Writer writer = new OutputStreamWriter(out, encoding);
             final CSVWriter csvWriter = new CSVWriter(writer, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, RFC4180_LINE_END)) {
            // Entête
            writeHeader(csvWriter, slipConfig);
            final List<Map<String, String>> lines = (List<Map<String, String>>)params.get("slipLines");
            lines.forEach(line -> {
                writeBody(csvWriter, line, slipConfig);
            });
        }
    }

    /**
     * Ecriture entete fichier CSV.
     *
     * @param csvWriter
     * @param slipConfig
     */
    private void writeHeader(final CSVWriter csvWriter, final Optional<CheckSlipConfiguration> slipConfig) {

        final List<String> listTypes = new ArrayList<>();
        if (!slipConfig.isPresent() || slipConfig.get().isPgcnId()) {
            listTypes.add("Cote");
        }
        if (!slipConfig.isPresent() || slipConfig.get().isTitle()) {
            listTypes.add("Titre");
        }
        if (!slipConfig.isPresent() || slipConfig.get().isState()) {
            listTypes.add("Etat");
        }
        if (!slipConfig.isPresent() || slipConfig.get().isErrs()) {
            listTypes.add("Erreurs");
        }
        if (!slipConfig.isPresent() || slipConfig.get().isNbPages()) {
            listTypes.add("Nombre de pages");
        }
        if (!slipConfig.isPresent() || slipConfig.get().isNbPagesToBill()) {
            listTypes.add("Pages à facturer");
        }

        String[] types = new String[listTypes.size()];
        types = listTypes.toArray(types);
        csvWriter.writeNext(types);
    }

    private void writeBody(final CSVWriter csvWriter,
                            final Map<String,String> slipLine,
                            final Optional<CheckSlipConfiguration> slipConfig) {
        final List<String> line = new ArrayList<>();
        if (!slipConfig.isPresent() || slipConfig.get().isPgcnId()) {
            line.add(slipLine.get("pgcnId"));
        }
        if (!slipConfig.isPresent() || slipConfig.get().isTitle()) {
            line.add(slipLine.get("title"));
        }
        if (!slipConfig.isPresent() || slipConfig.get().isState()) {
            line.add(slipLine.get("status"));
        }
        if (!slipConfig.isPresent() || slipConfig.get().isErrs()) {
            line.add(slipLine.get("errors"));
        }
        if (!slipConfig.isPresent() || slipConfig.get().isNbPages()) {
            line.add(slipLine.get("pageCount"));
        }
        if (!slipConfig.isPresent() || slipConfig.get().isNbPagesToBill()) {
            line.add(slipLine.get("pagesToBill"));
        }
        csvWriter.writeNext(line.toArray(new String[0]));
    }

    /**
     * Génération / persistence du bordereau de controle.
     *
     * @param deliveryId
     * @throws PgcnTechnicalException
     */
    @Transactional
    public void createCheckSlip(final String deliveryId) {

        LOG.debug("Construction du bordereau de controle");
        final Delivery delivery = deliveryService.findOneWithDep(deliveryId);
        final Library library;
        if (delivery.getLot() != null && delivery.getLot().getProject() != null) {
            library = delivery.getLot().getProject().getLibrary();
        } else {
            LOG.warn("Aucune bibliothèque rattachée à cette livraison: le bordereau ne peut pas être généré");
            return;
        }
        final Optional<CheckSlipConfiguration> config =
                checkSlipConfigurationService.getOneByLibrary(library.getIdentifier());
        // Construction
        final CheckSlip bordereau = createCheckSlipLines(delivery, config);
        // et on persiste..
        checkSlipRepository.save(bordereau);
    }

    /**
     * Construit et persiste les donnees du bordereau de controle.
     *
     * @param delivery
     * @param config
     */
    private CheckSlip createCheckSlipLines(final Delivery delivery, final Optional<CheckSlipConfiguration> config) {


        final Optional<DeliveredDocument> deliveredDoc = delivery.getDocuments().stream()
                .filter(delivDoc -> delivDoc.getCheckSlip()!= null
                                                && delivDoc.getCheckSlip().getIdentifier()!=null)
                .findAny();
        final CheckSlip bordereau;
        if (deliveredDoc.isPresent()) {
            bordereau = checkSlipRepository.findOneWithDep(deliveredDoc.get().getCheckSlip().getIdentifier());
            bordereau.deleteAllSlipLines();
            bordereau.deleteAllDocuments();
        } else {
            bordereau = new CheckSlip();
        }
        // Controle completement effectué ou bien ?
        bordereau.setUncompleted(isDeliveryChecksUncompleted(delivery));

        delivery.getDocuments().forEach(delivDoc -> {

            final DigitalDocument doc = delivDoc.getDigitalDocument();
            final CheckSlipLine slipLine = new CheckSlipLine();
            delivDoc.setCheckSlip(bordereau);
            bordereau.addDocument(delivDoc);
            slipLine.setCheckSlip(bordereau);

            if (!config.isPresent() || config.get().isPgcnId()) {
                slipLine.setPgcnId(doc.getDocUnit().getPgcnId());
            }
            if (!config.isPresent() || config.get().isTitle()) {
                final Set<BibliographicRecord> records = doc.getDocUnit().getRecords();
                if (records.isEmpty()) {
                    slipLine.setTitle(doc.getDocUnit().getLabel());
                } else {
                    slipLine.setTitle(StringUtils.abbreviate(records.iterator().next().getTitle(), 255));
                }
            }
            if (!config.isPresent() || config.get().isState()) {
                slipLine.setStatus(getStatusForReport(delivDoc.getStatus()));
            }
            if (!config.isPresent() || config.get().isErrs()) {
                // on evite ainsi de recuperer les erreurs d'un précédent controle.
                slipLine.setDocErrors(null);
                if (DigitalDocumentStatus.REJECTED == doc.getStatus()
                        || DigitalDocumentStatus.PRE_REJECTED == doc.getStatus()) {
                    slipLine.setDocErrors( writeErrors(doc));
                }
            }
            if (!config.isPresent() || config.get().isNbPages()) {
                slipLine.setNbPages(delivDoc.getNbPages());
            }
            if (!config.isPresent() || config.get().isNbPagesToBill()) {
                if (DigitalDocumentStatus.REJECTED == doc.getStatus()
                        || DigitalDocumentStatus.PRE_REJECTED == doc.getStatus()) {
                        slipLine.setNbPagesToBill(0);
                } else if (DigitalDocumentStatus.VALIDATED == doc.getStatus()) {
                        slipLine.setNbPagesToBill(delivDoc.getNbPages());
                }
            }

            bordereau.addSlipLine(slipLine);
        });
        bordereau.setLotLabel(delivery.getLot().getLabel());
        bordereau.setDepositDate(delivery.getDepositDate());
        return bordereau;
    }

    private String getStatusForReport(final DigitalDocumentStatus status) {
        String strStatus = null;
        switch (status) {
            case TO_CHECK:
                strStatus = "A contrôler";
                break;
            case PRE_REJECTED:
            case REJECTED:
                strStatus = "Rejeté";
                break;
            case VALIDATED:
                strStatus = "Validé";
                break;
            case CHECKING:
                strStatus = "En cours de contrôle";
                break;
            case CREATING:
                strStatus = "En cours de création";
                break;
            case DELIVERING:
                strStatus = "En cours de livraison";
                break;
            case WAITING_FOR_REPAIR:
                strStatus = "En attente de réfection";
                break;
        }
        return strStatus;
    }

    /**
     * Génération du bordereau de controle PDF via Jasper.
     *
     * @param out
     * @param deliveryId
     * @throws PgcnTechnicalException
     */
    @Transactional
    public void writePdfCheckSlip(final OutputStream out, final String deliveryId) throws PgcnTechnicalException {

        LOG.debug("Génération du bordereau de controle PDF");
        final Delivery delivery = deliveryService.findOneWithDep(deliveryId);
        final Library library;
        if (delivery.getLot() != null && delivery.getLot().getProject() != null) {
            library = delivery.getLot().getProject().getLibrary();
        } else {
            LOG.warn("La bibliothèque est null, le bordereau ne peut pas être généré");
            return;
        }
        final File logo = libraryService.getLibraryLogo(library);
        final Optional<CheckSlipConfiguration> config =
                checkSlipConfigurationService.getOneByLibrary(library.getIdentifier());

        // S1 1ere fois ou si livraison pas completement controlee, on remet à jour le bordereau.
        if (isCheckSlipAbsent(delivery) || isDeliveryChecksUncompleted(delivery)) {
            createCheckSlip(deliveryId);
        }
        final Map<String, Object> params = getSlipParams(delivery, config, true, logo);
        final List<Map<String, String>> lines = (List<Map<String, String>>)params.remove("slipLines");

        try {
          jasperReportService.exportReportToStream(JasperReportsService.REPORT_CHECK_SLIP,
                                                  JasperReportsService.ExportType.PDF,
                                                  params,
                                                  lines,
                                                  out,
                                                  library.getIdentifier());
        } catch (final PgcnException e) {
            LOG.error("Erreur a la generation du bordereau de livraison: {}", e.getLocalizedMessage());
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * Renvoie une map contenant les données du bordereau controle.
     *
     * @param delivery
     * @param config
     * @param isPdf
     * @param logo
     * @return
     */
    private Map<String, Object> getSlipParams(final Delivery delivery,
                                              final Optional<CheckSlipConfiguration> config,
                                              final boolean isPdf, final File logo) {

        final Map<String, Object> params = new HashMap<>();
        final List<Map<String, String>> slipLines = new ArrayList<>();
        final Optional<DeliveredDocument> delivDoc = delivery.getDocuments()
                                                .stream()
                                                .filter(doc-> doc.getCheckSlip()!=null)
                                                .findAny();
        delivDoc.ifPresent(doc -> {
            final CheckSlip bordereau = checkSlipRepository.findOneWithDep(doc.getCheckSlip().getIdentifier());
            if (isPdf) {
                params.put("isUncompleted", bordereau.isUncompleted());
                params.put("lot", bordereau.getLotLabel());
                params.put("dtLivraison", DateUtils.formatDateToString(bordereau.getDepositDate(), "dd/MM/yyyy"));
                if (logo != null) {
                    params.put("logoPath", logo.getName());
                }
                // Entetes
                params.put("isPgcnIdPresent", config.isPresent()? config.get().isPgcnId() : true);
                params.put("isTitlePresent", config.isPresent()? config.get().isTitle() : true);
                params.put("isStatusPresent", config.isPresent()? config.get().isState() : true);
                params.put("isErrorsPresent", config.isPresent()? config.get().isErrs() : true);
                params.put("isPagesPresent", config.isPresent()? config.get().isNbPages() : true);
                params.put("isPagesToBillPresent", config.isPresent()? config.get().isNbPagesToBill() : true);
            }
            bordereau.getSlipLines()
                        .stream()
                        .forEach(sLine -> {

                            final Map<String, String> line = new HashMap<>();
                            if (!config.isPresent() || config.get().isPgcnId()) {
                                line.put("pgcnId", sLine.getPgcnId());
                            }
                            if (!config.isPresent() || config.get().isTitle()) {
                                line.put("title", sLine.getTitle());
                            }
                            if (!config.isPresent() || config.get().isState()) {
                                line.put("status", sLine.getStatus());
                            }
                            if (!config.isPresent() || config.get().isErrs()) {
                                line.put("errors", sLine.getDocErrors());
                            }
                            if (!config.isPresent() || config.get().isNbPages()) {
                                line.put("pageCount", String.valueOf(sLine.getNbPages()));
                            }
                            if (!config.isPresent() || config.get().isNbPagesToBill()) {
                                line.put("pagesToBill", String.valueOf(sLine.getNbPagesToBill()));
                            }
                            slipLines.add(line);
                        });
            params.put("slipLines", slipLines);
        });

        return params;
    }

    /**
     * Retourne les erreurs présentées proprement pour l'edition.
     *
     * @param doc
     * @return
     */
    public String writeErrors(final DigitalDocument doc) {

        final String CRLF = "\r\n";
        final StringBuilder summary = new StringBuilder();
        final Set<ErrorLabel> globalErrorLabels = new HashSet<>();

        // Document errors
        if (!doc.getChecks().isEmpty()) {
            summary.append("Document global : ");
            for (final GlobalCheck check : doc.getChecks()) {
                summary.append(check.getErrorLabel().getLabel()).append(CRLF);
                globalErrorLabels.add(check.getErrorLabel());
            }
        }
        if (StringUtils.isNotBlank(doc.getCheckNotes())) {
            summary.append(" Note de contrôle globale : ")
                    .append(doc.getCheckNotes()).append(CRLF);
        }
        for (final AutomaticCheckResult result : doc.getAutomaticCheckResults()) {
            if (AutomaticCheckResult.AutoCheckResult.KO == result.getResult()) {
                summary.append(result.getCheck().getLabel()).append(": KO").append(CRLF);
            }
        }
        // Page errors
        doc.getOrderedPages()
            .stream()
            .forEach(page -> {

                page.getChecks()
                    .stream()
                    .filter(check-> !globalErrorLabels.contains(check.getErrorLabel()))
                    .forEach(check -> {

                        summary.append("Page ").append(page.getNumber())
                            .append(": ")
                            .append(check.getErrorLabel().getLabel()).append(CRLF);
                    });

                if (StringUtils.isNotBlank(page.getCheckNotes())) {
                    summary.append("Note p.").append(page.getNumber())
                            .append(": ")
                            .append(page.getCheckNotes()).append(CRLF);
                }
        });
        return summary.toString();
    }

    @Transactional(readOnly = true)
    public void writeSlipForLot(final OutputStream outputStream, final String id, final String encoding, final char separator) throws IOException {
        final Delivery delivery = deliveryService.findByLot(id)
                                           .stream()
                                           .filter(d -> DeliveryStatus.SAVED != d.getStatus())
                                           .reduce((delivery1, delivery2) -> delivery1.getCreatedDate().isAfter(delivery2.getCreatedDate()) ?
                                                                             delivery1 :
                                                                             delivery2)
                                           .get();
        if (delivery != null) {
            writeSlip(outputStream, delivery.getIdentifier(), encoding, separator);
        }
    }

    @Transactional(readOnly = true)
    public void writeSlipForLotPDF(final OutputStream outputStream, final String id) throws PgcnTechnicalException {
        final Delivery delivery = deliveryService.findByLot(id)
                                           .stream()
                                           .filter(d -> DeliveryStatus.SAVED != d.getStatus())
                                           .reduce((delivery1, delivery2) -> delivery1.getCreatedDate().isAfter(delivery2.getCreatedDate()) ?
                                                                             delivery1 :
                                                                             delivery2)
                                           .get();
        if (delivery != null) {
            writePdfCheckSlip(outputStream, delivery.getIdentifier());
        }
    }


    /**
     * Renvoie les données de controle d'un document en particulier.
     *
     * @param deliveryId
     * @param documentId
     * @return
     */
    public Map<String, Object> getDocumentSummaryResults(final String deliveryId, final String documentId) {

        final String CRLF = "\r\n";
        final DeliveredDocument delivDoc = deliveryService.getOneWithSlip(deliveryId, documentId);
        final Map<String, Object> line = new HashMap<>();

        if (delivDoc != null && delivDoc.getCheckSlip()!=null) {

            delivDoc.getCheckSlip().getSlipLines().stream()
                            .filter(sLine -> StringUtils.equals(delivDoc.getDigitalDocument().getDocUnit().getPgcnId(), sLine.getPgcnId()))
                            .forEach(sLine -> {

                line.put("pgcnId", sLine.getPgcnId());
                line.put("title", sLine.getTitle());
                if (StringUtils.isBlank(sLine.getStatus())) {
                    line.put("status", getStatusForReport(delivDoc.getStatus()));
                } else {
                    line.put("status", sLine.getStatus());
                }
                String[] allErrors = {"Aucune erreur trouvée"};
                if (StringUtils.isNotBlank(sLine.getDocErrors())) {
                    allErrors = sLine.getDocErrors().split(CRLF);
                }
                line.put("errors", Arrays.asList(allErrors));
                line.put("pageCount", String.valueOf(sLine.getNbPages()));
            });
        }
        return line;
    }

}
