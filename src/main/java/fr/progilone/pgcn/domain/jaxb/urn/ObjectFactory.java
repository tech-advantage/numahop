package fr.progilone.pgcn.domain.jaxb.urn;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

/**
 * Created by Sébastien on 03/01/2017.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LanguageCode_QNAME = new QName("urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", "LanguageCode");
    private final static QName _AccessRestrictionCode_QNAME = new QName("urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", "AccessRestrictionCode");

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:languageCode:2011-10-07", name = "LanguageCode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createLanguageCode(String value) {
        return new JAXBElement<>(_LanguageCode_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AccessRestrictionCodeType }{@code >}}
     */
    @XmlElementDecl(namespace = "urn:un:unece:uncefact:codelist:draft:DAF:accessRestrictionCode:2009-08-18", name = "AccessRestrictionCode")
    public JAXBElement<AccessRestrictionCodeType> createAccessRestrictionCode(AccessRestrictionCodeType value) {
        return new JAXBElement<>(_AccessRestrictionCode_QNAME, AccessRestrictionCodeType.class, null, value);
    }
}
