//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:53:40 AM CET
//

package fr.progilone.pgcn.domain.jaxb.sip;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.progilone.pgcn.domain.jaxb.sip package.
 * <p>
 * An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups. Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Pac_QNAME = new QName("http://www.cines.fr/pac/sip", "pac");
    private final static QName _DocDC_QNAME = new QName("http://www.cines.fr/pac/sip", "DocDC");
    private final static QName _Title_QNAME = new QName("http://www.cines.fr/pac/sip", "title");
    private final static QName _Creator_QNAME = new QName("http://www.cines.fr/pac/sip", "creator");
    private final static QName _Subject_QNAME = new QName("http://www.cines.fr/pac/sip", "subject");
    private final static QName _Description_QNAME = new QName("http://www.cines.fr/pac/sip", "description");
    private final static QName _Publisher_QNAME = new QName("http://www.cines.fr/pac/sip", "publisher");
    private final static QName _Contributor_QNAME = new QName("http://www.cines.fr/pac/sip", "contributor");
    private final static QName _Date_QNAME = new QName("http://www.cines.fr/pac/sip", "date");
    private final static QName _Type_QNAME = new QName("http://www.cines.fr/pac/sip", "type");
    private final static QName _Format_QNAME = new QName("http://www.cines.fr/pac/sip", "format");
    private final static QName _Source_QNAME = new QName("http://www.cines.fr/pac/sip", "source");
    private final static QName _Language_QNAME = new QName("http://www.cines.fr/pac/sip", "language");
    private final static QName _Relation_QNAME = new QName("http://www.cines.fr/pac/sip", "relation");
    private final static QName _Coverage_QNAME = new QName("http://www.cines.fr/pac/sip", "coverage");
    private final static QName _Rights_QNAME = new QName("http://www.cines.fr/pac/sip", "rights");
    private final static QName _DocMeta_QNAME = new QName("http://www.cines.fr/pac/sip", "DocMeta");
    private final static QName _IdentifiantDocProducteur_QNAME = new QName("http://www.cines.fr/pac/sip", "identifiantDocProducteur");
    private final static QName _DocRelation_QNAME = new QName("http://www.cines.fr/pac/sip", "docRelation");
    private final static QName _TypeRelation_QNAME = new QName("http://www.cines.fr/pac/sip", "typeRelation");
    private final static QName _SourceRelation_QNAME = new QName("http://www.cines.fr/pac/sip", "sourceRelation");
    private final static QName _IdentifiantSourceRelation_QNAME = new QName("http://www.cines.fr/pac/sip", "identifiantSourceRelation");
    private final static QName _Evaluation_QNAME = new QName("http://www.cines.fr/pac/sip", "evaluation");
    private final static QName _DUA_QNAME = new QName("http://www.cines.fr/pac/sip", "DUA");
    private final static QName _Traitement_QNAME = new QName("http://www.cines.fr/pac/sip", "traitement");
    private final static QName _DateDebut_QNAME = new QName("http://www.cines.fr/pac/sip", "dateDebut");
    private final static QName _Communicabilite_QNAME = new QName("http://www.cines.fr/pac/sip", "communicabilite");
    private final static QName _Code_QNAME = new QName("http://www.cines.fr/pac/sip", "code");
    private final static QName _NoteDocument_QNAME = new QName("http://www.cines.fr/pac/sip", "noteDocument");
    private final static QName _ServiceVersant_QNAME = new QName("http://www.cines.fr/pac/sip", "serviceVersant");
    private final static QName _PlanClassement_QNAME = new QName("http://www.cines.fr/pac/sip", "planClassement");
    private final static QName _StructureDocument_QNAME = new QName("http://www.cines.fr/pac/sip", "structureDocument");
    private final static QName _Version_QNAME = new QName("http://www.cines.fr/pac/sip", "version");
    private final static QName _VersionPrecedente_QNAME = new QName("http://www.cines.fr/pac/sip", "versionPrecedente");
    private final static QName _FichMeta_QNAME = new QName("http://www.cines.fr/pac/sip", "FichMeta");
    private final static QName _Compression_QNAME = new QName("http://www.cines.fr/pac/sip", "compression");
    private final static QName _Encodage_QNAME = new QName("http://www.cines.fr/pac/sip", "encodage");
    private final static QName _FormatFichier_QNAME = new QName("http://www.cines.fr/pac/sip", "formatFichier");
    private final static QName _NomFichier_QNAME = new QName("http://www.cines.fr/pac/sip", "nomFichier");
    private final static QName _NoteFichier_QNAME = new QName("http://www.cines.fr/pac/sip", "noteFichier");
    // private final static QName _LanguageCode_QNAME = new QName("urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", "LanguageCode");
    // private final static QName _AccessRestrictionCode_QNAME = new
    // QName("urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", "AccessRestrictionCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.progilone.pgcn.domain.jaxb.sip
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PacType }
     *
     */
    public PacType createPacType() {
        return new PacType();
    }

    /**
     * Create an instance of {@link DocDCType }
     *
     */
    public DocDCType createDocDCType() {
        return new DocDCType();
    }

    /**
     * Create an instance of {@link StringNotNULLtext }
     *
     */
    public StringNotNULLtext createStringNotNULLtext() {
        return new StringNotNULLtext();
    }

    /**
     * Create an instance of {@link DocMetaType }
     *
     */
    public DocMetaType createDocMetaType() {
        return new DocMetaType();
    }

    /**
     * Create an instance of {@link DocRelationType }
     *
     */
    public DocRelationType createDocRelationType() {
        return new DocRelationType();
    }

    /**
     * Create an instance of {@link EvaluationType }
     *
     */
    public EvaluationType createEvaluationType() {
        return new EvaluationType();
    }

    /**
     * Create an instance of {@link TraitementType }
     *
     */
    public TraitementType createTraitementType() {
        return new TraitementType();
    }

    /**
     * Create an instance of {@link CommunicabiliteType }
     *
     */
    public CommunicabiliteType createCommunicabiliteType() {
        return new CommunicabiliteType();
    }

    /**
     * Create an instance of {@link CodeType }
     *
     */
    public CodeType createCodeType() {
        return new CodeType();
    }

    /**
     * Create an instance of {@link FichMetaType }
     *
     */
    public FichMetaType createFichMetaType() {
        return new FichMetaType();
    }

    /**
     * Create an instance of {@link EmpreinteOri }
     *
     */
    public EmpreinteOri createEmpreinteOri() {
        return new EmpreinteOri();
    }

    /**
     * Create an instance of {@link StructureFichier }
     *
     */
    public StructureFichier createStructureFichier() {
        return new StructureFichier();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PacType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "pac")
    public JAXBElement<PacType> createPac(PacType value) {
        return new JAXBElement<>(_Pac_QNAME, PacType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocDCType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "DocDC")
    public JAXBElement<DocDCType> createDocDC(DocDCType value) {
        return new JAXBElement<>(_DocDC_QNAME, DocDCType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "title")
    public JAXBElement<StringNotNULLtext> createTitle(StringNotNULLtext value) {
        return new JAXBElement<>(_Title_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "creator")
    public JAXBElement<String> createCreator(String value) {
        return new JAXBElement<>(_Creator_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "subject")
    public JAXBElement<StringNotNULLtext> createSubject(StringNotNULLtext value) {
        return new JAXBElement<>(_Subject_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "description")
    public JAXBElement<StringNotNULLtext> createDescription(StringNotNULLtext value) {
        return new JAXBElement<>(_Description_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "publisher")
    public JAXBElement<String> createPublisher(String value) {
        return new JAXBElement<>(_Publisher_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "contributor")
    public JAXBElement<String> createContributor(String value) {
        return new JAXBElement<>(_Contributor_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "date")
    public JAXBElement<String> createDate(String value) {
        return new JAXBElement<>(_Date_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "type")
    public JAXBElement<StringNotNULLtext> createType(StringNotNULLtext value) {
        return new JAXBElement<>(_Type_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "format")
    public JAXBElement<StringNotNULLtext> createFormat(StringNotNULLtext value) {
        return new JAXBElement<>(_Format_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "source")
    public JAXBElement<StringNotNULLtext> createSource(StringNotNULLtext value) {
        return new JAXBElement<>(_Source_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "language")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguage(String value) {
        return new JAXBElement<>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "relation")
    public JAXBElement<StringNotNULLtext> createRelation(StringNotNULLtext value) {
        return new JAXBElement<>(_Relation_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "coverage")
    public JAXBElement<StringNotNULLtext> createCoverage(StringNotNULLtext value) {
        return new JAXBElement<>(_Coverage_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "rights")
    public JAXBElement<StringNotNULLtext> createRights(StringNotNULLtext value) {
        return new JAXBElement<>(_Rights_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocMetaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "DocMeta")
    public JAXBElement<DocMetaType> createDocMeta(DocMetaType value) {
        return new JAXBElement<>(_DocMeta_QNAME, DocMetaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "identifiantDocProducteur")
    public JAXBElement<String> createIdentifiantDocProducteur(String value) {
        return new JAXBElement<>(_IdentifiantDocProducteur_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocRelationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "docRelation")
    public JAXBElement<DocRelationType> createDocRelation(DocRelationType value) {
        return new JAXBElement<>(_DocRelation_QNAME, DocRelationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "typeRelation")
    public JAXBElement<String> createTypeRelation(String value) {
        return new JAXBElement<>(_TypeRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "sourceRelation")
    public JAXBElement<String> createSourceRelation(String value) {
        return new JAXBElement<>(_SourceRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "identifiantSourceRelation")
    public JAXBElement<String> createIdentifiantSourceRelation(String value) {
        return new JAXBElement<>(_IdentifiantSourceRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "evaluation")
    public JAXBElement<EvaluationType> createEvaluation(EvaluationType value) {
        return new JAXBElement<>(_Evaluation_QNAME, EvaluationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "DUA")
    public JAXBElement<Duration> createDUA(Duration value) {
        return new JAXBElement<>(_DUA_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraitementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "traitement")
    public JAXBElement<TraitementType> createTraitement(TraitementType value) {
        return new JAXBElement<>(_Traitement_QNAME, TraitementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalDateTime }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "dateDebut")
    public JAXBElement<LocalDateTime> createDateDebut(LocalDateTime value) {
        return new JAXBElement<>(_DateDebut_QNAME, LocalDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommunicabiliteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "communicabilite")
    public JAXBElement<CommunicabiliteType> createCommunicabilite(CommunicabiliteType value) {
        return new JAXBElement<>(_Communicabilite_QNAME, CommunicabiliteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "code")
    public JAXBElement<CodeType> createCode(CodeType value) {
        return new JAXBElement<>(_Code_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "noteDocument")
    public JAXBElement<StringNotNULLtext> createNoteDocument(StringNotNULLtext value) {
        return new JAXBElement<>(_NoteDocument_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "serviceVersant")
    public JAXBElement<String> createServiceVersant(String value) {
        return new JAXBElement<>(_ServiceVersant_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "planClassement")
    public JAXBElement<StringNotNULLtext> createPlanClassement(StringNotNULLtext value) {
        return new JAXBElement<>(_PlanClassement_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "structureDocument")
    public JAXBElement<String> createStructureDocument(String value) {
        return new JAXBElement<>(_StructureDocument_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "version")
    public JAXBElement<String> createVersion(String value) {
        return new JAXBElement<>(_Version_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "versionPrecedente")
    public JAXBElement<String> createVersionPrecedente(String value) {
        return new JAXBElement<>(_VersionPrecedente_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FichMetaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "FichMeta")
    public JAXBElement<FichMetaType> createFichMeta(FichMetaType value) {
        return new JAXBElement<>(_FichMeta_QNAME, FichMetaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "compression")
    public JAXBElement<String> createCompression(String value) {
        return new JAXBElement<>(_Compression_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "encodage")
    public JAXBElement<String> createEncodage(String value) {
        return new JAXBElement<>(_Encodage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "formatFichier")
    public JAXBElement<String> createFormatFichier(String value) {
        return new JAXBElement<>(_FormatFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "nomFichier")
    public JAXBElement<String> createNomFichier(String value) {
        return new JAXBElement<>(_NomFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/sip", name = "noteFichier")
    public JAXBElement<StringNotNULLtext> createNoteFichier(StringNotNULLtext value) {
        return new JAXBElement<>(_NoteFichier_QNAME, StringNotNULLtext.class, null, value);
    }

    // /**
    // * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
    // *
    // */
    // @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", name = "LanguageCode")
    // @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    // public JAXBElement<String> createLanguageCode(String value) {
    // return new JAXBElement<String>(_LanguageCode_QNAME, String.class, null, value);
    // }
    //
    // /**
    // * Create an instance of {@link JAXBElement }{@code <}{@link AccessRestrictionCodeType }{@code >}}
    // *
    // */
    // @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", name = "AccessRestrictionCode")
    // public JAXBElement<AccessRestrictionCodeType> createAccessRestrictionCode(AccessRestrictionCodeType value) {
    // return new JAXBElement<AccessRestrictionCodeType>(_AccessRestrictionCode_QNAME, AccessRestrictionCodeType.class, null, value);
    // }

}
