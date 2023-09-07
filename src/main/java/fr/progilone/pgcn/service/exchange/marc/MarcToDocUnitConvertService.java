package fr.progilone.pgcn.service.exchange.marc;

import com.google.common.collect.Iterables;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;
import fr.progilone.pgcn.service.exchange.ExchangeHelper;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMapping;
import fr.progilone.pgcn.service.exchange.marc.mapping.CompiledMappingRule;
import fr.progilone.pgcn.service.exchange.marc.mapping.MarcKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.marc4j.converter.CharConverter;
import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service assurant la conversion de notices aux format MARC (MARC, MARC JSON ou MARC XML) en unités documentaires
 * <p>
 * Created by Sebastien on 24/11/2016.
 */
@Service
public class MarcToDocUnitConvertService extends AbstractImportConvertService {

    private static final Logger LOG = LoggerFactory.getLogger(MarcToDocUnitConvertService.class);

    private final MarcMappingEvaluationService mappingEvaluationService;

    @Autowired
    public MarcToDocUnitConvertService(final MarcMappingEvaluationService mappingEvaluationService) {
        this.mappingEvaluationService = mappingEvaluationService;
    }

    /**
     * Conversion de tous les exemplaires contenu dans {@link Record} en autant de {@link DocUnit}
     *
     * @param record
     * @param charConverter
     * @param mapping
     * @param keyScript
     * @param propertyTypes
     * @return
     */
    public List<DocUnitWrapper> convert(final Record record,
                                        final CharConverter charConverter,
                                        final CompiledMapping mapping,
                                        final CompiledMappingRule keyScript,
                                        final Map<String, DocPropertyType> propertyTypes) {

        // Liste des tags correspondant aux exemplaires
        final List<MarcKey> itemTags = mapping.getCompiledRules()
                                              .stream()
                                              .filter(r -> StringUtils.equals(r.getRule().getDocUnitField(), "pgcnId"))
                                              .flatMap(r -> r.getExpressionStatement().getMarcKeys().stream())
                                              .distinct()
                                              .collect(Collectors.toList());
        // Alimentation du binding index
        final AtomicInteger index = new AtomicInteger();
        // Création d'une sous-notice par notice d'exemplaire
        return MarcUtils.splitRecordByMarcKeys(record, itemTags)
                        .stream()
                        // Création d'une unité documentaire par sous-notice d'exemplaire
                        .map(itemRecord -> {
                            final DocUnitWrapper wrapper = new DocUnitWrapper();
                            final int itemIndex = index.incrementAndGet();
                            final DocUnit docUnit = convertItem(itemRecord, charConverter, mapping, propertyTypes, itemIndex);
                            wrapper.setDocUnit(docUnit);

                            // Clé parent
                            if (keyScript != null) {
                                final List<String> values = getValues(itemRecord, keyScript, charConverter, itemIndex);
                                if (values.isEmpty()) {
                                    LOG.debug("Aucune valeur n'est définie pour la clé parent (UD {})", docUnit.getPgcnId());
                                } else {
                                    if (values.size() > 1) {
                                        LOG.debug("Il y a plus d'une valeur trouvée pour la clé parent (UD {}); seule la première sera récupérée.", docUnit.getPgcnId());
                                    }
                                    wrapper.setParentKey(values.get(0));
                                }
                            }
                            return wrapper;
                        })
                        .collect(Collectors.toList());
    }

    /**
     * Conversion d'un exemplaire d'un {@link Record} en {@link DocUnit}
     *
     * @param record
     * @param charConverter
     * @param mapping
     * @param propertyTypes
     * @param itemIndex
     * @return
     */
    private DocUnit convertItem(final Record record,
                                final CharConverter charConverter,
                                final CompiledMapping mapping,
                                final Map<String, DocPropertyType> propertyTypes,
                                final int itemIndex) {
        final DocUnit docUnit = initDocUnit(mapping.getLibrary(), BibliographicRecord.PropertyOrder.BY_PROPERTY_TYPE);
        final BibliographicRecord bibRecord = docUnit.getRecords().iterator().next();

        for (final CompiledMappingRule compiledRule : mapping.getCompiledRules()) {
            boolean applyOnDocUnit = true, applyOnBibRecord = true, applyOnProperty = true;
            final MappingRule rule = compiledRule.getRule();

            // Règle par défaut: ne s'applique que si la valeur n'est pas déjà renseignée
            if (rule.isDefaultRule()) {
                // UD
                if (FIELD_DIGITAL_ID.equals(rule.getDocUnitField())) {
                    applyOnDocUnit = rule.getDocUnitField() != null && !hasObjectField(Iterables.getOnlyElement(docUnit.getPhysicalDocuments()), rule.getDocUnitField());
                } else {
                    applyOnDocUnit = rule.getDocUnitField() != null && !hasObjectField(docUnit, rule.getDocUnitField());
                }
                // Record
                applyOnBibRecord = rule.getBibRecordField() != null && !hasObjectField(bibRecord, rule.getBibRecordField());
                // Propriété
                if (rule.getProperty() != null) {
                    applyOnProperty = rule.getProperty() != null && !hasDocProperty(bibRecord, rule.getProperty().getIdentifier());
                }
                if (!applyOnDocUnit && !applyOnBibRecord
                    && !applyOnProperty) {
                    continue;
                }
            }

            // Évaluation de la règle
            final List<String> values = getValues(record, compiledRule, charConverter, itemIndex);

            // UD
            if (applyOnDocUnit) {
                if (FIELD_DIGITAL_ID.equals(rule.getDocUnitField())) {
                    setObjectField(Iterables.getOnlyElement(docUnit.getPhysicalDocuments()), rule.getDocUnitField(), values);
                } else {
                    setObjectField(docUnit, rule.getDocUnitField(), values);
                }
            }
            // Record
            if (applyOnBibRecord) {
                setObjectField(bibRecord, rule.getBibRecordField(), values);
            }
            // Propriété
            if (applyOnProperty && rule.getProperty() != null) {
                setDocProperty(bibRecord, rule.getProperty().getIdentifier(), propertyTypes, values, compiledRule.getRule().getPosition());
            }
        }
        return docUnit;
    }

    /**
     * Applique la règle de mapping sur la notice MARC, et renvoie les valeurs trouvées
     *
     * @param record
     * @param rule
     * @param charConverter
     * @param itemIndex
     * @return
     */
    private List<String> getValues(final Record record, final CompiledMappingRule rule, final CharConverter charConverter, final int itemIndex) {
        // Liste des bindings à évaluer
        List<Map<String, Object>> bindings = new ArrayList<>();

        // La règle n'a pas de bindings basés sur les champs de la notice
        if (rule.isConstant()) {
            bindings.add(createBinding(record));
        }
        // Sinon on créé les bindings adéquats
        else {
            // Extraction des champs de la notice utilisée dans la règle de mapping (condition + expression)
            final Map<String, List<VariableField>> dataByTag = rule.getBindableMarcKeys().stream().map(MarcKey::getTag).distinct().map(tag -> {
                final List<VariableField> fields;
                // Champ Yxx => tous les tags commançant par Y
                if (tag.endsWith("xx")) {
                    char ch = tag.charAt(0);
                    fields = record.getVariableFields().stream().filter(fld -> fld.getTag().charAt(0) == ch).collect(Collectors.toList());
                }
                // Sinon directement le tag mappé
                else {
                    fields = record.getVariableFields(tag);
                }
                return Pair.of(tag, fields);
            }).filter(p -> !p.getRight().isEmpty()).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            // Combinaisons des différents champs utilisés dans la règle de mapping
            final List<Map<String, VariableField>> dataCombos = ExchangeHelper.cartesianProduct(dataByTag);

            // Création d'un binding pour chaque combinaison de champs
            for (final Map<String, VariableField> dataCombo : dataCombos) {
                Map<String, Object> binding = createBinding(record, rule.getMarcKeys());
                bindings.add(binding);

                // on bind chaque variable trouvée dans le script avec sa valeur, ou sinon une chaîne vide
                for (final MarcKey k : rule.getMarcKeys()) {
                    VariableField field = dataCombo.get(k.getTag());
                    Object data = null;

                    // Le champ n'a pas été trouvé, il est peut être géré par une clé xx
                    if (field == null) {
                        field = dataCombo.get(k.getTag().charAt(0) + "xx");
                        // Le champ xx ne correspond pas au MarcKey
                        if (field != null && !Objects.equals(k.getTag(), field.getTag())) {
                            field = null;
                        }
                    }
                    if (field != null) {
                        // DataField
                        if (field instanceof DataField) {
                            // Si un champ de donnée est précisé dans son code, on le passe lui-même en paramètre
                            if (k.getCode() == null) {
                                data = field;
                            }
                            // Sinon on va lire le sous-champ
                            else {
                                // Si le sous-champ est répété, on concatène ses valeurs séparées par des espaces
                                data = ((DataField) field).getSubfields(k.getCode())
                                                          .stream()
                                                          .map(Subfield::getData)
                                                          .map(val -> MarcUtils.convert(val, charConverter))
                                                          // Si le sous-champ est répété, on concaténe ses différentes valeurs
                                                          .reduce((a, b) -> a + " "
                                                                            + b)
                                                          .orElse(null);
                            }
                        }
                        // ControlField
                        else {
                            data = MarcUtils.convert(((ControlField) field).getData(), charConverter);
                        }
                    }
                    if (data != null) {
                        binding.put(k.getVariableName(), data);
                    }
                }
            }
            // expression constante et aucun binding défini: on rajoute un binding par défaut
            if (bindings.isEmpty() && rule.isConstantExpression()) {
                bindings.add(createBinding(record, rule.getMarcKeys()));
            }
        }
        // Filtrage des bindings par rapport à la condition de la règle
        if (rule.hasCondition()) {
            bindings = bindings.stream().filter(binding -> mappingEvaluationService.evalCondition(rule, binding)).collect(Collectors.toList());

            // Déduplication des bindings par rapport aux champs utilisés dans l'expression de la règle
            final List<String> variables = rule.getExpressionStatement().getMarcKeys().stream().map(MarcKey::getVariableName).collect(Collectors.toList());
            bindings = ExchangeHelper.distinctBindings(bindings, variables);
        }

        bindings.forEach(binding -> binding.put(MarcMappingEvaluationService.BINDING_INDEX_ITEM, itemIndex));
        // Évaluation de la règle de mapping pour les bindings respectant la condition
        return bindings.stream()
                       .map(binding -> mappingEvaluationService.evalExpression(rule, binding))
                       .filter(Objects::nonNull)
                       .map(String::valueOf)
                       .filter(StringUtils::isNotBlank)
                       .collect(Collectors.toList());
    }

    /**
     * Binding initialisé
     *
     * @param record
     * @return
     */
    private Map<String, Object> createBinding(final Record record) {
        Map<String, Object> binding = new HashMap<>();
        // bindings par défaut
        binding.put(MarcMappingEvaluationService.BINDING_RECORD, record);
        binding.put(MarcMappingEvaluationService.BINDING_LEADER, record.getLeader());
        return binding;
    }

    /**
     * Binding initialisé, avec des chaînes vides pour les markeys
     *
     * @param record
     * @param marcKeys
     * @return
     */
    private Map<String, Object> createBinding(final Record record, final Set<MarcKey> marcKeys) {
        final Map<String, Object> binding = createBinding(record);
        // le binding est toujours renseigné même si on n'a pas trouvé de valeur, pour ne pas faire planter le script
        for (final MarcKey k : marcKeys) {
            binding.put(k.getVariableName(), "");
        }
        return binding;
    }
}
