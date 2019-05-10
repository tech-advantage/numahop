package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.exchange.MappingDTO;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.repository.exchange.MappingRepository;
import fr.progilone.pgcn.repository.exchange.MappingRuleRepository;
import fr.progilone.pgcn.service.exchange.mapper.MappingMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sebastien on 23/11/2016.
 */
@Service
public class MappingService {

    private final ImportReportRepository importReportRepository;
    private final MappingRepository mappingRepository;
    private final MappingRuleRepository mappingRuleRepository;

    @Autowired
    public MappingService(final ImportReportRepository importReportRepository,
                          final MappingRepository mappingRepository,
                          final MappingRuleRepository mappingRuleRepository) {
        this.importReportRepository = importReportRepository;
        this.mappingRepository = mappingRepository;
        this.mappingRuleRepository = mappingRuleRepository;
    }

    @Transactional(readOnly = true)
    public Set<MappingDTO> findAllUsable() {
        final Set<Mapping> mappings = mappingRepository.findAllUsableWithRules();
        return MappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<MappingDTO> findByLibrary(final Library library) {
        final Set<Mapping> mappings = mappingRepository.findByLibraryWithRules(library);
        return MappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<MappingDTO> findByType(final Mapping.Type type) {
        final Set<Mapping> mappings = mappingRepository.findByTypeWithRules(type);
        return MappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<MappingDTO> findByTypeAndLibrary(final Mapping.Type type, final Library library) {
        final Set<Mapping> mappings = mappingRepository.findByTypeAndLibraryWithRules(type, library);
        return MappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Mapping findOne(final String id) {
        return mappingRepository.findOneWithRules(id);
    }

    @Transactional
    public void delete(final String id) {
        importReportRepository.setMappingNull(id);
        importReportRepository.setMappingChildrenNull(id);
        mappingRepository.delete(id);
    }

    @Transactional
    public void deleteByLibrary(final Library library) {
        mappingRepository.findByLibrary(library).forEach(mapping -> delete(mapping.getIdentifier()));
    }

    @Transactional
    public Mapping save(final Mapping mapping) {
        return save(mapping, true);
    }

    @Transactional
    public Mapping save(final Mapping mapping, final boolean validation) {
        setDefaultValues(mapping);
        if (validation) {
            validate(mapping);
        }
        final Mapping savedMapping = mappingRepository.save(mapping);
        return mappingRepository.findOneWithRules(savedMapping.getIdentifier());
    }

    private void setDefaultValues(final Mapping mapping) {
        int position = 10000;
        for (final MappingRule rule : mapping.getRules()) {
            rule.setPosition(position);

            if (rule.isDefaultRule()) {
                rule.setCondition(null);
                rule.setConditionConf(null);
            }
            position++;
        }
    }

    private PgcnList<PgcnError> validate(final Mapping mapping) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le libellé est obligatoire
        if (StringUtils.isEmpty(mapping.getLabel())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.MAPPING_LABEL_MANDATORY).setField("label").build());
        }
        // la bibliothèque est obligatoire
        if (mapping.getLibrary() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.MAPPING_LIBRARY_MANDATORY).setField("library").build());
        }
        // le type est obligatoire
        if (mapping.getType() == null) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.MAPPING_TYPE_MANDATORY).setField("type").build());
        }
        // Les champs field des règles de mapping sont obligatoires
        final List<PgcnError> rulesErrors = mapping.getRules().stream().flatMap(rule -> validateRule(rule).stream()).collect(Collectors.toList());

        // Les champs pgcnId, right et type doivent être mappés
        validateMandatoryRules(mapping.getRules(), builder).forEach(errors::add);

        // Retour
        if (!errors.isEmpty() || !rulesErrors.isEmpty()) {
            mapping.setErrors(errors);
            errors.addAll(rulesErrors);
            throw new PgcnValidationException(mapping, errors);
        }
        return errors;
    }

    private PgcnList<PgcnError> validateRule(final MappingRule rule) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le champ de la règle est obligatoire
        if (rule.getProperty() == null && StringUtils.isBlank(rule.getDocUnitField()) && StringUtils.isBlank(rule.getBibRecordField())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.MAPPING_RULE_FIELD_MANDATORY).build());
        }
        // Retour
        if (!errors.isEmpty()) {
            rule.setErrors(errors);
        }
        return errors;
    }

    private Stream<PgcnError> validateMandatoryRules(final List<MappingRule> rules, final PgcnError.Builder builder) {
        return Stream.of("label", "pgcnId", "rights", "type")
                     .filter(field -> rules.stream().noneMatch(rule -> StringUtils.equals(rule.getDocUnitField(), field)))
                     .map(field -> {
                         final PgcnErrorCode code;
                         switch (field) {
                             case "label":
                                 code = PgcnErrorCode.MAPPING_RULE_LABEL_MANDATORY;
                                 break;
                             case "pgcnId":
                                 code = PgcnErrorCode.MAPPING_RULE_PGCNID_MANDATORY;
                                 break;
                             case "rights":
                                 code = PgcnErrorCode.MAPPING_RULE_RIGHTS_MANDATORY;
                                 break;
                             default:
                                 code = null;
                         }
                         if (code != null) {
                             return builder.reinit().setCode(code).setField(field).build();
                         }
                         return null;
                     })
                     .filter(Objects::nonNull);
    }

    @Transactional
    public Mapping duplicateMapping(final String id, final String library) {
        Mapping original = findOne(id);
        Mapping dupe = new Mapping();
        copyAttributes(original, dupe);

        // Set library
        if (library != null && !StringUtils.equals(dupe.getLibrary().getIdentifier(), library)) {
            final Library dupeLibrary = new Library();
            dupeLibrary.setIdentifier(library);
            dupe.setLibrary(dupeLibrary);
        }
        // Sauvegarde sans validation
        final Mapping savedMapping = mappingRepository.save(dupe);
        return mappingRepository.findOneWithRules(savedMapping.getIdentifier());
    }

    @Transactional(readOnly = true)
    public Integer countByPropertyType(final DocPropertyType type) {
        return mappingRuleRepository.countByProperty(type);
    }

    private void copyAttributes(Mapping source, Mapping destination) {
        destination.setLibrary(source.getLibrary());
        destination.setLabel(source.getLabel());
        destination.setType(source.getType());
        destination.setJoinExpression(source.getJoinExpression());

        source.getRules().stream().sorted(Comparator.comparing(MappingRule::getPosition)).forEach(rule -> {
            MappingRule newRule = duplicateRule(rule);
            newRule.setMapping(destination);
            destination.getRules().add(newRule);
        });
    }

    private MappingRule duplicateRule(MappingRule source) {
        MappingRule destination = new MappingRule();
        destination.setBibRecordField(source.getBibRecordField());
        destination.setDefaultRule(source.isDefaultRule());
        destination.setDocUnitField(source.getDocUnitField());
        destination.setCondition(source.getCondition());
        destination.setConditionConf(source.getConditionConf());
        destination.setExpression(source.getExpression());
        destination.setExpressionConf(source.getExpressionConf());
        destination.setProperty(source.getProperty());
        destination.setPosition(source.getPosition());
        destination.setDefaultRule(source.isDefaultRule());
        return destination;
    }
}
