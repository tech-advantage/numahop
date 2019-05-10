package fr.progilone.pgcn.service.exportftpconfiguration;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.SimpleExportFTPConfDTO;
import fr.progilone.pgcn.domain.exportftpconfiguration.ExportFTPConfiguration;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.service.exportftpconfiguration.mapper.ExportFTPConfigurationMapper;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

@Service
public class UIExportFTPConfigurationService {
    
    private final ExportFTPConfigurationService exportFtpConfigurationService;
    private final ProjectService projectService;
    private final LibraryRepository libraryRepository;

    @Autowired
    public UIExportFTPConfigurationService(final ExportFTPConfigurationService exportFtpConfigurationService,
                                     final ProjectService projectService,
                                     final LibraryRepository libraryRepository) {
        this.exportFtpConfigurationService = exportFtpConfigurationService;
        this.projectService = projectService;
        this.libraryRepository = libraryRepository;
    }

    /**
     * Recherche paginée paramétrée
     *
     * @param search
     * @param libraries
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public Page<SimpleExportFTPConfDTO> search(final String search, final List<String> libraries, final Integer page, final Integer size) {
        final Pageable pageRequest = new PageRequest(page, size);
        final Page<ExportFTPConfiguration> ftpConfigurations = exportFtpConfigurationService.search(search, libraries, pageRequest);
        return ftpConfigurations.map(ExportFTPConfigurationMapper.INSTANCE::objectToSimpleDto);
    }
    
    @Transactional
    public ExportFTPConfigurationDTO create(final ExportFTPConfigurationDTO dto) throws PgcnTechnicalException {
        
        final ExportFTPConfiguration conf = ExportFTPConfigurationMapper.INSTANCE.dtoToObject(dto);
        return ExportFTPConfigurationMapper.INSTANCE.objectToDto(exportFtpConfigurationService.save(conf));
    }

    @Transactional
    public ExportFTPConfigurationDTO update(final ExportFTPConfigurationDTO dto) throws PgcnTechnicalException {
        
        final ExportFTPConfiguration conf = exportFtpConfigurationService.getOne(dto.getIdentifier());
        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(conf, dto);
        mapInto(dto, conf);

        final ExportFTPConfiguration savedConf = exportFtpConfigurationService.save(conf);
        return getOne(savedConf.getIdentifier());
    }

    @Transactional(readOnly = true)
    public ExportFTPConfigurationDTO getOne(final String id) {
        final ExportFTPConfiguration config = exportFtpConfigurationService.getOne(id);
        return ExportFTPConfigurationMapper.INSTANCE.objectToDto(config);
    }
    
    /**
     * Récupération des configurations par projet
     *
     * @param projectId
     * @param libraries
     *         filtrage par bibliothèque (pour les droits d'accès)
     * @return
     */
    @Transactional(readOnly = true)
    public List<SimpleExportFTPConfDTO> getAllByProjectId(final String projectId, final List<String> libraries) {
        final Project project = projectService.findOneWithExportFTPConfiguration(projectId);
        return project.getLibrary()
                      .getExportFtpConfigurations()
                      .stream()
                      .filter(conf -> CollectionUtils.isEmpty(libraries) || (conf.getLibrary() != null && libraries.contains(conf.getLibrary()
                                                                                                                                   .getIdentifier())))
                      .map(ExportFTPConfigurationMapper.INSTANCE::objectToSimpleDto)
                      .collect(Collectors.toList());
    }
    
    
    
    private void mapInto(final ExportFTPConfigurationDTO dto, final ExportFTPConfiguration conf) {
        conf.setAddress(dto.getAddress());
        conf.setLogin(dto.getLogin());
        conf.setLabel(dto.getLabel());
        conf.setPassword(dto.getPassword());
        conf.setStorageServer(dto.getStorageServer());
        conf.setPort(dto.getPort());
        conf.setActive(dto.isActive());
        
        conf.setExportView( dto.isExportView() );
        conf.setExportMaster( dto.isExportMaster() );
        conf.setExportThumb( dto.isExportThumb() );
        conf.setExportPdf( dto.isExportPdf() );
        conf.setExportMets( dto.isExportMets() );
        conf.setExportAipSip( dto.isExportAipSip() );

        if(dto.getLibrary() != null) {
            final Library library = libraryRepository.getOne(dto.getLibrary().getIdentifier());
            conf.setLibrary(library);
        }
    }

}
