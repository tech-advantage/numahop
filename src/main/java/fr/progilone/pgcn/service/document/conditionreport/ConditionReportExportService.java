package fr.progilone.pgcn.service.document.conditionreport;

import static fr.progilone.pgcn.service.document.conditionreport.ConditionReportExportService.WorkbookFormat.*;
import static org.apache.poi.ss.usermodel.DataValidationConstraint.OperatorType.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionProperty;
import fr.progilone.pgcn.domain.document.conditionreport.DescriptionValue;
import fr.progilone.pgcn.domain.document.conditionreport.PropertyConfiguration;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.template.MessageService;

@Service
public class ConditionReportExportService {

    private static final Logger LOG = LoggerFactory.getLogger(ConditionReportExportService.class);

    private static final String PROP_DESC_OTHER = "OTHER";
    private static final String PROP_INSURANCE = "INSURANCE";
    private static final String PROP_NB_VIEW_BINDING = "nbViewBinding";
    private static final String PROP_NB_VIEW_BODY = "nbViewBody";
    private static final String PROP_NB_VIEW_ADDITIONNAL = "nbViewAdditionnal";
    private static final String SHEET_CONFIGURATION = "Configuration";
    private static final String TYPE_DETAIL = "DETAIL";

    private final DescriptionPropertyService descriptionPropertyService;
    private final DescriptionValueService descriptionValueService;
    private final DocUnitService docUnitService;
    private final MessageService messageService;
    private final PropertyConfigurationService propertyConfigurationService;

    public enum WorkbookFormat {
        XLS,
        XLSX
    }

    @Autowired
    public ConditionReportExportService(final DescriptionPropertyService descriptionPropertyService,
                                        final DescriptionValueService descriptionValueService,
                                        final DocUnitService docUnitService,
                                        final MessageService messageService,
                                        final PropertyConfigurationService propertyConfigurationService) {
        this.descriptionPropertyService = descriptionPropertyService;
        this.descriptionValueService = descriptionValueService;
        this.docUnitService = docUnitService;
        this.messageService = messageService;
        this.propertyConfigurationService = propertyConfigurationService;
    }

    /**
     * Génération du fichier qui servira de modèle d'import des constats d'état des unités documentaires passées en paramètres
     *
     * @param out
     * @param docUnitIds
     * @param format
     * @throws IOException
     */
    @Transactional(readOnly = true)
    public void writeReportTemplate(final OutputStream out, final List<String> docUnitIds, final WorkbookFormat format) throws IOException {
        final List<DescriptionProperty> descProperties = descriptionPropertyService.findAllOrderByOrder();
        final List<DescriptionValue> descValues = descriptionValueService.findAll();

        final TemplateBuilder templateBuilder = new TemplateBuilder(format).setDescProperties(descProperties).setDescValues(descValues);
        templateBuilder.initWorkbook();

        // Unités documentaires
        final Set<DocUnit> docUnits = docUnitService.findAllById(docUnitIds);

        for (final DocUnit docUnit : docUnits) {
            final List<PropertyConfiguration> configurations = propertyConfigurationService.findByLibrary(docUnit.getLibrary());
            templateBuilder.buildSheet(docUnit, configurations);
        }
        templateBuilder.write(out);
    }

    /**
     * Classe regroupant toutes les opérations de génération du modèle d'import de constat d'état
     */
    private final class TemplateBuilder {

        private final WorkbookFormat format;
        private final Map<String, DataValidationConstraint> descriptionsConstraints = new HashMap<>();
        private final List<DescriptionProperty> descProperties = new ArrayList<>();
        private final List<DescriptionValue> descValues = new ArrayList<>();

        private Workbook wb;
        private final Object validationHelper;
        private final Map<Style, CellStyle> styles = new HashMap<>();

        private TemplateBuilder(final WorkbookFormat format) {
            this.format = format;
            this.validationHelper = format == XLS ? new HSSFDataValidationHelper(null) : new XSSFDataValidationHelper(null);
        }

        /**
         * Initialisation du classeur
         */
        public void initWorkbook() {
            this.wb = format == XLS ? new HSSFWorkbook() : new XSSFWorkbook();
            initStyles();
            buildConfSheet();
        }

        /**
         * Feuille de configuration (plages nommées des listes déroulantes)
         */
        private void buildConfSheet() {
            final Sheet sheet = wb.createSheet(SHEET_CONFIGURATION);
            int rowNb = 0;

            for (final DescriptionProperty property : descProperties) {
                final AtomicInteger colNb = new AtomicInteger();
                final Row row = sheet.createRow(rowNb);

                // ppté / valeurs
                createStyledCell(row, colNb.getAndIncrement(), property.getLabel(), Style.BOLD);
                descValues.stream()
                          .filter(val -> StringUtils.equals(val.getProperty().getIdentifier(), property.getIdentifier()))
                          .forEach(val -> row.createCell(colNb.getAndIncrement()).setCellValue(val.getLabel()));

                // plage nommée
                final Name named = wb.createName();
                named.setNameName(getNameName(property));
                final CellRangeAddress adr = new CellRangeAddress(rowNb, rowNb, 1, colNb.get());
                named.setRefersToFormula(adr.formatAsString(SHEET_CONFIGURATION, true));

                rowNb++;

                // Validation des listes déroulante
                final DataValidationConstraint constraint = format == XLS ?
                                                            ((HSSFDataValidationHelper) validationHelper).createFormulaListConstraint(getNameName(
                                                                property)) :
                                                            ((XSSFDataValidationHelper) validationHelper).createFormulaListConstraint(getNameName(
                                                                property));
                descriptionsConstraints.put(property.getIdentifier(), constraint);
            }
        }

        /**
         * Nom d'une plage nommée liée à une liste de valeurs
         *
         * @param property
         * @return
         */
        private String getNameName(final DescriptionProperty property) {
            return "PROP_" + property.getIdentifier().replaceAll("\\W+", "_");
        }

        /**
         * Création de la feuille correspondant à l'unité documentaire
         *
         * @param docUnit
         */
        public void buildSheet(final DocUnit docUnit, final List<PropertyConfiguration> configurations) {
            final Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(docUnit.getPgcnId()));
            short rowNb = 0;

            // Identification du document
            rowNb = buildDocumentRows(sheet, rowNb, docUnit);
            rowNb++;

            // Entête
            final Row row = sheet.createRow(rowNb++);
            createStyledCell(row, 3, messageService.getMessage("condreport", "column.value"), Style.BOLD);
            createStyledCell(row, 4, messageService.getMessage("condreport", "column.comment"), Style.BOLD);

            // Valeur d'assurance
            buildInsuranceBlock(sheet, rowNb);
            rowNb += 2;
            // Type de document
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.type"),
                                     DescriptionProperty.Type.TYPE,
                                     descProperties,
                                     configurations);
            // Descriptions
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.description"),
                                     DescriptionProperty.Type.DESCRIPTION,
                                     descProperties,
                                     configurations);
            // Etat du document
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.state"),
                                     DescriptionProperty.Type.STATE,
                                     descProperties,
                                     configurations);
            // Dimensions du document
            rowNb = buildDimensionBlock(sheet, rowNb, PropertyConfiguration.InternalProperty.DIMENSION, configurations);
            // État de la reliure
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.binding"),
                                     DescriptionProperty.Type.BINDING,
                                     descProperties,
                                     configurations);
            rowNb = buildInfoBlock(sheet, rowNb, PropertyConfiguration.InternalProperty.BINDING_DESC, configurations);
            // Numérotation
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.numbering"),
                                     DescriptionProperty.Type.NUMBERING,
                                     descProperties,
                                     configurations);
            // Estimation du nombre de vues
            rowNb = buildViewBlock(sheet, ++rowNb);
            rowNb = buildInfoBlock(sheet, ++rowNb, PROP_DESC_OTHER);
            // Points de vigilance
            rowNb = buildDetailBlock(sheet,
                                     ++rowNb,
                                     messageService.getMessage("condreport", "title.vigilance"),
                                     DescriptionProperty.Type.VIGILANCE,
                                     descProperties,
                                     configurations);
            rowNb = buildInfoBlock(sheet, rowNb, PropertyConfiguration.InternalProperty.BODY_DESC, configurations);

            // Configuration des colonnes
            sheet.setColumnHidden(0, true);
            sheet.setColumnHidden(1, true);
            sheet.autoSizeColumn(2);
            sheet.setColumnWidth(3, 10000);
            sheet.setColumnWidth(4, 25000);

            sheet.setActiveCell(new CellAddress("D9"));
        }

        /**
         * Informations générale concernant l'unité documentaire
         *
         * @param sheet
         * @param rowNb
         * @param docUnit
         * @return
         */
        private short buildDocumentRows(final Sheet sheet, short rowNb, final DocUnit docUnit) {
            Row row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(docUnit.getIdentifier());    // identifiant interne
            createStyledCell(row, 2, messageService.getMessage("condreport", "title.docunit.import"), Style.BOLD_COLOR);
            createStyledCell(row, 3, messageService.getMessage("condreport", "validation.boolean.true"), Style.BOLD_COLOR);
            addBooleanValidationConstraint(sheet, new CellRangeAddressList(rowNb, rowNb, 3, 3));
            rowNb += 2;

            row = sheet.createRow(rowNb);
            createStyledCell(row, 2, messageService.getMessage("condreport", "title.docunit"), Style.BOLD);
            rowNb++;

            row = sheet.createRow(rowNb++);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", "title.docunit.pgcnid"));
            row.createCell(3).setCellValue(docUnit.getPgcnId());

            row = sheet.createRow(rowNb++);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", "title.docunit.label"));
            row.createCell(3).setCellValue(docUnit.getLabel());

            return rowNb;
        }

        /**
         * Bloc de détail des {@link DescriptionProperty}
         *
         * @param sheet
         * @param rowNb
         * @param title
         * @param type
         * @param bProperties
         * @param configurations
         * @return
         */
        private short buildDetailBlock(final Sheet sheet,
                                       short rowNb,
                                       final String title,
                                       final DescriptionProperty.Type type,
                                       final List<DescriptionProperty> bProperties,
                                       final List<PropertyConfiguration> configurations) {
            // Titre
            Row row = sheet.createRow(rowNb);
            createStyledCell(row, 2, title, Style.BOLD);
            sheet.addMergedRegion(new CellRangeAddress(rowNb, rowNb, 2, 3));
            rowNb++;

            // Propriétés
            for (final DescriptionProperty property : bProperties) {
                if (property.getType() != type) {
                    continue;
                }
                row = sheet.createRow(rowNb);
                row.createCell(0).setCellValue(property.getType().name());
                row.createCell(1).setCellValue(property.getIdentifier());
                row.createCell(2).setCellValue(property.getLabel());

                // cellules de saisie
                final Optional<PropertyConfiguration> confOpt = configurations.stream()
                                                                              .filter(c -> c.getDescProperty() != null
                                                                                           && StringUtils.equals(c.getDescProperty().getIdentifier(),
                                                                                                                 property.getIdentifier()))
                                                                              .findAny();
                final boolean isRequired, allowComment;
                if (confOpt.isPresent()) {
                    final PropertyConfiguration conf = confOpt.get();
                    isRequired = conf.isRequired();
                    allowComment = conf.isAllowComment();
                } else {
                    isRequired = false;
                    allowComment = property.isAllowComment();
                }
                if (isRequired) {
                    createRequiredBorderedCell(sheet, rowNb, 3);

                    if (allowComment) {
                        createRequiredBorderedCell(sheet, rowNb, 4);
                    }
                } else {
                    createBorderedCell(sheet, rowNb, 3);

                    if (allowComment) {
                        createBorderedCell(sheet, rowNb, 4);
                    }
                }
                // listes de valeurs
                addListValidationConstraint(sheet, rowNb, property.getIdentifier());

                rowNb++;
            }
            return rowNb;
        }

        /**
         * Bloc valeur d'assurance
         *
         * @param sheet
         * @param rowNb
         * @return
         */
        private short buildInsuranceBlock(final Sheet sheet, short rowNb) {
            final Row row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(PROP_INSURANCE);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", "INSURANCE"));

            createBorderedCell(sheet, rowNb, 3);

            addIntegerValidationConstraint(sheet, new CellRangeAddressList(rowNb, rowNb, 3, 3), messageService.getMessage("condreport", "INSURANCE"));

            return ++rowNb;
        }

        /**
         * Bloc des champs dimension
         *
         * @param sheet
         * @param rowNb
         * @param property
         * @param configurations
         * @return
         */
        private short buildDimensionBlock(final Sheet sheet,
                                          short rowNb,
                                          final PropertyConfiguration.InternalProperty property,
                                          final List<PropertyConfiguration> configurations) {
            final Row row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(property.name());
            row.createCell(2).setCellValue(messageService.getMessage("condreport", "DIMENSION"));

            final Optional<PropertyConfiguration> confOpt = configurations.stream().filter(c -> c.getInternalProperty() == property).findAny();

            if (confOpt.isPresent() && confOpt.get().isRequired()) {
                createRequiredBorderedCell(sheet, rowNb, 3);
                createRequiredBorderedCell(sheet, rowNb, 4);
                createRequiredBorderedCell(sheet, rowNb, 5);
            } else {
                createBorderedCell(sheet, rowNb, 3);
                createBorderedCell(sheet, rowNb, 4);
                createBorderedCell(sheet, rowNb, 5);
            }

            addIntegerValidationConstraint(sheet, new CellRangeAddressList(rowNb, rowNb, 3, 5), messageService.getMessage("condreport", "DIMENSION"));

            return ++rowNb;
        }

        /**
         * Bloc des commentaire (autres infos)
         *
         * @param sheet
         * @param rowNb
         * @param field
         * @return
         */
        private short buildInfoBlock(final Sheet sheet, short rowNb, final String field) {
            final Row row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(field);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", field));

            sheet.addMergedRegion(new CellRangeAddress(rowNb, rowNb, 3, 4));
            createBorderedCell(sheet, rowNb, 3);

            row.setHeight((short) 600);

            return ++rowNb;
        }

        /**
         * Bloc des commentaire (autres infos)
         *
         * @param sheet
         * @param rowNb
         * @param field
         * @return
         */
        private short buildInfoBlock(final Sheet sheet,
                                     short rowNb,
                                     final PropertyConfiguration.InternalProperty field,
                                     final List<PropertyConfiguration> configurations) {
            final Row row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(field.name());
            row.createCell(2).setCellValue(messageService.getMessage("condreport", field.name()));

            sheet.addMergedRegion(new CellRangeAddress(rowNb, rowNb, 3, 4));

            final Optional<PropertyConfiguration> confOpt = configurations.stream().filter(c -> c.getInternalProperty() == field).findAny();

            if (confOpt.isPresent() && confOpt.get().isRequired()) {
                createRequiredBorderedCell(sheet, rowNb, 3);
            } else {
                createBorderedCell(sheet, rowNb, 3);
            }

            row.setHeight((short) 600);

            return ++rowNb;
        }

        /**
         * Bloc esimation du nombr de vues
         *
         * @param sheet
         * @param rowNb
         * @return
         */
        private short buildViewBlock(final Sheet sheet, short rowNb) {
            // Titre
            Row row = sheet.createRow(rowNb);
            createStyledCell(row, 2, messageService.getMessage("condreport", "title.nbview"), Style.BOLD);
            sheet.addMergedRegion(new CellRangeAddress(rowNb, rowNb, 2, 3));
            rowNb++;

            addIntegerValidationConstraint(sheet,
                                           new CellRangeAddressList(rowNb, rowNb + 2, 3, 3),
                                           messageService.getMessage("condreport", "title.nbview"));

            // Estimation du nombre de vues de la reliure
            row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(PROP_NB_VIEW_BINDING);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", PROP_NB_VIEW_BINDING));
            createBorderedCell(sheet, rowNb, 3);
            rowNb++;

            // Estimation du nombre de vues du corps d'ouvrage
            row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(PROP_NB_VIEW_BODY);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", PROP_NB_VIEW_BODY));
            createBorderedCell(sheet, rowNb, 3);
            rowNb++;

            // Estimation du nombre de vues suplémentaires
            row = sheet.createRow(rowNb);
            row.createCell(0).setCellValue(TYPE_DETAIL);
            row.createCell(1).setCellValue(PROP_NB_VIEW_ADDITIONNAL);
            row.createCell(2).setCellValue(messageService.getMessage("condreport", PROP_NB_VIEW_ADDITIONNAL));
            createBorderedCell(sheet, rowNb, 3);
            rowNb++;

            return rowNb;
        }

        /**
         * Écriture du fichier excel généré, dans le fulx de sortie out
         *
         * @param out
         * @throws IOException
         */
        public void write(final OutputStream out) throws IOException {
            wb.setActiveSheet(1);
            wb.setSheetHidden(0, true); // conf

            wb.write(out);
        }

        /**
         * Initialisation des {@link DescriptionProperty}
         *
         * @param descProperties
         * @return
         */
        public TemplateBuilder setDescProperties(final List<DescriptionProperty> descProperties) {
            this.descProperties.clear();
            this.descProperties.addAll(descProperties);
            return this;
        }

        /**
         * Initialisation du builder: définition des {@link DescriptionValue} disponibles pour les états de reliure
         *
         * @param descValues
         * @return
         */
        public TemplateBuilder setDescValues(final List<DescriptionValue> descValues) {
            this.descValues.clear();
            this.descValues.addAll(descValues);
            return this;
        }

        private void initStyles() {
            // BOLD
            CellStyle cellStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            styles.put(Style.BOLD, cellStyle);

            // BOLD_COLOR
            cellStyle = wb.createCellStyle();
            font = wb.createFont();
            font.setBold(true);
            font.setColor(HSSFColor.HSSFColorPredefined.DARK_RED.getIndex());
            cellStyle.setFont(font);
            styles.put(Style.BOLD_COLOR, cellStyle);
        }

        private Cell createStyledCell(final Row row, final int colNb, final String value, final Style style) {
            final Cell cell = row.createCell(colNb);
            cell.setCellValue(value);

            if (styles.containsKey(style)) {
                cell.setCellStyle(styles.get(style));
            }
            return cell;
        }

        private void createBorderedCell(final Sheet sheet, final short rowNb, final int colNb) {
            final BorderStyle borderStyle = BorderStyle.MEDIUM;
            final short borderColor = IndexedColors.GOLD.getIndex();
            createBorderedCell(sheet, rowNb, colNb, borderStyle, borderColor);
        }

        private void createRequiredBorderedCell(final Sheet sheet, final short rowNb, final int colNb) {
            final BorderStyle borderStyle = BorderStyle.MEDIUM;
            final short borderColor = IndexedColors.RED.getIndex();
            createBorderedCell(sheet, rowNb, colNb, borderStyle, borderColor);
        }

        private void createBorderedCell(final Sheet sheet,
                                        final short rowNb,
                                        final int colNb,
                                        final BorderStyle borderStyle,
                                        final short borderColor) {
            final CellRangeAddress region = new CellRangeAddress(rowNb, rowNb, colNb, colNb);

            RegionUtil.setBorderBottom(borderStyle, region, sheet);
            RegionUtil.setBottomBorderColor(borderColor, region, sheet);
        }

        /**
         * Cellule de type liste déroulante Oui / Non
         *
         * @param sheet
         * @param addressList
         */
        private void addBooleanValidationConstraint(final Sheet sheet, final CellRangeAddressList addressList) {
            final String[] constraintValues = {messageService.getMessage("condreport", "validation.boolean.true"),
                                               messageService.getMessage("condreport", "validation.boolean.false")};
            final DataValidationConstraint dvConstraint = format == XLS ?
                                                          ((HSSFDataValidationHelper) validationHelper).createExplicitListConstraint(constraintValues) :
                                                          ((XSSFDataValidationHelper) validationHelper).createExplicitListConstraint(constraintValues);
            final DataValidation dataValidation = this.format == XLS ?
                                                  ((HSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList) :
                                                  ((XSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList);
            dataValidation.setShowErrorBox(true);

            try {
                sheet.addValidationData(dataValidation);
            }
            // pb de longueur de formule avec le format xls; si bloquant, baser la formule sur des plages excel
            catch (final IllegalArgumentException e) {
                LOG.error("Une erreur s'est produite lors de l'ajout de la liste booléenne pour la plage {}: {}",
                          addressList.getCellRangeAddresses()[0].formatAsString(),
                          e.getMessage());
            }
        }

        /**
         * Cellule numérique
         *
         * @param sheet
         * @param addressList
         * @param field
         */
        private void addIntegerValidationConstraint(final Sheet sheet, final CellRangeAddressList addressList, final String field) {
            final DataValidationConstraint dvConstraint = this.format == XLS ?
                                                          ((HSSFDataValidationHelper) validationHelper).createIntegerConstraint(GREATER_OR_EQUAL,
                                                                                                                                "0",
                                                                                                                                null) :
                                                          ((XSSFDataValidationHelper) validationHelper).createIntegerConstraint(GREATER_OR_EQUAL,
                                                                                                                                "0",
                                                                                                                                null);
            final DataValidation dataValidation = this.format == XLS ?
                                                  ((HSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList) :
                                                  ((XSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList);
            dataValidation.setShowErrorBox(true);
            dataValidation.setShowPromptBox(true);
            dataValidation.createPromptBox(field, messageService.getMessage("condreport", "validation.integer.promptText"));

            try {
                sheet.addValidationData(dataValidation);
            }
            // pb de longueur de formule avec le format xls; si bloquant, baser la formule sur des plages excel
            catch (final IllegalArgumentException e) {
                LOG.error("Une erreur s'est produite lors de l'ajout de la liste de validation pour la plage {}: {}",
                          addressList.getCellRangeAddresses()[0].formatAsString(),
                          e.getMessage());
            }
        }

        /**
         * Cellule de type liste déroulante, associée à une propriété
         *
         * @param sheet
         * @param rowNb
         * @param property
         */
        private void addListValidationConstraint(final Sheet sheet, final short rowNb, final String property) {
            // Liste de valeurs
            if (descriptionsConstraints.containsKey(property)) {
                final CellRangeAddressList addressList = new CellRangeAddressList(rowNb, rowNb, 3, 3);
                final DataValidationConstraint dvConstraint = descriptionsConstraints.get(property);
                final DataValidation dataValidation = this.format == XLS ?
                                                      ((HSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList) :
                                                      ((XSSFDataValidationHelper) validationHelper).createValidation(dvConstraint, addressList);
                dataValidation.setShowErrorBox(true);

                try {
                    sheet.addValidationData(dataValidation);
                }
                // pb de longueur de formule avec le format xls; si bloquant, baser la formule sur des plages excel
                catch (final IllegalArgumentException e) {
                    LOG.error("Une erreur s'est produite lors de l'ajout de la liste de validation pour la propriété {}: {}",
                              property,
                              e.getMessage());
                }
            }
        }
    }

    /**
     * Style des fonts
     */
    private enum Style {
        BOLD,
        BOLD_COLOR
    }
}
