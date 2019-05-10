package fr.progilone.pgcn.service.ftpconfiguration.ui;

import fr.progilone.pgcn.domain.dto.ftpconfiguration.FTPConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ftpconfiguration.SimpleFTPConfigurationDTO;
import fr.progilone.pgcn.domain.ftpconfiguration.FTPConfiguration;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.service.ftpconfiguration.FTPConfigurationService;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.FTPConfigurationMapper;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.SimpleFTPConfigurationMapper;
import fr.progilone.pgcn.service.ftpconfiguration.mapper.UIFTPConfigurationMapper;
import fr.progilone.pgcn.service.project.ProjectService;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lebouchp on 03/02/2017.
 */
@Service
public class UIFTPConfigurationService {

    private final FTPConfigurationService ftpConfigurationService;
    private final ProjectService projectService;
    private final UIFTPConfigurationMapper uiFTPConfigurationMapper;

    public UIFTPConfigurationService(final FTPConfigurationService ftpConfigurationService,
                                     final ProjectService projectService,
                                     final UIFTPConfigurationMapper uiFTPConfigurationMapper) {
        this.ftpConfigurationService = ftpConfigurationService;
        this.projectService = projectService;
        this.uiFTPConfigurationMapper = uiFTPConfigurationMapper;
    }

    @Transactional
    public FTPConfigurationDTO create(FTPConfigurationDTO ftpConfigurationDTO) throws PgcnTechnicalException {
        final FTPConfiguration ftpConfiguration = new FTPConfiguration();
        uiFTPConfigurationMapper.mapInto(ftpConfigurationDTO, ftpConfiguration);
        return FTPConfigurationMapper.INSTANCE.ftpConfigurationToFTPConfigurationDTO(ftpConfigurationService.save(ftpConfiguration));
    }

    @Transactional
    public FTPConfigurationDTO update(FTPConfigurationDTO ftpConfigurationDTO) throws PgcnTechnicalException {
        final FTPConfiguration ftpConfiguration = ftpConfigurationService.getOne(ftpConfigurationDTO.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(ftpConfiguration, ftpConfigurationDTO);

        uiFTPConfigurationMapper.mapInto(ftpConfigurationDTO, ftpConfiguration);

        final FTPConfiguration savedConf = ftpConfigurationService.save(ftpConfiguration);
        return getOne(savedConf.getIdentifier());
    }

    @Transactional(readOnly = true)
    public FTPConfigurationDTO getOne(String id) {
        FTPConfiguration ftpConfiguration = ftpConfigurationService.getOne(id);
        return FTPConfigurationMapper.INSTANCE.ftpConfigurationToFTPConfigurationDTO(ftpConfiguration);
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
    public Page<SimpleFTPConfigurationDTO> search(String search, List<String> libraries, Integer page, Integer size) {
        final Pageable pageRequest = new PageRequest(page, size);
        final Page<FTPConfiguration> ftpConfigurations = ftpConfigurationService.search(search, libraries, pageRequest);
        return ftpConfigurations.map(SimpleFTPConfigurationMapper.INSTANCE::ftpConfigurationToSimpleFTPConfigurationDTO);
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
    public List<SimpleFTPConfigurationDTO> getAllByProjectId(String projectId, final List<String> libraries) {
        final Project project = projectService.findOneWithFTPConfiguration(projectId);
        return project.getLibrary()
                      .getFtpConfigurations()
                      .stream()
                      .filter(conf -> CollectionUtils.isEmpty(libraries) || (conf.getLibrary() != null && libraries.contains(conf.getLibrary()
                                                                                                                                   .getIdentifier())))
                      .map(SimpleFTPConfigurationMapper.INSTANCE::ftpConfigurationToSimpleFTPConfigurationDTO)
                      .collect(Collectors.toList());
    }
}
