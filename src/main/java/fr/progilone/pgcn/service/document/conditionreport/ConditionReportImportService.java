package fr.progilone.pgcn.service.document.conditionreport;

import static fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail.Type.LIBRARY_LEAVING;

import com.google.common.base.MoreObjects;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReport;
import fr.progilone.pgcn.domain.document.conditionreport.ConditionReportDetail;
import fr.progilone.pgcn.domain.document.conditionreport.Description;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.template.MessageService;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConditionReportImportService {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportImportService.class);

    private static final String PROP_BINDING_OTHER = "BINDING_DESC";
    private static final String PROP_BODY_DESC = "BODY_DESC";
    private static final String PROP_DESC_OTHER = "OTHER";
    private static final String PROP_DIMENSION = "DIMENSION";
    private static final String PROP_INSURANCE = "INSURANCE";
    private static final String PROP_NB_VIEW_BINDING = "nbViewBinding";
    private static final String PROP_NB_VIEW_BODY = "nbViewBody";
    private static final String PROP_NB_VIEW_ADDITIONNAL = "nbViewAdditionnal";
    private static final String TYPE_DETAIL = "DETAIL";

    private final ConditionReportService conditionReportService;
    private final ConditionReportDetailService conditionReportDetailService;
    private final DescriptionPropertyService descriptionPropertyService;
    private final DescriptionValueService descriptionValueService;
    private final DocUnitService docUnitService;
    private final MessageService messageService;
    private final PropertyConfigurationService propertyConfigurationService;

    @Autowired
    public ConditionReportImportService(final ConditionReportService conditionReportService,
                                        final ConditionReportDetailService conditionReportDetailService,
                                        final DescriptionPropertyService descriptionPropertyService,
                                        final DescriptionValueService descriptionValueService,
                                        final DocUnitService docUnitService,
                                        final MessageService messageService,
                                        final PropertyConfigurationService propertyConfigurationService) {
        this.conditionReportService = conditionReportService;
        this.conditionReportDetailService = conditionReportDetailService;
        this.descriptionPropertyService = descriptionPropertyService;
        this.descriptionValueService = descriptionValueService;
        this.docUnitService = docUnitService;
        this.messageService = messageService;
        this.propertyConfigurationService = propertyConfigurationService;
    }

    /**
     * Import de constats d'état à partir du fichier excel
     *
     * @param in
     * @throws IOException
     * @throws InvalidFormatException
     */
    @Transactional
    public List<ImportResult> importReport(final InputStream in, final Predicate<DocUnit> checkAccess) throws IOException, InvalidFormatException {
        final List<ImportResult> importResults = new ArrayList<>();
        final List<DescriptionProperty> descProperties = descriptionPropertyService.findAll();
        final List<DescriptionValue> descValues = descriptionValueService.findAll();
        final Map<Library, List<PropertyConfiguration>> confCache = Collections.synchronizedMap(new HashMap<>()); // cache des PropertyConfiguration

        new SheetReader().setDescProperties(descProperties).setDescValues(descValues).filter(checkAccess).stream(in).process((report, detail) -> {
            final List<PropertyConfiguration> confs = confCache.computeIfAbsent(report.getDocUnit().getLibrary(), propertyConfigurationService::findByLibrary);
            final PgcnList<PgcnError> pgcnErrors = conditionReportDetailService.validateSave(detail, confs);

            if (pgcnErrors.isEmpty()) {
                final ConditionReport savedReport = conditionReportService.save(report);
                detail.setReport(savedReport);
                conditionReportDetailService.save(detail, false);

                final ImportResult importResult = new ImportResult();
                importResult.setReportId(savedReport.getIdentifier());
                importResult.setDocUnitId(report.getDocUnit().getIdentifier());
                importResult.setPgcnId(report.getDocUnit().getPgcnId());
                importResults.add(importResult);

            } else {
                LOG.error("Le constat d'état {} ({}) n'est pas sauvegardé car il n'est pas valide: {}",
                          report.getDocUnit().getPgcnId(),
                          report.getDocUnit().getIdentifier(),
                          StringUtils.join(pgcnErrors, ", "));

                final ImportResult importResult = new ImportResult();
                importResult.setDocUnitId(report.getDocUnit().getIdentifier());
                importResult.setPgcnId(report.getDocUnit().getPgcnId());
                importResult.setErrors(pgcnErrors.get());
                importResults.add(importResult);
            }
        });
        return importResults;
    }

    /**
     * Classe gérant la lecture du fichier excel, et la création / mise à jour des constats d'état
     */
    private class SheetReader {

        private final List<DescriptionProperty> descProperties = new ArrayList<>();
        private final List<DescriptionValue> descValues = new ArrayList<>();

        private Workbook wb;
        private Predicate<DocUnit> filter = (report) -> true;

        /**
         * Initialisation du workbook à partir d'un flux
         *
         * @param in
         * @return
         * @throws IOException
         * @throws InvalidFormatException
         */
        public SheetReader stream(final InputStream in) throws IOException, InvalidFormatException {
            this.wb = WorkbookFactory.create(in);
            return this;
        }

        /**
         * Traitement du workbook
         * Création/Mise à jour du constat d'état, puis appel de la fonction de mise à jour si tout s'est bien passé
         *
         * @param updateFn
         * @return
         */
        public SheetReader process(final BiConsumer<ConditionReport, ConditionReportDetail> updateFn) {
            this.wb.sheetIterator().forEachRemaining(sheet -> this.readSheet(sheet, updateFn));
            return this;
        }

        /**
         * Lecture de la feuille et création / mise à jour du constat d'état
         *
         * @param sheet
         */
        private void readSheet(final Sheet sheet, final BiConsumer<ConditionReport, ConditionReportDetail> updateFn) {
            // docunit identifier
            final Optional<String> optId = getCell(sheet, 0, 0).map(Cell::getStringCellValue);
            if (!optId.isPresent()) {
                LOG.warn("L'identifiant de l'unité documentaire n'est pas renseigné; feuille {}, cellule [0;0]", sheet.getSheetName());
                return;
            }
            final String docUnitId = optId.get();

            // import = oui
            final Optional<String> optAction = getCell(sheet, 0, 3).map(Cell::getStringCellValue);
            if (!optAction.isPresent() || !StringUtils.equals(optAction.get(), messageService.getMessage("condreport", "validation.boolean.true"))) {
                LOG.debug("L'import du constat d'état pour l'unité documentaire {} est ignoré", docUnitId);
                return;
            }

            // Recherche de l'unité documentaire
            final DocUnit docUnit = docUnitService.findOne(docUnitId);
            if (docUnit == null) {
                LOG.error("L'unité documentaire {} n'existe pas", docUnitId);
                return;
            }
            // Vérification des droits d'accès
            if (!filter.test(docUnit)) {
                LOG.error("L'accès à l'unité documentaire {} n'est pas autorisé pour cet utilisateur", docUnitId);
                return;
            }

            // Création du constat d'état
            ConditionReport report = conditionReportService.findByDocUnit(docUnitId);
            if (report == null) {
                report = conditionReportService.getNewConditionReport(docUnit);
            }
            final ConditionReport fReport = report;
            // 1er étape du constat d'état
            final ConditionReportDetail detail = report.getDetails()
                                                       .stream()
                                                       .filter(d -> d.getType() == LIBRARY_LEAVING)
                                                       .findFirst()
                                                       .orElseGet(() -> conditionReportDetailService.getNewDetail(LIBRARY_LEAVING, fReport, 0));

            LOG.debug("Création/mise à jour du constat d'état de l'unité documentaire {} ({})", docUnit.getPgcnId(), docUnit.getIdentifier());
            // Lecture des valeurs saisies
            final List<ImportData> data = parseSheet(sheet);

            // Copie des données dans le constat d'état
            setDescriptions(detail, data);
            setDetails(detail, data);

            updateFn.accept(report, detail);
        }

        /**
         * Lecture des valeurs saisies dans la feuille
         *
         * @param sheet
         * @return
         */
        private List<ImportData> parseSheet(final Sheet sheet) {
            final Iterator<Row> rowIterator = sheet.rowIterator();
            final List<ImportData> data = new ArrayList<>();

            while (rowIterator.hasNext()) {
                final Row row = rowIterator.next();
                // on bypass la 1e ligne (qui contient l'identifiant de l'unité documentaire)
                if (row.getRowNum() == 0) {
                    continue;
                }

                final Optional<String> optType = Optional.ofNullable(row.getCell(0)).map(Cell::getStringCellValue);
                final Optional<String> optProp = Optional.ofNullable(row.getCell(1)).map(Cell::getStringCellValue);
                // type, valeur doivent être définis
                if (!optType.isPresent() || !optProp.isPresent()) {
                    continue;
                }

                final ImportData importData = new ImportData();
                importData.setType(optType.get());
                importData.setProp(optProp.get());
                data.add(importData);

                // Les valeurs sont dans les cellules 3 à 5
                for (int i = 0; i < 3; i++) {
                    final Cell cell = row.getCell(i + 3);

                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                importData.setValue(i, cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                importData.setDblValue(i, cell.getNumericCellValue());
                                break;
                        }
                    }
                }
            }
            return data;
        }

        private void setDescriptions(final ConditionReportDetail detail, final List<ImportData> data) {
            final Set<Description> descriptions = data.stream()
                                                      // Filtrage sur le type de données importée
                                                      .filter(d -> Arrays.stream(DescriptionProperty.Type.values()).anyMatch(type -> StringUtils.equals(type.name(), d.getType())))
                                                      // Construction de l'objet Description
                                                      .map(d -> {
                                                          // Propriété
                                                          final Optional<DescriptionProperty> propOpt = this.descProperties.stream()
                                                                                                                           .filter(prop -> StringUtils.equals(prop.getIdentifier(),
                                                                                                                                                              d.getProp()))
                                                                                                                           .findAny();

                                                          if (!propOpt.isPresent()) {
                                                              LOG.error("La propriété {} n'est pas supportée", d.getProp());
                                                              return null;
                                                          }
                                                          final DescriptionProperty property = propOpt.get();

                                                          // Valeur (liste), Valeur (libre)
                                                          final String listValue = d.getValues()[0];
                                                          final String freeValue = d.getValue(1);
                                                          if (StringUtils.isBlank(listValue) && StringUtils.isBlank(freeValue)) {
                                                              return null;
                                                          }

                                                          // Description de reliure
                                                          final Description description = new Description();
                                                          description.setProperty(property);
                                                          getDescriptionValue(property, listValue).ifPresent(description::setValue);
                                                          description.setComment(freeValue);
                                                          return description;

                                                      })
                                                      .filter(Objects::nonNull)
                                                      .collect(Collectors.toSet());
            detail.setDescriptions(descriptions);
        }

        private void setDetails(final ConditionReportDetail detail, final List<ImportData> data) {
            data.stream()
                // Filtrage dur le type de données importée
                .filter(d -> StringUtils.equals(TYPE_DETAIL, d.getType()))
                // Set values
                .forEach(d -> {
                    final String value = d.getValues()[0];
                    final double dValue = d.getDblValues()[0];

                    switch (d.getProp()) {
                        case PROP_BINDING_OTHER:
                            if (StringUtils.isNotBlank(value)) {
                                detail.setBindingDesc(value);
                            }
                            break;
                        case PROP_DESC_OTHER:
                            if (StringUtils.isNotBlank(value)) {
                                detail.setAdditionnalDesc(value);
                            }
                            break;
                        case PROP_DIMENSION:
                            final double[] dblValues = d.getDblValues();
                            detail.setDim1((int) dblValues[0]);
                            detail.setDim2((int) dblValues[1]);
                            detail.setDim3((int) dblValues[2]);
                            break;
                        case PROP_INSURANCE:
                            detail.setInsurance(dValue);
                            break;
                        case PROP_NB_VIEW_BINDING:
                            detail.setNbViewBinding((int) dValue);
                            break;
                        case PROP_NB_VIEW_BODY:
                            detail.setNbViewBody((int) dValue);
                            break;
                        case PROP_NB_VIEW_ADDITIONNAL:
                            detail.setNbViewAdditionnal((int) dValue);
                            break;
                        case PROP_BODY_DESC:
                            if (StringUtils.isNotBlank(value)) {
                                detail.setBodyDesc(value);
                            }
                            break;
                        default:
                            LOG.error("La propriété {} n'est pas supportée", d.getProp());
                    }
                });
        }

        private Optional<Cell> getCell(final Sheet sheet, final int rowNb, final int cellNb) {
            final Row row = sheet.getRow(rowNb);
            if (row == null) {
                return Optional.empty();
            }
            final Cell cell = row.getCell(cellNb);
            return Optional.ofNullable(cell);
        }

        public SheetReader filter(final Predicate<DocUnit> filter) {
            this.filter = filter;
            return this;
        }

        public SheetReader setDescProperties(final List<DescriptionProperty> descProperties) {
            this.descProperties.clear();
            this.descProperties.addAll(descProperties);
            return this;
        }

        public SheetReader setDescValues(final List<DescriptionValue> descValues) {
            this.descValues.clear();
            this.descValues.addAll(descValues);
            return this;
        }

        private Optional<DescriptionValue> getDescriptionValue(final DescriptionProperty property, final String value) {
            if (StringUtils.isBlank(value)) {
                return Optional.empty();
            }
            return this.descValues.stream()
                                  .filter(descValue -> StringUtils.equals(descValue.getProperty().getIdentifier(), property.getIdentifier()) && StringUtils.equals(descValue
                                                                                                                                                                            .getLabel(),
                                                                                                                                                                   value))
                                  .findAny();
        }
    }

    /**
     * Classe représentant les valeurs saisies dans le fichiers excel d'import
     */
    private static class ImportData {

        private String type;
        private String prop;
        private final String[] values = new String[3];
        private final Double[] dblValues = new Double[3];

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public String getProp() {
            return prop;
        }

        public void setProp(final String prop) {
            this.prop = prop;
        }

        public String[] getValues() {
            return values;
        }

        public void setValue(final int idx, final String value) {
            this.values[idx] = value;
        }

        public double[] getDblValues() {
            return Arrays.stream(dblValues)
                         .mapToDouble(dbl -> dbl == null ? 0D
                                                         : dbl)
                         .toArray();
        }

        public void setDblValue(final int idx, final Double dblValue) {
            this.dblValues[idx] = dblValue;
        }

        public String getValue(final int idx) {
            return values[idx] != null ? values[idx] : dblValues[idx] != null ? String.valueOf(dblValues[idx]) : null;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                              .omitNullValues()
                              .add("type", type)
                              .add("prop", prop)
                              .add("values", streamToString(Stream.of(values)))
                              .add("dblValues", streamToString(Stream.of(dblValues)))
                              .toString();
        }

        private String streamToString(final Stream<?> values) {
            return values.filter(Objects::nonNull)
                         .map(String::valueOf)
                         .reduce((a, b) -> a + ", "
                                           + b)
                         .orElse(null);
        }
    }

    /**
     * Classe représentant le résultat de l'import
     */
    public static class ImportResult {

        private String reportId;
        private String docUnitId;
        private String pgcnId;
        private Collection<PgcnError> errors;

        public String getReportId() {
            return reportId;
        }

        public void setReportId(final String reportId) {
            this.reportId = reportId;
        }

        public String getDocUnitId() {
            return docUnitId;
        }

        public void setDocUnitId(final String docUnitId) {
            this.docUnitId = docUnitId;
        }

        public String getPgcnId() {
            return pgcnId;
        }

        public void setPgcnId(final String pgcnId) {
            this.pgcnId = pgcnId;
        }

        public Collection<PgcnError> getErrors() {
            return errors;
        }

        public void setErrors(final Collection<PgcnError> errors) {
            this.errors = errors;
        }
    }
}
