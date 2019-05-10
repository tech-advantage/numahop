package fr.progilone.pgcn.service.exchange.marc.mapping;

import com.google.common.collect.Ordering;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.domain.exchange.MappingRule;
import fr.progilone.pgcn.domain.library.Library;
import org.marc4j.converter.CharConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de précompiler les règles de mapping pour accélérer leur évaluation au moment de leur application
 */
public final class CompiledMapping {

    private final Mapping mapping;
    private final List<CompiledMappingRule> compiledRules = new ArrayList<>();

    public CompiledMapping(final Mapping mapping) {
        this.mapping = mapping;
        // alimentation de compiledRules, par ordre de position croissante
        mapping.getRules()
               .stream()
               .sorted(Ordering.natural().nullsFirst().onResultOf(MappingRule::getPosition))
               .forEach(rule -> compiledRules.add(new CompiledMappingRule(rule)));
    }

    public String getLabel() {
        return mapping.getLabel();
    }

    public Library getLibrary() {
        return mapping.getLibrary();
    }

    public Mapping.Type getType() {
        return mapping.getType();
    }

    public List<MappingRule> getRules() {
        return mapping.getRules();
    }

    public Mapping getMapping() {
        return mapping;
    }

    public List<CompiledMappingRule> getCompiledRules() {
        return compiledRules;
    }

    /**
     * Initialisation des scripts personnalisés pour chaque règle de mapping
     *
     * @param charConverter
     */
    public void initialize(final CharConverter charConverter) {
        compiledRules.forEach(rule -> rule.initialize(charConverter));
    }
}
