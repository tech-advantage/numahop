package fr.progilone.pgcn.service.exchange.dc;

import com.google.common.collect.Ordering;
import com.opencsv.CSVWriter;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocPropertyType.DocPropertySuperType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.lot.LotService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.opencsv.CSVWriter.*;

/**
 * Created by Sébastien on 27/12/2016.
 */
@Service
public class DocUnitToCSVService {

    private static final Logger LOG = LoggerFactory.getLogger(DocUnitToCSVService.class);

    private static final char CSV_REPEATED_FIELD_SEPARATOR = ',';

    private final DocPropertyTypeService docPropertyTypeService;
    private final DocUnitService docUnitService;
    private final LotService lotService;

    @Autowired
    public DocUnitToCSVService(final DocPropertyTypeService docPropertyTypeService, final DocUnitService docUnitService, LotService lotService) {
        this.docPropertyTypeService = docPropertyTypeService;
        this.docUnitService = docUnitService;
        this.lotService = lotService;
    }

    /**
     * Convertit l'unité documentaire en CSV, et retourne le résultat sous la forme d'une chaîne de caractères
     *
     * @param out
     * @param docUnitIds
     * @param fields
     * @param docUnitFields
     * @param bibFields
     * @param physFields
     * @param encoding
     * @param separator
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public void convertFromList(final OutputStream out,
                                final Collection<String> docUnitIds,
                                final List<String> fields,
                                final List<String> docUnitFields,
                                final List<String> bibFields,
                                final List<String> physFields,
                                final String encoding,
                                final char separator) throws IOException {
        final List<DocUnit> docUnits = docUnitService.findAllByIdWithDependencies(docUnitIds);
        final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAllByIdentifierIn(fields);
        convert(out, docUnits, propertyTypes, docUnitFields, bibFields, physFields, encoding, separator);
    }

    /**
     * Convertit les UD du lot en CSV, et écrit le résultat dans l'OutputStream passé en paramètre
     *
     * @param out
     * @param lotId
     * @param fields
     * @param docUnitFields
     * @param bibFields
     * @param physFields
     * @param encoding
     * @param separator
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public void convertLotFromList(ServletOutputStream out,
                                   String lotId,
                                   List<String> fields,
                                   final List<String> docUnitFields,
                                   final List<String> bibFields,
                                   final List<String> physFields,
                                   final String encoding,
                                   final char separator) throws IOException {
        final Lot lot = lotService.findByIdentifier(lotId);
        final List<DocPropertyType> propertyTypes = docPropertyTypeService.findAllByIdentifierIn(fields);
        convert(out, lot.getDocUnits(), propertyTypes, docUnitFields, bibFields, physFields, encoding, separator);
    }

    /**
     * Convertit les UD du lot en CSV, et écrit le résultat dans l'OutputStream passé en paramètre
     *
     * @param out
     * @param docUnits
     * @param propertyTypes
     * @param encoding
     * @param separator
     * @throws IOException
     */
    private void convert(final OutputStream out,
                         final Collection<DocUnit> docUnits,
                         List<DocPropertyType> propertyTypes,
                         final List<String> docUnitFields,
                         final List<String> bibFields,
                         final List<String> physFields,
                         final String encoding,
                         final char separator) throws IOException {

        // Tri des types de ppté par super-type + rang
        propertyTypes = sortPropertyTypes(propertyTypes);

        // Alimentation du CSV
        try (Writer writer = new OutputStreamWriter(out, encoding);
             CSVWriter csvWriter = new CSVWriter(writer, separator, DEFAULT_QUOTE_CHARACTER, DEFAULT_ESCAPE_CHARACTER, RFC4180_LINE_END)) {

            // Entête
            writeHeader(csvWriter, propertyTypes, docUnitFields, bibFields, physFields);

            for (final DocUnit docUnit : docUnits) {
                // Champs de l'unité documentaire
                final List<String> docUnitValues = getObjectValues(docUnit, docUnitFields);
                // Champs des documents physiques
                final List<String> physValues = getCollectionOfObjectsValues(docUnit.getPhysicalDocuments(), physFields);

                // Corps
                writeBody(csvWriter, propertyTypes, docUnit.getRecords(), docUnitValues, bibFields, physValues);
            }
        }
    }

    /**
     * Tri de la liste de propriétés passées en paramètres
     *
     * @param types
     * @return
     */
    private List<DocPropertyType> sortPropertyTypes(final List<DocPropertyType> types) {
        return types.stream()
                    // tri par super-type + rang
                    .sorted(Ordering.explicit(DocPropertySuperType.DC, DocPropertySuperType.DCQ, DocPropertySuperType.CUSTOM)
                                    .nullsLast()
                                    .onResultOf(DocPropertyType::getSuperType)
                                    .compound(Ordering.natural().nullsFirst().onResultOf(DocPropertyType::getRank))).collect(Collectors.toList());
    }

    /**
     * Écriture de l'entête du fichier CSV
     *
     * @param csvWriter
     * @param propertyTypes
     * @param docUnitFields
     * @param bibFields
     * @param physFields
     */
    private void writeHeader(final CSVWriter csvWriter,
                             final List<DocPropertyType> propertyTypes,
                             final List<String> docUnitFields,
                             final List<String> bibFields,
                             final List<String> physFields) {
        final List<String> types = propertyTypes.stream().map(this::getRecordHeader).collect(Collectors.toList());
        Stream.of(docUnitFields, bibFields, physFields).filter(CollectionUtils::isNotEmpty).forEach(types::addAll);

        csvWriter.writeNext(types.toArray(new String[0]));
    }

    /**
     * Écriture de l'unité documentaire et de ses notices dans le fichier CSV
     *
     * @param csvWriter
     * @param propertyTypes
     * @param records
     * @param docUnitValues
     * @param bibFields
     * @param physValues
     */
    private void writeBody(final CSVWriter csvWriter,
                           final List<DocPropertyType> propertyTypes,
                           final Collection<BibliographicRecord> records,
                           final List<String> docUnitValues,
                           final List<String> bibFields,
                           final List<String> physValues) {

        for (final BibliographicRecord record : records) {
            // Champs de la notice bibliographique
            final List<String> bibValues = getObjectValues(record, bibFields);

            final Map<DocPropertyType, List<DocProperty>> propByType =
                record.getProperties().stream().collect(Collectors.groupingBy(DocProperty::getType));

            // Ajout de propriétés DC, DCQ et CUSTOM
            final List<String> line = propertyTypes.stream()
                                                   .map(type -> propByType.getOrDefault(type, Collections.emptyList()))
                                                   .map(values -> values.stream()
                                                                        .map(DocProperty::getValue)
                                                                        .reduce((a, b) -> a + CSV_REPEATED_FIELD_SEPARATOR + b)
                                                                        .orElse(""))
                                                   .collect(Collectors.toList());
            // Champs de l'unité documentaire
            line.addAll(docUnitValues);
            // Champs de la notice bibliographique
            line.addAll(bibValues);
            // Champs des documents physiques
            line.addAll(physValues);

            csvWriter.writeNext(line.toArray(new String[0]));
        }
    }

    private String getRecordHeader(DocPropertyType property) {
        String identifier = property.getIdentifier();
        switch (property.getSuperType()) {
            case DC:
                return "dc:" + identifier;
            case DCQ:
                return "dcq:" + identifier;
            case CUSTOM:
                return identifier;
            default:
                return identifier;
        }
    }

    /**
     * Récupère les valeurs des champs d'une liste d'objets
     *
     * @param objects
     * @param fields
     * @return
     */
    private List<String> getCollectionOfObjectsValues(final Collection<?> objects, final List<String> fields) {
        return CollectionUtils.isNotEmpty(objects) && CollectionUtils.isNotEmpty(fields) ?
               fields.stream()
                     .map(f -> objects.stream().map(o -> getObjectValue(o, f)).reduce((a, b) -> a + CSV_REPEATED_FIELD_SEPARATOR + b).orElse(""))
                     .collect(Collectors.toList()) :
               Collections.emptyList();
    }

    /**
     * Récupère les valeurs des champs d'un objet
     *
     * @param object
     * @param fields
     * @return
     */
    private List<String> getObjectValues(final Object object, final List<String> fields) {
        return CollectionUtils.isNotEmpty(fields) ?
               fields.stream().map(f -> getObjectValue(object, f)).collect(Collectors.toList()) :
               Collections.emptyList();
    }

    /**
     * Récupère la valeur de fieldName dans object sous forme de String
     *
     * @param object
     * @param fieldName
     * @return
     */
    private String getObjectValue(final Object object, final String fieldName) {
        try {
            final Object current = PropertyUtils.getSimpleProperty(object, fieldName);
            return current != null ? String.valueOf(current) : "";

        } catch (ReflectiveOperationException | IllegalArgumentException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }
}
