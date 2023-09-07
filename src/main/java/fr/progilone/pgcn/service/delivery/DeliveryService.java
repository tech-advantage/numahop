package fr.progilone.pgcn.service.delivery;

import static com.opencsv.CSVWriter.*;

import com.opencsv.CSVWriter;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.delivery.*;
import fr.progilone.pgcn.domain.delivery.Delivery.DeliveryStatus;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.dto.document.ValidatedDeliveredDocumentDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.multilotsdelivery.MultiLotsDelivery;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.repository.delivery.DeliveryRepository;
import fr.progilone.pgcn.repository.delivery.helper.DeliverySearchBuilder;
import fr.progilone.pgcn.repository.multilotsdelivery.MultiLotsDeliveryRepository;
import fr.progilone.pgcn.repository.sample.SampleRepository;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.document.mapper.DeliveredDocumentMapper;
import fr.progilone.pgcn.service.es.EsDeliveryService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.util.DateUtils;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service de gestion des livraisons : interaction repository
 *
 * @author jbrunet
 *         Créé le 16 mai 2017
 */
@Service
public class DeliveryService {

    private static final Logger LOG = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final EsDeliveryService esDeliveryService;
    private final LibraryService libraryService;
    private final DeliveryConfigurationService deliveryConfigurationService;
    private final JasperReportsService jasperReportService;
    private final MultiLotsDeliveryRepository multiLotsDeliveryRepository;
    private final SampleRepository sampleRepository;

    @Autowired
    public DeliveryService(final DeliveryRepository deliveryRepository,
                           final EsDeliveryService esDeliveryService,
                           final LibraryService libraryService,
                           final DeliveryConfigurationService deliveryConfigurationService,
                           final JasperReportsService jasperReportService,
                           final MultiLotsDeliveryRepository multiLotsDeliveryRepository,
                           final SampleRepository sampleRepository) {
        this.deliveryRepository = deliveryRepository;
        this.esDeliveryService = esDeliveryService;
        this.libraryService = libraryService;
        this.deliveryConfigurationService = deliveryConfigurationService;
        this.jasperReportService = jasperReportService;
        this.multiLotsDeliveryRepository = multiLotsDeliveryRepository;
        this.sampleRepository = sampleRepository;
    }

    /**
     * Lance une recherche paginée
     */
    @Transactional(readOnly = true)
    public Page<Delivery> search(final String search,
                                 final List<String> libraries,
                                 final List<String> projects,
                                 final List<String> lots,
                                 final List<String> providers,
                                 final List<DeliveryStatus> status,
                                 final LocalDate dateFrom,
                                 final LocalDate dateTo,
                                 final Integer page,
                                 final Integer size) {

        return deliveryRepository.search(new DeliverySearchBuilder().setSearch(search)
                                                                    .setLibraries(libraries)
                                                                    .setLots(lots)
                                                                    .setProjects(projects)
                                                                    .setProviders(providers)
                                                                    .setStatus(status)
                                                                    .setDateFrom(dateFrom)
                                                                    .setDateTo(dateTo), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Delivery getOne(final String id) {
        final Delivery one = deliveryRepository.findById(id).orElse(null);
        Hibernate.initialize(one);
        return one;
    }

    @Transactional(readOnly = true)
    public Delivery findOneByLabel(final String label) {
        return deliveryRepository.findOneByLabel(label);
    }

    @Transactional(readOnly = true)
    public Delivery getSimpleWithLot(final String id) {
        return deliveryRepository.getSimpleWithLot(id);
    }

    @Transactional(readOnly = true)
    public Delivery findByIdentifier(final String identifier) {
        return deliveryRepository.findByIdentifier(identifier);
    }

    @Transactional(readOnly = true)
    public Delivery findOneWithDocuments(final String identifier) {
        return deliveryRepository.findOneWithDocuments(identifier);
    }

    @Transactional(readOnly = true)
    public List<Delivery> findByLot(final String lotId) {
        return deliveryRepository.findByLotIdentifier(lotId);
    }

    @Transactional(readOnly = true)
    public Delivery findByLotIdentifierAndMultiLotsDeliveryIdentifier(final String lotId, final String multiLotsId) {
        return deliveryRepository.findByLotIdentifierAndMultiLotsDeliveryIdentifier(lotId, multiLotsId);
    }

    @Transactional(readOnly = true)
    public Delivery findOneWithDep(final String id) {
        return deliveryRepository.findOneWithDependencies(id);
    }

    @Transactional(readOnly = true)
    public List<Delivery> findAll() {
        return deliveryRepository.findAll(Sort.by(Direction.DESC, "receptionDate"));
    }

    @Transactional(readOnly = true)
    public List<Delivery> findAll(final Iterable<String> identifiers) {
        return deliveryRepository.findAllById(identifiers);
    }

    @Transactional(readOnly = true)
    public List<Delivery> findByProjectsAndLots(final List<String> projectIds, final List<String> lotIds) {
        return deliveryRepository.findByProjectsAndLots(projectIds, lotIds);
    }

    @Transactional(readOnly = true)
    public Set<DeliveredDocument> getDigitalDocumentsByDelivery(final String id) {

        final Set<DeliveredDocument> deliveredDocs = deliveryRepository.findDeliveredDocumentsByDeliveryIdentifier(id);
        deliveredDocs.forEach(dd -> {
            final DigitalDocument dig = dd.getDigitalDocument();
            Hibernate.initialize(dig.getDocUnit());
            Hibernate.initialize(dig.getAutomaticCheckResults());
            Hibernate.initialize(dig.getPages());
        });
        return deliveredDocs;
    }

    @Transactional(readOnly = true)
    public Set<DeliveredDocument> getSimpleDigitalDocumentsByDelivery(final String id) {
        return deliveryRepository.findSimpleDeliveredDocumentsByDeliveryIdentifier(id);
    }

    @Transactional
    public Delivery save(final Delivery delivery) throws PgcnValidationException, PgcnBusinessException {
        if (delivery.getIdentifier() == null) {
            delivery.setStatus(DeliveryStatus.SAVED);
            setDefaultValues(delivery);
        }
        if (delivery.getMultiLotsDelivery() != null) {
            final MultiLotsDelivery multiLotsDelivery = delivery.getMultiLotsDelivery();
            multiLotsDelivery.setStatus(delivery.getStatus());
            multiLotsDeliveryRepository.save(multiLotsDelivery);
        }
        final Delivery savedDelivery = deliveryRepository.save(delivery);
        return getOne(savedDelivery.getIdentifier());
    }

    @Transactional
    public void delete(final String identifier) {
        if (sampleRepository.findByDeliveryIdentifier(identifier) != null) {
            sampleRepository.deleteById(sampleRepository.findByDeliveryIdentifier(identifier).getIdentifier());
        }
        deliveryRepository.deleteById(identifier);
        esDeliveryService.deleteAsync(identifier);
    }

    @Transactional(readOnly = true)
    public Set<ValidatedDeliveredDocumentDTO> getValidatedDeliveredDocs(final LocalDate dateFrom) {
        final Set<DeliveredDocument> delivDocs = deliveryRepository.getValidatedDeliveredDocs(dateFrom);
        return DeliveredDocumentMapper.INSTANCE.validatedToDTOs(delivDocs);
    }

    @Transactional(readOnly = true)
    public void deleteDeliveredDocument(final String deliveryId, final String docId) {
        deliveryRepository.deleteDeliveredDocument(deliveryId, docId);
    }

    @Transactional(readOnly = true)
    public DeliveredDocument getOneWithSlip(final String deliveryId, final String documentId) {
        return deliveryRepository.getOneWithSlip(deliveryId, documentId);
    }

    @Transactional(readOnly = true)
    public List<DeliveredDocument> findDeliveredWithCheckSlipsByDigitalDoc(final String digitalDocId) {
        return deliveryRepository.findDeliveredDocumentsByDigitalDocIdentifier(digitalDocId);
    }

    @Transactional(readOnly = true)
    public List<Delivery> findDeliveriesForWidget(final LocalDate fromDate,
                                                  final List<String> libraries,
                                                  final List<String> projects,
                                                  final List<String> lots,
                                                  final List<Delivery.DeliveryStatus> status,
                                                  final boolean sampled) {

        return deliveryRepository.findDeliveriesForWidget(fromDate, libraries, projects, lots, status, sampled);
    }

    /**
     * Edition du bordereau de livraison CSV.
     */
    @Transactional(readOnly = true)
    public void writeSlip(final OutputStream out, final String deliveryId, final String encoding, final char separator) throws IOException {

        final Delivery delivery = findOneWithDep(deliveryId);
        final DeliverySlip deliverySlip = findOneWithDep(deliveryId).getDeliverySlip();
        final Optional<DeliverySlipConfiguration> configuration = deliveryConfigurationService.getOneByLibrary(delivery.getLot().getProject().getLibrary().getIdentifier());
        if (deliverySlip == null) {
            return;
        }
        final List<CSVColumn> columns;
        if (configuration.isPresent()) {
            columns = CSVColumn.getColumnsFromConfiguration(configuration.get());
        } else {
            columns = Arrays.asList(CSVColumn.values());
        }
        Collections.sort(columns);
        writeCSV(out, columns, deliverySlip, encoding, separator);
    }

    /**
     * Ecriture du CSV
     */
    public void writeCSV(final OutputStream out, final List<CSVColumn> columns, final DeliverySlip deliverySlip, final String encoding, final char separator) throws IOException {

        // Alimentation du CSV
        try (final Writer writer = new OutputStreamWriter(out, encoding);
             final CSVWriter csvWriter = new CSVWriter(writer, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, RFC4180_LINE_END)) {
            // Entête
            writeHeader(csvWriter, columns);
            for (final DeliverySlipLine line : deliverySlip.getSlipLines()) {
                // Corps
                writeBody(csvWriter, columns, line);
            }
        }
    }

    /**
     * Écriture de l'entête du fichier CSV
     */
    private void writeHeader(final CSVWriter csvWriter, final List<CSVColumn> columns) {
        final String[] types = columns.stream().map(field -> field.label).toArray(String[]::new);
        csvWriter.writeNext(types);
    }

    /**
     * Écriture d'une ligne du bordereau
     */
    private void writeBody(final CSVWriter csvWriter, final List<CSVColumn> columns, final DeliverySlipLine slipLine) {

        final List<String> line = new ArrayList<>();
        if (columns.contains(CSVColumn.PGCN_ID)) {
            line.add(slipLine.getPgcnId());
        }
        if (columns.contains(CSVColumn.LOT)) {
            line.add(slipLine.getLot());
        }
        if (columns.contains(CSVColumn.TRAIN)) {
            line.add(slipLine.getTrain());
        }
        if (columns.contains(CSVColumn.RADICAL)) {
            line.add(slipLine.getRadical());
        }
        if (columns.contains(CSVColumn.TITLE)) {
            line.add(slipLine.getTitle());
        }
        if (columns.contains(CSVColumn.NB_PAGES)) {
            line.add(slipLine.getNbPages());
        }
        if (columns.contains(CSVColumn.DATE)) {
            line.add(slipLine.getDate());
        }

        csvWriter.writeNext(line.toArray(new String[0]));
    }

    /**
     * Génération du bordereau de livraison PDF via Jasper.
     */
    @Transactional(readOnly = true)
    public void writeDeliveryPdfSlip(final OutputStream out, final String deliveryId) throws PgcnTechnicalException {

        final Delivery delivery = findOneWithDep(deliveryId);
        final DeliverySlip deliverySlip = delivery.getDeliverySlip();
        if (deliverySlip == null) {
            return;
        }

        LOG.debug("Génération du bordereau de livraison PDF");

        final Library library = delivery.getLot().getProject().getLibrary();
        final Optional<DeliverySlipConfiguration> config = deliveryConfigurationService.getOneByLibrary(library.getIdentifier());

        final Map<String, Object> params = getSlipParams(deliverySlip, config);
        final File logo = libraryService.getLibraryLogo(library);

        final Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("dtLivraison", params.get("date"));
        paramsMap.put("totalPages", params.get("totalPages"));
        if (logo != null) {
            paramsMap.put("logoPath", logo.getName());
        }
        paramsMap.put("isPgcnIdPresent",
                      config.isPresent() ? config.get().getPgcnId()
                                         : true);
        paramsMap.put("isLotPresent",
                      config.isPresent() ? config.get().getLot()
                                         : true);
        paramsMap.put("isTrainPresent",
                      config.isPresent() ? config.get().getTrain()
                                         : true);
        paramsMap.put("isRadicalPresent",
                      config.isPresent() ? config.get().getRadical()
                                         : true);
        paramsMap.put("isTitlePresent",
                      config.isPresent() ? config.get().getTitle()
                                         : true);
        paramsMap.put("isPagesPresent",
                      config.isPresent() ? config.get().getNbPages()
                                         : true);
        paramsMap.put("isDatePresent",
                      config.isPresent() ? config.get().getDate()
                                         : true);
        final List<DeliverySlipLine> lines = (List<DeliverySlipLine>) params.get("slipLines");

        try {
            jasperReportService.exportReportToStream(JasperReportsService.REPORT_DELIV_SLIP, JasperReportsService.ExportType.PDF, paramsMap, lines, out, library.getIdentifier());
        } catch (final PgcnException e) {
            LOG.error("Erreur a la generation du bordereau de livraison: {}", e.getLocalizedMessage());
            throw new PgcnTechnicalException(e);
        }
    }

    /**
     * @param deliverySlip
     * @param config
     * @return
     */
    private Map<String, Object> getSlipParams(final DeliverySlip deliverySlip, final Optional<DeliverySlipConfiguration> config) {

        final List<Map<String, String>> slipLines = new ArrayList<>();
        final Map<String, Object> params = new HashMap<>();
        long totalPages = 0;

        for (final DeliverySlipLine slipLine : deliverySlip.getSlipLines()) {
            final Map<String, String> line = new HashMap<>();
            if (config.isPresent()) {
                if (config.get().getPgcnId()) {
                    line.put("pgcnId", slipLine.getPgcnId());
                }
                if (config.get().getLot()) {
                    line.put("lot", slipLine.getLot());
                }
                if (config.get().getTrain()) {
                    line.put("train", slipLine.getTrain());
                }
                if (config.get().getRadical()) {
                    line.put("radical", slipLine.getRadical());
                }
                if (config.get().getTitle()) {
                    line.put("title", slipLine.getTitle());
                }
                if (config.get().getNbPages()) {
                    line.put("pages", slipLine.getNbPages());
                }
                if (config.get().getDate()) {
                    line.put("date", slipLine.getDate());
                }

            } else {
                line.put("pgcnId", slipLine.getPgcnId());
                line.put("lot", slipLine.getLot());
                line.put("train", slipLine.getTrain());
                line.put("radical", slipLine.getRadical());
                line.put("title", slipLine.getTitle());
                line.put("pages", slipLine.getNbPages());
                line.put("date", slipLine.getDate());
            }

            totalPages += Integer.parseInt(slipLine.getNbPages());
            slipLines.add(line);
        }

        params.put("totalPages", "" + totalPages);
        params.put("slipLines", slipLines);

        final Delivery delivery = deliverySlip.getDelivery();
        if (delivery != null) {
            params.put("date", DateUtils.formatDateToString(delivery.getDepositDate(), "dd/MM/yyyy"));
        }
        return params;
    }

    /**
     * Remplissage des valeurs par défaut
     *
     * @param delivery
     */
    private void setDefaultValues(final Delivery delivery) {
        delivery.setDocumentCount(2);
        delivery.setFileFormatOK(true);
        delivery.setSequentialNumbers(true);
        delivery.setNumberOfFilesOK(true);
        delivery.setNumberOfFilesMatching(true);
        delivery.setMireOK(true);
        delivery.setMirePresent(true);
        delivery.setTableOfContentsOK(true);
        delivery.setTableOfContentsPresent(true);
        delivery.setAltoPresent(true);
        delivery.setCompressionRateOK(true);
        delivery.setCompressionTypeOK(true);
        delivery.setResolutionOK(true);
        delivery.setFileDefinitionOK(true);
        delivery.setColorspaceOK(true);
        delivery.setFileIntegrityOk(true);
        delivery.setFileBibPrefixOK(true);
        delivery.setFileCaseOK(true);
        delivery.setFileRadicalOK(true);
        delivery.setFileImageMetadataOK(true);
    }

    public List<Delivery> findAllByProjectId(final String projectId) {
        return deliveryRepository.findAllByLotProjectIdentifier(projectId);
    }

    /**
     * Renvoie une livraison avec copie des proprietes à dupliquer.
     */
    @Transactional
    public Delivery duplicate(final String id) {
        final Delivery original = getOne(id);
        final Delivery duplicated = new Delivery();
        BeanUtils.copyProperties(original, duplicated, "identifier", "automaticCheckResults", "documents", "version");
        duplicated.setStatus(DeliveryStatus.SAVED);
        duplicated.setLabel(duplicated.getLabel().concat(" [copie]"));
        setDefaultValues(duplicated);
        return duplicated;
    }

    /**
     * Récupération de la {@link CheckConfiguration} active de la delivery.
     */
    @Transactional(readOnly = true)
    public Delivery getWithActiveCheckConfiguration(final String deliveryId) {
        return deliveryRepository.getWithActiveCheckConfiguration(deliveryId);
    }

    /**
     * Recherche la dernière livraison d'une unité documentaire
     */
    @Transactional(readOnly = true)
    public Delivery findLatestDelivery(final String docUnitId) {
        final List<Delivery> deliveries = deliveryRepository.findAllByDocUnitIdentifier(docUnitId);
        return deliveries.stream()
                         .max(Comparator.comparing(del -> del.getDocuments()
                                                             .stream()
                                                             .filter(doc -> StringUtils.equals(doc.getDigitalDocument().getDocUnit().getIdentifier(), docUnitId))
                                                             .map(DeliveredDocument::getDeliveryDate)
                                                             .filter(Objects::nonNull)
                                                             .max(Comparator.naturalOrder())
                                                             .orElse(LocalDate.MIN)))
                         .orElse(null);
    }

    /**
     * Recherche les lots par bibliothèque, groupés par statut
     *
     * @return liste de map avec 2 clés: statut et décompte
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDeliveryGroupByStatus(final List<String> libraries, final List<String> projects, final List<String> lots) {
        final List<Object[]> results = deliveryRepository.getDeliveryGroupByStatus(libraries, projects, lots);    // status, count

        return results.stream().map(res -> {
            final Map<String, Object> resMap = new HashMap<>();
            resMap.put("status", res[0]);
            resMap.put("count", res[1]);
            return resMap;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Delivery> findByProviders(final List<String> libraries,
                                          final List<String> providers,
                                          final List<DeliveryStatus> statuses,
                                          final LocalDate fromDate,
                                          final LocalDate toDate) {
        return deliveryRepository.findByProviders(libraries, providers, statuses, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public void writeSlipLot(final OutputStream outputStream, final String id, final String encoding, final char separator) throws IOException {
        final Optional<Delivery> deliveryOpt = findByLot(id).stream()
                                                            .reduce((delivery1, delivery2) -> delivery1.getCreatedDate().isAfter(delivery2.getCreatedDate()) ? delivery1
                                                                                                                                                             : delivery2);
        if (deliveryOpt.isPresent()) {
            writeSlip(outputStream, deliveryOpt.get().getIdentifier(), encoding, separator);
        }
    }

    @Transactional(readOnly = true)
    public void writeSlipLotPDF(final OutputStream outputStream, final String id) throws PgcnTechnicalException {
        final Optional<Delivery> deliveryOpt = findByLot(id).stream()
                                                            .reduce((delivery1, delivery2) -> delivery1.getCreatedDate().isAfter(delivery2.getCreatedDate()) ? delivery1
                                                                                                                                                             : delivery2);
        if (deliveryOpt.isPresent()) {
            writeDeliveryPdfSlip(outputStream, deliveryOpt.get().getIdentifier());
        }
    }

    private enum CSVColumn {

        PGCN_ID("Cote"),
        LOT("Lot"),
        TRAIN("Train"),
        RADICAL("Radical des fichiers"),
        TITLE("Titre"),
        NB_PAGES("Nombre de pages"),
        DATE("Date");

        private final String label;

        CSVColumn(final String label) {
            this.label = label;
        }

        private static List<CSVColumn> getColumnsFromConfiguration(final DeliverySlipConfiguration configuration) {
            final List<CSVColumn> columns = new ArrayList<>();
            if (configuration.getPgcnId()) {
                columns.add(PGCN_ID);
            }
            if (configuration.getLot()) {
                columns.add(LOT);
            }
            if (configuration.getTrain()) {
                columns.add(TRAIN);
            }
            if (configuration.getRadical()) {
                columns.add(RADICAL);
            }
            if (configuration.getTitle()) {
                columns.add(TITLE);
            }
            if (configuration.getNbPages()) {
                columns.add(NB_PAGES);
            }
            if (configuration.getDate()) {
                columns.add(DATE);
            }
            return columns;
        }
    }
}
