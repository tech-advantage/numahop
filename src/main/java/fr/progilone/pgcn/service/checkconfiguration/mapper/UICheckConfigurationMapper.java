package fr.progilone.pgcn.service.checkconfiguration.mapper;

import fr.progilone.pgcn.domain.checkconfiguration.AutomaticCheckRule;
import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class UICheckConfigurationMapper {

    @Autowired
    private LibraryRepository libraryRepository;

    private AutomaticCheckRuleMapper ruleMapper = Mappers.getMapper(AutomaticCheckRuleMapper.class);

    public void mapInto(CheckConfigurationDTO checkConfigurationDTO, CheckConfiguration checkConfiguration) {

        checkConfiguration.setMajorErrorRate(checkConfigurationDTO.getMajorErrorRate());
        checkConfiguration.setMinorErrorRate(checkConfigurationDTO.getMinorErrorRate());
        checkConfiguration.setDefinitionErrorRate(checkConfigurationDTO.getDefinitionErrorRate());
        checkConfiguration.setSampleRate(checkConfigurationDTO.getSampleRate());
        checkConfiguration.setSampleMode(checkConfigurationDTO.getSampleMode());
        checkConfiguration.setLabel(checkConfigurationDTO.getLabel());
        checkConfiguration.setSeparators(checkConfigurationDTO.getSeparators() == null ? "_"
                                                                                       : checkConfigurationDTO.getSeparators());

        checkConfiguration.setSeparators(checkConfigurationDTO.getSeparators() == null ? "_"
                                                                                       : checkConfigurationDTO.getSeparators());

        if (checkConfigurationDTO.getAutomaticCheckRules() != null) {
            final List<AutomaticCheckRule> checkRules = new ArrayList<>();
            checkConfigurationDTO.getAutomaticCheckRules().stream().forEach(ruleDto -> {
                final AutomaticCheckRule rule = ruleMapper.checkRuleDtoToCheckRule(ruleDto);
                rule.setCheckConfiguration(checkConfiguration);
                checkRules.add(rule);
            });
            checkConfiguration.setAutomaticCheckRules(checkRules);
        }

        if (checkConfigurationDTO.getLibrary() != null) {
            Library library = libraryRepository.getOne(checkConfigurationDTO.getLibrary().getIdentifier());
            checkConfiguration.setLibrary(library);
        }

    }
}
