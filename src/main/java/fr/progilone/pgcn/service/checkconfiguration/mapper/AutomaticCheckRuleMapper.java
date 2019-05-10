package fr.progilone.pgcn.service.checkconfiguration.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.dto.checkconfiguration.AutomaticCheckRuleDTO;
import fr.progilone.pgcn.service.check.mapper.AutomaticCheckTypeMapper;

/**
 * Created by ert on 15/09/2017.
 */
@Mapper(uses = AutomaticCheckTypeMapper.class )
public interface AutomaticCheckRuleMapper {

    AutomaticCheckRuleMapper INSTANCE = Mappers.getMapper(AutomaticCheckRuleMapper.class);

    AutomaticCheckRuleDTO checkRuleToCheckRuleDTO(AutomaticCheckRule rule);
    
    AutomaticCheckRule checkRuleDtoToCheckRule(AutomaticCheckRuleDTO dto);
    
    List<AutomaticCheckRuleDTO> checkRulesToCheckRulesDTO(List<AutomaticCheckRule> rules);
    
    List<AutomaticCheckRule> checkRulesDTOToCheckRules(List<AutomaticCheckRuleDTO> rulesDto);
}
