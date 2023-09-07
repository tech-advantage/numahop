//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.01.03 à 11:16:18 AM CET
//

package fr.progilone.pgcn.domain.jaxb.avis;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import java.time.LocalDateTime;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.progilone.pgcn.domain.jaxb.avis package.
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

    private final static QName _PacAvis_QNAME = new QName("http://www.cines.fr/pac/avis", "pac_avis");
    private final static QName _Serveur_QNAME = new QName("http://www.cines.fr/pac/avis", "serveur");
    private final static QName _Id_QNAME = new QName("http://www.cines.fr/pac/avis", "id");
    private final static QName _IdVersement_QNAME = new QName("http://www.cines.fr/pac/avis", "id_versement");
    private final static QName _IdDemande_QNAME = new QName("http://www.cines.fr/pac/avis", "id_demande");
    private final static QName _Commentaire_QNAME = new QName("http://www.cines.fr/pac/avis", "commentaire");
    private final static QName _ErreurValidation_QNAME = new QName("http://www.cines.fr/pac/avis", "erreurValidation");
    private final static QName _Contrles_QNAME = new QName("http://www.cines.fr/pac/avis", "contrles");
    private final static QName _Title_QNAME = new QName("http://www.cines.fr/pac/avis", "title");
    private final static QName _DateArchivage_QNAME = new QName("http://www.cines.fr/pac/avis", "dateArchivage");
    private final static QName _DateDemande_QNAME = new QName("http://www.cines.fr/pac/avis", "dateDemande");
    private final static QName _DateCommunication_QNAME = new QName("http://www.cines.fr/pac/avis", "dateCommunication");
    private final static QName _IdentifiantDocPac_QNAME = new QName("http://www.cines.fr/pac/avis", "identifiantDocPac");
    private final static QName _IdentifiantDocProducteur_QNAME = new QName("http://www.cines.fr/pac/avis", "identifiantDocProducteur");
    private final static QName _CodeErreur_QNAME = new QName("http://www.cines.fr/pac/avis", "codeErreur");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.progilone.pgcn.domain.jaxb.avis
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PacAvisType }
     *
     */
    public PacAvisType createPacAvisType() {
        return new PacAvisType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PacAvisType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "pac_avis")
    public JAXBElement<PacAvisType> createPacAvis(final PacAvisType value) {
        return new JAXBElement<>(_PacAvis_QNAME, PacAvisType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "serveur")
    public JAXBElement<String> createServeur(final String value) {
        return new JAXBElement<>(_Serveur_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "id")
    public JAXBElement<String> createId(final String value) {
        return new JAXBElement<>(_Id_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "id_versement")
    public JAXBElement<String> createIdVersement(final String value) {
        return new JAXBElement<>(_IdVersement_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "id_demande")
    public JAXBElement<String> createIdDemande(final String value) {
        return new JAXBElement<>(_IdDemande_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "commentaire")
    public JAXBElement<String> createCommentaire(final String value) {
        return new JAXBElement<>(_Commentaire_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "erreurValidation")
    public JAXBElement<String> createErreurValidation(final String value) {
        return new JAXBElement<>(_ErreurValidation_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "contrles")
    public JAXBElement<String> createContrles(final String value) {
        return new JAXBElement<>(_Contrles_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "title")
    public JAXBElement<String> createTitle(final String value) {
        return new JAXBElement<>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalDateTime }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "dateArchivage")
    public JAXBElement<LocalDateTime> createDateArchivage(final LocalDateTime value) {
        return new JAXBElement<>(_DateArchivage_QNAME, LocalDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalDateTime }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "dateDemande")
    public JAXBElement<LocalDateTime> createDateDemande(final LocalDateTime value) {
        return new JAXBElement<>(_DateDemande_QNAME, LocalDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocalDateTime }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "dateCommunication")
    public JAXBElement<LocalDateTime> createDateCommunication(final LocalDateTime value) {
        return new JAXBElement<>(_DateCommunication_QNAME, LocalDateTime.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "identifiantDocPac")
    public JAXBElement<String> createIdentifiantDocPac(final String value) {
        return new JAXBElement<>(_IdentifiantDocPac_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "identifiantDocProducteur")
    public JAXBElement<String> createIdentifiantDocProducteur(final String value) {
        return new JAXBElement<>(_IdentifiantDocProducteur_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CodeErreurType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.cines.fr/pac/avis", name = "codeErreur")
    public JAXBElement<CodeErreurType> createCodeErreur(final CodeErreurType value) {
        return new JAXBElement<>(_CodeErreur_QNAME, CodeErreurType.class, null, value);
    }

}
