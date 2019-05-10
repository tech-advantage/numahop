package fr.progilone.pgcn.service.administration.viewsformat;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.administration.viewsformat.ViewsFormatConfiguration;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.SimpleViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.viewsFormat.ViewsFormatConfigurationDTO;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.administration.mapper.SimpleViewsFormatConfigurationMapper;
import fr.progilone.pgcn.service.administration.mapper.ViewsFormatConfigurationMapper;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.util.DefaultFileFormats;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

@Service
public class UIViewsFormatConfigurationService {

    private final ViewsFormatConfigurationService service;
    private final ProjectService projectService;
    private final DefaultFileFormats defaultFormats;
    
    @Autowired
    public UIViewsFormatConfigurationService(final ViewsFormatConfigurationService viewsFormatConfigurationService,
                                             final ProjectService projectService,
                                             final DefaultFileFormats defaultFormats) {
        this.service = viewsFormatConfigurationService;
        this.projectService = projectService;
        this.defaultFormats = defaultFormats;
    }
    
    
    @Transactional
    public ViewsFormatConfigurationDTO create(final ViewsFormatConfigurationDTO confDTO) {
        
        final ViewsFormatConfiguration formatConfiguration = ViewsFormatConfigurationMapper.INSTANCE.dtoToFormatConfig(confDTO);
        formatConfiguration.setDefaultFormats(defaultFormats);
        return ViewsFormatConfigurationMapper.INSTANCE.formatConfigToDto(service.save(formatConfiguration));
    }
    
    @Transactional
    public ViewsFormatConfigurationDTO update(final ViewsFormatConfigurationDTO formatConfigurationDTO) {
        
        final ViewsFormatConfiguration formatConfiguration = service.findOne(formatConfigurationDTO.getIdentifier());
        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(formatConfiguration, formatConfigurationDTO);
        
        // maj entity
        BeanUtils.copyProperties(formatConfigurationDTO, formatConfiguration);
        final ViewsFormatConfiguration saved = service.save(formatConfiguration);
        saved.setDefaultFormats(defaultFormats);
        // persist and return dto.
        return ViewsFormatConfigurationMapper.INSTANCE.formatConfigToDto(saved);
    }
    
    @Transactional
    public void delete(final String id) {
        service.delete(id);
    }
    
    
    /**
     * Récupération des configurations par projet
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleViewsFormatConfigurationDTO> getAllByProjectId(final String id) {
        final Project project = projectService.findOneWithFTPConfiguration(id);
        return project.getLibrary()
                      .getViewsFormatConfigurations()
                      .stream()
                      .map(SimpleViewsFormatConfigurationMapper.INSTANCE::formatConfigToDto)
                      .collect(Collectors.toList());
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
    public Page<SimpleViewsFormatConfigurationDTO> search(final String search, final List<String> libraries, final Integer page, final Integer size) {
        final Pageable pageRequest = new PageRequest(page, size);
        final Page<ViewsFormatConfiguration> formatConfigurations = service.search(search, libraries, pageRequest);
        return formatConfigurations.map(SimpleViewsFormatConfigurationMapper.INSTANCE::formatConfigToDto);
    }
    
    @Transactional(readOnly = true)
    public ViewsFormatConfigurationDTO getOne(final String id) {
        final ViewsFormatConfiguration test = service.findOne(id);
        test.setDefaultFormats(defaultFormats);
        return ViewsFormatConfigurationMapper.INSTANCE.formatConfigToDto(test);
    }
}
