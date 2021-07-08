package fr.progilone.pgcn.service.exchange.cines;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import fr.progilone.pgcn.domain.document.DigitalDocument;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.jaxb.dc.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.dc.SimpleLiteral;
import fr.progilone.pgcn.domain.jaxb.ead.Ead;
import fr.progilone.pgcn.domain.jaxb.mets.AmdSecType;
import fr.progilone.pgcn.domain.jaxb.mets.DivType;
import fr.progilone.pgcn.domain.jaxb.mets.DivType.Fptr;
import fr.progilone.pgcn.domain.jaxb.mets.FileType;
import fr.progilone.pgcn.domain.jaxb.mets.FileType.FLocat;
import fr.progilone.pgcn.domain.jaxb.mets.MdSecType;
import fr.progilone.pgcn.domain.jaxb.mets.Mets;
import fr.progilone.pgcn.domain.jaxb.mets.MetsType;
import fr.progilone.pgcn.domain.jaxb.mets.MetsType.FileSec;
import fr.progilone.pgcn.domain.jaxb.mets.MetsType.FileSec.FileGrp;
import fr.progilone.pgcn.domain.jaxb.mets.MetsType.MetsHdr.AltRecordID;
import fr.progilone.pgcn.domain.jaxb.mets.StructMapType;
import fr.progilone.pgcn.domain.jaxb.mix.Mix;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.domain.storage.StoredFile;
import fr.progilone.pgcn.exception.PgcnUncheckedException;
import fr.progilone.pgcn.service.check.MetaDatasCheckService;
import fr.progilone.pgcn.service.document.TableOfContentsService;
import fr.progilone.pgcn.service.exchange.ead.ExportEadService;
import fr.progilone.pgcn.service.exchange.iiif.manifest.Structures;
import fr.progilone.pgcn.service.util.FileUtils;

/**
 * Service de génération du fichier mets.xml
 *
 * @see <a href="http://www.loc.gov/standards/mets/METSOverview.v2_fr.html">description</a>
 * @see <a href="http://www.loc.gov/standards/mets/mets.xsd">xsd</a><br/>
 * <p>
 * Created by Sébastien on 28/12/2016.
 */
@Service
public class ExportMetsService {

    private static final String ID_DMD_SEC_DC = "DMD-DC";
    private static final String ID_DMD_SEC_EAD = "DMD-EAD";
    private static final ObjectFactory DC_FACTORY = new ObjectFactory();
    private static final fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory METS_FACTORY = new fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory();
    private static final String METS_SCHEMA_VALIDATION = "https://www.loc.gov/standards/mets/mets.xsd";
    private static final String METS_SCHEMA_LOCATION = "http://www.loc.gov/METS/ https://www.loc.gov/standards/mets/mets.xsd";
    // #4412 aout 2019 - changement au cines  (bien formé, mais pas valide White spaces are required between publicId and systemId)...                                                   
    //+ "http://purl.org/dc/elements/1.1/ http://dublincore.org/schemas/xmls/simpledc20021212.xsd";

    private static final Logger LOG = LoggerFactory.getLogger(ExportMetsService.class);

    private final MetaDatasCheckService mdCheckService;
    private final ExportEadService exportEadService;
    private final TableOfContentsService tocService;

    @Autowired
    public ExportMetsService(final ExportEadService exportEadService,
                             final MetaDatasCheckService mdCheckService,
                             final TableOfContentsService tocService) {
        this.exportEadService = exportEadService;
        this.mdCheckService = mdCheckService;
        this.tocService = tocService;
    }

    /**
     * Génération du mets.xml
     * Les metadonnées metaDc et metaEad sont renseignées si elles sont renseignées, sinon on les récupère sur la 1e notice de l'unité documentaire.
     *
     * @param out
     * @param docUnit
     * @param metaDc
     * @param metaEad
     * @param listStoredFiles
     * @throws JAXBException
     * @throws MalformedURLException
     * @throws SAXException
     */
    public void writeMetadata(final OutputStream out,
                              final DocUnit docUnit,
                              final BibliographicRecordDcDTO metaDc,
                              final boolean metaEad,
                              final List<CheckSummedStoredFile> listStoredFiles) throws JAXBException, MalformedURLException, SAXException {
        final Mets mets = METS_FACTORY.createMets();
        // Information du fichier METS
        mets.setMetsHdr(getMetsHdr(docUnit));

        // Métadonnées descriptives
        final List<MdSecType> dmdSec = mets.getDmdSec();
        final List<MdSecType> mdSecs = new ArrayList<>();

        // metas EAD
        if (metaEad) {
            getDmdSecEad(docUnit).ifPresent(mdSecs::add);
        } else {
            // metas DC
            getDmdSecDc(metaDc).ifPresent(mdSecs::add);
        }

        dmdSec.addAll(mdSecs);
        
        final String libraryId = docUnit.getLibrary().getIdentifier();

        // amdSec - Recup depuis mets de livraison s'il existe
        docUnit.getDigitalDocuments().stream()
                            .findFirst().ifPresent(dd -> {
                                    getMasterAmdSec(dd.getDigitalId(), libraryId).ifPresent(asecs -> mets.getAmdSec().addAll(asecs));
        });

        // Inventaire des fichiers
        mets.setFileSec(getFileSec(listStoredFiles, docUnit.getLot().getRequiredFormat()));

        // Structure du document (TOC)
        mdSecs.forEach(mdSec -> mets.getStructMap().add(getStructMap(mdSec, docUnit, listStoredFiles, mets.getFileSec())));

        // Écriture du XML dans le flux de sortie
        final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.mets.ObjectFactory.class,
                                                            SimpleLiteral.class,
                                                            Mix.class,
                                                            fr.progilone.pgcn.domain.jaxb.ead.ObjectFactory.class); // METS + DC + EAD
        final Marshaller m = context.createMarshaller();
        // Validation
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        m.setSchema(sf.newSchema(new URL(METS_SCHEMA_VALIDATION)));
        m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, METS_SCHEMA_LOCATION);
        m.setProperty("com.sun.xml.bind.namespacePrefixMapper", new MetsNamespacePrefixMapper());
        m.marshal(mets, out);
    }

    /**
     * Entête METS
     *
     * @param docUnit
     * @return
     */
    private MetsType.MetsHdr getMetsHdr(final DocUnit docUnit) {
        final MetsType.MetsHdr metsHdr = METS_FACTORY.createMetsTypeMetsHdr();
        metsHdr.setCREATEDATE(docUnit.getCreatedDate());
        metsHdr.setLASTMODDATE(docUnit.getLastModifiedDate());
        // Identifiant
        final AltRecordID recordId = METS_FACTORY.createMetsTypeMetsHdrAltRecordID();
        recordId.setValue(docUnit.getIdentifier());
        metsHdr.getAltRecordID().add(recordId);
        return metsHdr;
    }

    /**
     * Section AmdSec correspondant aux fichiers Master du mets initial.
     *
     * @param digitalId
     * @return
     */
    private Optional<List<AmdSecType>> getMasterAmdSec(final String digitalId, final String libraryId) {

        final Optional<Mets> inputMets = mdCheckService.getMetaDataMetsFile(digitalId, libraryId);
        if (inputMets.isPresent()) {
            return Optional.of(mdCheckService.getMasterAmdSec(inputMets.get()));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Recupere la Physical StructMap du mets initial.
     *
     * @param digitalId
     * @return
     */
    private Optional<StructMapType> getPhysicalStruct(final String digitalId, final String libraryId) {
        if (digitalId != null) {
            final Optional<Mets> inputMets = mdCheckService.getMetaDataMetsFile(digitalId, libraryId);
            if (inputMets.isPresent()) {
                final String TYPE_STRUCT_PHYSICAL = "physical";
                return inputMets.get().getStructMap().stream()
                                            .filter(smt -> StringUtils.equals(smt.getTYPE(), TYPE_STRUCT_PHYSICAL))
                                            .findAny();
            }
        }
        return Optional.empty();
    }

    /**
     * Métadonnées descriptives internes (Dublin core).
     *
     * @param recordDto
     * @return
     */
    private Optional<MdSecType> getDmdSecDc(final BibliographicRecordDcDTO recordDto) {
        if (recordDto == null) {
            return Optional.empty();
        }

        final MdSecType.MdWrap mdWrap = METS_FACTORY.createMdSecTypeMdWrap();
        mdWrap.setMIMETYPE("text/xml");
        mdWrap.setMDTYPE("DC");
        mdWrap.setLABEL("Dublin Code Metadata");

        final MdSecType mdSecType = METS_FACTORY.createMdSecType();
        mdSecType.setID(ID_DMD_SEC_DC);
        mdSecType.setMdWrap(mdWrap);

        final MdSecType.MdWrap.XmlData xmlData = METS_FACTORY.createMdSecTypeMdWrapXmlData();
        mdWrap.setXmlData(xmlData);

        addSimpleLiteral(xmlData, recordDto.getTitle(), DC_FACTORY::createTitle);
        addSimpleLiteral(xmlData, recordDto.getCreator(), DC_FACTORY::createCreator);
        addSimpleLiteral(xmlData, recordDto.getSubject(), DC_FACTORY::createSubject);
        addSimpleLiteral(xmlData, recordDto.getDescription(), DC_FACTORY::createDescription);
        addSimpleLiteral(xmlData, recordDto.getPublisher(), DC_FACTORY::createPublisher);
        addSimpleLiteral(xmlData, recordDto.getContributor(), DC_FACTORY::createContributor);
        addSimpleLiteral(xmlData, recordDto.getDate(), DC_FACTORY::createDate);
        addSimpleLiteral(xmlData, recordDto.getType(), DC_FACTORY::createType);
        addSimpleLiteral(xmlData, recordDto.getFormat(), DC_FACTORY::createFormat);
        addSimpleLiteral(xmlData, recordDto.getIdentifier(), DC_FACTORY::createIdentifier);
        addSimpleLiteral(xmlData, recordDto.getSource(), DC_FACTORY::createSource);
        addSimpleLiteral(xmlData, recordDto.getLanguage(), DC_FACTORY::createLanguage);
        addSimpleLiteral(xmlData, recordDto.getRelation(), DC_FACTORY::createRelation);
        addSimpleLiteral(xmlData, recordDto.getCoverage(), DC_FACTORY::createCoverage);
        addSimpleLiteral(xmlData, recordDto.getRights(), DC_FACTORY::createRights);
        // si rien n'est renseigné...
        if (xmlData.getAny().isEmpty()) {
            addSimpleLiteral(xmlData, Collections.singletonList(" "), DC_FACTORY::createTitle);
        }
        return Optional.of(mdSecType);
    }

    /**
     * Métadonnées descriptives internes (EAD).
     *
     * @param docUnit
     * @return
     */
    private Optional<MdSecType> getDmdSecEad(final DocUnit docUnit) {
        final File eadFile = exportEadService.retrieveEad(docUnit.getIdentifier());
        // Cette unité documentaire ne provient pas d'un import EAD
        if (eadFile == null) {
            return Optional.empty();
        }

        final MdSecType.MdWrap mdWrap = METS_FACTORY.createMdSecTypeMdWrap();
        mdWrap.setMIMETYPE("text/xml");
        mdWrap.setMDTYPE("EAD");
        mdWrap.setLABEL("EAD");

        final MdSecType mdSecType = METS_FACTORY.createMdSecType();
        mdSecType.setID(ID_DMD_SEC_EAD);
        mdSecType.setMdWrap(mdWrap);

        final MdSecType.MdWrap.XmlData xmlData = METS_FACTORY.createMdSecTypeMdWrapXmlData();
        mdWrap.setXmlData(xmlData);

        try {
            final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.ead.ObjectFactory.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final Ead ead = (Ead) unmarshaller.unmarshal(eadFile);
            xmlData.getAny().add(ead);

        } catch (final JAXBException e) {
            LOG.error(e.getMessage(), e);
        }
        return Optional.of(mdSecType);
    }

    /**
     * Inventaire des fichiers liés.
     *
     * @param listStoredFiles
     * @return
     */
    private FileSec getFileSec(final List<CheckSummedStoredFile> listStoredFiles, final String format) {
        final FileSec fileSec = METS_FACTORY.createMetsTypeFileSec();
        fileSec.setID("FILESEC1");
        final FileGrp fileGrp = METS_FACTORY.createMetsTypeFileSecFileGrp();
        fileGrp.setUSE("master");

        List<CheckSummedStoredFile> realListStoredFiles = listStoredFiles;
        if (!format.equals("PDF")) {
            // elimine le master pdf
            realListStoredFiles = listStoredFiles.stream()
                                                 .filter(cssf -> cssf.getStoredFile().getPage().getNumber() != null)
                                                 .collect(Collectors.toList());
        }

        final List<CheckSummedStoredFile> finalRealListStoredFiles = realListStoredFiles;
        IntStream.range(0, realListStoredFiles.size())
                 .forEach(idx -> {
                     final CheckSummedStoredFile cSStoredFile = finalRealListStoredFiles.get(idx);
                     fileGrp.getFile().add(buildFileGrp(cSStoredFile, idx));
                 });
        fileSec.getFileGrp().add(fileGrp);
        return fileSec;
    }

    private FileType buildFileGrp(final CheckSummedStoredFile cSStoredFile, final int idx) {
        final FileType file = METS_FACTORY.createFileType();
        file.setID("master_000" + idx); // todo completion de zeros avant l'index
        file.setCHECKSUMTYPE(cSStoredFile.getCheckSumType().getValue());
        file.setCHECKSUM(cSStoredFile.getCheckSum());
        file.setMIMETYPE(cSStoredFile.getStoredFile().getMimetype());
        final FLocat fileLoc = METS_FACTORY.createFileTypeFLocat();
        fileLoc.setLOCTYPE("URL");
        fileLoc.setHref(cSStoredFile.getStoredFile().getFilename());
        file.getFLocat().add(fileLoc);
        return file;
    }

    /**
     *
     * @param storedFile
     * @param sourceFile
     * @return
     * @throws IOException
     */
    public CheckSummedStoredFile getCheckSummedStoredFile(final StoredFile storedFile, final File sourceFile) throws IOException {
        final CheckSummedStoredFile checkSummed = new CheckSummedStoredFile();
        checkSummed.setStoredFile(storedFile);
        checkSummed.setCheckSumType(FileUtils.CheckSumType.MD5);
        try {
            checkSummed.setCheckSum(fr.progilone.pgcn.service.util.FileUtils.checkSum(sourceFile, FileUtils.CheckSumType.MD5));
        } catch (final NoSuchAlgorithmException e) {
                LOG.error(e.getMessage(), e);
                throw new PgcnUncheckedException(e);
        }
        return checkSummed;
    }

    /**
     * Génération de la structure (structure physique uniquement).
     *
     * @param mdSec
     * @param docUnit
     * @param listStoredFiles
     * @param fileSec
     * @return
     */
    private StructMapType getStructMap(final MdSecType mdSec, final DocUnit docUnit, final List<CheckSummedStoredFile> listStoredFiles, final FileSec fileSec) {

        final StructMapType structMap = METS_FACTORY.createStructMapType();
        final DigitalDocument dDoc = getDigitalDocFromDocUnit(docUnit).orElse(null);
        final String libraryId = docUnit.getLibrary().getIdentifier();
        // structure physique mets initial
        final Optional<StructMapType> initialStructMets = dDoc != null ? getPhysicalStruct(dDoc.getIdentifier(), libraryId) : Optional.empty();
        final List<DivType> listDivsToc = new ArrayList<>();
        // structure excel initial
        final Optional<List<Structures>> initialStructExcel = initialStructMets.isPresent()?
                                                                            Optional.empty():
                                                                            getExcelStructures(dDoc, docUnit.getLabel());

        final DivType div = METS_FACTORY.createDivType();
        structMap.setID("structmap_physical");
        structMap.setTYPE("physical");
        div.getDMDID().add(mdSec);

        if (initialStructMets.isPresent()) {
            listDivsToc.addAll(initialStructMets.get().getDiv().getDiv());
            div.setLABEL(initialStructMets.get().getDiv().getLABEL());
            div.setTYPE(initialStructMets.get().getDiv().getTYPE());
        } else {
            div.setTYPE(docUnit.getType());
            div.setLABEL(docUnit.getLabel());
        }

        final List<CheckSummedStoredFile> realListStoredFiles = listStoredFiles.stream()
                            .filter(cssf-> cssf.getStoredFile().getPage().getNumber() != null) // elimine le master pdf
                            .collect(Collectors.toList());
        final int firstIndex = realListStoredFiles.isEmpty() ? 0 : realListStoredFiles.get(0).getStoredFile().getPage().getNumber();  // demarre à 0 ou 1 ?

        fileSec.getFileGrp()
                .stream()
                .filter(fg -> StringUtils.equalsIgnoreCase("master", fg.getUSE()))
                .forEach(fg -> {

                    IntStream.range(0, realListStoredFiles.size())
                                                .forEach(idx -> {
                        final StoredFile sf = realListStoredFiles.get(idx).getStoredFile();
                        final int rectifiedIndex = idx+firstIndex;

                        final FileType ft = fg.getFile().get(idx);

                        final DivType subDiv = METS_FACTORY.createDivType();
                        final Fptr fptr = METS_FACTORY.createDivTypeFptr();
                        fptr.setFILEID(ft);
                        subDiv.getFptr().add(fptr);

                        subDiv.setORDER(BigInteger.valueOf(rectifiedIndex));
                        subDiv.setID("id_"+ rectifiedIndex);

                        if (StringUtils.isAllBlank(sf.getOrderToc(), sf.getTitleToc(), sf.getTypeToc())) {
                            // recup depuis le mets de la livraison s'il existe
                            if (initialStructMets.isPresent()) {
                                final DivType divToc = listDivsToc.get(idx);
                                subDiv.setLABEL(divToc.getLABEL());
                                subDiv.setORDERLABEL(divToc.getORDERLABEL());
                                subDiv.setTYPE(divToc.getTYPE());
                            } else if (initialStructExcel.isPresent()) {
                                 // recup depuis le xlsx de la livraison s'il existe
                                 if (initialStructExcel.get().size() > idx+2 &&
                                         initialStructExcel.get().get(idx+2) != null) {
                                     final Map<String, Object> excelToc = initialStructExcel.get().get(idx+2).getAdditionalProperties();
                                     subDiv.setLABEL(excelToc.get("label")!=null?excelToc.get("label").toString():"");
                                     subDiv.setORDERLABEL(excelToc.get("order")!=null?excelToc.get("order").toString():"");
                                     subDiv.setTYPE(excelToc.get("type")!=null?excelToc.get("type").toString():"");
                                 }
                            }
                        } else {
                            // et sinon Recup des données saisies au controle.
                            subDiv.setLABEL(sf.getTitleToc());
                            subDiv.setORDERLABEL(sf.getOrderToc());
                            subDiv.setTYPE(sf.getTypeToc());
                        }
                        div.getDiv().add(subDiv);
                    });
                });
        structMap.setDiv(div);
        return structMap;
    }

    /**
     * Recupere le doc digital depuis le docUnit.
     *
     * @param docUnit
     * @return
     */
    private Optional<DigitalDocument> getDigitalDocFromDocUnit(final DocUnit docUnit) {
        if (docUnit.getDigitalDocuments().isEmpty()) {
            return Optional.empty();
        }
        return docUnit.getDigitalDocuments().stream().findFirst();
    }

    /**
     * Recupere la structure (au sens manifest) de l'excel initial.
     *
     * @param dDoc
     * @param title
     * @return
     */
    private Optional<List<Structures>> getExcelStructures(final DigitalDocument dDoc, final String title) {

        Optional<List<Structures>> struct = Optional.empty();
 
        if (dDoc != null) {
            final String libraryId = dDoc.getDocUnit().getLibrary().getIdentifier();
            final Optional<File> excel = mdCheckService.getMetaDataExcelFile(dDoc.getDigitalId(), libraryId);
            if (excel.isPresent() && excel.get().canRead()) {
                try (final InputStream is = org.apache.commons.io.FileUtils.openInputStream(excel.get())) {
                    final List<Structures> structures =
                                    tocService.getTableOfContentExcel(dDoc.getIdentifier(), is, title);
                    if(CollectionUtils.isNotEmpty(structures)) {
                        struct = Optional.of(structures);
                    }

                } catch (final InvalidFormatException | IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return struct;
    }

    /**
     * Ajout d'une propriété Dublin Core.
     *
     * @param xmlData
     * @param dcProperties
     * @param getElement
     */
    private void addSimpleLiteral(final MdSecType.MdWrap.XmlData xmlData,
                                  final List<String> dcProperties,
                                  final Function<SimpleLiteral, JAXBElement<SimpleLiteral>> getElement) {
        if (!dcProperties.isEmpty()) {
            // #1916 : pas de concatenation des proprietes
            dcProperties.forEach(p -> {
                final SimpleLiteral value = DC_FACTORY.createSimpleLiteral();
                value.getContent().add(p);
                xmlData.getAny().add(getElement.apply(value));
            });
        }
    }

    /**
     * Personnalisation des préfixes des namespaces utilisés
     */
    private static class MetsNamespacePrefixMapper extends NamespacePrefixMapper {

        private final Map<String, String> namespaceMap = new HashMap<>();

        public MetsNamespacePrefixMapper() {
            namespaceMap.put("http://www.loc.gov/METS/", "mets");
            namespaceMap.put("http://purl.org/dc/elements/1.1/", "dc");
            namespaceMap.put("http://www.w3.org/1999/xlink", "xlink");
            namespaceMap.put("urn:isbn:1-931666-22-9", "ead");
        }

        @Override
        public String getPreferredPrefix(final String namespaceUri, final String suggestion, final boolean requirePrefix) {
            return namespaceMap.getOrDefault(namespaceUri, suggestion);
        }
    }
}
