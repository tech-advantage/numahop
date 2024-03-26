package fr.progilone.pgcn.service.exchange.csv;

import com.google.common.collect.Iterables;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocProperty;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.*;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.exchange.CSVMappingRule;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataProperty;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataValue;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.service.document.conditionreport.ConditionReportDetailService;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionPropertyService;
import fr.progilone.pgcn.service.document.conditionreport.DescriptionValueService;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;
import fr.progilone.pgcn.service.exchange.marc.DocUnitWrapper;
import fr.progilone.pgcn.service.imagemetadata.ImageMetadataService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CSVToDocUnitConvertService extends AbstractImportConvertService {

    private final DescriptionPropertyService descriptionPropertyService;
    private final DescriptionValueService descriptionValueService;
    private final ImageMetadataService imageMetadataService;
    private final ConditionReportDetailService conditionReportDetailService;

    private static final Logger LOG = LoggerFactory.getLogger(CSVToDocUnitConvertService.class);

    @Autowired
    public CSVToDocUnitConvertService(DescriptionValueService descriptionValueService,
                                      DescriptionPropertyService descriptionPropertyService,
                                      ImageMetadataService imageMetadataService,
                                      ConditionReportDetailService conditionReportDetailService) {
        this.descriptionValueService = descriptionValueService;
        this.descriptionPropertyService = descriptionPropertyService;
        this.imageMetadataService = imageMetadataService;
        this.conditionReportDetailService = conditionReportDetailService;
    }

    public DocUnitWrapper convert(final CSVRecord record,
                                  final CSVMapping mapping,
                                  final CSVRecord header,
                                  final String parentKeyExpr,
                                  final Map<String, DocPropertyType> propertyTypes,
                                  final Map<Integer, String> entetes,
                                  final boolean archivable,
                                  final boolean distributable) {

        final DocUnit docUnit = initDocUnit(mapping.getLibrary(), BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);
        final BibliographicRecord bibRecord = Iterables.getOnlyElement(docUnit.getRecords());

        docUnit.setArchivable(archivable);
        docUnit.setDistributable(distributable);

        final Map<String, String> attributes = getLineFromMappedHeader(entetes, record);

        // Propriétés
        attributes.forEach((colKey, value) -> {
            // on veut la cle sans le prefixe colx_
            final String key = colKey.substring(colKey.indexOf('_') + 1);

            // Propriétés
            if (key.startsWith("dc:") && propertyTypes.keySet().contains(key.substring(3))) {

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
            final String realKey = unusedKeys.stream().filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField())).findFirst().orElse(null);

            if (realKey != null) {
                if ("digitalId".equals(rule.getDocUnitField())) {
                    setObjectField(Iterables.getOnlyElement(docUnit.getPhysicalDocuments()), rule.getDocUnitField(), Collections.singletonList(attributes.get(realKey)));
                } else {
                    if (rule.getDocUnitField() != null) {
                        setObjectField(docUnit, rule.getDocUnitField(), Collections.singletonList(attributes.get(realKey)));
                    }
                    if (rule.getBibRecordField() != null) {
                        setObjectField(bibRecord, rule.getBibRecordField(), Collections.singletonList(attributes.get(realKey)));
                    }
                }
            }

        }

        setRanks(bibRecord);
        final DocUnitWrapper wrapper = new DocUnitWrapper();
        wrapper.setDocUnit(docUnit);
        // Clé parent
        if (StringUtils.isNotBlank(parentKeyExpr) && attributes.keySet().stream().anyMatch(key -> StringUtils.contains(key, parentKeyExpr))) {
            final String parentKey = attributes.keySet().stream().filter(key -> StringUtils.contains(key, parentKeyExpr)).findFirst().get();
            wrapper.setParentKey(attributes.get(parentKey));
        }
        return wrapper;
    }

    /**
     * Create CondReport and all the child objects
     *
     * @param record
     * @param mapping
     * @param entetes
     * @return
     */
    protected List<Object> convertCondReport(final CSVRecord record,
                                             final CSVMapping mapping,
                                             final Map<Integer, String> entetes,
                                             final PgcnList<PgcnError> errors,
                                             ConditionReport report) {
        List<Object> condReportValues = new ArrayList();
        final PgcnError.Builder builder = new PgcnError.Builder();

        ConditionReportDetail condReportDetail = conditionReportDetailService.getNewDetailWithoutWriters(ConditionReportDetail.Type.LIBRARY_LEAVING, report, 0);
        ;

        final Map<String, String> attributes = getLineFromMappedHeader(entetes, record);

        final List<String> unusedKeys = new ArrayList<String>(attributes.keySet());
        unusedKeys.sort(Comparator.naturalOrder());

        final List<CSVMappingRule> rules = mapping.getRules();
        rules.sort(Comparator.comparing(CSVMappingRule::getRank));

        // Règles de mapping
        for (final CSVMappingRule rule : mapping.getRules()) {

            // retrouve la clé avec son prefixe colx_ qui correspond à la regle de mapping.
            final String realKey = unusedKeys.stream().filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField())).findFirst().orElse(null);

            String property = rule.getCondReport();

            if (realKey != null && property != null) {
                String recordValue = attributes.get(realKey);

                if (EnumUtils.isValidEnum(DescriptionPropertyService.FakeDescriptionProperty.class, property)) {
                    if (condReportDetail == null)
                        condReportDetail = new ConditionReportDetail();

                    DescriptionPropertyService.FakeDescriptionProperty fakeDescProp = DescriptionPropertyService.FakeDescriptionProperty.valueOf(property);

                    try {
                        switch (fakeDescProp) {
                            case INSURANCE:
                                condReportDetail.setInsurance(Double.parseDouble(recordValue));
                                break;
                            case NB_VIEW_BODY:
                                condReportDetail.setNbViewBody(Integer.parseInt(recordValue));
                                break;
                            case NB_VIEW_BINDING:
                                condReportDetail.setNbViewBinding(Integer.parseInt(recordValue));
                                break;
                            case NB_VIEW_ADDITIONNAL:
                                condReportDetail.setNbViewAdditionnal(Integer.parseInt(recordValue));
                                break;
                            case ADDITIONNAL_DESC:
                                condReportDetail.setAdditionnalDesc(recordValue);
                                break;
                            case BINDING_DESC:
                                condReportDetail.setBindingDesc(recordValue);
                                break;
                            case BODY_DESC:
                                condReportDetail.setBodyDesc(recordValue);
                                break;
                            case DIMENSION:
                                String[] dimensions = StringUtils.split(recordValue, "/");
                                if (dimensions.length == 3) {
                                    if (!StringUtils.equals(dimensions[0], "-1")) {
                                        condReportDetail.setDim1(Integer.parseInt(dimensions[0]));
                                    }
                                    if (!StringUtils.equals(dimensions[1], "-1")) {
                                        condReportDetail.setDim2(Integer.parseInt(dimensions[1]));
                                    }
                                    if (!StringUtils.equals(dimensions[2], "-1")) {
                                        condReportDetail.setDim3(Integer.parseInt(dimensions[2]));
                                    }
                                } else {
                                    LOG.warn("Impossible to split the dimension values");
                                    errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_DIMENSION).setField("pgcnId").build());
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_BAD_VALUE).setField("pgcnId").build());
                        LOG.error("Impossible to set value to property", e);
                    }

                } else {
                    // get the allowed values
                    DescriptionProperty descProp = descriptionPropertyService.findByIdentifier(property);
                    List<DescriptionValue> allowedValues = descriptionValueService.findByProperty(descProp);
                    // if empty list, value is free to this property
                    if (!allowedValues.isEmpty()) {    // value have to be in the allowedValues
                        Optional<DescriptionValue> findedVal = allowedValues.stream().filter(val -> StringUtils.equalsIgnoreCase(val.getLabel(), recordValue)).findFirst();
                        if (findedVal.isPresent()) {
                            Description desc = new Description();
                            desc.setProperty(descProp);
                            desc.setValue(findedVal.get());
                            condReportValues.add(desc);
                            LOG.warn("Propriété {} mappée", descProp.getLabel());
                        } else if (!checkAndWriteComments(descProp, recordValue, condReportValues)) {
                            LOG.warn("Propriété {} non mappée", descProp.getLabel());
                            errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_NO_VALUE_FOR_PROP).setField("pgcnId").build());
                        }
                    } else if (!checkAndWriteComments(descProp, recordValue, condReportValues)) {
                        LOG.warn("Pas de valeurs associées à la propriété {}", descProp.getLabel());
                        errors.add(builder.reinit().setCode(PgcnErrorCode.CONDREPORT_DETAIL_DESC_BAD_VALUE).setField("pgcnId").build());
                    }
                }
            }
        }

        if (condReportDetail != null) {
            condReportValues.add(condReportDetail);
        }

        return condReportValues;
    }

    /**
     * Check if comments are authorized, if so add it to the description
     *
     * @param descProp
     * @param recordValue
     * @param condReportValues
     * @return true if comments were added, false otherwise
     */
    protected boolean checkAndWriteComments(DescriptionProperty descProp, String recordValue, List<Object> condReportValues) {
        final Optional<PropertyConfiguration> confOpt = descProp.getConfigurations()
                                                                .stream()
                                                                .filter(c -> c.getDescProperty() != null && StringUtils.equals(c.getDescProperty().getIdentifier(),
                                                                                                                               descProp.getIdentifier()))
                                                                .findAny();
        if (confOpt.isPresent() && confOpt.get().isAllowComment() || descProp.isAllowComment()) {
            Description desc = new Description();
            desc.setProperty(descProp);
            desc.setComment(recordValue);
            condReportValues.add(desc);
            LOG.warn("Commentaire {} mappé", descProp.getLabel());
            return true;
        }
        return false;
    }

    /**
     * create metadata values on the doc unit
     *
     * @param record
     * @param mapping
     * @param entetes
     * @param docUnit
     * @return
     */
    protected List<ImageMetadataValue> convertMetadata(final CSVRecord record,
                                                       final CSVMapping mapping,
                                                       final Map<Integer, String> entetes,
                                                       final PgcnList<PgcnError> errors,
                                                       DocUnit docUnit) {
        List<ImageMetadataValue> metadataValues = new ArrayList();
        final PgcnError.Builder builder = new PgcnError.Builder();

        final Map<String, String> attributes = getLineFromMappedHeader(entetes, record);

        final List<String> unusedKeys = new ArrayList<String>(attributes.keySet());
        unusedKeys.sort(Comparator.naturalOrder());

        final List<CSVMappingRule> rules = mapping.getRules();
        rules.sort(Comparator.comparing(CSVMappingRule::getRank));

        // Règles de mapping
        for (final CSVMappingRule rule : mapping.getRules()) {

            // retrouve la clé avec son prefixe colx_ qui correspond à la regle de mapping.
            final String realKey = unusedKeys.stream().filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField())).findFirst().orElse(null);

            String metadataPropId = rule.getMetadata();

            if (realKey != null && metadataPropId != null) {
                String recordValue = attributes.get(realKey);

                ImageMetadataProperty metaProp = imageMetadataService.findPropertyByIdentifier(metadataPropId);

                if (metaProp != null) {
                    // check if meta is repeatable
                    if (metaProp.isRepeat()) {

                        // before add, check if the value and the property type are correct
                        ImageMetadataValue metaValue = new ImageMetadataValue();
                        metaValue.setMetadata(metaProp);
                        metaValue.setDocUnit(docUnit);
                        setImageMetadataValue(metaProp, metaValue, recordValue, errors, builder);
                        if (metaValue.getValue() != null) {
                            metadataValues.add(metaValue);
                        }

                        // search if same key in the list of unusedKeys
                        final List<String> realKeys = unusedKeys.stream()
                                                                .filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField()))
                                                                .collect(Collectors.toList());

                        if (realKeys.size() > 1) {
                            realKeys.remove(0); // this item is already added above

                            realKeys.forEach(key -> {
                                String val = attributes.get(key);

                                // before add, check if the value and the property type are correct
                                ImageMetadataValue metaVal = new ImageMetadataValue();
                                metaVal.setMetadata(metaProp);
                                metaVal.setDocUnit(docUnit);
                                setImageMetadataValue(metaProp, metaVal, val, errors, builder);
                                if (metaVal.getValue() != null) {
                                    metadataValues.add(metaVal);
                                }
                            });
                        }
                    } else {
                        // not repeat so check if already set
                        Optional<ImageMetadataValue> oldMetaValue = metadataValues.stream()
                                                                                  .filter(meta -> StringUtils.equals(meta.getMetadata().getIdentifier(), metaProp.getIdentifier()))
                                                                                  .findFirst();
                        if (oldMetaValue.isPresent()) {
                            ImageMetadataValue oldMetaVal = oldMetaValue.get();
                            LOG.warn("Metadata {} non répétable, la dernière valeur est conservée", metaProp.getLabel());
                            setImageMetadataValue(metaProp, oldMetaVal, recordValue, errors, builder);
                        } else {
                            // before add, check if the value and the property type are correct
                            ImageMetadataValue metaValue = new ImageMetadataValue();
                            metaValue.setMetadata(metaProp);
                            metaValue.setDocUnit(docUnit);
                            setImageMetadataValue(metaProp, metaValue, recordValue, errors, builder);
                            if (metaValue.getValue() != null) {
                                metadataValues.add(metaValue);
                            }
                        }
                    }
                } else {
                    LOG.warn("Pas de metadata property associée au label {}", metadataPropId);
                    errors.add(builder.reinit().setCode(PgcnErrorCode.IMAGE_METADATA_NOT_EXIST).setField("label").build());
                }
            }
        }

        return metadataValues;
    }

    public void checkResolutionData(final CSVRecord record, final CSVMapping mapping, final Map<Integer, String> entetes, PgcnList errors) {
        final PgcnError.Builder builder = new PgcnError.Builder();
        final Map<String, String> attributes = getLineFromMappedHeader(entetes, record);

        final List<String> unusedKeys = new ArrayList<String>(attributes.keySet());
        unusedKeys.sort(Comparator.naturalOrder());

        final List<CSVMappingRule> rules = mapping.getRules();
        rules.sort(Comparator.comparing(CSVMappingRule::getRank));

        // Règles de mapping
        for (final CSVMappingRule rule : mapping.getRules()) {

            // retrouve la clé avec son prefixe colx_ qui correspond à la regle de mapping.
            final String realKey = unusedKeys.stream().filter(k -> StringUtils.equals(k.substring(k.indexOf('_') + 1), rule.getCsvField())).findFirst().orElse(null);

            if (realKey != null && (StringUtils.equals(rule.getCsvField(), "def:imageheight") || StringUtils.equals(rule.getCsvField(), "def:imagewidth"))) {
                try {
                    Integer.parseInt(attributes.get(realKey));
                } catch (NumberFormatException ex) {
                    if (errors == null) {
                        errors = new PgcnList<>();
                    }
                    errors.add(builder.reinit().setCode(PgcnErrorCode.DOC_UNIT_IMAGERESOLUTION).addComplement(rule.getCsvField()).build());
                }
            }
        }

    }

    /**
     * Check if data is conform to the metadataProperty type and set the value
     *
     * @param metaProp
     * @param metaValue
     * @param newValue
     * @param errors
     * @param builder
     */
    private void setImageMetadataValue(ImageMetadataProperty metaProp, ImageMetadataValue metaValue, String newValue, PgcnList<PgcnError> errors, PgcnError.Builder builder) {
        switch (metaProp.getType()) {
            case STRING:
                metaValue.setValue(newValue);
                break;

            case INTEGER:
                try {
                    Integer.parseInt(newValue);
                    metaValue.setValue(newValue);
                } catch (NumberFormatException e) {
                    errors.add(builder.reinit()
                                      .setCode(PgcnErrorCode.IMAGE_METADATA_CANT_PARSE)
                                      .addComplement("Metadonnée: " + metaProp.getLabel()
                                                     + " de type: "
                                                     + metaProp.getType().toString()
                                                     + ", valeur trouvée: "
                                                     + newValue)
                                      .build());
                }
                break;

            case REAL:
                try {
                    Double.parseDouble(newValue);
                    metaValue.setValue(newValue);
                } catch (NumberFormatException e) {
                    errors.add(builder.reinit()
                                      .setCode(PgcnErrorCode.IMAGE_METADATA_CANT_PARSE)
                                      .addComplement("Metadonnée: " + metaProp.getLabel()
                                                     + " de type: "
                                                     + metaProp.getType().toString()
                                                     + ", valeur trouvée: "
                                                     + newValue)
                                      .build());
                }
                break;

            case BOOLEAN:
                if (StringUtils.equalsIgnoreCase(newValue, "true") || StringUtils.equalsIgnoreCase(newValue, "false")) {
                    metaValue.setValue(newValue.toLowerCase());
                } else {
                    errors.add(builder.reinit()
                                      .setCode(PgcnErrorCode.IMAGE_METADATA_CANT_PARSE)
                                      .addComplement("Metadonnée: " + metaProp.getLabel()
                                                     + " de type: "
                                                     + metaProp.getType().toString()
                                                     + ", valeur trouvée: "
                                                     + newValue)
                                      .build());
                }
                break;

            case DATE:
                try {
                    LocalDate.parse(newValue);
                    metaValue.setValue(newValue);
                } catch (DateTimeParseException e) {
                    errors.add(builder.reinit()
                                      .setCode(PgcnErrorCode.IMAGE_METADATA_CANT_PARSE)
                                      .addComplement("Metadonnée: " + metaProp.getLabel()
                                                     + " de type: "
                                                     + metaProp.getType().toString()
                                                     + ", valeur trouvée: "
                                                     + newValue)
                                      .build());
                }
                break;

            default:
                break;
        }
    }

    private Map<String, String> getLine(final CSVRecord header, final CSVRecord record) {
        final Map<String, String> line = new HashMap<>();

        for (int i = 0; i < record.size(); i++) {
            if (i < header.size() && StringUtils.isNotEmpty(record.get(i))) {
                // ajout prefixe colx_ pour gerer les proprietes multiples
                final String key = "col" + i
                                   + "_"
                                   + header.get(i);
                final String value = record.get(i);

                line.putIfAbsent(key, value);
            }
        }
        return line;
    }

    private Map<String, String> getLineFromMappedHeader(final Map<Integer, String> entetes, final CSVRecord record) {
        final Map<String, String> line = new HashMap<>();

        final int maxEntete = entetes.keySet().stream().max(Comparator.naturalOrder()).get();

        for (int i = 0; i < record.size(); i++) {
            if (i <= maxEntete && StringUtils.isNotEmpty(record.get(i))
                && entetes.get(i) != null) {
                // ajout prefixe colx_ pour gerer les proprietes multiples
                final String key = "col" + i
                                   + "_"
                                   + entetes.get(i);
                final String value = record.get(i);

                line.putIfAbsent(key, value);
            }
        }
        return line;
    }

}
