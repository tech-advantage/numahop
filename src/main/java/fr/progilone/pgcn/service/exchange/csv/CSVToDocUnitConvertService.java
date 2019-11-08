package fr.progilone.pgcn.service.exchange.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.exchange.CSVMappingRule;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;
import fr.progilone.pgcn.service.exchange.marc.DocUnitWrapper;

@Service
public class CSVToDocUnitConvertService extends AbstractImportConvertService {

    

    public DocUnitWrapper convert(final CSVRecord record,
                                  final CSVMapping mapping,
                                  final CSVRecord header,
                                  final String parentKeyExpr,
                                  final Map<String, DocPropertyType> propertyTypes, 
                                  final List<String> entetes) {

        final DocUnit docUnit = initDocUnit(mapping.getLibrary(), BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);
        final BibliographicRecord bibRecord = Iterables.getOnlyElement(docUnit.getRecords());

       // final Map<String, String> attributes = getLine(header, record);
        
        final Map<String, String> attributes = getLineFromMappedHeader(entetes, record);

        // Propriétés
        attributes.forEach((colKey, value) -> {
            // on veut la cle sans le prefixe colx_
            final String key = colKey.substring(colKey.indexOf('_') + 1);  
            
            // Propriétés
            if (key.startsWith("dc:") 
                        && propertyTypes.keySet().contains(key.substring(3))) {
                
                final DocProperty property = new DocProperty();
                property.setType(propertyTypes.get(key.substring(3)));
                property.setValue(value);
                bibRecord.addProperty(property);
                
            } else if (propertyTypes.keySet().contains(key)) { 
                
                final DocProperty property = new DocProperty();
                property.setType(propertyTypes.get(key));
                property.setValue(value);
                bibRecord.addProperty(property);
            }
        });
        
        final List<String> unusedKeys = new ArrayList<String>(attributes.keySet());
        unusedKeys.sort(Comparator.naturalOrder());
        
        final List<CSVMappingRule> rules = mapping.getRules();
        rules.sort(Comparator.comparing(CSVMappingRule::getRank));

        // Règles de mapping
        for (final CSVMappingRule rule : mapping.getRules()) {
            
            // retrouve la clé avec son prefixe colx_ qui correspond à la regle de mapping.
            final String realKey = unusedKeys.stream()
                                .filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField()))
                                .findFirst().orElse(null);
            
            if (realKey != null) {
                if ("digitalId".equals(rule.getDocUnitField())) {
                    setObjectField(Iterables.getOnlyElement(docUnit.getPhysicalDocuments()),
                                   rule.getDocUnitField(),
                                   Collections.singletonList(attributes.get(realKey)));
                } else {
                    if (rule.getDocUnitField() != null) {
                        setObjectField(docUnit, rule.getDocUnitField(), Collections.singletonList(attributes.get(realKey)));
                    }
                    if (rule.getBibRecordField() != null) {
                        setObjectField(bibRecord, rule.getBibRecordField(), Collections.singletonList(attributes.get(realKey)));
                    }    
                }
                unusedKeys.remove(realKey);
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
                // ajout prefixe colx_ pour gerer les proprietes multiples
                final String key = "col" + i + "_" + header.get(i);
                final String value = record.get(i);

                line.putIfAbsent(key, value);
            }
        }
        return line;
    }
    
    private Map<String, String> getLineFromMappedHeader(final List<String> entetes, final CSVRecord record) {
        final Map<String, String> line = new HashMap<>();

        for (int i = 0; i < record.size(); i++) {
            if (i < entetes.size() && StringUtils.isNotEmpty(record.get(i))) {   
                // ajout prefixe colx_ pour gerer les proprietes multiples
                final String key = "col" + i + "_" + entetes.get(i);
                final String value = record.get(i);

                line.putIfAbsent(key, value);
            }
        }
        return line;
    }

    
    
}
