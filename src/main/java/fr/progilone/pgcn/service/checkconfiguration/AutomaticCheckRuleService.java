package fr.progilone.pgcn.service.checkconfiguration;

import fr.progilone.pgcn.domain.check.AutomaticCheckType;
import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.dto.check.AutomaticCheckTypeDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.AutomaticCheckRuleDTO;
import fr.progilone.pgcn.repository.checkconfiguration.AutomaticCheckRuleRepository;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by ert on 15/09/2017.
 */
@Service
public class AutomaticCheckRuleService {

    private final AutomaticCheckService automaticCheckService;
    private final AutomaticCheckRuleRepository automaticCheckRuleRepository;

    @Autowired
    public AutomaticCheckRuleService(final AutomaticCheckService automaticCheckService, final AutomaticCheckRuleRepository automaticCheckRuleRepository) {
        this.automaticCheckService = automaticCheckService;
        this.automaticCheckRuleRepository = automaticCheckRuleRepository;
    }

    @Transactional(readOnly = true)
    public List<AutomaticCheckRuleDTO> getInitRulesForConfiguration() {

        final List<AutomaticCheckRuleDTO> checkInitRules = new ArrayList<>();
        final List<AutomaticCheckTypeDTO> typesAuto = automaticCheckService.findAllDto();
        typesAuto.forEach(actDto -> {
            // pas de controle auto facile pour le moment..
            if (AutomaticCheckType.AutoCheckType.FACILE != actDto.getType()) {
                final AutomaticCheckRuleDTO dto = new AutomaticCheckRuleDTO();
                dto.setActive(actDto.isActive());
                dto.setBlocking(actDto.isActive() ? true
                                                  : false);
                dto.setAutomaticCheckType(actDto);
                checkInitRules.add(dto);
            }
        });
        return checkInitRules;
    }

    public AutomaticCheckRule duplicateAutomaticCheckRule(final AutomaticCheckRule acr) {
        final AutomaticCheckRule duplicata = new AutomaticCheckRule();
        duplicata.setActive(acr.isActive());
        duplicata.setBlocking(acr.isBlocking());
        duplicata.setAutomaticCheckType(acr.getAutomaticCheckType());
        return duplicata;
    }

    @Transactional(readOnly = true)
    public Map<AutoCheckType, AutomaticCheckRule> findRulesByConfigId(final String configId) {

        final Map<AutoCheckType, AutomaticCheckRule> rulesByCheckType = new HashMap<>();
        final CheckConfiguration cc = new CheckConfiguration();
        cc.setIdentifier(configId);
        automaticCheckRuleRepository.findByCheckConfiguration(cc).stream().filter(rule -> rule.getAutomaticCheckType() != null).forEach(rule -> {
            // On initialise la relation vers checkConfiguration car on en a besoin par la suite
            // mais pas dans la même transaction, de même pour la library
            if (rule.getCheckConfiguration() != null) {
                Hibernate.initialize(rule.getCheckConfiguration().getLibrary());
            }
            rulesByCheckType.put(rule.getAutomaticCheckType().getType(), rule);
        });
        return rulesByCheckType;
    }

}
