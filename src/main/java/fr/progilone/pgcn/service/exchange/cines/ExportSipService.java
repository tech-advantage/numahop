package fr.progilone.pgcn.service.exchange.cines;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.dto.document.BibliographicRecordDcDTO;
import fr.progilone.pgcn.domain.jaxb.sip.DocDCType;
import fr.progilone.pgcn.domain.jaxb.sip.DocMetaType;
import fr.progilone.pgcn.domain.jaxb.sip.DocRelationType;
import fr.progilone.pgcn.domain.jaxb.sip.EmpreinteOri;
import fr.progilone.pgcn.domain.jaxb.sip.FichMetaType;
import fr.progilone.pgcn.domain.jaxb.sip.ObjectFactory;
import fr.progilone.pgcn.domain.jaxb.sip.PacType;
import fr.progilone.pgcn.domain.jaxb.sip.StringNotNULLtext;
import fr.progilone.pgcn.domain.jaxb.sip.StructureFichier;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.library.LibraryParameter;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines;
import fr.progilone.pgcn.domain.library.LibraryParameterValueCines.LibraryParameterValueCinesType;
import fr.progilone.pgcn.domain.storage.CheckSummedStoredFile;
import fr.progilone.pgcn.exception.ExportCinesException;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.service.document.common.LanguageCodeService;
import fr.progilone.pgcn.service.library.LibraryParameterService;
import fr.progilone.pgcn.service.storage.FileStorageManager;
import fr.progilone.pgcn.service.util.DateIso8601Util;
import fr.progilone.pgcn.service.util.FileUtils.CheckSumType;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * Service de génération du fichiers sip.xml
 *
 * @see <a href="http://www.cines.fr/pac/sip.xsd">xsd</a>
 *      <br/>
 *      Created by Sébastien on 28/12/2016.
 */
@Service
public class ExportSipService {

    private static final String FORMAT_XML = "XML";
    private static final String UTF_8 = "UTF-8";
    private static final String DESC_METS_XML = "DESC/mets.xml";
    private static final String HTTP_WWW_W3_ORG_2001_03_DATATYPES_DTD = "http://www.w3.org/2001/03/datatypes.dtd";
    private static final String HTTP_WWW_W3_ORG_2001_03_XML_SCHEMA_DTD = "http://www.w3.org/2001/03/XMLSchema.dtd";
    private static final String HTTP_WWW_W3_ORG_2001_03_XML_XSD = "http://www.w3.org/2001/03/xml.xsd";
    private static final String HTTP_DUBLINCORE_ORG_SCHEMAS_XMLS_SIMPLEDC20021212_XSD = "http://dublincore.org/schemas/xmls/simpledc20021212.xsd";
    private static final String STRUCTURE_FICHIER_TYPE_DTD = "DTD";
    private static final String STRUCTURE_FICHIER_TYPE_XSD = "XSD";
    private static final ObjectFactory SIP_FACTORY = new ObjectFactory();
    private static final String SIP_SCHEMA_LOCATION = "http://www.cines.fr/pac/sip http://www.cines.fr/pac/sip.xsd";
    // private static final String SIP_SCHEMA_LOCATION = "http://www.cines.fr/pactest/sip http://www.cines.fr/pactest/sip.xsd";

    public static final String AIP_XML_FILE = "aip.xml";

    private static final Logger LOG = LoggerFactory.getLogger(ExportSipService.class);

    // Validation locale
    @Value("${services.cines.xsd.sip}")
    private String sipSchema;

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    // Stockage des AIP
    @Value("${services.cines.aip}")
    private String aipDir;

    private final LanguageCodeService languageCodeService;
    private final CinesLanguageCodeService cinesLanguageCodeService;
    private final LibraryParameterService libraryParameterService;
    private final DocUnitRepository docUnitRepository;
    private final FileStorageManager fm;

    @Autowired
    public ExportSipService(final LanguageCodeService languageCodeService,
                            final CinesLanguageCodeService cinesLanguageCodeService,
                            final LibraryParameterService libraryParameterService,
                            final DocUnitRepository docUnitRepository,
                            final FileStorageManager fm) {
        this.languageCodeService = languageCodeService;
        this.cinesLanguageCodeService = cinesLanguageCodeService;
        this.libraryParameterService = libraryParameterService;
        this.docUnitRepository = docUnitRepository;
        this.fm = fm;
    }

    @PostConstruct
    public void initialize() {

        Arrays.asList(instanceLibraries).forEach(lib -> {
            try {
                FileUtils.forceMkdir(new File(aipDir, lib));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        });

    }

    public void writeMetadata(final OutputStream out,
                              final BibliographicRecordDcDTO metaDc,
                              final DocUnit docUnit,
                              final List<CheckSummedStoredFile> list,
                              final String checkSumMets,
                              final boolean reversion) throws JAXBException, MalformedURLException, SAXException, PgcnTechnicalException, ExportCinesException {
        final PacType pacType = SIP_FACTORY.createPacType();
        final DocDCType docDC = getDocDC(docUnit, metaDc);
        checkDocDC(docDC);
        pacType.setDocDC(docDC);
        pacType.setDocMeta(getDocMeta(docUnit, reversion));

        pacType.getFichMeta().add(getFichMetaMets(checkSumMets));
        pacType.getFichMeta().addAll(getFichMeta(list));

        final JAXBElement<PacType> pac = SIP_FACTORY.createPac(pacType);

        // Écriture du XML dans le flux de sortie
        final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        final Marshaller m = context.createMarshaller();
        // Validation
        final SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        m.setSchema(sf.newSchema(new File(sipSchema)));
        m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SIP_SCHEMA_LOCATION);
        m.marshal(pac, out);
    }

    private void checkDocDC(final DocDCType docDc) throws ExportCinesException {
        // check mandatory fields : title, subject,
        if (docDc.getTitle().isEmpty()) {
            throw new ExportCinesException("Champ \"Title\" manquant");
        }
        if (docDc.getCreator().isEmpty()) {
            throw new ExportCinesException("Champ \"Creator\" manquant");
        }
        if (docDc.getSubject().isEmpty()) {
            throw new ExportCinesException("Champ \"Subject\" manquant");
        }
        if (docDc.getDescription().isEmpty()) {
            throw new ExportCinesException("Champ \"Description\" manquant");
        }
        if (docDc.getLanguage().isEmpty()) {
            throw new ExportCinesException("Champ \"Language\" manquant");
        }
        // check language found in table.
        for (final String language : docDc.getLanguage()) {
            if (!languageCodeService.checkCinesLangCodeExists(language)) {
                throw new ExportCinesException("Champ \"Language\" incorrect");
            }
        }
        if (docDc.getPublisher().isEmpty()) {
            throw new ExportCinesException("Champ \"Publisher\" manquant");
        }
        if (docDc.getDate() == null || docDc.getDate().isEmpty()) {
            docDc.setDate("s.d.");
        }
        if (docDc.getType().isEmpty()) {
            throw new ExportCinesException("Champ \"Type\" manquant");
        }
        if (docDc.getFormat().isEmpty()) {
            throw new ExportCinesException("Champ \"Format\" manquant");
        }
        if (docDc.getRights().isEmpty()) {
            throw new ExportCinesException("Champ \"Rights\" manquant");
        }
    }

    /**
     * Initialisation d'un {@link DocDCType} à partir d'un BibliographicRecordDcDTO
     *
     * @param metaDc
     * @return
     */
    private DocDCType getDocDC(final DocUnit docUnit, final BibliographicRecordDcDTO metaDc) {
        final DocDCType docDCType = SIP_FACTORY.createDocDCType();

        // custom properties
        final BibliographicRecord notice = docUnit.getRecords().iterator().next();
        List<DocProperty> customProps = new ArrayList<>();
        if (notice != null) {
            // il faut reordonner les props custom selon le rank
            customProps = notice.getProperties()
                                .stream()
                                .filter(p -> p.getType().getSuperType() == DocPropertyType.DocPropertySuperType.CUSTOM_CINES || p.getType().getSuperType()
                                                                                                                                == DocPropertyType.DocPropertySuperType.CUSTOM)
                                .sorted(Comparator.comparing(DocProperty::getRank))
                                .collect(Collectors.toCollection(ArrayList::new));
        }

        final String defaultLang = getDefaultLanguageByDocUnit(docUnit);
        // récupération du langage
        final String languageIso;
        if (metaDc.getLanguage().isEmpty() || StringUtils.isBlank(metaDc.getLanguage().get(0))) {
            languageIso = defaultLang;
        } else {
            // transpose le language par celui indiqué dans la conf cines - "Codes lang à transposer" s'il y a lieu...
            languageIso = cinesLanguageCodeService.getCinesLanguageByLangDC(metaDc.getLanguage().get(0));
        }

        // Proprietes avec plusieurs languages possibles
        setPropertyDocType(docDCType.getTitle(), metaDc.getTitle(), languageIso, customProps, "title_language");
        setPropertyDocType(docDCType.getSubject(), metaDc.getSubject(), languageIso, customProps, "subject_language");
        setPropertyDocType(docDCType.getDescription(), metaDc.getDescription(), languageIso, customProps, "description_language");
        setPropertyDocType(docDCType.getType(), metaDc.getType(), languageIso, customProps, "type_language");
        setPropertyDocType(docDCType.getFormat(), metaDc.getFormat(), languageIso, customProps, "format_language");
        setPropertyDocType(docDCType.getRights(), metaDc.getRights(), languageIso, customProps, "rights_language");
        setPropertyDocType(docDCType.getSource(), metaDc.getSource(), languageIso, customProps, "source_language");
        setPropertyDocType(docDCType.getRelation(), metaDc.getRelation(), languageIso, customProps, "relation_language");
        setPropertyDocType(docDCType.getCoverage(), metaDc.getCoverage(), languageIso, customProps, "coverage_language");

        // Autres proprietes
        for (final String s : metaDc.getCreator()) {
            addString(docDCType.getCreator(), s);
        }
        for (final String s : metaDc.getPublisher()) {
            addString(docDCType.getPublisher(), s);
        }
        for (final String s : metaDc.getContributor()) {
            addString(docDCType.getContributor(), s);
        }
        for (final String s : metaDc.getDate()) {
            setDateValue(docDCType, s);
        }
        for (final String s : metaDc.getLanguage()) {
            addLanguageString(docDCType.getLanguage(), s);
        }

        setDefaultValues(docUnit, docDCType, defaultLang);
        return docDCType;
    }

    /**
     *
     * @param docDCTypeValues
     *            liste de props à valoriser
     * @param metaDcValues
     *            liste de props sources
     * @param languageIso
     * @param customProps
     *            liste des proprietes personnalisees
     * @param propFilter
     */
    private void setPropertyDocType(final List<StringNotNULLtext> docDCTypeValues,
                                    final List<String> metaDcValues,
                                    final String languageIso,
                                    final List<DocProperty> customProps,
                                    final String propFilter) {
        if (!metaDcValues.isEmpty()) {
            final List<DocProperty> customLang = customProps.stream()
                                                            .filter(p -> propFilter.equals(p.getType().getIdentifier()))
                                                            .sorted(Comparator.comparing(DocProperty::getRank))
                                                            .collect(Collectors.toList());
            int idx = 0;
            for (final String s : metaDcValues) {
                if (customLang != null && customLang.size() > idx) {
                    final DocProperty p = customLang.get(idx);
                    addStringNotNull(docDCTypeValues, s, p.getValue());
                } else {
                    addStringNotNull(docDCTypeValues, s, languageIso);
                }
                idx++;
            }
        }
    }

    /**
     *
     * @param docUnit
     * @return
     */
    private String getDefaultLanguageByDocUnit(final DocUnit docUnit) {

        // Vérification présence configuration
        final LibraryParameter libParam = libraryParameterService.findCinesParameterForLibrary(docUnit.getLibrary());
        return getDefaultLanguage(libParam);
    }

    /**
     *
     * @param libParam
     * @return
     */
    private String getDefaultLanguage(final LibraryParameter libParam) {
        String defaultLang = "fra";
        if (libParam != null) {
            final LibraryParameterValueCines cinesParam = getLibraryParameterValueForType(libParam, LibraryParameterValueCinesType.LANGUAGE_DEFAULT_VALUE);
            if (cinesParam != null) {
                defaultLang = cinesParam.getValue();
            }
        }
        return defaultLang;
    }

    /**
     * Initialisation d'un {@link DocMetaType} à partir d'une unité documentaire
     *
     * @param docUnit
     * @return
     * @throws PgcnTechnicalException
     */
    private DocMetaType getDocMeta(final DocUnit docUnit, final boolean reversion) throws PgcnTechnicalException {
        final DocMetaType docMetaType = SIP_FACTORY.createDocMetaType();

        if (!docUnit.getRecords().isEmpty()) {

            handleServiceVersant(docMetaType, docUnit);
            docMetaType.setIdentifiantDocProducteur(docUnit.getPgcnId());

            if (docUnit.getPlanClassementPAC() != null) {
                docMetaType.setPlanClassement(createStringNotNull(docUnit.getPlanClassementPAC().getName(), getDefaultLanguageByDocUnit(docUnit)));
            }

            final boolean firstTime = docUnit.getCinesVersion() == null;
            if (firstTime) { // jamais archivé au cines
                docMetaType.setVersion("1.0");

            } else { // déjà connu du cines
                docMetaType.setVersion("1." + docUnit.getCinesVersion());
                if (docUnit.getCinesVersion() > 0) {
                    docMetaType.setVersionPrecedente("1." + (docUnit.getCinesVersion() - 1));
                }

                // Recup identifiantdocPac depuis le fichier aip.
                final String aipIdentifier = getAipIdentifier(docUnit);
                final String idDocProd = getCoteRecord(docUnit);

                if (reversion) {
                    docMetaType.getDocRelation().add(getDocRelation("version", aipIdentifier, "PAC"));
                    docMetaType.getDocRelation().add(getDocRelation("version", idDocProd, "Producteur"));

                } else {
                    docMetaType.getDocRelation().add(getDocRelation("maj", aipIdentifier, "PAC"));
                    docMetaType.getDocRelation().add(getDocRelation("maj", idDocProd, "Producteur"));
                }
                // Filiation si parent present.
                if (docUnit.getParent() != null) {
                    final DocUnit parentDoc = docUnitRepository.findOneByIdentifierWithRecords(docUnit.getParent().getIdentifier());
                    final String aipParentIdentifier = getAipIdentifier(parentDoc);
                    if (aipParentIdentifier != null) {
                        docMetaType.getDocRelation().add(getDocRelation("filiation", aipParentIdentifier, "PAC"));
                        docMetaType.getDocRelation().add(getDocRelation("filiation", getCoteRecord(parentDoc), "Producteur"));
                    }
                }
            }
        }

        return docMetaType;
    }

    /**
     * Recupere l'identifiantDocPAC attribué par le Cines d'un doc déjà archivé.
     *
     * @param docUnit
     * @return
     */
    private String getAipIdentifier(final DocUnit docUnit) {

        final File aipFile = retrieveAip(docUnit);
        // pas trouvé => aie !
        if (aipFile == null) {
            return null;
        }

        String aipIdentifier;
        try {
            final JAXBContext context = JAXBContext.newInstance(fr.progilone.pgcn.domain.jaxb.aip.ObjectFactory.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final fr.progilone.pgcn.domain.jaxb.aip.PacType pac = (fr.progilone.pgcn.domain.jaxb.aip.PacType) unmarshaller.unmarshal(aipFile);

            if (pac != null && pac.getDocMeta() != null) {
                aipIdentifier = pac.getDocMeta().getIdentifiantDocPac();
            } else {
                aipIdentifier = null;
            }

        } catch (final JAXBException e) {
            LOG.error(e.getMessage(), e);
            aipIdentifier = null;
        }

        return aipIdentifier;
    }

    /**
     * Récupération du fichier aip.xml stocké
     * pour un docUnit donné
     *
     * @param docUnit
     * @return
     */
    public File retrieveAip(final DocUnit docUnit) {
        if (docUnit != null) {
            final Path root = Paths.get(aipDir, docUnit.getLibrary().getIdentifier(), docUnit.getIdentifier());
            if (root != null) {
                return fm.retrieveFile(root.toFile(), AIP_XML_FILE);
            }
        }
        return null;
    }

    /**
     *
     * @param typeRelation
     * @param idRelation
     * @return
     */
    private DocRelationType getDocRelation(final String typeRelation, final String idRelation, final String srcRelation) {
        final DocRelationType docRelType = SIP_FACTORY.createDocRelationType();
        docRelType.setTypeRelation(typeRelation);
        docRelType.setSourceRelation(srcRelation);
        docRelType.setIdentifiantSourceRelation(idRelation);
        return docRelType;
    }

    private String getCoteRecord(final DocUnit docUnit) {
        final BibliographicRecord record = docUnit.getRecords().iterator().next();
        // recuperation de la cote (prop. identifier de la notice).
        final Optional<DocProperty> identifierProp = record.getProperties().stream().filter(property -> {
            final DocPropertyType type = property.getType();
            if (type.getSuperType() == DocPropertyType.DocPropertySuperType.DC && "identifier".equals(type.getIdentifier())) {
                return true;
            }
            return false;
        }).findFirst();

        return identifierProp.isPresent() ? identifierProp.get().getValue()
                                          : docUnit.getIdentifier();
    }

    /**
     * Vérification du service versant
     *
     * @param docMetaType
     * @param docUnit
     * @throws PgcnTechnicalException
     */
    private void handleServiceVersant(final DocMetaType docMetaType, final DocUnit docUnit) throws PgcnTechnicalException {
        final String serviceVersant = getServiceVersantFromDocUnit(docUnit);
        if (StringUtils.isBlank(serviceVersant)) {
            throw new PgcnTechnicalException("L'identifiant du service versant CINES n'est pas renseigné pour la bibliothèque " + docUnit.getLibrary().getName());
        }
        docMetaType.setServiceVersant(serviceVersant);
    }

    /**
     * Initialisation de la liste de {@link FichMetaType} à partir d'une liste optimisée
     * contenant les storedFile du DocUnit et les checksum associés
     *
     * @param list
     * @return
     */
    private List<FichMetaType> getFichMeta(final List<CheckSummedStoredFile> list) {
        final List<FichMetaType> listFichMeta = new ArrayList<>();
        list.forEach(checkSummedStoredFile -> {
            final FichMetaType fichMetaType = SIP_FACTORY.createFichMetaType();
            fichMetaType.setFormatFichier(SipUtils.convertMimeTypeToSipFormat(checkSummedStoredFile.getStoredFile().getMimetype()));
            fichMetaType.setNomFichier(checkSummedStoredFile.getStoredFile().getFilename());
            // Uniquement MD5 (extensible à d'autre si le besoin s'en fait sentir
            final EmpreinteOri empreinte = SIP_FACTORY.createEmpreinteOri();
            empreinte.setType(checkSummedStoredFile.getCheckSumType().getValue());
            empreinte.setValue(checkSummedStoredFile.getCheckSum());
            fichMetaType.setEmpreinteOri(empreinte);
            listFichMeta.add(fichMetaType);
        });
        return listFichMeta;
    }

    /**
     * Initialisation de {@link FichMetaType} du mets.xml
     *
     * @param checkSumMets
     * @return
     */
    private FichMetaType getFichMetaMets(final String checkSumMets) {
        final FichMetaType fichMetaType = SIP_FACTORY.createFichMetaType();
        fichMetaType.setEncodage(UTF_8);
        fichMetaType.setFormatFichier(FORMAT_XML);
        fichMetaType.setNomFichier(DESC_METS_XML);
        // Uniquement MD5 (extensible à d'autre si le besoin s'en fait sentir
        final EmpreinteOri empreinte = SIP_FACTORY.createEmpreinteOri();
        empreinte.setType(CheckSumType.MD5.getValue());
        empreinte.setValue(checkSumMets);
        fichMetaType.setEmpreinteOri(empreinte);
        // Nécessaire ?
        final StructureFichier xsd = SIP_FACTORY.createStructureFichier();
        xsd.setType(STRUCTURE_FICHIER_TYPE_XSD);
        xsd.setValue(HTTP_DUBLINCORE_ORG_SCHEMAS_XMLS_SIMPLEDC20021212_XSD);
        final StructureFichier xsd2 = SIP_FACTORY.createStructureFichier();
        xsd2.setType(STRUCTURE_FICHIER_TYPE_XSD);
        xsd2.setValue(HTTP_WWW_W3_ORG_2001_03_XML_XSD);
        final StructureFichier dtd = SIP_FACTORY.createStructureFichier();
        dtd.setType(STRUCTURE_FICHIER_TYPE_DTD);
        dtd.setValue(HTTP_WWW_W3_ORG_2001_03_XML_SCHEMA_DTD);
        final StructureFichier dtd2 = SIP_FACTORY.createStructureFichier();
        dtd2.setType(STRUCTURE_FICHIER_TYPE_DTD);
        dtd2.setValue(HTTP_WWW_W3_ORG_2001_03_DATATYPES_DTD);
        fichMetaType.getStructureFichier().add(xsd);
        fichMetaType.getStructureFichier().add(xsd2);
        fichMetaType.getStructureFichier().add(dtd);
        fichMetaType.getStructureFichier().add(dtd2);
        return fichMetaType;
    }

    /**
     * Ajout d'un {@link String} à une liste de valeurs
     * à partir d'une DocProperty
     *
     * @param texts
     * @param docProperty
     * @return
     */
    private boolean addString(final List<String> texts, final DocProperty docProperty) {
        return addString(texts, docProperty.getValue());
    }

    /**
     * Ajout d'un {@link String} à une liste de valeurs
     * à partir d'une DocProperty de type language
     * Permet de filter les iso 639-2 B en iso 639-3 (ou ISO 639 2 T)
     *
     * @param texts
     * @param docProperty
     * @return
     */
    private boolean addLanguageString(final List<String> texts, final DocProperty docProperty) {
        return addString(texts, languageCodeService.getIso6393TForLanguage(docProperty.getValue()));
    }

    private boolean addLanguageString(final List<String> texts, final String property) {
        return addString(texts, languageCodeService.getIso6393TForLanguage(property));
    }

    /**
     * Ajout d'un {@link String} à une liste de valeurs
     *
     * @param texts
     * @param value
     * @return
     */
    private boolean addString(final List<String> texts, final String value) {
        return texts.add(value);
    }

    /**
     * Ajout d'un {@link StringNotNULLtext} à une liste de valeurs
     *
     * @param texts
     * @param docProperty
     * @param defaultLanguage
     * @return
     */
    private boolean addStringNotNull(final List<StringNotNULLtext> texts, final DocProperty docProperty, final String defaultLanguage) {
        return texts.add(getStringNotNULLtext(docProperty, defaultLanguage));
    }

    /**
     * Ajout d'un {@link StringNotNULLtext} à une liste de valeurs
     *
     * @param texts
     * @param property
     * @param defaultLanguage
     * @return
     */
    private boolean addStringNotNull(final List<StringNotNULLtext> texts, final String property, final String defaultLanguage) {
        return texts.add(createStringNotNull(property, defaultLanguage));
    }

    /**
     * Initialisation d'un {@link StringNotNULLtext} à partir d'un {@link DocProperty}
     *
     * @param docProperty
     * @param defaultLanguage
     * @return
     */
    private StringNotNULLtext getStringNotNULLtext(final DocProperty docProperty, final String defaultLanguage) {
        final StringNotNULLtext value = createStringNotNull(docProperty.getValue(), defaultLanguage);
        if (docProperty.getLanguage() != null && !docProperty.getLanguage().isEmpty()) {
            value.setLanguage(docProperty.getLanguage());
        }
        return value;
    }

    private StringNotNULLtext createStringNotNull(final String value, final String defaultLanguage) {
        final StringNotNULLtext string = new StringNotNULLtext();
        string.setValue(value);
        string.setLanguage(defaultLanguage);
        return string;
    }

    private void setDateValue(final DocDCType docDCType, final DocProperty docProperty) {
        docDCType.setDate(DateIso8601Util.importedDateToIso8601(docProperty.getValue()));
    }

    private void setDateValue(final DocDCType docDCType, final String property) {
        docDCType.setDate(DateIso8601Util.importedDateToIso8601(property));
    }

    /**
     * Mise en place des valeurs par défaut obligatoires paramétrées.
     * Voir {@link LibraryParameterValueCines}
     *
     * @param docUnit
     * @param docDCType
     * @param defaultLanguage
     */
    private void setDefaultValues(final DocUnit docUnit, final DocDCType docDCType, final String defaultLanguage) {
        // Vérification de la présence de configuration
        final LibraryParameter libraryParameter = libraryParameterService.findCinesParameterForLibrary(docUnit.getLibrary());
        // Si elle est présente alors elle est initialisée (entityGraph)
        if (libraryParameter != null) {

            // StringNotNull : creator, publisher,
            handleDefaultValueForType(docDCType.getCreator(), libraryParameter, LibraryParameterValueCinesType.CREATOR_DEFAULT_VALUE);
            handleDefaultValueForType(docDCType.getPublisher(), libraryParameter, LibraryParameterValueCinesType.PUBLISHER_DEFAULT_VALUE);

            // StringNotNullText: Subject, title, description, type, format, rights
            handleDefaultNotNullValueForType(docDCType.getSubject(), libraryParameter, LibraryParameterValueCinesType.SUBJECT_DEFAULT_VALUE, defaultLanguage);
            handleDefaultNotNullValueForType(docDCType.getTitle(), libraryParameter, LibraryParameterValueCinesType.TITLE_DEFAULT_VALUE, defaultLanguage);
            handleDefaultNotNullValueForType(docDCType.getDescription(), libraryParameter, LibraryParameterValueCinesType.DESCRIPTION_DEFAULT_VALUE, defaultLanguage);
            handleDefaultNotNullValueForType(docDCType.getType(), libraryParameter, LibraryParameterValueCinesType.TYPE_DEFAULT_VALUE, defaultLanguage);
            handleDefaultNotNullValueForType(docDCType.getFormat(), libraryParameter, LibraryParameterValueCinesType.FORMAT_DEFAULT_VALUE, defaultLanguage);
            handleDefaultNotNullValueForType(docDCType.getRights(), libraryParameter, LibraryParameterValueCinesType.RIGHTS_DEFAULT_VALUE, defaultLanguage);
            if (StringUtils.isBlank(docDCType.getDate())) {
                docDCType.setDate("s.d.");
            }
        }
    }

    public void handleDefaultValueForType(final List<String> dcList, final LibraryParameter libParam, final LibraryParameterValueCinesType type) {
        final LibraryParameterValueCines cinesParameter = getLibraryParameterValueForType(libParam, type);
        if (dcList.isEmpty() && cinesParameter != null) {
            addString(dcList, cinesParameter.getValue());
        }
    }

    private void handleDefaultNotNullValueForType(final List<StringNotNULLtext> dcList,
                                                  final LibraryParameter libParam,
                                                  final LibraryParameterValueCinesType type,
                                                  final String defaultLanguage) {
        final LibraryParameterValueCines cinesParameter = getLibraryParameterValueForType(libParam, type);
        if (dcList.isEmpty() && cinesParameter != null) {
            dcList.add(createStringNotNull(cinesParameter.getValue(), defaultLanguage));
        }
    }

    private LibraryParameterValueCines getLibraryParameterValueForType(final LibraryParameter libParam, final LibraryParameterValueCinesType type) {
        return libParam.getValues()
                       .stream()
                       .map(LibraryParameterValueCines.class::cast)
                       .filter(param -> param.getType() == type)
                       .filter(param -> param.getValue() != null)
                       .findFirst()
                       .orElse(null);
    }

    private String getServiceVersantFromDocUnit(final DocUnit doc) {
        final Library lib = doc.getLibrary();
        if (!Hibernate.isInitialized(lib)) {
            Hibernate.initialize(lib);
        }
        return lib.getCinesService();
    }
}
