//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.2.11
// Voir <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source.
// Généré le : 2016.12.29 à 11:54:46 AM CET
//

@jakarta.xml.bind.annotation.XmlSchema(namespace = "http://www.loc.gov/METS/",
                                       elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
                                       xmlns = {@jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://www.loc.gov/METS/", prefix = "mets"),
                                                @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://purl.org/dc/", prefix = "dc"),
                                                @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "http://www.w3.org/1999/xlink", prefix = "xlink"),
                                                @jakarta.xml.bind.annotation.XmlNs(namespaceURI = "urn:isbn:1-931666-22-9", prefix = "ead")})
@XmlJavaTypeAdapter(value = LocalDateTimeXmlAdapter.class, type = LocalDateTime.class)
package fr.progilone.pgcn.domain.jaxb.mets;

import fr.progilone.pgcn.domain.jaxb.adapters.LocalDateTimeXmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDateTime;
