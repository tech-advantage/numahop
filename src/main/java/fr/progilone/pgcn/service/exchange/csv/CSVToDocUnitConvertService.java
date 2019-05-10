package fr.progilone.pgcn.service.exchange.csv;

import com.google.common.collect.Iterables;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.exchange.CSVMappingRule;
import fr.progilone.pgcn.service.document.BibliographicRecordService;
import fr.progilone.pgcn.service.document.DocPropertyService;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;
import fr.progilone.pgcn.service.exchange.ImportDocUnitService;
import fr.progilone.pgcn.service.exchange.marc.DocUnitWrapper;
import fr.progilone.pgcn.service.library.LibraryService;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class CSVToDocUnitConvertService extends AbstractImportConvertService {

    @Autowired
    private DocPropertyTypeService docPropertyTypeService;
    @Autowired
    private DocUnitService docUnitService;
    @Autowired
    private LibraryService libraryService;
    @Autowired
    private DocPropertyService docPropertyService;
    @Autowired
    private BibliographicRecordService bibliographicRecordService;
    @Autowired
    private ImportDocUnitService importDocUnitService;

    public DocUnitWrapper convert(final CSVRecord record,
                                  final CSVMapping mapping,
                                  final CSVRecord header,
                                  final String parentKeyExpr,
                                  final Map<String, DocPropertyType> propertyTypes) {

        final DocUnit docUnit = initDocUnit(mapping.getLibrary(), BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);
        final BibliographicRecord bibRecord = Iterables.getOnlyElement(docUnit.getRecords());

        final Map<String, String> attributes = getLine(header, record);

        // Propriétés
        attributes.forEach((key, value) -> {
            if (key.startsWith("dc:") && propertyTypes.keySet().contains(key.substring(3))) {
                DocProperty property = new DocProperty();
                property.setType(propertyTypes.get(key.substring(3)));
                property.setValue(value);
                bibRecord.addProperty(property);
            }
        });

        // Règles de mapping
        for (CSVMappingRule rule : mapping.getRules()) {
            if ("digitalId".equals(rule.getDocUnitField())) {
                setObjectField(Iterables.getOnlyElement(docUnit.getPhysicalDocuments()),
                               rule.getDocUnitField(),
                               Collections.singletonList(attributes.get(rule.getCsvField())));
            } else {
                setObjectField(docUnit, rule.getDocUnitField(), Collections.singletonList(attributes.get(rule.getCsvField())));
            }
        }

        setRanks(bibRecord);

        final DocUnitWrapper wrapper = new DocUnitWrapper();
        wrapper.setDocUnit(docUnit);
        // Clé parent
        if (StringUtils.isNotBlank(parentKeyExpr) && attributes.containsKey(parentKeyExpr)) {
            wrapper.setParentKey(attributes.get(parentKeyExpr));
        }
        return wrapper;
    }

    private Map<String, String> getLine(final CSVRecord header, final CSVRecord record) {
        final Map<String, String> line = new HashMap<>();

        for (int i = 0; i < record.size(); i++) {
            if (i < header.size() && StringUtils.isNotEmpty(record.get(i))) {
                final String key = header.get(i);
                final String value = record.get(i);

                line.putIfAbsent(key, value);
            }
        }
        return line;
    }
}
