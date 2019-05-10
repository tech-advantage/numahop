//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:35 AM CET
//


package fr.progilone.pgcn.domain.jaxb.aip;

import java.math.BigInteger;
import java.time.LocalDateTime;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.Duration;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.progilone.pgcn.domain.jaxb.aip package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Pac_QNAME = new QName("http://www.cines.fr/pac/aip", "pac");
    private final static QName _DocDC_QNAME = new QName("http://www.cines.fr/pac/aip", "DocDC");
    private final static QName _Title_QNAME = new QName("http://www.cines.fr/pac/aip", "title");
    private final static QName _Creator_QNAME = new QName("http://www.cines.fr/pac/aip", "creator");
    private final static QName _Subject_QNAME = new QName("http://www.cines.fr/pac/aip", "subject");
    private final static QName _Description_QNAME = new QName("http://www.cines.fr/pac/aip", "description");
    private final static QName _Publisher_QNAME = new QName("http://www.cines.fr/pac/aip", "publisher");
    private final static QName _Contributor_QNAME = new QName("http://www.cines.fr/pac/aip", "contributor");
    private final static QName _Date_QNAME = new QName("http://www.cines.fr/pac/aip", "date");
    private final static QName _Type_QNAME = new QName("http://www.cines.fr/pac/aip", "type");
    private final static QName _Format_QNAME = new QName("http://www.cines.fr/pac/aip", "format");
    private final static QName _Identifier_QNAME = new QName("http://www.cines.fr/pac/aip", "identifier");
    private final static QName _Source_QNAME = new QName("http://www.cines.fr/pac/aip", "source");
    private final static QName _Language_QNAME = new QName("http://www.cines.fr/pac/aip", "language");
    private final static QName _Relation_QNAME = new QName("http://www.cines.fr/pac/aip", "relation");
    private final static QName _Coverage_QNAME = new QName("http://www.cines.fr/pac/aip", "coverage");
    private final static QName _Rights_QNAME = new QName("http://www.cines.fr/pac/aip", "rights");
    private final static QName _DocMeta_QNAME = new QName("http://www.cines.fr/pac/aip", "DocMeta");
    private final static QName _Authenticite_QNAME = new QName("http://www.cines.fr/pac/aip", "authenticite");
    private final static QName _DateArchivage_QNAME = new QName("http://www.cines.fr/pac/aip", "dateArchivage");
    private final static QName _DureeConservation_QNAME = new QName("http://www.cines.fr/pac/aip", "dureeConservation");
    private final static QName _IdentifiantDocPac_QNAME = new QName("http://www.cines.fr/pac/aip", "identifiantDocPac");
    private final static QName _IdentifiantDocProducteur_QNAME = new QName("http://www.cines.fr/pac/aip", "identifiantDocProducteur");
    private final static QName _DocRelation_QNAME = new QName("http://www.cines.fr/pac/aip", "docRelation");
    private final static QName _TypeRelation_QNAME = new QName("http://www.cines.fr/pac/aip", "typeRelation");
    private final static QName _SourceRelation_QNAME = new QName("http://www.cines.fr/pac/aip", "sourceRelation");
    private final static QName _IdentifiantSourceRelation_QNAME = new QName("http://www.cines.fr/pac/aip", "identifiantSourceRelation");
    private final static QName _Evaluation_QNAME = new QName("http://www.cines.fr/pac/aip", "evaluation");
    private final static QName _DUA_QNAME = new QName("http://www.cines.fr/pac/aip", "DUA");
    private final static QName _Traitement_QNAME = new QName("http://www.cines.fr/pac/aip", "traitement");
    private final static QName _DateDebut_QNAME = new QName("http://www.cines.fr/pac/aip", "dateDebut");
    private final static QName _Communicabilite_QNAME = new QName("http://www.cines.fr/pac/aip", "communicabilite");
    private final static QName _Code_QNAME = new QName("http://www.cines.fr/pac/aip", "code");
    private final static QName _NoteDocument_QNAME = new QName("http://www.cines.fr/pac/aip", "noteDocument");
    private final static QName _ServiceVersant_QNAME = new QName("http://www.cines.fr/pac/aip", "serviceVersant");
    private final static QName _PlanClassement_QNAME = new QName("http://www.cines.fr/pac/aip", "planClassement");
    private final static QName _IdentifiantVersement_QNAME = new QName("http://www.cines.fr/pac/aip", "identifiantVersement");
    private final static QName _Projet_QNAME = new QName("http://www.cines.fr/pac/aip", "projet");
    private final static QName _SortFinal_QNAME = new QName("http://www.cines.fr/pac/aip", "sortFinal");
    private final static QName _StructureDocument_QNAME = new QName("http://www.cines.fr/pac/aip", "structureDocument");
    private final static QName _Version_QNAME = new QName("http://www.cines.fr/pac/aip", "version");
    private final static QName _VersionPrecedente_QNAME = new QName("http://www.cines.fr/pac/aip", "versionPrecedente");
    private final static QName _FichMeta_QNAME = new QName("http://www.cines.fr/pac/aip", "FichMeta");
    private final static QName _IdFichier_QNAME = new QName("http://www.cines.fr/pac/aip", "idFichier");
    private final static QName _NomFichier_QNAME = new QName("http://www.cines.fr/pac/aip", "nomFichier");
    private final static QName _Compression_QNAME = new QName("http://www.cines.fr/pac/aip", "compression");
    private final static QName _Encodage_QNAME = new QName("http://www.cines.fr/pac/aip", "encodage");
    private final static QName _FormatFichier_QNAME = new QName("http://www.cines.fr/pac/aip", "formatFichier");
    private final static QName _NoteFichier_QNAME = new QName("http://www.cines.fr/pac/aip", "noteFichier");
    private final static QName _VersionFormatFichier_QNAME = new QName("http://www.cines.fr/pac/aip", "versionFormatFichier");
    private final static QName _Empreinte_QNAME = new QName("http://www.cines.fr/pac/aip", "empreinte");
    private final static QName _EmpreinteOri_QNAME = new QName("http://www.cines.fr/pac/aip", "empreinteOri");
    private final static QName _IdDocument_QNAME = new QName("http://www.cines.fr/pac/aip", "idDocument");
    private final static QName _Migration_QNAME = new QName("http://www.cines.fr/pac/aip", "migration");
    private final static QName _TailleEnOctets_QNAME = new QName("http://www.cines.fr/pac/aip", "tailleEnOctets");
//    private final static QName _LanguageCode_QNAME = new QName("urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", "LanguageCode");
//    private final static QName _AccessRestrictionCode_QNAME = new QName("urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", "AccessRestrictionCode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.progilone.pgcn.domain.jaxb.aip
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
     * Create an instance of {@link StructureFichier }
     *
     */
    public StructureFichier createStructureFichier() {
        return new StructureFichier();
    }

    /**
     * Create an instance of {@link EmpreinteType }
     *
     */
    public EmpreinteType createEmpreinteType() {
        return new EmpreinteType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PacType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "pac")
    public JAXBElement<PacType> createPac(final PacType value) {
        return new JAXBElement<>(_Pac_QNAME, PacType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocDCType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "DocDC")
    public JAXBElement<DocDCType> createDocDC(final DocDCType value) {
        return new JAXBElement<>(_DocDC_QNAME, DocDCType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "title")
    public JAXBElement<StringNotNULLtext> createTitle(final StringNotNULLtext value) {
        return new JAXBElement<>(_Title_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "creator")
    public JAXBElement<String> createCreator(final String value) {
        return new JAXBElement<>(_Creator_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "subject")
    public JAXBElement<StringNotNULLtext> createSubject(final StringNotNULLtext value) {
        return new JAXBElement<>(_Subject_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "description")
    public JAXBElement<StringNotNULLtext> createDescription(final StringNotNULLtext value) {
        return new JAXBElement<>(_Description_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "publisher")
    public JAXBElement<String> createPublisher(final String value) {
        return new JAXBElement<>(_Publisher_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "contributor")
    public JAXBElement<String> createContributor(final String value) {
        return new JAXBElement<>(_Contributor_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "date")
    public JAXBElement<String> createDate(final String value) {
        return new JAXBElement<>(_Date_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "type")
    public JAXBElement<StringNotNULLtext> createType(final StringNotNULLtext value) {
        return new JAXBElement<>(_Type_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "format")
    public JAXBElement<StringNotNULLtext> createFormat(final StringNotNULLtext value) {
        return new JAXBElement<>(_Format_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "identifier")
    public JAXBElement<String> createIdentifier(final String value) {
        return new JAXBElement<>(_Identifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "source")
    public JAXBElement<StringNotNULLtext> createSource(final StringNotNULLtext value) {
        return new JAXBElement<>(_Source_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "language")
    public JAXBElement<String> createLanguage(final String value) {
        return new JAXBElement<>(_Language_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "relation")
    public JAXBElement<StringNotNULLtext> createRelation(final StringNotNULLtext value) {
        return new JAXBElement<>(_Relation_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "coverage")
    public JAXBElement<StringNotNULLtext> createCoverage(final StringNotNULLtext value) {
        return new JAXBElement<>(_Coverage_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "rights")
    public JAXBElement<StringNotNULLtext> createRights(final StringNotNULLtext value) {
        return new JAXBElement<>(_Rights_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocMetaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "DocMeta")
    public JAXBElement<DocMetaType> createDocMeta(final DocMetaType value) {
        return new JAXBElement<>(_DocMeta_QNAME, DocMetaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "authenticite")
    public JAXBElement<String> createAuthenticite(final String value) {
        return new JAXBElement<>(_Authenticite_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "dateArchivage")
    public JAXBElement<String> createDateArchivage(final String value) {
        return new JAXBElement<>(_DateArchivage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "dureeConservation")
    public JAXBElement<Duration> createDureeConservation(final Duration value) {
        return new JAXBElement<>(_DureeConservation_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "identifiantDocPac")
    public JAXBElement<String> createIdentifiantDocPac(final String value) {
        return new JAXBElement<>(_IdentifiantDocPac_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "identifiantDocProducteur")
    public JAXBElement<String> createIdentifiantDocProducteur(final String value) {
        return new JAXBElement<>(_IdentifiantDocProducteur_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocRelationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "docRelation")
    public JAXBElement<DocRelationType> createDocRelation(final DocRelationType value) {
        return new JAXBElement<>(_DocRelation_QNAME, DocRelationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "typeRelation")
    public JAXBElement<String> createTypeRelation(final String value) {
        return new JAXBElement<>(_TypeRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "sourceRelation")
    public JAXBElement<String> createSourceRelation(final String value) {
        return new JAXBElement<>(_SourceRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "identifiantSourceRelation")
    public JAXBElement<String> createIdentifiantSourceRelation(final String value) {
        return new JAXBElement<>(_IdentifiantSourceRelation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "evaluation")
    public JAXBElement<EvaluationType> createEvaluation(final EvaluationType value) {
        return new JAXBElement<>(_Evaluation_QNAME, EvaluationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Duration }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "DUA")
    public JAXBElement<Duration> createDUA(final Duration value) {
        return new JAXBElement<>(_DUA_QNAME, Duration.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraitementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "traitement")
    public JAXBElement<TraitementType> createTraitement(final TraitementType value) {
        return new JAXBElement<>(_Traitement_QNAME, TraitementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalDateTime }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "dateDebut")
    public JAXBElement<LocalDateTime> createDateDebut(final LocalDateTime value) {
        return new JAXBElement<>(_DateDebut_QNAME, LocalDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommunicabiliteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "communicabilite")
    public JAXBElement<CommunicabiliteType> createCommunicabilite(final CommunicabiliteType value) {
        return new JAXBElement<>(_Communicabilite_QNAME, CommunicabiliteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "code")
    public JAXBElement<CodeType> createCode(final CodeType value) {
        return new JAXBElement<>(_Code_QNAME, CodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "noteDocument")
    public JAXBElement<StringNotNULLtext> createNoteDocument(final StringNotNULLtext value) {
        return new JAXBElement<>(_NoteDocument_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "serviceVersant")
    public JAXBElement<String> createServiceVersant(final String value) {
        return new JAXBElement<>(_ServiceVersant_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "planClassement")
    public JAXBElement<StringNotNULLtext> createPlanClassement(final StringNotNULLtext value) {
        return new JAXBElement<>(_PlanClassement_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "identifiantVersement")
    public JAXBElement<String> createIdentifiantVersement(final String value) {
        return new JAXBElement<>(_IdentifiantVersement_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "projet")
    public JAXBElement<String> createProjet(final String value) {
        return new JAXBElement<>(_Projet_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "sortFinal")
    public JAXBElement<StringNotNULLtext> createSortFinal(final StringNotNULLtext value) {
        return new JAXBElement<>(_SortFinal_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "structureDocument")
    public JAXBElement<String> createStructureDocument(final String value) {
        return new JAXBElement<>(_StructureDocument_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "version")
    public JAXBElement<String> createVersion(final String value) {
        return new JAXBElement<>(_Version_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "versionPrecedente")
    public JAXBElement<String> createVersionPrecedente(final String value) {
        return new JAXBElement<>(_VersionPrecedente_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FichMetaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "FichMeta")
    public JAXBElement<FichMetaType> createFichMeta(final FichMetaType value) {
        return new JAXBElement<>(_FichMeta_QNAME, FichMetaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "idFichier")
    public JAXBElement<String> createIdFichier(final String value) {
        return new JAXBElement<>(_IdFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "nomFichier")
    public JAXBElement<String> createNomFichier(final String value) {
        return new JAXBElement<>(_NomFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "compression")
    public JAXBElement<String> createCompression(final String value) {
        return new JAXBElement<>(_Compression_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "encodage")
    public JAXBElement<String> createEncodage(final String value) {
        return new JAXBElement<>(_Encodage_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "formatFichier")
    public JAXBElement<String> createFormatFichier(final String value) {
        return new JAXBElement<>(_FormatFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringNotNULLtext }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "noteFichier")
    public JAXBElement<StringNotNULLtext> createNoteFichier(final StringNotNULLtext value) {
        return new JAXBElement<>(_NoteFichier_QNAME, StringNotNULLtext.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "versionFormatFichier")
    public JAXBElement<String> createVersionFormatFichier(final String value) {
        return new JAXBElement<>(_VersionFormatFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmpreinteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "empreinte")
    public JAXBElement<EmpreinteType> createEmpreinte(final EmpreinteType value) {
        return new JAXBElement<>(_Empreinte_QNAME, EmpreinteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EmpreinteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "empreinteOri")
    public JAXBElement<EmpreinteType> createEmpreinteOri(final EmpreinteType value) {
        return new JAXBElement<>(_EmpreinteOri_QNAME, EmpreinteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "idDocument")
    public JAXBElement<BigInteger> createIdDocument(final BigInteger value) {
        return new JAXBElement<>(_IdDocument_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "migration")
    public JAXBElement<String> createMigration(final String value) {
        return new JAXBElement<>(_Migration_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/aip", name = "tailleEnOctets")
    public JAXBElement<BigInteger> createTailleEnOctets(final BigInteger value) {
        return new JAXBElement<>(_TailleEnOctets_QNAME, BigInteger.class, null, value);
    }

//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
//     *
//     */
//    @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", name = "LanguageCode")
//    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
//    public JAXBElement<String> createLanguageCode(String value) {
//        return new JAXBElement<String>(_LanguageCode_QNAME, String.class, null, value);
//    }
//
//    /**
//     * Create an instance of {@link JAXBElement }{@code <}{@link AccessRestrictionCodeType }{@code >}}
//     *
//     */
//    @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", name = "AccessRestrictionCode")
//    public JAXBElement<AccessRestrictionCodeType> createAccessRestrictionCode(AccessRestrictionCodeType value) {
//        return new JAXBElement<AccessRestrictionCodeType>(_AccessRestrictionCode_QNAME, AccessRestrictionCodeType.class, null, value);
//    }

}
