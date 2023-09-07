package fr.progilone.pgcn.service.exchange.ead;

import com.google.common.collect.Iterables;
import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.jaxb.ead.C;
import fr.progilone.pgcn.service.exchange.AbstractImportConvertService;
import fr.progilone.pgcn.service.exchange.ExchangeHelper;
import fr.progilone.pgcn.service.exchange.ead.mapping.CompiledMapping;
import fr.progilone.pgcn.service.exchange.ead.mapping.CompiledMappingRule;
import fr.progilone.pgcn.service.exchange.ead.mapping.RuleKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service assurant la conversion de notices au format EAD en unités documentaires
 * <p>
 * Created by Sébastien on 17/05/2017.
 */
@Service
public class EadToDocUnitConvertService extends AbstractImportConvertService {

    private static final Logger LOG = LoggerFactory.getLogger(EadToDocUnitConvertService.class);

    private final EadMappingEvaluationService eadMappingEvaluationService;

    @Autowired
    public EadToDocUnitConvertService(final EadMappingEvaluationService eadMappingEvaluationService) {
        this.eadMappingEvaluationService = eadMappingEvaluationService;
    }

    /**
     * Conversion d'un {@link C} en {@link DocUnit}
     *
     * @param c
     * @param eadCParser
     * @param mapping
     * @param propertyTypes
     * @param propertyOrder
     * @return
     */
    public DocUnit convert(final C c,
                           final EadCParser eadCParser,
                           final CompiledMapping mapping,
                           final Map<String, DocPropertyType> propertyTypes,
                           final BibliographicRecord.PropertyOrder propertyOrder) {
        final DocUnit docUnit = initDocUnit(mapping.getLibrary(), propertyOrder);
        return convert(docUnit, c, eadCParser, mapping, propertyTypes);
    }

    /**
     * Conversion d'un {@link C} en {@link DocUnit}, le {@link DocUnit} étant fourni au préalable
     *
     * @param docUnit
     * @param c
     * @param eadCParser
     * @param mapping
     * @param propertyTypes
     * @return
     */
    public DocUnit convert(final DocUnit docUnit, final C c, final EadCParser eadCParser, final CompiledMapping mapping, final Map<String, DocPropertyType> propertyTypes) {
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
                    applyOnProperty = !hasDocProperty(bibRecord, rule.getProperty().getIdentifier());
                }
                if (!applyOnDocUnit && !applyOnBibRecord
                    && !applyOnProperty) {
                    continue;
                }
            }

            // Évaluation de la règle
            final List<String> values = getValues(c, compiledRule, eadCParser);

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
     * Applique la règle de mapping sur l'objet c, et renvoie les valeurs trouvées
     *
     * @param c
     * @param rule
     * @param eadCParser
     * @return
     */
    private List<String> getValues(final C c, final CompiledMappingRule rule, final EadCParser eadCParser) {
        // Liste des bindings à évaluer
        List<Map<String, Object>> bindings = new ArrayList<>();

        // La règle n'a pas de bindings basés sur les champs de la notice
        if (rule.isConstant()) {
            bindings.add(createBinding(c));
        }
        // Sinon on créé les bindings adéquats
        else {
            // Map nom des champs communs => valeurs
            final Set<String> allFields = rule.getBindableRuleKeys().stream().map(RuleKey::getField).collect(Collectors.toSet());
            // Contrairement au format MARC qui est sur 2 niveaux: tag > code, avec un regroupement des valeurs par tag,
            // on recherche au préalable pour le format EAD les niveaux sur lesquels regrouper les valeurs (qui seront les plus hauts niveaux communs)
            final Set<String> commonFields = getCommonFields(allFields);

            final Map<String, List<Object>> dataByCommonField = commonFields.stream()
                                                                            .map(field -> Pair.of(field, (List<Object>) eadCParser.getValues(c, field)))
                                                                            .filter(p -> !p.getRight().isEmpty())
                                                                            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
            // Combinaisons des différents champs communs utilisés dans la règle de mapping
            final List<Map<String, Object>> dataCombos = ExchangeHelper.cartesianProduct(dataByCommonField);

            for (final Map<String, Object> dataCombo : dataCombos) {
                // Ajout de tous les champs dans chaque combinaisons de valeurs
                allFields.stream()
                         // Les valeurs n'ont pas déjà été récupérées
                         .filter(path -> !dataCombo.containsKey(path))
                         .map(path -> dataCombo.keySet()
                                               .stream()
                                               .filter(k -> path.startsWith(k + "."))
                                               .findAny()
                                               // on recherche la valeur à partir de son chemin relatif dans la valeur commune
                                               .map(rootPath -> {
                                                   final Object commonO = dataCombo.get(rootPath);
                                                   final String subPath = path.substring(rootPath.length() + 1);
                                                   final Object value = EadCParser.getObjectValue(commonO, subPath);
                                                   return value != null ? Pair.of(path, value)
                                                                        : null;
                                               }))
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .forEach(p -> dataCombo.put(p.getLeft(), p.getRight()));

                // Création d'un binding pour chaque combinaison de champs
                final Map<String, Object> binding = createBinding(c);
                bindings.add(binding);

                // on bind chaque variable trouvée dans le script avec sa valeur, ou sinon une chaîne vide
                for (final RuleKey k : rule.getRuleKeys()) {
                    final Object cData = dataCombo.get(k.getField());
                    // le binding est toujours renseigné même si on n'a pas trouvé de valeur, pour ne pas faire planter le script
                    binding.put(k.getVariableName(),
                                cData != null ? cData
                                              : "");
                }
            }
            // expression constante et aucun binding défini: on rajoute un binding par défaut
            if (bindings.isEmpty() && rule.isConstantExpression()) {
                bindings.add(createBinding(c));
            }
        }
        // Filtrage des bindings par rapport à la condition de la règle
        if (rule.hasCondition()) {
            bindings = bindings.stream().filter(binding -> eadMappingEvaluationService.evalCondition(rule, binding)).collect(Collectors.toList());

            // Déduplication des bindings par rapport aux champs utilisés dans l'expression de la règle
            final List<String> variables = rule.getExpressionStatement().getRuleKeys().stream().map(RuleKey::getVariableName).collect(Collectors.toList());
            bindings = ExchangeHelper.distinctBindings(bindings, variables);
        }
        // Évaluation de la règle de mapping pour les bindings respectant la condition
        return bindings.stream()
                       .map(binding -> eadMappingEvaluationService.evalExpression(rule, binding))
                       .filter(Objects::nonNull)
                       .map(String::valueOf)
                       .filter(StringUtils::isNotBlank)
                       .collect(Collectors.toList());
    }

    /**
     * Binding initialisé
     *
     * @param c
     * @return
     */
    private Map<String, Object> createBinding(final C c) {
        final Map<String, Object> binding = new HashMap<>();
        // bindings par défaut
        binding.put(EadMappingEvaluationService.BINDING_C, c);
        return binding;
    }

    /**
     * Recherche des propriétés communes
     * Par ex. avec ["scopecontent.p.persname.role", "scopecontent.p.persname", "controlaccess.persname.content", "controlaccess.persname.role"],
     * on obtient ["scopecontent.p.persname", "controlaccess.persname"]
     *
     * @param fields
     * @return
     */
    private Set<String> getCommonFields(final Set<String> fields) {
        final AtomicInteger counter = new AtomicInteger();
        final List<Pair<Integer, String>> indexedFields = fields.stream().map(field -> Pair.of(counter.getAndIncrement(), field)).collect(Collectors.toList());

        return indexedFields.stream().map(indField -> {
            final Integer ind = indField.getKey();
            final String field = indField.getValue();
            String subField = field;
            boolean found;
            do {
                final String fSubField = subField;
                found = indexedFields.stream().anyMatch(oth -> !oth.getKey().equals(ind) && (oth.getValue().equals(fSubField) || oth.getValue().startsWith(fSubField + ".")));
                if (!found) {
                    subField = getParent(subField);
                }
            } while (!found && subField != null);

            return subField != null ? subField
                                    : field;
        }).collect(Collectors.toSet());
    }

    /**
     * Récupération du chemin parent (les hiérarchies étant séparées par des points)
     *
     * @param path
     * @return
     */
    private String getParent(final String path) {
        final int pos = path.lastIndexOf('.');
        return pos < 0 ? null
                       : path.substring(0, pos);
    }
}
