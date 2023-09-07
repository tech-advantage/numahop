package fr.progilone.pgcn.service.checkconfiguration.ui;

import fr.progilone.pgcn.domain.checkconfiguration.CheckConfiguration;
import fr.progilone.pgcn.domain.dto.checkconfiguration.CheckConfigurationDTO;
import fr.progilone.pgcn.domain.dto.checkconfiguration.SimpleCheckConfigurationDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import fr.progilone.pgcn.service.checkconfiguration.CheckConfigurationService;
import fr.progilone.pgcn.service.checkconfiguration.mapper.CheckConfigurationMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.SimpleCheckConfigurationMapper;
import fr.progilone.pgcn.service.checkconfiguration.mapper.UICheckConfigurationMapper;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class UICheckConfigurationService {

    private final CheckConfigurationMapper checkConfigMapper = Mappers.getMapper(CheckConfigurationMapper.class);

    private final CheckConfigurationService checkConfigurationService;
    private final ProjectService projectService;
    private final UICheckConfigurationMapper uiCheckConfigurationMapper;

    @Autowired
    public UICheckConfigurationService(final CheckConfigurationService checkConfigurationService,
                                       final ProjectService projectService,
                                       final AutomaticCheckService automaticCheckService,
                                       final UICheckConfigurationMapper uiCheckConfigurationMapper) {
        this.checkConfigurationService = checkConfigurationService;
        this.projectService = projectService;
        this.uiCheckConfigurationMapper = uiCheckConfigurationMapper;
    }

    @Transactional
    public CheckConfigurationDTO create(final CheckConfigurationDTO checkConfigurationDTO) {
        final CheckConfiguration checkConfiguration = new CheckConfiguration();
        uiCheckConfigurationMapper.mapInto(checkConfigurationDTO, checkConfiguration);
        return checkConfigMapper.checkConfigurationToCheckConfigurationDTO(checkConfigurationService.save(checkConfiguration));
    }

    @Transactional
    public CheckConfigurationDTO update(final CheckConfigurationDTO checkConfigurationDTO) {
        final CheckConfiguration checkConfiguration = checkConfigurationService.findOne(checkConfigurationDTO.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(checkConfiguration, checkConfigurationDTO);
        uiCheckConfigurationMapper.mapInto(checkConfigurationDTO, checkConfiguration);
        // persist object
        final CheckConfiguration savedConf = checkConfigurationService.save(checkConfiguration);
        // and return dto.
        return checkConfigMapper.checkConfigurationToCheckConfigurationDTO(savedConf);
    }

    @Transactional(readOnly = true)
    public CheckConfigurationDTO getOne(final String id) {
        return checkConfigMapper.checkConfigurationToCheckConfigurationDTO(checkConfigurationService.findOne(id));
    }

    @Transactional(readOnly = true)
    public CheckConfigurationDTO getOneForEdition(final String id) {
        return checkConfigMapper.checkConfigurationToCheckConfigurationDTO(checkConfigurationService.findAndEnrich(id));
    }

    /**
     * Recherche paramétrée paginée
     *
     * @param search
     * @param libraries
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SimpleCheckConfigurationDTO> search(final String search, final List<String> libraries, final Integer page, final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        final Page<CheckConfiguration> checkConfigurations = checkConfigurationService.search(search, libraries, pageRequest);
        return checkConfigurations.map(SimpleCheckConfigurationMapper.INSTANCE::checkConfigurationToSimpleCheckConfigurationDTO);
    }

    /**
     * Récupération des configurations par projet
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleCheckConfigurationDTO> getAllByProjectId(final String id) {
        final Project project = projectService.findOneWithFTPConfiguration(id);
        return project.getLibrary()
                      .getCheckConfigurations()
                      .stream()
                      .map(SimpleCheckConfigurationMapper.INSTANCE::checkConfigurationToSimpleCheckConfigurationDTO)
                      .collect(Collectors.toList());
    }

    @Transactional
    public CheckConfigurationDTO duplicateCheckConfiguration(final String id) {
        final CheckConfiguration duplicated = checkConfigurationService.duplicateCheckConfiguration(id);
        return checkConfigMapper.checkConfigurationToCheckConfigurationDTO(duplicated);
    }

}
