package fr.progilone.pgcn.service.project.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import fr.progilone.pgcn.domain.administration.ExportFTPDeliveryFolder;
import fr.progilone.pgcn.domain.dto.exportftpconfiguration.ExportFTPConfigurationDeliveryFolderDTO;
import fr.progilone.pgcn.service.exportftpconfiguration.ExportFTPConfigurationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.dto.project.ProjectDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.library.Library;
import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.InternetArchiveCollectionService;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.administration.omeka.OmekaListService;
import fr.progilone.pgcn.service.administration.viewsformat.ViewsFormatConfigurationService;
import fr.progilone.pgcn.service.checkconfiguration.CheckConfigurationService;
import fr.progilone.pgcn.service.ftpconfiguration.FTPConfigurationService;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.workflow.WorkflowModelService;

@Service
public class UIProjectMapper {

    @Autowired
    private LibraryService libraryService;
    @Autowired
    private FTPConfigurationService ftpConfigurationService;
    @Autowired
    private ExportFTPConfigurationService exportFTPConfigurationService;
    @Autowired
    private CheckConfigurationService checkConfigurationService;
    @Autowired
    private ViewsFormatConfigurationService viewsFormatConfigurationService;
    @Autowired
    private WorkflowModelService workflowModelService;
    @Autowired
    private UserService userService;
    @Autowired
    private InternetArchiveCollectionService iaCollectionService;
    @Autowired
    private CinesPACService cinesPACService;
    @Autowired
    private OmekaListService omekaListService;
    @Autowired
    private OmekaConfigurationService omekaConfigurationService;

    public void mapInto(final ProjectDTO projectDTO, final Project project) {
        // Library
        // Récupère libraryDTO du projectDTO
        final SimpleLibraryDTO libraryDTO = projectDTO.getLibrary();

        if (libraryDTO.getIdentifier() != null) {
            //Récupère bibliotheque depuis repository
            final Library library = libraryService.findOne(libraryDTO.getIdentifier());
            project.setLibrary(library);
        }

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setActive(projectDTO.isActive());
        project.setStartDate(projectDTO.getStartDate());
        project.setForecastEndDate(projectDTO.getForecastEndDate());
        project.setRealEndDate(projectDTO.getRealEndDate());

        // AssociatedLibraries
        final Set<SimpleLibraryDTO> libraryDTOs = projectDTO.getAssociatedLibraries();
        if (libraryDTOs != null) {
            project.setAssociatedLibraries(libraryDTOs.stream()
                                                      .map(library -> libraryService.findOne(library.getIdentifier()))
                                                      .collect(Collectors.toSet()));
        }
        // AssociatedUsers
        final Set<SimpleUserDTO> userDTOs = projectDTO.getAssociatedUsers();
        if (userDTOs != null) {
            project.setAssociatedUsers(userDTOs.stream().map(user -> userService.findByIdentifier(user.getIdentifier())).collect(Collectors.toSet()));
        }

        if (projectDTO.getActiveFTPConfiguration() != null) {
            project.setActiveFTPConfiguration(ftpConfigurationService.getOne(projectDTO.getActiveFTPConfiguration().getIdentifier()));
        }
        if (projectDTO.getActiveExportFTPConfiguration() != null) {
            project.setActiveExportFTPConfiguration(exportFTPConfigurationService.getOne(projectDTO.getActiveExportFTPConfiguration().getIdentifier()));
            //delivery folders update
            if(project.getActiveExportFTPConfiguration() != null && projectDTO.getActiveExportFTPDeliveryFolder() != null) {
                ExportFTPDeliveryFolder newFolder = new ExportFTPDeliveryFolder();
                newFolder.setIdentifier(projectDTO.getActiveExportFTPDeliveryFolder().getIdentifier());
                newFolder.setName(projectDTO.getActiveExportFTPDeliveryFolder().getName());
                project.setActiveExportFTPDeliveryFolder(newFolder);
            } else {
                project.setActiveExportFTPDeliveryFolder(null);
            }
        }
        if (projectDTO.getActiveCheckConfiguration() != null) {
            project.setActiveCheckConfiguration(checkConfigurationService.findOne(projectDTO.getActiveCheckConfiguration().getIdentifier()));
        }
        if (projectDTO.getActiveFormatConfiguration() != null) {
            project.setActiveFormatConfiguration(viewsFormatConfigurationService.findOne(projectDTO.getActiveFormatConfiguration().getIdentifier()));
        }
        if (projectDTO.getWorkflowModel() != null) {
            project.setWorkflowModel(workflowModelService.getOne(projectDTO.getWorkflowModel().getIdentifier()));
        }
        if (projectDTO.getOmekaConfiguration() != null) {
            project.setOmekaConfiguration(omekaConfigurationService.findOne(projectDTO.getOmekaConfiguration().getIdentifier()));
        }

        final InternetArchiveCollectionDTO iaCollection = projectDTO.getCollectionIA();
        if (iaCollection != null && iaCollection.getIdentifier() != null) {
            final InternetArchiveCollection internetArchiveCollection = iaCollectionService.findOne(iaCollection.getIdentifier());
            project.setCollectionIA(internetArchiveCollection);
        } else {
            project.setCollectionIA(null);
        }

        if(projectDTO.getLicenseUrl() != null){
            project.setLicenseUrl(projectDTO.getLicenseUrl());
        }

        final CinesPACDTO cinesPACDTO = projectDTO.getPlanClassementPAC();
        if (cinesPACDTO != null && cinesPACDTO.getIdentifier() != null) {
            final CinesPAC cinesPAC = cinesPACService.findOne(cinesPACDTO.getIdentifier());
            project.setPlanClassementPAC(cinesPAC);
        } else {
            project.setPlanClassementPAC(null);
        }

        final OmekaListDTO collecOmeka = projectDTO.getOmekaCollection();
        if (collecOmeka != null && collecOmeka.getIdentifier() != null) {
            final OmekaList omekaCollection = omekaListService.findOne(collecOmeka.getIdentifier());
            project.setOmekaCollection(omekaCollection);
        } else {
            project.setOmekaCollection(null);
        }

        final OmekaListDTO itemOmeka = projectDTO.getOmekaItem();
        if (itemOmeka != null && itemOmeka.getIdentifier() != null) {
            final OmekaList omekaItem = omekaListService.findOne(itemOmeka.getIdentifier());
            project.setOmekaItem(omekaItem);
        } else {
            project.setOmekaItem(null);
        }

        final SimpleUserDTO providerDto = projectDTO.getProvider();
        if (providerDto != null && providerDto.getIdentifier() != null) {
            project.setProvider(userService.findByIdentifier(providerDto.getIdentifier()));
        } else {
            project.setProvider(null);
        }

        if(projectDTO.getLibRespName() != null){
            project.setLibRespName(projectDTO.getLibRespName());
        }
        if(projectDTO.getLibRespPhone() != null){
            project.setLibRespPhone(projectDTO.getLibRespPhone());
        }
        if(projectDTO.getLibRespEmail() != null){
            project.setLibRespEmail(projectDTO.getLibRespEmail());
        }
    }
}
