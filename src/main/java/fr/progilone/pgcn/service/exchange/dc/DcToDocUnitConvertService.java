package fr.progilone.pgcn.service.exchange.dc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.jaxb.dc.ElementContainer;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;

/**
 * Service assurant la conversion de notices au format Dublin Core en unités documentaires
 * <p>
 * Created by Sébastien on 23/12/2016.
 */
@Service
public class DcToDocUnitConvertService extends AbstractImportConvertService {

    private static final Logger LOG = LoggerFactory.getLogger(DcToDocUnitConvertService.class);

    private static final String NS_DC = "http://purl.org/dc/elements/1.1/";

    /**
     * Conversion d'un {@link ElementContainer} en {@link DocUnit}
     *
     * @param dcGroup
     * @param library
     * @param propertyTypes
     * @return
     */
    public <T extends ElementContainer> DocUnit convert(final T dcGroup, final Library library, final List<DocPropertyType> propertyTypes) {
        final DocUnit docUnit = initDocUnit(library, BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();

        dcGroup.getAny()
               .stream()
               .filter(e -> !e.isNil())
               // Regroupement des valeurs par propriété Dublin Core
               .collect(Collectors.groupingBy(JAXBElement::getName, Collectors.mapping(JAXBElement::getValue, Collectors.toList())))
               .forEach((eltName, literals) -> {
                   final List<String> values = literals.stream().flatMap(c -> c.getContent().stream()).collect(Collectors.toList());
                   // Récupération de la propriété PGCN correspondant à la propriété Dublin Core
                   getProperty(propertyTypes, eltName.getNamespaceURI(), eltName.getLocalPart()).ifPresent(property -> {
                       // Création de la propriété sur l'unité documentaire
                       setDocProperty(bibRecord, property, values, null);

                       // title => docunit.label
                       if (StringUtils.equals(FIELD_TITLE, property.getIdentifier())) {
                           String value = Iterables.getFirst(values, "");
                           value = StringUtils.abbreviate(value, getColMaxWidth(FIELD_LABEL));
                           docUnit.setLabel(value);
                       }
                       // identifier => docunit.pgcnId
                       else if (StringUtils.equals(FIELD_IDENTIFIER, property.getIdentifier())) {
                           String value = library.getPrefix() + ", " + Iterables.getFirst(values, "");
                           value = StringUtils.abbreviate(value, getColMaxWidth(FIELD_PGCN_ID));
                           docUnit.setPgcnId(value);
                       }
                   });
               });

        setDefaultValues(docUnit);
        setRanks(bibRecord);
        return docUnit;
    }

    /**
     * Recherche la propriété PGCN {@link DocPropertyType} correspondant à la propriété Dublin Core namespaceURI + localPart
     *
     * @param propertyTypes
     * @param namespaceURI
     * @param localPart
     * @return
     */
    private Optional<DocPropertyType> getProperty(final List<DocPropertyType> propertyTypes, final String namespaceURI, final String localPart) {
        final DocPropertyType.DocPropertySuperType expectedType;
        switch (namespaceURI) {
            case NS_DC:
                expectedType = DocPropertyType.DocPropertySuperType.DC;
                break;
            default:
                LOG.warn("L'espace de nommage {} ne correspond à aucun DocPropertyType.DocPropertySuperType; la propriété {} est ignorée",
                         namespaceURI,
                         localPart);
                return Optional.empty();
        }
        return propertyTypes.stream()
                            // filtrage par espace de nommage / DocPropertySuperType
                            .filter(p -> p.getSuperType() == expectedType)
                            // filtrage par nom de la propriété
                            .filter(p -> StringUtils.equalsIgnoreCase(p.getIdentifier(), localPart)).findAny();
    }
}
