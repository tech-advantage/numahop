package fr.progilone.pgcn.service.exchange;

import fr.progilone.pgcn.domain.dto.exchange.CSVMappingDTO;
import fr.progilone.pgcn.domain.exchange.CSVMapping;
import fr.progilone.pgcn.domain.exchange.CSVMappingRule;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.exchange.CSVMappingRepository;
import fr.progilone.pgcn.repository.exchange.ImportReportRepository;
import fr.progilone.pgcn.service.exchange.mapper.CSVMappingMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sebastien on 23/11/2016.
 */
@Service
public class CSVMappingService {

    private final ImportReportRepository importReportRepository;
    private final CSVMappingRepository mappingRepository;

    @Autowired
    public CSVMappingService(final ImportReportRepository importReportRepository, final CSVMappingRepository mappingRepository) {
        this.importReportRepository = importReportRepository;
        this.mappingRepository = mappingRepository;
    }

    @Transactional(readOnly = true)
    public Set<CSVMappingDTO> findAll() {
        final Set<CSVMapping> mappings = mappingRepository.findAllWithRules();
        return CSVMappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<CSVMappingDTO> findAllUsable() {
        final Set<CSVMapping> mappings = mappingRepository.findAllUsableWithRules();
        return CSVMappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<CSVMappingDTO> findByLibrary(final Library library) {
        final Set<CSVMapping> mappings = mappingRepository.findByLibraryWithRules(library);
        return CSVMappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public Set<CSVMappingDTO> findUsableByLibrary(final Library library) {
        final Set<CSVMapping> mappings = mappingRepository.findUsableByLibrary(library);
        return CSVMappingMapper.INSTANCE.mappingToDtos(mappings);
    }

    @Transactional(readOnly = true)
    public CSVMapping findOne(final String id) {
        return mappingRepository.findOneWithRules(id);
    }

    @Transactional
    public void delete(final String id) {
        importReportRepository.setCsvMappingNull(id);
        mappingRepository.delete(id);
    }

    @Transactional
    public void deleteByLibrary(final Library library) {
        mappingRepository.findByLibrary(library).forEach(mapping -> delete(mapping.getIdentifier()));
    }

    @Transactional
    public CSVMapping save(final CSVMapping mapping) {
        validate(mapping);
        final CSVMapping savedMapping = mappingRepository.save(mapping);
        return mappingRepository.findOneWithRules(savedMapping.getIdentifier());
    }

    private PgcnList<PgcnError> validate(final CSVMapping mapping) throws PgcnValidationException {
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

    private PgcnList<PgcnError> validateRule(final CSVMappingRule rule) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // le champ de la règle est obligatoire
        if (StringUtils.isBlank(rule.getDocUnitField()) && StringUtils.isBlank(rule.getCsvField())) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.MAPPING_RULE_FIELD_MANDATORY).build());
        }
        // Retour
        if (!errors.isEmpty()) {
            rule.setErrors(errors);
        }
        return errors;
    }

    private Stream<PgcnError> validateMandatoryRules(final List<CSVMappingRule> rules, final PgcnError.Builder builder) {
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
    public CSVMapping duplicateMapping(String id, final String library) {
        CSVMapping original = findOne(id);
        CSVMapping dupe = new CSVMapping();
        copyAttributes(original, dupe);

        // Set library
        if (library != null && !StringUtils.equals(dupe.getLibrary().getIdentifier(), library)) {
            final Library dupeLibrary = new Library();
            dupeLibrary.setIdentifier(library);
            dupe.setLibrary(dupeLibrary);
        }
        // Sauvegarde sans validation
        final CSVMapping savedMapping = mappingRepository.save(dupe);
        return mappingRepository.findOneWithRules(savedMapping.getIdentifier());
    }

    private void copyAttributes(CSVMapping source, CSVMapping destination) {
        destination.setLibrary(source.getLibrary());
        destination.setLabel(source.getLabel());
        for (CSVMappingRule rule : source.getRules()) {
            CSVMappingRule newRule = duplicateRule(rule);
            newRule.setMapping(destination);
            destination.getRules().add(newRule);
        }
    }

    private CSVMappingRule duplicateRule(CSVMappingRule source) {
        CSVMappingRule destination = new CSVMappingRule();
        destination.setCsvField(source.getCsvField());
        destination.setDocUnitField(source.getDocUnitField());
        return destination;
    }
}
