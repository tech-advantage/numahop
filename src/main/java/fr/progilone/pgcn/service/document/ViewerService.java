package fr.progilone.pgcn.service.document;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.delivery.DeliveredDocument;
import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocPage;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.sample.Sample;
import fr.progilone.pgcn.domain.dto.sample.SampleDTO;
import fr.progilone.pgcn.domain.jaxb.mets.Mets;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DigitalDocumentRepository;
import fr.progilone.pgcn.repository.document.DocPageRepository;
import fr.progilone.pgcn.repository.storage.BinaryRepository;
import fr.progilone.pgcn.service.administration.viewsformat.ViewsFormatConfigurationService;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.delivery.DeliveryService;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Canvases;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Images;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Manifest;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Metadata;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Resource;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Sequences;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Structures;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Thumbnail;
import fr.progilone.pgcn.service.sample.SampleService;
import fr.progilone.pgcn.service.storage.BinaryStorageManager;

@Service
public class ViewerService {

    private static final Logger LOG = LoggerFactory.getLogger(ViewerService.class);

    public static final String URI_WS_VIEWER = "/api/rest/viewer/document/";
    private static final String URI_PRES_CONTEXT_IIIF = "http://iiif.io/api/presentation/2/context.json";
    private static final String URI_IMG_CONTEXT_IIIF = "http://iiif.io/api/image/2/context.json";
    private static final String URI_PROFILE_IIIF = "http://iiif.io/api/image/2/level1";
    private static final String URI_PROTOCOL_IIIF = "http://iiif.io/api/image";

    public static final String LABEL_MASTER = "master";
    private static final String NON_RENSEIGNE = "Non renseigné";

    private final DigitalDocumentRepository digitalDocumentRepository;
    private final DocPageRepository docPageRepository;
    private final BinaryRepository binaryRepository;

    private final MetaDatasCheckService metaDatasCheckService;
    private final BinaryStorageManager bsm;
    private final TableOfContentsService tocService;
    private final DigitalDocumentService digitalDocumentService;
    private final DeliveryService deliveryService;
    private final SampleService sampleService;
    private final ViewsFormatConfigurationService formatConfigurationService;

    @Autowired
    public ViewerService(final DigitalDocumentRepository digitalDocumentRepository,
                         final DocPageRepository docPageRepository,
                         final BinaryRepository binaryRepository,
                         final MetaDatasCheckService metaDatasCheckService,
                         final TableOfContentsService tocService,
                         final BinaryStorageManager bsm,
                         final DigitalDocumentService digitalDocumentService,
                         final DeliveryService deliveryService,
                         final SampleService sampleService,
                         final ViewsFormatConfigurationService formatConfigurationService) {
        this.digitalDocumentRepository = digitalDocumentRepository;
        this.docPageRepository = docPageRepository;
        this.binaryRepository = binaryRepository;
        this.metaDatasCheckService = metaDatasCheckService;
        this.tocService = tocService;
        this.bsm = bsm;
        this.digitalDocumentService = digitalDocumentService;
        this.deliveryService = deliveryService;
        this.sampleService = sampleService;
        this.formatConfigurationService = formatConfigurationService;
    }

    public List<StoredFile> getStoredFiles(final String identifier, final ViewsFormatConfiguration.FileFormat format, final boolean sampling) {

        final List<DocPage> dps = new ArrayList<>();
        if (sampling) {
            dps.addAll(docPageRepository.getAllBySampleIdentifier(identifier));
        } else {
            dps.addAll(docPageRepository.getAllByDigitalDocumentIdentifier(identifier));
        }
        if (dps.isEmpty()) {
            return Collections.emptyList();
        }

        final List<String> ids = dps.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList());
        if (sampling) {
            return binaryRepository.getAllWithDocByPageIdentifiersAndFileFormat(ids, format);
        } else {
            return binaryRepository.getAllByPageIdentifiersAndFileFormat(ids, format);
        }
    }

    /**
     * Retourne les dimensions du format XTRAZOOM, ZOOM, ou à défaut du format PRINT.
     *
     * @param identifier
     * @param page
     * @return
     * @throws PgcnTechnicalException
     */
    private int[] getSizeFile(final String identifier, final int page) throws PgcnTechnicalException {

        final DocPage dp =  digitalDocumentService.getPage(identifier, page);
        StoredFile sf = null;
        sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.XTRAZOOM);
        if (sf == null) {
            sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.ZOOM);
            if (sf == null) {
                sf = binaryRepository.getOneByPageIdentifierAndFileFormat(dp.getIdentifier(), ViewsFormatConfiguration.FileFormat.PRINT);
            }
        }
        final File f = bsm.getFileForStoredFile(sf);
        final int[] dims = new int[2];
        if (sf.getHeight()==null || sf.getWidth() == null) {
            final Optional<Dimension> dim =  bsm.getImgDimension(f, Optional.empty());
            if (dim.isPresent()) {
                dims[0] = (int)dim.get().getHeight();
                dims[1] = (int)dim.get().getWidth();
            }
        } else {
            dims[0] = sf.getHeight().intValue();
            dims[1] = sf.getWidth().intValue();
        }

        return dims;
    }

    @Transactional(readOnly = true)
    public DocUnit getDocUnit(final String identifier) {
        final DigitalDocument ddoc = digitalDocumentRepository.getOneWithDocUnitAndLibrary(identifier);
        return ddoc != null ? ddoc.getDocUnit() : null;
    }


    /**
     * Point d'entrée de la generation du fichier manifest pour affichage dans Mirador.
     *
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    @Transactional(readOnly = true)
    public String buildManifestViewer(final String identifier) throws PgcnTechnicalException {
        try {
            final Manifest mf = getManifest(identifier);
            return new ObjectMapper().writeValueAsString(mf);
        } catch (final IOException e) {
            throw new PgcnTechnicalException("Could not build manifest: ".concat(e.getMessage()));
        }
    }

    /**
     * Point d'entrée de la generation du fichier manifest de l'echantillon.
     *
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    @Transactional(readOnly = true)
    public String buildSampledManifestViewer(final String identifier) throws PgcnTechnicalException {
        try {
            final Manifest mf = getSampledManifest(identifier);
            return new ObjectMapper().writeValueAsString(mf);
        } catch (final IOException e) {
            throw new PgcnTechnicalException("Could not build manifest: ".concat(e.getMessage()));
        }
    }

    /**
     * Construction de l'objet Manifest.
     *
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    private Manifest getManifest(final String identifier) throws PgcnTechnicalException {

        final DocUnit docUnit = getDocUnit(identifier);
        final Manifest manifest = new Manifest();
        manifest.setAdditionalProperty("@context", URI_PRES_CONTEXT_IIIF);
        manifest.setAdditionalProperty("@id", URI_WS_VIEWER.concat(identifier));
        manifest.setAdditionalProperty("@type", "sc:Manifest");
        manifest.setAdditionalProperty("label", docUnit.getPgcnId());
        manifest.setAdditionalProperty("metadata", getGlobalMetadatas(docUnit));

        // Table des matières (TOC).
        final Optional<Mets> mets = getMetsFile(identifier);
        // Fichier METS ?
        if(mets.isPresent()) {
            // Ajout de la table des matieres si presente.
            final List<Structures> structures = tocService.getTableOfContent(identifier, mets.get());
            if (CollectionUtils.isNotEmpty(structures)) {
                manifest.setAdditionalProperty("structures", structures);
            }
        } else {
            // Fichier Excel ?
            final Optional<File> excel = getExcelFile(identifier, docUnit.getLibrary().getIdentifier());
            if (excel.isPresent() && excel.get().canRead()) {
                try (final InputStream is = FileUtils.openInputStream(excel.get())) {
                    final List<Structures> structures =
                                    tocService.getTableOfContentExcel(identifier, is, docUnit.getLabel());
                    if (CollectionUtils.isNotEmpty(structures)) {
                        manifest.setAdditionalProperty("structures", structures);
                    }
                } catch (final InvalidFormatException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            } else {
                final List<Structures> structures =
                        tocService.getTableOfContentFromDb(identifier);
                if (CollectionUtils.isNotEmpty(structures)) {
                    manifest.setAdditionalProperty("structures", structures);
                }
            }
        }

        // Ajout de sequences / images / canevas etc...
        manifest.setAdditionalProperty("sequences", getSequences(identifier, false));
        return manifest;
    }

    /**
     * Construction de l'objet Manifest de l'échantillon.
     *
     * @param identifier
     * @return
     * @throws PgcnTechnicalException
     */
    private Manifest getSampledManifest(final String identifier) throws PgcnTechnicalException {

        final Manifest manifest = new Manifest();
        manifest.setAdditionalProperty("@context", URI_PRES_CONTEXT_IIIF);
        manifest.setAdditionalProperty("@id", URI_WS_VIEWER.concat(identifier));
        manifest.setAdditionalProperty("@type", "sc:Manifest");
        manifest.setAdditionalProperty("label", "Contrôle échantillon");

        // Ajout de sequences / images / canevas etc...
        manifest.setAdditionalProperty("sequences", getSequences(identifier, true));
        return manifest;
    }


    /**
     * Recuperation du fichier Mets.
     * @param identifier
     * @return
     */
    private Optional<Mets> getMetsFile(final String identifier) {
        final DigitalDocument dd = digitalDocumentRepository.getOneWithDocUnitAndLibrary(identifier);
        Optional<Mets> mets = Optional.ofNullable(null);
        if (dd != null) {
            mets = metaDatasCheckService.getMetaDataMetsFile(dd.getDigitalId(),
                                                             dd.getDocUnit().getLibrary().getIdentifier());
        }
        return mets;
    }

    /**
     * Recuperation du fichier Excel de Toc.
     *
     * @param identifier
     * @return
     */
    private Optional<File> getExcelFile(final String identifier, final String libraryId) {
        final DigitalDocument dd = digitalDocumentRepository.findOne(identifier);
        Optional<File> excel = Optional.ofNullable(null);
        if (dd != null) {
            excel = metaDatasCheckService.getMetaDataExcelFile(dd.getDigitalId(), libraryId);
        }
        return excel;
    }
    
    /**
     * Récupération du fichier de TDM (TOC) excel, ou à défaut mets.
     * 
     * @param identifier
     * @return
     */
    @Transactional
    public File getTableOfContent(final String identifier) {
        final DigitalDocument dd = digitalDocumentRepository.getOneWithDocUnitAndLibrary(identifier);
        Optional<File> toc = getExcelFile(dd.getIdentifier(), dd.getDocUnit().getLibrary().getIdentifier());
        if(toc.isPresent() && toc.get().canRead()) {
            return toc.get();
        } else {
            // pas de toc excel, on tente de recuperer le mets
            toc = metaDatasCheckService.getMetsXmlFile(dd.getDigitalId(), dd.getDocUnit().getLibrary().getIdentifier());
            if (toc.isPresent() && toc.get().canRead()) {
                return toc.get();
            } else {
                return null;
            }
        }
    }

    /**
     * Recuperation des metadonnees générales du doc.
     *
     * @param docUnit
     * @return
     */
    private List<Metadata> getGlobalMetadatas(final DocUnit docUnit) {

        final List<Metadata> metas = new ArrayList<>();
        final Metadata mdTitle = new Metadata();
        mdTitle.setAdditionalProperty("label", "Titre");
        mdTitle.setAdditionalProperty("value", docUnit.getLabel());
        metas.add(mdTitle);
        if (docUnit.getLibrary() != null) {
            final Metadata mdProv = new Metadata();
            mdProv.setAdditionalProperty("label", "Bibliothèque");
            mdProv.setAdditionalProperty("value", docUnit.getLibrary().getName());
            metas.add(mdProv);
        }
        final Metadata mdType = new Metadata();
        mdType.setAdditionalProperty("label", "Classification");
        mdType.setAdditionalProperty("value", StringUtils.isNotBlank(docUnit.getType()) ? docUnit.getType() : NON_RENSEIGNE);
        metas.add(mdType);
        final Metadata mdDelay = new Metadata();
        mdDelay.setAdditionalProperty("label", "Délai avant contrôle (jours)");
        mdDelay.setAdditionalProperty("value", docUnit.getCheckDelay() != null ? String.valueOf(docUnit.getCheckDelay()) : NON_RENSEIGNE);
        metas.add(mdDelay);
        final Metadata mdDtFin = new Metadata();
        mdDtFin.setAdditionalProperty("label", "Date de fin de contrôle prévue");
        mdDtFin.setAdditionalProperty("value", docUnit.getCheckEndTime() != null ? docUnit.getCheckEndTime().toString() : NON_RENSEIGNE);
        metas.add(mdDtFin);

        return metas;
    }

   /**
    * Construction des objets Sequences.
    *
    * @param identifier
    * @return
    * @throws PgcnTechnicalException
    */
    private List<Sequences> getSequences(final String identifier, final boolean sampling) throws PgcnTechnicalException {
        final List<Sequences> seqs = new ArrayList<>();
        final Sequences sequences = new Sequences();
        sequences.setType("sc:Sequence");
        sequences.setAdditionalProperty("viewingDirection", "left-to-right");

        /*sequences.setAdditionalProperty("viewingDirection", "right-to-left"); */

        sequences.setAdditionalProperty("viewingHint", "paged");  // detecte selon le doc si "paged" ou "individuals" (evite la bookview) ?
        sequences.setAdditionalProperty("canvases", getCanvases(identifier, sampling));
        seqs.add(sequences);
        return seqs;
    }

    /**
     * Construction des objets Canvases.
     *
     * @param identifier
     * @param docUnit
     * @return
     * @throws PgcnTechnicalException
     */
    private List<Canvases> getCanvases(final String identifier, final boolean sampling) throws PgcnTechnicalException {

        final DigitalDocument ddoc = sampling ? null : digitalDocumentRepository.getOneWithDocUnitAndLibrary(identifier);
        final List<StoredFile> storedThumbs = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.THUMB, sampling);
        // prise en compte des zooms eventuels
        final List<StoredFile> storedFiles = mergeStoredFileList(identifier, sampling);

        // Recuperation config format images.
        final ViewsFormatConfiguration formatConfig;
        if (ddoc != null) {
            formatConfig = formatConfigurationService.getOneByLot(ddoc.getDocUnit().getLot().getIdentifier());
        } else {
            final Sample sample = sampleService.getOneWithDep(identifier);
            formatConfig = formatConfigurationService.getOneByLot(sample.getDelivery().getLot().getIdentifier());
        }
        // default height/width du format print si pas stockés en DB.
        final long maxWidth  = formatConfig.getWidthByFormat(ViewsFormatConfiguration.FileFormat.PRINT);
        final long maxHeight = formatConfig.getHeightByFormat(ViewsFormatConfiguration.FileFormat.PRINT);

        final List<Canvases> canvs = new ArrayList<>();
        int cpt = 0;
        for(final StoredFile sf : storedFiles) {

            final DocPage page = sf.getPage();
            final int sfPageNumber = page!=null ? page.getNumber() : 0;
            cpt++;
            final Canvases canvases = new Canvases();
            canvases.setId(String.valueOf(cpt));
            canvases.setType("sc:Canvas");
            if (sampling) {
                canvases.setAdditionalProperty("label", sf.getFilename());
            } else {
                canvases.setAdditionalProperty("label", getSplittedName(sf.getFilename(), ddoc.getDigitalId()));
                if (page != null && page.getSample() != null) {  // pour reperer les img echantillonnees
                   canvases.setAdditionalProperty("sampled", true);
                }
            }
            if (page!=null && DocPage.PageStatus.VALIDATED == page.getStatus()) {
                canvases.setAdditionalProperty("checkedOK", true);
            } else if (page!=null && DocPage.PageStatus.REJECTED == page.getStatus()) {
                canvases.setAdditionalProperty("checkedKO", true);
            }

            if (sf.getHeight() < sf.getWidth()) {
                // empeche pagination pour mode paysage
                canvases.setAdditionalProperty("viewingHint", "non-paged");
            }
            canvases.setAdditionalProperty("height", sf.getHeight()!=null?sf.getHeight():maxHeight);
            canvases.setAdditionalProperty("width", sf.getWidth()!=null?sf.getWidth():maxWidth);

            final String digDocId = sampling ? sf.getPage().getDigitalDocument().getIdentifier() : identifier;

            // ajout du thumbnail => on ne charge que les vignettes a l'initialisation du viewer.
            canvases.setAdditionalProperty("thumbnail", getThumb(digDocId, sfPageNumber, storedThumbs.get(cpt-1), formatConfig));
            canvases.setAdditionalProperty("images", getImages(digDocId, cpt, sfPageNumber, sf.getWidth()!=null?sf.getWidth():maxWidth, sf.getHeight()!=null?sf.getHeight():maxHeight));
            canvs.add(canvases);
        }
        return canvs;
    }

    /**
     * Retourne la liste des fichiers aux plus grandes dimensions.
     *
     * @param identifier
     * @return
     */
    private List<StoredFile> mergeStoredFileList(final String identifier, final boolean sampling) {

        final List<StoredFile> printFiles = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.PRINT, sampling);
        final List<StoredFile> zoomFiles = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.ZOOM, sampling);
        final List<StoredFile> xtraZoomFiles = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.XTRAZOOM, sampling);

        final List<StoredFile> mergedZooms = mergeLists(zoomFiles, xtraZoomFiles);
        if (mergedZooms.size() == printFiles.size()) {
            return mergedZooms;
        } else {
            return mergeLists(printFiles, mergedZooms);
        }
    }


    /**
     * Merge les items de list2 dans list1.
     *
     * @param list1
     * @param list2
     * @return
     */
    private List<StoredFile> mergeLists(final List<StoredFile> list1, final List<StoredFile> list2) {

        final List<StoredFile> merged = new ArrayList<>(list1);
        if (CollectionUtils.isEmpty(list2)) {
            return list1;
        } else if (list1.size() == list2.size()) {
            return list2;
        } else {
            // il faut merger list2 dans list1
            int idx = 0;
            for(final StoredFile sf1:list1) {
                for(final StoredFile sf2:list2) {
                    if (StringUtils.equals(sf2.getPage().getIdentifier(), sf1.getPage().getIdentifier())) {
                        merged.set(idx, sf2);
                    }
                }
                idx++;
            }
        }
        return merged;
    }

    /**
     * Retourne le nom du fichier sans le radical.
     *
     * @param name
     * @return
     */
    private String getSplittedName(final String name, final String radical) {
        String splittedName = StringUtils.isBlank(name)?"":name;
        final String[] splitName = name.split(radical.concat("_"));
        if (splitName.length==1){
            splittedName = splitName[0];
        } else if (splitName.length > 1) {
            splittedName = splitName[1];
        }
        return splittedName;
    }

    /**
     * Construit un objet Thumbnail.
     *
     * @param identifier
     * @param cpt
     * @param sf
     * @return
     */
    private Thumbnail getThumb(final String identifier, final int pageNumber, final StoredFile thumb, final ViewsFormatConfiguration formatConfig) {

        final Thumbnail th = new Thumbnail();
        th.setId(URI_WS_VIEWER.concat(identifier).concat("/thumbnail/")
                     .concat(String.valueOf(pageNumber)).concat("/thumb.jpg"));
        th.setType("dctypes:Image");
        th.setAdditionalProperty("height", thumb.getHeight()!=null?thumb.getHeight():formatConfig.getHeightByFormat(ViewsFormatConfiguration.FileFormat.THUMB));
        th.setAdditionalProperty("width", thumb.getWidth()!=null?thumb.getWidth():formatConfig.getWidthByFormat(ViewsFormatConfiguration.FileFormat.THUMB));
        return th;
    }

    /**
     * Construction des objets Images.
     *
     * @param identifier
     * @param cpt
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private List<Images> getImages(final String identifier, final int cpt, final int pageNumber, final long maxWidth, final long maxHeight) {

        final List<Images> imgs = new ArrayList<>();
        final Images images = new Images();
        images.setType("oa:Annotation");
        images.setAdditionalProperty("motivation", "sc:painting");
        images.setAdditionalProperty("resource", getResource(identifier, pageNumber, maxWidth, maxHeight));
        images.setAdditionalProperty("on", String.valueOf(cpt));

        imgs.add(images);
        return imgs;
    }

    /**
     * Construction de l'objet Resource.
     *
     * @param identifier
     * @param cpt
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Resource getResource(final String identifier, final int pageNumber, final long maxWidth, final long maxHeight) {

        final Resource resource = new Resource();
        resource.setId(URI_WS_VIEWER.concat(identifier) + "/" + pageNumber + "/full");
        resource.setType("dctypes:Image");
        resource.setAdditionalProperty("format", "image/jpeg");
        resource.setAdditionalProperty("height", maxHeight);
        resource.setAdditionalProperty("width", maxWidth);
        resource.setAdditionalProperty("service", getService(identifier, pageNumber));
        return resource;
    }

    /**
     * Construction de l'objet Service.
     *
     * @param identifier
     * @param cpt
     * @return
     */
    private fr.progilone.pgcn.service.exchange.iiif.manifest.Service getService(final String identifier, final int pageNumber) {

        final fr.progilone.pgcn.service.exchange.iiif.manifest.Service service = new fr.progilone.pgcn.service.exchange.iiif.manifest.Service();
        service.setId(URI_WS_VIEWER.concat(identifier) + "/" + pageNumber);
        service.setAdditionalProperty("@context", URI_IMG_CONTEXT_IIIF);
        service.setAdditionalProperty("profile", URI_PROFILE_IIIF);
        return service;
    }

    /**
     * Generation du fichier info.json pour chaque image.
     *
     * @param identifier
     * @param pageNumber
     * @return
     * @throws PgcnTechnicalException
     */
    @Transactional(readOnly = true)
    public Map<String, Object> buildInfoFileViewer(final String identifier, final int pageNumber) throws PgcnTechnicalException {

        final int sizeFile[] = getSizeFile(identifier, pageNumber);
        final int width = sizeFile[1];
        final int height = sizeFile[0];

        final List<Object> sizes = new ArrayList<>();
        final List<Object> tiles = new ArrayList<>();
        final Map<String, Object> info = new LinkedHashMap<>();
        final List<Integer> scaleFactors = Arrays.asList(1, 2, 4, 8);

        info.put("@context", URI_IMG_CONTEXT_IIIF);
        info.put("@id", URI_WS_VIEWER.concat(identifier).concat("/") + pageNumber);
        info.put("width", width);
        info.put("height", height);
        info.put("protocol", URI_PROTOCOL_IIIF);

        for(final Integer i: scaleFactors) {
            final Map<String, Integer> size = new LinkedHashMap<>();
            size.put("width", Integer.divideUnsigned(width, i));
            size.put("height", Integer.divideUnsigned(height, i));
            sizes.add(size);
        }
        Collections.reverse(sizes);
        info.put("sizes", sizes);

        final Map<String, Object> tile = new LinkedHashMap<>();
        tile.put("width", width);
        tile.put("height", height);
        tile.put("scaleFactors", scaleFactors);
        tiles.add(tile);
        info.put("tiles", tiles);
        info.put("profile", URI_PROFILE_IIIF);

        return info;
    }

    /**
     * Retourne la structure de TOC d'un document si présente.
     *
     * @param doc
     * @return
     */
    private List<Structures> getStructureByDocument(final DigitalDocument doc) {

        final String identifier = doc.getIdentifier();
        final Optional<Mets> mets = getMetsFile(identifier);
        List<Structures> structures = mets.map(value -> tocService.getTableOfContent(identifier, value)).orElse(null);
        if (structures == null) {
            // pas de mets => on cherche un excel...
            final Optional<File> excel = getExcelFile(identifier, doc.getDocUnit().getLibrary().getIdentifier());
            if (excel.isPresent() && excel.get().canRead()) {
                try (final InputStream is = FileUtils.openInputStream(excel.get())) {
                    structures = tocService.getTableOfContentExcel(identifier, is, doc.getDocUnit().getLabel());
                } catch (final InvalidFormatException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            } else {
                structures = new ArrayList<>();
            }
        }
        return structures;
    }

    /**
     * Construit la map de metadonnées des fichiers à controler.
     * Datas extraits des masters via Image Magick
     * et datas de TOC (mets ou excel)
     *
     * @param identifier
     * @return
     *
     */
    public Map<String, Map<String, String>> getMetadatasForFiles(final String identifier) {

        LOG.info("Recuperation metadonnees des fichiers masters");

        final Map<String, Map<String, String>> metadataFiles = new HashMap<>();
        final List<StoredFile> storedMasters = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.MASTER, false);
        // pour retrouver le radical
        final DigitalDocument ddoc = digitalDocumentRepository.getOneWithDocUnitAndLibrary(identifier);
        final List<Structures> consolidedStruct = getStructureByDocument(ddoc);

        storedMasters.stream()
                    .filter(sf->sf.getPage().getNumber() != null)  // cas du master pdf
                    .forEach(sf -> {
                        final Map<String, String> datas = new LinkedHashMap<>();

                        // splitted name
                        datas.put("name", getSplittedName(sf.getFilename(), ddoc.getDigitalId()));
                        datas.put("fileName", sf.getFilename());
                        datas.put("size", String.valueOf(sf.getLength()).concat(" octets"));
                        datas.put("WxH", String.valueOf(sf.getWidth()).concat(" x ").concat(String.valueOf(sf.getHeight())));
                        datas.put("compressionType", sf.getCompressionType() );
                        final String txComp = (sf.getCompressionRate() == null
                                                || sf.getCompressionRate() == 0) ? "" : String.valueOf(sf.getCompressionRate());
                        datas.put("bitsDepth", StringUtils.defaultIfBlank(txComp, ViewerService.NON_RENSEIGNE));
                        datas.put("colorspace", StringUtils.defaultIfBlank(sf.getColorspace(), ViewerService.NON_RENSEIGNE));
                        final String res = (sf.getResolution() == null
                                || sf.getResolution() == 0) ? "" : String.valueOf(sf.getResolution());
                        datas.put("resolution", StringUtils.defaultIfBlank(res, ViewerService.NON_RENSEIGNE));

                        // TOC fields
                        if (StringUtils.isBlank(sf.getTypeToc())
                                        && StringUtils.isBlank(sf.getOrderToc())
                                        && StringUtils.isBlank(sf.getTitleToc()) ) {
                            final String[] tocDatas = getTocDatas(consolidedStruct, sf.getPage().getNumber());
                            datas.put("typeTOC", tocDatas[0]);
                            datas.put("orderTOC", tocDatas[1]);
                            datas.put("nameTOC", tocDatas[2]);
                        } else {
                            datas.put("typeTOC", sf.getTypeToc());
                            datas.put("orderTOC", sf.getOrderToc());
                            datas.put("nameTOC", sf.getTitleToc());
                        }
                        // Text OCR
                        datas.put("textOcr", sf.getTextOcr());
                         // Piece
                         datas.put("piece", sf.getPage().getPiece());
                        // cle : simple increment => permet de rester synchro mm avec une sequence bizarre
                        metadataFiles.put(String.valueOf(metadataFiles.size()), datas);
                    });

        return metadataFiles;
    }

    /**
     * Construit la map de metadonnées des fichiers de l'echantillon à controler.
     * Datas extraits des masters via Image Magick
     *
     * @param identifier
     * @return
     */
    public Map<String, Map<String, String>> getMetadatasForSample(final String identifier) {

        LOG.info("Recuperation metadonnees des fichiers masters de l'echantillon");
        final Map<String, Map<String, String>> metadataFiles = new HashMap<>();
        final List<StoredFile> storedMasters = getStoredFiles(identifier, ViewsFormatConfiguration.FileFormat.MASTER, true);

        final SampleDTO sample = sampleService.getOne(identifier);
        final Set<DeliveredDocument> documents = deliveryService.getSimpleDigitalDocumentsByDelivery(sample.getDelivery().getIdentifier());
        final Map<String, List<Structures>> structuresByDoc = new HashMap<>();
        documents.forEach(doc -> {
            structuresByDoc.put(doc.getDigitalDocument().getIdentifier(), getStructureByDocument(doc.getDigitalDocument()));
        });

        storedMasters.stream()
                    .filter(sf->sf.getPage().getNumber() != null)
                    .forEach(sf -> {

            final Map<String, String> datas = new LinkedHashMap<>();
            datas.put("name", getSplittedName(sf.getFilename(), sf.getPage().getDigitalDocument().getDigitalId()));
            datas.put("fileName", sf.getFilename());
            datas.put("size", String.valueOf(sf.getLength()).concat(" octets"));
            datas.put("WxH", String.valueOf(sf.getWidth()).concat(" x ").concat(String.valueOf(sf.getHeight())));
            datas.put("compressionType", sf.getCompressionType() );
            final String txComp = (sf.getCompressionRate() == null
                                    || sf.getCompressionRate() == 0) ? "" : String.valueOf(sf.getCompressionRate());
            datas.put("bitsDepth)", StringUtils.defaultIfBlank(txComp, ViewerService.NON_RENSEIGNE));
            datas.put("colorspace", StringUtils.defaultIfBlank(sf.getColorspace(), ViewerService.NON_RENSEIGNE));
            final String res = (sf.getResolution() == null
                    || sf.getResolution() == 0) ? "" : String.valueOf(sf.getResolution());
            datas.put("resolution", StringUtils.defaultIfBlank(res, ViewerService.NON_RENSEIGNE));

            // TOC fields
            if (StringUtils.isBlank(sf.getTypeToc())
                            && StringUtils.isBlank(sf.getOrderToc())
                            && StringUtils.isBlank(sf.getTitleToc()) ) {

                final String[] tocDatas = getTocDatas(structuresByDoc.get(sf.getPage().getDigitalDocument().getIdentifier()),
                                                           sf.getPage().getNumber());
                datas.put("typeTOC", tocDatas[0]);
                datas.put("orderTOC", tocDatas[1]);
                datas.put("nameTOC", tocDatas[2]);
            } else {
                datas.put("typeTOC", sf.getTypeToc());
                datas.put("orderTOC", sf.getOrderToc());
                datas.put("nameTOC", sf.getTitleToc());
            }

            // Text OCR
            datas.put("textOcr", sf.getTextOcr());
                         // Piece
                         datas.put("piece", sf.getPage().getPiece());
            // cle : simple increment => permet de rester synchro mm avec une sequence bizarre
            metadataFiles.put(String.valueOf(metadataFiles.size()), datas);
        });
        return metadataFiles;
    }


    /**
     * Retourne les données affichées ds la table de matières.
     *
     * @param structures
     * @param page
     * @return
     */
    private String[] getTocDatas(final List<Structures> structures, final int page) {

        final Optional<Structures> struc = structures
                .stream()
                .filter(s ->
                    CollectionUtils.isNotEmpty(s.getAdditionalProperties().keySet())
                        && s.getAdditionalProperties().get("canvases")!= null
                        && StringUtils.equals(String.valueOf(page),  ((List<String>)s.getAdditionalProperties().get("canvases")).get(0))
                )
                .findFirst();

        final String[] datas = new String[3];
        if (struc.isPresent()) {
            datas[0] = StringUtils.defaultIfBlank(String.valueOf(struc.get().getAdditionalProperties().get("type")), null);
            datas[1] = StringUtils.defaultIfBlank(String.valueOf(struc.get().getAdditionalProperties().get("order")), null);
            datas[2] = StringUtils.defaultIfBlank(String.valueOf(struc.get().getAdditionalProperties().get("title")), null);
        }
        return datas;
    }


}
