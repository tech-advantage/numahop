package fr.progilone.pgcn.service.document.conditionreport;

import static com.opencsv.CSVWriter.DEFAULT_ESCAPE_CHARACTER;
import static com.opencsv.CSVWriter.DEFAULT_QUOTE_CHARACTER;
import static com.opencsv.CSVWriter.RFC4180_LINE_END;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVWriter;

import fr.opensagres.xdocreport.document.images.ClassPathImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.DocUnit_;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportSlipConfiguration;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.dto.document.DocPropertyDTO;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import fr.progilone.pgcn.domain.dto.document.DocUnitBibliographicRecordDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleListDocUnitDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDetailDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportDetailVelocityDTO;
import fr.progilone.pgcn.domain.dto.document.conditionreport.ConditionReportValueVelocityDTO;
import fr.progilone.pgcn.domain.exchange.template.Name;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepository;
import fr.progilone.pgcn.repository.document.conditionreport.ConditionReportRepositoryCustom;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.JasperReportsService;
import fr.progilone.pgcn.service.document.mapper.BibliographicRecordMapper;
import fr.progilone.pgcn.service.document.mapper.ConditionReportAttachmentMapper;
import fr.progilone.pgcn.service.document.mapper.ConditionReportDetailMapper;
import fr.progilone.pgcn.service.document.mapper.ConditionReportMapper;
import fr.progilone.pgcn.service.document.mapper.SimpleDocUnitMapper;
import fr.progilone.pgcn.service.es.EsConditionReportService;
import fr.progilone.pgcn.service.exchange.template.OdtEngineService;
import fr.progilone.pgcn.service.exchange.template.OdtEngineService.TypedFileImageProvider;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.util.ImageUtils;
import fr.progilone.pgcn.service.util.SortUtils;

@Service
public class ConditionReportService {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportService.class);
    private static final String DEFAULT_THUMBNAIL = "images/empty.png";

    private final ConditionReportRepository conditionReportRepository;
    private final ConditionReportDetailService conditionReportDetailService;
    private final ConditionReportAttachmentService conditionReportAttachmentService;
    private final DocUnitRepository docUnitRepository;
    private final EsConditionReportService esConditionReportService;
    private final LibraryService libraryService;
    private final OdtEngineService odtEngineService;
    private final UserService userService;
    private final JasperReportsService jasperReportService;
    private final ConditionReportSlipConfigurationService conditionReportSlipConfigurationService;
    

    private static final ConditionReportMapper REPORT_MAPPER = ConditionReportMapper.INSTANCE;
    private static final ConditionReportDetailMapper DETAIL_MAPPER = ConditionReportDetailMapper.INSTANCE;

    /**
     * Type de document généré
     */
    public enum ConvertType {
        ODT,
        PDF
    }

    @Autowired
    public ConditionReportService(final ConditionReportRepository conditionReportRepository,
                                  final ConditionReportDetailService conditionReportDetailService,
                                  final ConditionReportAttachmentService conditionReportAttachmentService,
                                  final DocUnitRepository docUnitRepository,
                                  final EsConditionReportService esConditionReportService,
                                  final LibraryService libraryService,
                                  final OdtEngineService odtEngineService,
                                  final UserService userService,
                                  final JasperReportsService jasperReportService,
                                  final ConditionReportSlipConfigurationService conditionReportSlipConfigurationService) {
        this.conditionReportRepository = conditionReportRepository;
        this.conditionReportAttachmentService = conditionReportAttachmentService;
        this.docUnitRepository = docUnitRepository;
        this.conditionReportDetailService = conditionReportDetailService;
        this.esConditionReportService = esConditionReportService;
        this.libraryService = libraryService;
        this.odtEngineService = odtEngineService;
        this.userService = userService;
        this.jasperReportService = jasperReportService;
        this.conditionReportSlipConfigurationService = conditionReportSlipConfigurationService;
    }

    /**
     * Création d'un constat d'état, et du 1er constat avant départ de la bibliothèque
     *
     * @param docUnitId
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public ConditionReport create(final String docUnitId) throws PgcnValidationException {
        // Recherche de l'unité documentaire
        final DocUnit docUnit = docUnitRepository.findOne(docUnitId);
        if (docUnit == null) {
            LOG.error("L'unité documentaire {} n'existe pas", docUnitId);
            return null;
        }
        // Recherche d'un constat d'état existant
        final ConditionReport existingReport = conditionReportRepository.findByDocUnit(docUnitId);
        if (existingReport != null) {
            final PgcnList<PgcnError> errors = new PgcnList<>();
            final PgcnError error = new PgcnError.Builder().setCode(PgcnErrorCode.CONDREPORT_DUPLICATE).build();
            errors.add(error);
            throw new PgcnValidationException(existingReport, errors);
        }

        // Création du constat d'état
        final ConditionReport conditionReport = getNewConditionReport(docUnit);
        final ConditionReport savedReport = conditionReportRepository.save(conditionReport);

        // Création de la 1e étape du constat d'état
        conditionReportDetailService.create(ConditionReportDetail.Type.LIBRARY_LEAVING, savedReport);
        return findByIdentifier(savedReport.getIdentifier());
    }

    /**
     * Initialisation d'un nouveau constat d'état
     *
     * @param docUnit
     * @return
     */
    public ConditionReport getNewConditionReport(final DocUnit docUnit) {
        final ConditionReport conditionReport = new ConditionReport();
        conditionReport.setDocUnit(docUnit);

        // Prestataire de numérisation
        final User provider;
        if (docUnit.getLot() != null && docUnit.getLot().getProvider() != null) {
            provider = docUnit.getLot().getProvider();
        } else if (docUnit.getProject() != null && docUnit.getProject().getProvider() != null) {
            provider = docUnit.getProject().getProvider();
        } else {
            provider = null;
        }
                                                                                                                                 
        if (provider != null) {
            conditionReport.setProviderEmail(provider.getEmail());
            conditionReport.setProviderName(provider.getFullName());
            conditionReport.setProviderPhone(provider.getPhoneNumber());
        }
        return conditionReport;
    }
    
    /**
     * Propagation du rapport aux relations filles.
     * 
     * @param docUnitId
     * @param typeDetail
     */
    @Transactional
    public Map<String, String> propagateReport(final String docUnitId, final String detailId) {
        
        final Map<String, String> result = new HashMap<>();
        final List<DocUnit> childs =  docUnitRepository.findByParentIdentifier(docUnitId);
        // pas d'enfant, on quitte sans rien faire...
        if (CollectionUtils.isEmpty(childs)) {
            return result;
        }
              
        final ConditionReport parentReport = conditionReportRepository.findByDocUnit(docUnitId);       
        final Optional<ConditionReportDetail> detail = parentReport.getDetails().stream()
                                                            .filter(det -> det.getIdentifier().equals(detailId))
                                                                .findFirst();
        if (detail.isPresent()) {            
            for (final DocUnit doc : childs) {
                createChildReport(doc, detail.get(), result); 
            }
        }
        return result;
    }

    
    /**
     * Creation du constat par propagation sur l'UD fille.
     * 
     * @param doc
     * @param detail
     * @param result
     */
    private void createChildReport(final DocUnit doc, final ConditionReportDetail detail, final Map<String, String> result) {
        
        // Recherche d'un constat d'état existant
        final ConditionReport existingReport = conditionReportRepository.findByDocUnit(doc.getIdentifier());
        final ConditionReportDetail childDetail;
        
        if (existingReport == null) {
            // il faut creer constat + detail
            final ConditionReport childReport = new ConditionReport();
            childReport.setDocUnit(doc);
            //childReport.setDocUnitId(doc.getIdentifier());
            BeanUtils.copyProperties(detail.getReport(), childReport, "identifier", "version", "docUnit", "attachments", "details", "docUnitId", "docUnitLabel", "docUnitPgcnId", "docUnitCondReportType");
            
            final ConditionReport savedReport = conditionReportRepository.save(childReport);
            
            childDetail = conditionReportDetailService.getNewDetailWithoutWriters(detail.getType(), savedReport, 0);
            savedReport.addDetail(childDetail);
            BeanUtils.copyProperties(detail, childDetail, "identifier", "version", "position", "report", "sortedType");
            
        } else {
            if (Type.LIBRARY_LEAVING == detail.getType()) {
                // constat initial deja present
                result.put(existingReport.getIdentifier(), "Attention: le constat d'état initial existait déjà");
                return;
            } else {
                childDetail = conditionReportDetailService.getNewDetail(detail.getType(), existingReport, existingReport.getDetails().size()-1);

                childDetail.setReport(existingReport);
                existingReport.addDetail(childDetail);
                BeanUtils.copyProperties(detail, childDetail, "identifier", "version", "position", "report", "sortedType");
            }
        }
        
        final ConditionReportDetail saved = conditionReportDetailService.save(childDetail, false);
        LOG.debug("Detail persiste de type {} - identifier : {}", saved.getType(), saved.getIdentifier());

        String typConstat = "";
        switch (saved.getType()) {
            case LIBRARY_LEAVING:
                typConstat = "initial";
                break;
            case PROVIDER_RECEPTION:    
            case DIGITALIZATION:
                typConstat = "avant numérisation";
                break;
            case LIBRARY_BACK:
                typConstat = "après numérisation";
                break;
            default:
                break;
        }
        final String docLabel = saved.getReport().getDocUnitLabel();
        result.put(saved.getReport().getIdentifier(), "DocUnit " + docLabel +" - Constat d'état " + typConstat + " ajouté");
    }
    

    /**
     * Chargement des constats d'état de l'unité documentaire
     *
     * @param docUnitId
     * @return
     */
    @Transactional(readOnly = true)
    public ConditionReport findByDocUnit(final String docUnitId) {
        return conditionReportRepository.findByDocUnit(docUnitId);
    }

    /**
     * Vérifie que le type de constat d'état existe déjà
     *
     * @param docUnitId
     * @param type
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isConditionReportDetailPresentInDocUnit(final String docUnitId, final Type type) {
        final ConditionReport report = findByDocUnit(docUnitId);
        if (report != null && type != null) {
            if (report.getDetails().stream().anyMatch(detail -> type.equals(detail.getType()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recherche d'un constat d'état à partir de son identifiant
     * Récupération des objets liés
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public ConditionReport findByIdentifier(final String identifier) {
        return conditionReportRepository.findByIdentifier(identifier);
    }

    /**
     * Recherche d'un constat d'état à partir de son identifiant
     *
     * @param identifier
     * @return
     */
    @Transactional(readOnly = true)
    public DocUnit findDocUnitByIdentifier(final String identifier) {
        return conditionReportRepository.findDocUnitByIdentifier(identifier);
    }

    /**
     * Recherche de constats d'état à partir de leurs identifiants
     *
     * @param identifiers
     * @return
     */
    @Transactional(readOnly = true)
    public List<ConditionReport> findDocUnitByIdentifierIn(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        return conditionReportRepository.findByDocUnitIdentifierIn(identifiers);
    }

    /**
     * Suppression d'un constat d'état
     *
     * @param identifier
     */
    @Transactional
    public void delete(final String identifier) {
        final ConditionReport report = conditionReportRepository.findOne(identifier);
        delete(report);
    }

    /**
     * Suppression d'un constat d'état à partir de l'identifiant de son unité documentaire
     *
     * @param docUnitId
     */
    @Transactional
    public void deleteByDocUnitIdentifier(final String docUnitId) {
        final ConditionReport report = conditionReportRepository.findByDocUnitIdentifier(docUnitId);
        delete(report);
    }

    /**
     * Suppression d'un constat d'état
     *
     * @param report
     */
    private void delete(final ConditionReport report) {
        if (report != null) {
            // Suppession des pièces jointes
            final File attachmentDir = conditionReportAttachmentService.getAttachmentDir(report);
            FileUtils.deleteQuietly(attachmentDir);
            // Suppression du constat d'état
            conditionReportRepository.delete(report);
            // Désindexation
            esConditionReportService.deleteAsync(report);
        }
    }

    /**
     * Sauvegarde d'un constat d'état
     *
     * @param report
     * @return
     * @throws PgcnValidationException
     */
    @Transactional
    public ConditionReport save(final ConditionReport report) throws PgcnValidationException {
        final ConditionReport savedReport = conditionReportRepository.save(report);
        return findByIdentifier(savedReport.getIdentifier());
    }

    /**
     * Recherche paginée de constats d'états
     *
     * @param libraries
     *         Filtrage: sites
     * @param dimensions
     *         Filtrage: dimensions du document
     * @param from
     *         Filtrage: date min du constat d'état
     * @param to
     *         Filtrage: date max du constat d'état
     * @param descriptions
     *         Filtrage: descriptions
     * @param docIdentifiers
     * @param page
     *         Pagination: n° de page
     * @param size
     *         Pagination: taille de la page
     * @param sorts
     *         Critères de tri
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SearchResult> search(final List<String> libraries,
                                     final ConditionReportRepositoryCustom.DimensionFilter dimensions,
                                     final LocalDate from,
                                     final LocalDate to,
                                     final List<String> descriptions,
                                     final List<String> docIdentifiers,
                                     final boolean toValidateOnly,
                                     final Integer page,
                                     final Integer size,
                                     final List<String> sorts) {
        Sort sort = SortUtils.getSort(sorts);
        if (sort == null) {
            sort = new Sort(DocUnit_.pgcnId.getName());
        }
        final Pageable pageable = new PageRequest(page, size, sort);
        // Recherche de la page d'identifiants
        final Page<String> pageOfIds =
            conditionReportRepository.search(libraries, from, to, dimensions, parseDescriptions(descriptions), docIdentifiers, toValidateOnly, pageable);

        if (pageOfIds.getNumberOfElements() > 0) {
            final List<SearchResult> results = findSearchResultByIdentifierIn(pageOfIds.getContent(), toValidateOnly);
                return new PageImpl<>(results, pageable, pageOfIds.getTotalElements() );
        } else {
            return new PageImpl<>(Collections.emptyList(), pageable, pageOfIds.getTotalElements());
        }
    }

    /**
     * Retourne la liste de constats à afficher dans l'ecran 'Liste constats d'état'. 
     * 
     * @param reportIds
     * @param toValidateOnly
     * @return
     */
    private List<SearchResult> findSearchResultByIdentifierIn(final List<String> reportIds, final boolean toValidateOnly) {
        // Chargement des constats d'état
        final List<ConditionReport> reports = conditionReportRepository.findByIdentifierIn(reportIds);
        // Chargement des unités documentaires
        final List<String> docUnitIds = reports.stream().map(report -> report.getDocUnit().getIdentifier()).collect(Collectors.toList());

        // DTO docUnits
        final List<SimpleListDocUnitDTO> docDtos;
        docDtos = docUnitRepository.findByIdentifierInWithProj(docUnitIds)
                .stream()
                .map(SimpleDocUnitMapper.INSTANCE::docUnitToSimpleListDocUnitDTO)
                .collect(Collectors.toList());
        
        // Liste de résultats; on part de la liste des identifiants pour conserver le tri demandé
        return reportIds.stream()
                        // report correspondant à l'identifiant
                        .map(id -> reports.stream().filter(report -> StringUtils.equals(report.getIdentifier(), id)).findAny().orElse(null))
                        .filter(Objects::nonNull)
                        // résultat de recherche
                        .map(report -> {
                            // docunit correspondant au report
                            final SimpleListDocUnitDTO docDto = docDtos.stream()
                                                                       .filter(doc -> StringUtils.equals(doc.getIdentifier(),
                                                                                                         report.getDocUnit().getIdentifier()))
                                                                       .findAny()
                                                                       .orElse(null);
                            
                            final SearchResult res = new SearchResult();
                            res.setDocUnit(docDto);
                            res.setReport(REPORT_MAPPER.reportToDTO(report));
                            report.getDetails()
                                  .stream()
                                  .max(Comparator.comparing(ConditionReportDetail::getPosition))
                                  .map(DETAIL_MAPPER::detailToDTO)
                                  .ifPresent(res::setDetail);
                            return res;
                        })
                        .filter(res -> res.getDocUnit() != null)
                        .collect(Collectors.toList());
    }

    /**
     * Regroupement des valeurs de filtrage en provenance du client par propriété (état de la reliure)
     *
     * @param bindings
     * @return
     */
    private Map<String, List<String>> parseDescriptions(final List<String> bindings) {
        if (CollectionUtils.isEmpty(bindings)) {
            return Collections.emptyMap();
        }
        return bindings.stream()
                       // split property=value
                       .map(desc -> StringUtils.split(desc, "=", 2))
                       // le binding est correctement parsé
                       .filter(arr -> arr.length == 2)
                       // regroupement des valeurs par propriété
                       .collect(Collectors.groupingBy(arr -> arr[0], Collectors.mapping(arr -> arr[1], Collectors.toList())));
    }

    /**
     * Génération du document récapitulatif à partir du template
     *
     * @param identifier
     *         Identifiant du constat d'état
     * @param out
     *         Flux dans lequel sera écrit le rapport généré
     * @param convertType
     * @throws PgcnTechnicalException
     */
    @Transactional(readOnly = true)
    public void exportDocument(final String identifier, final OutputStream out, final ConvertType convertType) throws PgcnTechnicalException {
        final ConditionReport report = conditionReportRepository.findByIdentifier(identifier);
        final Map<String, Object> params = getExportParams(report);
        final Map<String, IImageProvider> imageParams = getImageParams(report);
        switch (convertType) {
            case ODT:
                odtEngineService.generateDocumentODT(Name.ConditionReport, report.getDocUnit().getLibrary(), params, imageParams, out);
                break;
            default:
            case PDF:
                odtEngineService.generateDocumentPDF(Name.ConditionReport, report.getDocUnit().getLibrary(), params, imageParams, out, null);
                break;
        }
    }

    /**
     * Génération du bordereau PDF
     */
    @Transactional(readOnly = true)
    public void writeSlipDocUnitsPDF(final OutputStream out, final Collection<DocUnit> docUnits, final String docTitle) throws
                                                                                                                        PgcnTechnicalException {
        final List<String> details = new ArrayList<>();

        for (final DocUnit docUnit : docUnits) {
            final ConditionReport report = findByDocUnit(docUnit.getIdentifier());
            conditionReportDetailService.getLatest(report).ifPresent(detail -> details.add(detail.getIdentifier()));
        }
        writeSlipPDF(out, details, docTitle);
    }

    public void writeSlipDocUnitsCSV(final ServletOutputStream outputStream, final Set<DocUnit> docUnits, final String encoding, final char separator) throws IOException {
        final List<String> details = new ArrayList<>();

        for (final DocUnit docUnit : docUnits) {
            final ConditionReport report = findByDocUnit(docUnit.getIdentifier());
            conditionReportDetailService.getLatest(report).ifPresent(detail -> details.add(detail.getIdentifier()));
        }
        writeSlip(outputStream, details, encoding, separator);
    }

    /**
     * Génération du bordereau d'envoi du train PDF.
     */
    @Transactional(readOnly = true)
    public void writeSlipPDF(final OutputStream out, final List<String> reportIds, final String docTitle) throws PgcnTechnicalException {

        LOG.debug("Génération du bordereau d'envoi du train PDF");

        String libraryId = null;
        if (SecurityUtils.getCurrentUser() != null) {
            libraryId = SecurityUtils.getCurrentUser().getLibraryId();
        }
        if (StringUtils.isBlank(libraryId)) {
            libraryId = "SUPERLIB";
        }
        final Library library = libraryService.findByIdentifier(libraryId);

        final Optional<ConditionReportSlipConfiguration> config = conditionReportSlipConfigurationService.getOneByLibrary(library.getIdentifier());

        final File logo = libraryService.getLibraryLogo(library);
        final List<ConditionReportDetail> reports = conditionReportDetailService.findByIdentifierIn(reportIds);
        final Map<String, Object> params = getSlipParams(reports, config);

        final Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("titleDoc", docTitle);
        paramsMap.put("totalPages", params.get("totalPages"));
        if (logo != null) {
            paramsMap.put("logoPath", logo.getName());
        }
        paramsMap.put("isPgcnIdPresent", config.isPresent() ? config.get().isPgcnId() : true);
        paramsMap.put("isTitlePresent", config.isPresent() ? config.get().isTitle() : true);
        paramsMap.put("isNbPagesPresent", config.isPresent() ? config.get().isNbPages() : true);
        paramsMap.put("isSummaryPresent", config.isPresent() ? config.get().isGlobalReport() : true);

        final List<Map<String, String>> lines = (List<Map<String, String>>) params.get("slipLines");

        try {
            jasperReportService.exportReportToStream(JasperReportsService.REPORT_CONDREPORT_SLIP,
                                                     JasperReportsService.ExportType.PDF,
                                                     paramsMap,
                                                     lines,
                                                     out,
                                                     library.getIdentifier());
        } catch (final PgcnException e) {
            LOG.error("Erreur a la generation du bordereau d'envoi de train : {}", e.getLocalizedMessage());
            throw new PgcnTechnicalException(e);
        }

    }

    private Map<String, Object> getSlipParams(final List<ConditionReportDetail> reports, final Optional<ConditionReportSlipConfiguration> config) {

        final Map<String, Object> params = new HashMap<>();
        final List<Map<String, String>> slipLines = new ArrayList<>();
        long totalPages = 0;

        for (final ConditionReportDetail detail : reports) {

            final Map<String, String> line = new HashMap<>();
            if (config.isPresent()) {
                if (config.get().isPgcnId()) {
                    line.put("pgcnId", detail.getReport().getDocUnit().getPgcnId());
                }
                if (config.get().isTitle()) {
                    line.put("title", detail.getReport().getDocUnit().getLabel());
                }
                if (config.get().isNbPages()) {
                    line.put("nbPages", String.valueOf(detail.getNbViewTotal()));
                }
                if (config.get().isGlobalReport()) {
                    line.put("summary", writeSummary(detail));
                }

            } else {
                line.put("pgcnId", detail.getReport().getDocUnit().getPgcnId());
                line.put("title", detail.getReport().getDocUnit().getLabel());
                line.put("nbPages", String.valueOf(detail.getNbViewTotal()));
                line.put("summary", writeSummary(detail));
            }
            totalPages += detail.getNbViewTotal();
            slipLines.add(line);
        }

        params.put("totalPages", String.valueOf(totalPages));
        params.put("slipLines", slipLines);
        return params;
    }

    /**
     * Edition du bordereau CSV
     */
    @Transactional(readOnly = true)
    public void writeSlip(final OutputStream out, final List<String> reportIds, final String encoding, final char separator) throws IOException {
        final List<ConditionReportDetail> reports = conditionReportDetailService.findByIdentifierIn(reportIds);

        writeCSV(out, reports, encoding, separator);
    }

    /**
     * Ecriture du CSV
     */
    public void writeCSV(final OutputStream out, final List<ConditionReportDetail> reports, final String encoding, final char separator) throws
                                                                                                                                         IOException {
        // Alimentation du CSV
        try (final Writer writer = new OutputStreamWriter(out, encoding);
             final CSVWriter csvWriter = new CSVWriter(writer, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, RFC4180_LINE_END)) {
            // Entête
            writeHeader(csvWriter);
            for (final ConditionReportDetail report : reports) {
                // Corps
                writeBody(csvWriter, report);
            }
        }
    }

    /**
     * Écriture de l'entête du fichier CSV
     */
    private void writeHeader(final CSVWriter csvWriter) {
        final String[] types = {"Cote", "Titre", "NbDePagesEstimées", "Synthèse constat d'état"};
        csvWriter.writeNext(types);
    }

    /**
     * Écriture de la ligne pour ce constat d'état
     */
    private void writeBody(final CSVWriter csvWriter, final ConditionReportDetail report) {

        final List<String> line = new ArrayList<>();

        line.add(report.getReport().getDocUnit().getPgcnId());
        final Set<BibliographicRecord> records = report.getReport().getDocUnit().getRecords();
        if (records.isEmpty()) {
            line.add(report.getReport().getDocUnit().getLabel());
        } else {
            line.add(records.iterator().next().getTitle());
        }
        line.add(String.valueOf(report.getNbViewTotal()));
        line.add(writeSummary(report));

        csvWriter.writeNext(line.toArray(new String[0]));
    }

    private String writeSummary(final ConditionReportDetail detail) {
        final StringBuilder summary = new StringBuilder();
        for (final Description description : detail.getDescriptions()) {
            if (description.getProperty() != null && description.getProperty().getLabel() != null) {
                summary.append(description.getProperty().getLabel()).append(" : ");
            }
            if (description.getValue() != null) {
                summary.append(description.getValue().getLabel());
            }
            if (description.getComment() != null) {
                summary.append("(");
                summary.append(description.getComment());
                summary.append(")");
            }
            summary.append(System.getProperty("line.separator"));
        }
        if (detail.getAdditionnalDesc() != null && !detail.getAdditionnalDesc().isEmpty()) {
            summary.append("Descriptions supplémentaires : ");
            summary.append(detail.getAdditionnalDesc());
        }
        return summary.toString();
    }

    public Set<String> getSummary(final ConditionReport report) {

        final Set<String> summaries = new HashSet<>();
        if (report != null) {
            final List<ConditionReportDetail> details =
                conditionReportDetailService.findWithDescriptionsByCondReportIdentifier(report.getIdentifier());
            details.forEach(detail -> summaries.add(writeSummary(detail)));
        }
        return summaries;
    }

    /**
     * Paramètres demandés par l'export du constat d'état
     *
     * @param report
     * @return
     */
    private Map<String, Object> getExportParams(final ConditionReport report) {
        final Map<String, Object> params = new HashMap<>();

        params.put("report", ConditionReportMapper.INSTANCE.reportToDTO(report));

        // états
        final List<ConditionReportDetailVelocityDTO> steps = report.getDetails()
                                                                   .stream()
                                                                   .map(ConditionReportDetailMapper.INSTANCE::detailToVelocityDTO)
                                                                   .sorted(Comparator.comparing(ConditionReportDetailVelocityDTO::getPosition))
                                                                   .collect(Collectors.toList());
        params.put("steps", steps);

        // accès direct au dernier état
        if (!steps.isEmpty()) {
            params.put("laststep", steps.get(steps.size() - 1));
        }
        // pièces jointe
        params.put("attachments",
                   report.getAttachments().stream().map(ConditionReportAttachmentMapper.INSTANCE::attachmentToDTO).collect(Collectors.toList()));

        // Unité documentaire, bibliothèque, projet, lot, train
        final SimpleListDocUnitDTO docUnitDto = SimpleDocUnitMapper.INSTANCE.docUnitToSimpleListDocUnitDTO(report.getDocUnit());
        params.put("docunit", docUnitDto);

        if (docUnitDto.getLibrary() != null) {
            params.put("library", docUnitDto.getLibrary());
        }
        if (docUnitDto.getProject() != null) {
            params.put("project", docUnitDto.getProject());
        }
        if (docUnitDto.getLot() != null) {
            params.put("lot", docUnitDto.getLot());
        }
        if (docUnitDto.getTrain() != null) {
            params.put("train", docUnitDto.getTrain());
        }

        // Notice bibliographique
        final Set<BibliographicRecord> records = report.getDocUnit().getRecords();
        if (!records.isEmpty()) {
            final DocUnitBibliographicRecordDTO recordDto =
                BibliographicRecordMapper.INSTANCE.bibliographicRecordToDocUnitBibliographicRecordDTO(records.iterator().next());
            params.put("record", recordDto);

            final Map<String, List<String>> properties = recordDto.getProperties()
                                                                  .stream()
                                                                  .sorted(Comparator.comparing(DocPropertyDTO::getWeightedRank))
                                                                  .collect(Collectors.groupingBy(p -> p.getType().getIdentifier(),
                                                                                                 Collectors.mapping(DocPropertyDTO::getValue,
                                                                                                                    Collectors.toList())));
            params.put("properties", properties);

            final Map<String, String> types = recordDto.getProperties()
                                                       .stream()
                                                       .map(DocPropertyDTO::getType)
                                                       .collect(Collectors.toMap(DocPropertyTypeDTO::getIdentifier,
                                                                                 DocPropertyTypeDTO::getLabel,
                                                                                 (a, b) -> a));
            params.put("types", types);
        }
        // Accès à une description particulière depuis le rapport, par ex.: $Get.apply($laststep.Vigilances, "MAX_ANGLE")
        params.put("Get", (BiFunction<List<ConditionReportValueVelocityDTO>, String, List<String>>) (list, ppty) -> {
            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            return list.stream()
                       .filter(l -> StringUtils.equalsIgnoreCase(l.getPropertyCode(), ppty))
                       .flatMap(r -> r.getValues().stream().map(ConditionReportValueVelocityDTO.StringValue::getValue))
                       .collect(Collectors.toList());
        });
        return params;
    }

    /**
     * Insertion d'images dans le fichier généré
     *
     * @param report
     * @return
     */
    private Map<String, IImageProvider> getImageParams(final ConditionReport report) {
        final Map<String, IImageProvider> imageParams = new HashMap<>();

        // image vide
        final ClassPathImageProvider defaultImage =
            new ClassPathImageProvider(Thread.currentThread().getContextClassLoader(), DEFAULT_THUMBNAIL, false);
        imageParams.put("img_empty", defaultImage);

        // bibliothèque
        final File libLogo = libraryService.getLibraryLogo(report.getDocUnit().getLibrary());
        if (libLogo != null && libLogo.exists()) {
            imageParams.put("img_library", new TypedFileImageProvider(libLogo, ImageUtils.FORMAT_PNG, false));
        }

        // signatures
        for (final ConditionReportDetail detail : report.getDetails()) {
            final String libWriterName = detail.getLibWriterName();
            IImageProvider imgProvider = defaultImage;

            // Construction d'un TypedFileImageProvider à partir de la signature de l'utilisateur
            if (StringUtils.isNotBlank(libWriterName)) {
                final User user = userService.findByLogin(libWriterName);

                if (user != null) {
                    final File thumbnail = userService.getUserThumbnail(user);

                    if (thumbnail != null) {
                        imgProvider = new TypedFileImageProvider(thumbnail, ImageUtils.FORMAT_PNG, false);
                    }
                }
            }
            imageParams.put("img_" + detail.getPosition() + "_libwriter", imgProvider);
        }

        // pièces jointes
        report.getAttachments().stream()
              // paires (id attachment, fichiers)
              .map(att -> {
                  final File thumbnail = conditionReportAttachmentService.downloadAttachmentThumbnail(att);
                  if (thumbnail != null) {
                      return Pair.of("img_" + att.getIdentifier(), new TypedFileImageProvider(thumbnail, ImageUtils.FORMAT_PNG, true));
                  }
                  return null;
              }).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getLeft, Pair::getRight)).forEach(imageParams::put);

        return imageParams;
    }

    /**
     * Résultat de recherche associant un rapport d'import avec l'unité documentaire associée
     */
    public static class SearchResult {

        private ConditionReportDTO report;
        private ConditionReportDetailDTO detail;
        private SimpleListDocUnitDTO docUnit;

        public ConditionReportDTO getReport() {
            return report;
        }

        public void setReport(final ConditionReportDTO report) {
            this.report = report;
        }

        public ConditionReportDetailDTO getDetail() {
            return detail;
        }

        public void setDetail(final ConditionReportDetailDTO detail) {
            this.detail = detail;
        }

        public SimpleListDocUnitDTO getDocUnit() {
            return docUnit;
        }

        public void setDocUnit(final SimpleListDocUnitDTO docUnit) {
            this.docUnit = docUnit;
        }
    }
}
