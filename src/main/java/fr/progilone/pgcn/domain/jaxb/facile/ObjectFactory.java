//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2017.02.10 à 05:24:22 PM CET
//

package fr.progilone.pgcn.domain.jaxb.facile;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the fr.progilone.pgcn.domain.jaxb.facile package.
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

    private final static QName _Validator_QNAME = new QName("http://facile.cines.fr", "validator");
    private final static QName _Md5Sum_QNAME = new QName("http://facile.cines.fr", "md5sum");
    private final static QName _Sha256Sum_QNAME = new QName("http://facile.cines.fr", "sha256sum");
    private final static QName _Note_QNAME = new QName("http://facile.cines.fr", "note");
    private final static QName _FileName_QNAME = new QName("http://facile.cines.fr", "fileName");
    private final static QName _FileSize_QNAME = new QName("http://facile.cines.fr", "fileSize");
    private final static QName _Format_QNAME = new QName("http://facile.cines.fr", "format");
    private final static QName _Encoding_QNAME = new QName("http://facile.cines.fr", "encoding");
    private final static QName _Version_QNAME = new QName("http://facile.cines.fr", "version");
    private final static QName _Wellformed_QNAME = new QName("http://facile.cines.fr", "wellformed");
    private final static QName _Valid_QNAME = new QName("http://facile.cines.fr", "valid");
    private final static QName _Message_QNAME = new QName("http://facile.cines.fr", "message");
    private final static QName _Archivable_QNAME = new QName("http://facile.cines.fr", "archivable");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: fr.progilone.pgcn.domain.jaxb.facile
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ValidatorType }
     *
     */
    public ValidatorType createValidatorType() {
        return new ValidatorType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ValidatorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "validator")
    public JAXBElement<ValidatorType> createValidator(ValidatorType value) {
        return new JAXBElement<>(_Validator_QNAME, ValidatorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "md5sum")
    public JAXBElement<String> createMd5Sum(String value) {
        return new JAXBElement<>(_Md5Sum_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "sha256sum")
    public JAXBElement<String> createSha256Sum(String value) {
        return new JAXBElement<>(_Sha256Sum_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "note")
    public JAXBElement<String> createNote(String value) {
        return new JAXBElement<>(_Note_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "fileName")
    public JAXBElement<String> createFileName(String value) {
        return new JAXBElement<>(_FileName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "fileSize")
    public JAXBElement<Integer> createFileSize(Integer value) {
        return new JAXBElement<>(_FileSize_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "format")
    public JAXBElement<String> createFormat(String value) {
        return new JAXBElement<>(_Format_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "encoding")
    public JAXBElement<String> createEncoding(String value) {
        return new JAXBElement<>(_Encoding_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "version")
    public JAXBElement<String> createVersion(String value) {
        return new JAXBElement<>(_Version_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "wellformed")
    public JAXBElement<Boolean> createWellformed(Boolean value) {
        return new JAXBElement<>(_Wellformed_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "valid")
    public JAXBElement<Boolean> createValid(Boolean value) {
        return new JAXBElement<>(_Valid_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "message")
    public JAXBElement<String> createMessage(String value) {
        return new JAXBElement<>(_Message_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://facile.cines.fr", name = "archivable")
    public JAXBElement<Boolean> createArchivable(Boolean value) {
        return new JAXBElement<>(_Archivable_QNAME, Boolean.class, null, value);
    }

}
