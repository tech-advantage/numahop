//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.8-b130911.1802
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.12.08 à 03:23:15 PM CET
//


package fr.progilone.pgcn.domain.jaxb.ppdi;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.cines.pac.ppdi package.
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

    private final static QName _CircuitProduction_QNAME = new QName("http://www.cines.fr/pac/ppdi", "CircuitProduction");
    private final static QName _Evaluation_QNAME = new QName("http://www.cines.fr/pac/ppdi", "evaluation");
    private final static QName _ContexteLegal_QNAME = new QName("http://www.cines.fr/pac/ppdi", "contexteLegal");
    private final static QName _StructureDocuments_QNAME = new QName("http://www.cines.fr/pac/ppdi", "structureDocuments");
    private final static QName _SourcesComplementaires_QNAME = new QName("http://www.cines.fr/pac/ppdi", "sourcesComplementaires");
    private final static QName _Quantite_QNAME = new QName("http://www.cines.fr/pac/ppdi", "quantite");
    private final static QName _SipDescription_QNAME = new QName("http://www.cines.fr/pac/ppdi", "SipDescription");
    private final static QName _SortFinal_QNAME = new QName("http://www.cines.fr/pac/ppdi", "sortFinal");
    private final static QName _MdFichier_QNAME = new QName("http://www.cines.fr/pac/ppdi", "mdFichier");
    private final static QName _MdMetier_QNAME = new QName("http://www.cines.fr/pac/ppdi", "mdMetier");
    private final static QName _NotesProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "notesProd");
    private final static QName _NotesSV_QNAME = new QName("http://www.cines.fr/pac/ppdi", "notesSV");
    private final static QName _DateArchivage_QNAME = new QName("http://www.cines.fr/pac/ppdi", "dateArchivage");
    private final static QName _Ppdi_QNAME = new QName("http://www.cines.fr/pac/ppdi", "ppdi");
    private final static QName _NotesFonds_QNAME = new QName("http://www.cines.fr/pac/ppdi", "notesFonds");
    private final static QName _Documents_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Documents");
    private final static QName _NomProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "nomProd");
    private final static QName _Contexte_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Contexte");
    private final static QName _Titre_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Titre");
    private final static QName _FichMetaDescription_QNAME = new QName("http://www.cines.fr/pac/ppdi", "FichMetaDescription");
    private final static QName _DocRelation_QNAME = new QName("http://www.cines.fr/pac/ppdi", "docRelation");
    private final static QName _Communicabilite_QNAME = new QName("http://www.cines.fr/pac/ppdi", "communicabilite");
    private final static QName _Langue_QNAME = new QName("http://www.cines.fr/pac/ppdi", "langue");
    private final static QName _InfoPreserv_QNAME = new QName("http://www.cines.fr/pac/ppdi", "infoPreserv");
    private final static QName _DatesSV_QNAME = new QName("http://www.cines.fr/pac/ppdi", "datesSV");
    private final static QName _Intitule_QNAME = new QName("http://www.cines.fr/pac/ppdi", "intitule");
    private final static QName _HistoriqueProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "historiqueProd");
    private final static QName _ClasseService_QNAME = new QName("http://www.cines.fr/pac/ppdi", "classeService");
    private final static QName _ServiceVersant_QNAME = new QName("http://www.cines.fr/pac/ppdi", "ServiceVersant");
    private final static QName _Archivage_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Archivage");
    private final static QName _Cadre_QNAME = new QName("http://www.cines.fr/pac/ppdi", "cadre");
    private final static QName _RelationFdsProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "relationFdsProd");
    private final static QName _Producteur_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Producteur");
    private final static QName _HistoriqueSV_QNAME = new QName("http://www.cines.fr/pac/ppdi", "historiqueSV");
    private final static QName _Caracteristiques_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Caracteristiques");
    private final static QName _DatesProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "datesProd");
    private final static QName _RelationSvProd_QNAME = new QName("http://www.cines.fr/pac/ppdi", "relationSvProd");
    private final static QName _Contenu_QNAME = new QName("http://www.cines.fr/pac/ppdi", "contenu");
    private final static QName _DocDCDescription_QNAME = new QName("http://www.cines.fr/pac/ppdi", "DocDCDescription");
    private final static QName _Fonds_QNAME = new QName("http://www.cines.fr/pac/ppdi", "Fonds");
    private final static QName _Reproduction_QNAME = new QName("http://www.cines.fr/pac/ppdi", "reproduction");
    private final static QName _DocMetaDescription_QNAME = new QName("http://www.cines.fr/pac/ppdi", "DocMetaDescription");
    private final static QName _Original_QNAME = new QName("http://www.cines.fr/pac/ppdi", "original");
    private final static QName _CommunauteCible_QNAME = new QName("http://www.cines.fr/pac/ppdi", "communauteCible");
    private final static QName _Numerisation_QNAME = new QName("http://www.cines.fr/pac/ppdi", "numerisation");
    private final static QName _ProductionCollecte_QNAME = new QName("http://www.cines.fr/pac/ppdi", "productionCollecte");
    private final static QName _Acces_QNAME = new QName("http://www.cines.fr/pac/ppdi", "acces");
    private final static QName _MdDesc_QNAME = new QName("http://www.cines.fr/pac/ppdi", "mdDesc");
    private final static QName _Structure_QNAME = new QName("http://www.cines.fr/pac/ppdi", "structure");
    private final static QName _NomSV_QNAME = new QName("http://www.cines.fr/pac/ppdi", "nomSV");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.cines.pac.ppdi
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FichMetaDescriptionType }
     *
     */
    public FichMetaDescriptionType createFichMetaDescriptionType() {
        return new FichMetaDescriptionType();
    }

    /**
     * Create an instance of {@link MdMetierType }
     *
     */
    public MdMetierType createMdMetierType() {
        return new MdMetierType();
    }

    /**
     * Create an instance of {@link EvaluationType }
     *
     */
    public EvaluationType createEvaluationType() {
        return new EvaluationType();
    }

    /**
     * Create an instance of {@link CircuitProductionType }
     *
     */
    public CircuitProductionType createCircuitProductionType() {
        return new CircuitProductionType();
    }

    /**
     * Create an instance of {@link SipDescriptionType }
     *
     */
    public SipDescriptionType createSipDescriptionType() {
        return new SipDescriptionType();
    }

    /**
     * Create an instance of {@link DocRelationType }
     *
     */
    public DocRelationType createDocRelationType() {
        return new DocRelationType();
    }

    /**
     * Create an instance of {@link CommunicabiliteType }
     *
     */
    public CommunicabiliteType createCommunicabiliteType() {
        return new CommunicabiliteType();
    }

    /**
     * Create an instance of {@link PpdiType }
     *
     */
    public PpdiType createPpdiType() {
        return new PpdiType();
    }

    /**
     * Create an instance of {@link DocumentsType }
     *
     */
    public DocumentsType createDocumentsType() {
        return new DocumentsType();
    }

    /**
     * Create an instance of {@link ContexteType }
     *
     */
    public ContexteType createContexteType() {
        return new ContexteType();
    }

    /**
     * Create an instance of {@link CaracteristiquesType }
     *
     */
    public CaracteristiquesType createCaracteristiquesType() {
        return new CaracteristiquesType();
    }

    /**
     * Create an instance of {@link ServiceVersantType }
     *
     */
    public ServiceVersantType createServiceVersantType() {
        return new ServiceVersantType();
    }

    /**
     * Create an instance of {@link ArchivageType }
     *
     */
    public ArchivageType createArchivageType() {
        return new ArchivageType();
    }

    /**
     * Create an instance of {@link ProducteurType }
     *
     */
    public ProducteurType createProducteurType() {
        return new ProducteurType();
    }

    /**
     * Create an instance of {@link DocMetaDescriptionType }
     *
     */
    public DocMetaDescriptionType createDocMetaDescriptionType() {
        return new DocMetaDescriptionType();
    }

    /**
     * Create an instance of {@link DocDCDescriptionType }
     *
     */
    public DocDCDescriptionType createDocDCDescriptionType() {
        return new DocDCDescriptionType();
    }

    /**
     * Create an instance of {@link FondsType }
     *
     */
    public FondsType createFondsType() {
        return new FondsType();
    }

    /**
     * Create an instance of {@link FichMetaDescriptionType.StructureFichier }
     *
     */
    public FichMetaDescriptionType.StructureFichier createFichMetaDescriptionTypeStructureFichier() {
        return new FichMetaDescriptionType.StructureFichier();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CircuitProductionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "CircuitProduction")
    public JAXBElement<CircuitProductionType> createCircuitProduction(CircuitProductionType value) {
        return new JAXBElement<>(_CircuitProduction_QNAME, CircuitProductionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "evaluation")
    public JAXBElement<EvaluationType> createEvaluation(EvaluationType value) {
        return new JAXBElement<>(_Evaluation_QNAME, EvaluationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "contexteLegal")
    public JAXBElement<String> createContexteLegal(String value) {
        return new JAXBElement<>(_ContexteLegal_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "structureDocuments")
    public JAXBElement<String> createStructureDocuments(String value) {
        return new JAXBElement<>(_StructureDocuments_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "sourcesComplementaires")
    public JAXBElement<String> createSourcesComplementaires(String value) {
        return new JAXBElement<>(_SourcesComplementaires_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "quantite")
    public JAXBElement<String> createQuantite(String value) {
        return new JAXBElement<>(_Quantite_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SipDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "SipDescription")
    public JAXBElement<SipDescriptionType> createSipDescription(SipDescriptionType value) {
        return new JAXBElement<>(_SipDescription_QNAME, SipDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "sortFinal")
    public JAXBElement<String> createSortFinal(String value) {
        return new JAXBElement<>(_SortFinal_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "mdFichier")
    public JAXBElement<String> createMdFichier(String value) {
        return new JAXBElement<>(_MdFichier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MdMetierType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "mdMetier")
    public JAXBElement<MdMetierType> createMdMetier(MdMetierType value) {
        return new JAXBElement<>(_MdMetier_QNAME, MdMetierType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "notesProd")
    public JAXBElement<String> createNotesProd(String value) {
        return new JAXBElement<>(_NotesProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "notesSV")
    public JAXBElement<String> createNotesSV(String value) {
        return new JAXBElement<>(_NotesSV_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "dateArchivage")
    public JAXBElement<XMLGregorianCalendar> createDateArchivage(XMLGregorianCalendar value) {
        return new JAXBElement<>(_DateArchivage_QNAME, XMLGregorianCalendar.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PpdiType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "ppdi")
    public JAXBElement<PpdiType> createPpdi(PpdiType value) {
        return new JAXBElement<>(_Ppdi_QNAME, PpdiType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "notesFonds")
    public JAXBElement<String> createNotesFonds(String value) {
        return new JAXBElement<>(_NotesFonds_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Documents")
    public JAXBElement<DocumentsType> createDocuments(DocumentsType value) {
        return new JAXBElement<>(_Documents_QNAME, DocumentsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "nomProd")
    public JAXBElement<String> createNomProd(String value) {
        return new JAXBElement<>(_NomProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContexteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Contexte")
    public JAXBElement<ContexteType> createContexte(ContexteType value) {
        return new JAXBElement<>(_Contexte_QNAME, ContexteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Titre")
    public JAXBElement<String> createTitre(String value) {
        return new JAXBElement<>(_Titre_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FichMetaDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "FichMetaDescription")
    public JAXBElement<FichMetaDescriptionType> createFichMetaDescription(FichMetaDescriptionType value) {
        return new JAXBElement<>(_FichMetaDescription_QNAME, FichMetaDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocRelationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "docRelation")
    public JAXBElement<DocRelationType> createDocRelation(DocRelationType value) {
        return new JAXBElement<>(_DocRelation_QNAME, DocRelationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CommunicabiliteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "communicabilite")
    public JAXBElement<CommunicabiliteType> createCommunicabilite(CommunicabiliteType value) {
        return new JAXBElement<>(_Communicabilite_QNAME, CommunicabiliteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "langue")
    public JAXBElement<String> createLangue(String value) {
        return new JAXBElement<>(_Langue_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "infoPreserv")
    public JAXBElement<String> createInfoPreserv(String value) {
        return new JAXBElement<>(_InfoPreserv_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "datesSV")
    public JAXBElement<String> createDatesSV(String value) {
        return new JAXBElement<>(_DatesSV_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "intitule")
    public JAXBElement<String> createIntitule(String value) {
        return new JAXBElement<>(_Intitule_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "historiqueProd")
    public JAXBElement<String> createHistoriqueProd(String value) {
        return new JAXBElement<>(_HistoriqueProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "classeService")
    public JAXBElement<String> createClasseService(String value) {
        return new JAXBElement<>(_ClasseService_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceVersantType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "ServiceVersant")
    public JAXBElement<ServiceVersantType> createServiceVersant(ServiceVersantType value) {
        return new JAXBElement<>(_ServiceVersant_QNAME, ServiceVersantType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArchivageType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Archivage")
    public JAXBElement<ArchivageType> createArchivage(ArchivageType value) {
        return new JAXBElement<>(_Archivage_QNAME, ArchivageType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CadreType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "cadre")
    public JAXBElement<CadreType> createCadre(CadreType value) {
        return new JAXBElement<>(_Cadre_QNAME, CadreType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "relationFdsProd")
    public JAXBElement<String> createRelationFdsProd(String value) {
        return new JAXBElement<>(_RelationFdsProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProducteurType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Producteur")
    public JAXBElement<ProducteurType> createProducteur(ProducteurType value) {
        return new JAXBElement<>(_Producteur_QNAME, ProducteurType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "historiqueSV")
    public JAXBElement<String> createHistoriqueSV(String value) {
        return new JAXBElement<>(_HistoriqueSV_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CaracteristiquesType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Caracteristiques")
    public JAXBElement<CaracteristiquesType> createCaracteristiques(CaracteristiquesType value) {
        return new JAXBElement<>(_Caracteristiques_QNAME, CaracteristiquesType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "datesProd")
    public JAXBElement<String> createDatesProd(String value) {
        return new JAXBElement<>(_DatesProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "relationSvProd")
    public JAXBElement<String> createRelationSvProd(String value) {
        return new JAXBElement<>(_RelationSvProd_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "contenu")
    public JAXBElement<String> createContenu(String value) {
        return new JAXBElement<>(_Contenu_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocDCDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "DocDCDescription")
    public JAXBElement<DocDCDescriptionType> createDocDCDescription(DocDCDescriptionType value) {
        return new JAXBElement<>(_DocDCDescription_QNAME, DocDCDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FondsType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "Fonds")
    public JAXBElement<FondsType> createFonds(FondsType value) {
        return new JAXBElement<>(_Fonds_QNAME, FondsType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "reproduction")
    public JAXBElement<String> createReproduction(String value) {
        return new JAXBElement<>(_Reproduction_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocMetaDescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "DocMetaDescription")
    public JAXBElement<DocMetaDescriptionType> createDocMetaDescription(DocMetaDescriptionType value) {
        return new JAXBElement<>(_DocMetaDescription_QNAME, DocMetaDescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "original")
    public JAXBElement<String> createOriginal(String value) {
        return new JAXBElement<>(_Original_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "communauteCible")
    public JAXBElement<String> createCommunauteCible(String value) {
        return new JAXBElement<>(_CommunauteCible_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "numerisation")
    public JAXBElement<String> createNumerisation(String value) {
        return new JAXBElement<>(_Numerisation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "productionCollecte")
    public JAXBElement<String> createProductionCollecte(String value) {
        return new JAXBElement<>(_ProductionCollecte_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "acces")
    public JAXBElement<String> createAcces(String value) {
        return new JAXBElement<>(_Acces_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "mdDesc")
    public JAXBElement<String> createMdDesc(String value) {
        return new JAXBElement<>(_MdDesc_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "structure")
    public JAXBElement<String> createStructure(String value) {
        return new JAXBElement<>(_Structure_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/ppdi", name = "nomSV")
    public JAXBElement<String> createNomSV(String value) {
        return new JAXBElement<>(_NomSV_QNAME, String.class, null, value);
    }

}
