package fr.progilone.pgcn.service.lot.mapper;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.progilone.pgcn.domain.administration.CinesPAC;
import fr.progilone.pgcn.domain.administration.InternetArchiveCollection;
import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.administration.omeka.OmekaList;
import fr.progilone.pgcn.domain.dto.administration.CinesPACDTO;
import fr.progilone.pgcn.domain.dto.administration.InternetArchiveCollectionDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaConfigurationDTO;
import fr.progilone.pgcn.domain.dto.administration.omeka.OmekaListDTO;
import fr.progilone.pgcn.domain.dto.document.SimpleDocUnitDTO;
import fr.progilone.pgcn.domain.dto.lot.LotDTO;
import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.lot.Lot;
import fr.progilone.pgcn.repository.document.DocUnitRepository;
import fr.progilone.pgcn.repository.ocrlangconfiguration.OcrLanguageRepository;
import fr.progilone.pgcn.repository.project.ProjectRepository;
import fr.progilone.pgcn.service.administration.CinesPACService;
import fr.progilone.pgcn.service.administration.InternetArchiveCollectionService;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.administration.omeka.OmekaListService;
import fr.progilone.pgcn.service.administration.viewsformat.ViewsFormatConfigurationService;
import fr.progilone.pgcn.service.checkconfiguration.CheckConfigurationService;
import fr.progilone.pgcn.service.ftpconfiguration.FTPConfigurationService;
import fr.progilone.pgcn.service.user.UserService;
import fr.progilone.pgcn.service.workflow.WorkflowModelService;

@Component
public class UILotMapper {

    @Autowired
    private DocUnitRepository docUnitRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private FTPConfigurationService ftpConfigurationService;
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
    @Autowired
    private OcrLanguageRepository ocrLangRepository; 

    public UILotMapper() {
    }

    public void mapInto(final LotDTO lotDTO, final Lot lot) {
        lot.setIdentifier(lotDTO.getIdentifier());
        lot.setLabel(lotDTO.getLabel());
        lot.setCode(lotDTO.getCode());
        // Type
        if (lotDTO.getType() != null) {
            lot.setType(Lot.Type.valueOf(lotDTO.getType()));
        }
        lot.setDescription(lotDTO.getDescription());
        lot.setActive(lotDTO.getActive());
        lot.setCondNotes(lotDTO.getCondNotes());
        lot.setNumNotes(lotDTO.getNumNotes());
        lot.setDeliveryDateForseen(lotDTO.getDeliveryDateForseen());
        lot.setRequiredFormat(lotDTO.getRequiredFormat());
        lot.setRequiredTypeCompression(lotDTO.getRequiredTypeCompression());
        lot.setRequiredTauxCompression(lotDTO.getRequiredTauxCompression());
        lot.setRequiredResolution(lotDTO.getRequiredResolution());
        lot.setRequiredColorspace(lotDTO.getRequiredColorspace());

        final Set<SimpleDocUnitDTO> docUnits = lotDTO.getDocUnits();
        if (docUnits != null) {
            lot.setDocUnits(docUnits.stream().map(docUnit -> docUnitRepository.findOne(docUnit.getIdentifier())).collect(Collectors.toSet()));
        } else {
            lot.setDocUnits(new HashSet<>());
        }
        if (lotDTO.getProject() != null) {
            lot.setProject(projectRepository.findOneByIdentifier(lotDTO.getProject().getIdentifier()));
        }
        if (lotDTO.getActiveFTPConfiguration() != null) {
            lot.setActiveFTPConfiguration(ftpConfigurationService.getOne(lotDTO.getActiveFTPConfiguration().getIdentifier()));
        }
        if (lotDTO.getActiveCheckConfiguration() != null) {
            lot.setActiveCheckConfiguration(checkConfigurationService.findOne(lotDTO.getActiveCheckConfiguration().getIdentifier()));
        }
        if (lotDTO.getActiveFormatConfiguration() != null) {
            lot.setActiveFormatConfiguration(viewsFormatConfigurationService.findOne(lotDTO.getActiveFormatConfiguration().getIdentifier()));
        }
        if (lotDTO.getWorkflowModel() != null) {
            lot.setWorkflowModel(workflowModelService.getOne(lotDTO.getWorkflowModel().getIdentifier()));
        }
        if (lotDTO.getActiveOcrLanguage() != null) {
            lot.setActiveOcrLanguage(ocrLangRepository.getOne(lotDTO.getActiveOcrLanguage().getIdentifier()));
        }
        

        final InternetArchiveCollectionDTO iaCollection = lotDTO.getCollectionIA();
        if (iaCollection != null && iaCollection.getIdentifier() != null) {
            final InternetArchiveCollection internetArchiveCollection = iaCollectionService.findOne(iaCollection.getIdentifier());
            lot.setCollectionIA(internetArchiveCollection);
        }

        final CinesPACDTO cinesPACDTO = lotDTO.getPlanClassementPAC();
        if (cinesPACDTO != null && cinesPACDTO.getIdentifier() != null) {
            final CinesPAC cinesPAC = cinesPACService.findOne(cinesPACDTO.getIdentifier());
            lot.setPlanClassementPAC(cinesPAC);
        }
        
        final OmekaListDTO collecOmeka = lotDTO.getOmekaCollection();
        if (collecOmeka != null && collecOmeka.getIdentifier() != null) {
            final OmekaList omekaCollection = omekaListService.findOne(collecOmeka.getIdentifier());
            lot.setOmekaCollection(omekaCollection);
        } else {
            lot.setOmekaCollection(null);
        }

        final OmekaListDTO itemOmeka = lotDTO.getOmekaItem();
        if (itemOmeka != null && itemOmeka.getIdentifier() != null) {
            final OmekaList omekaItem = omekaListService.findOne(itemOmeka.getIdentifier());
            lot.setOmekaItem(omekaItem);
        } else {
            lot.setOmekaItem(null);
        }

        final OmekaConfigurationDTO omekaConfDTO = lotDTO.getOmekaConfiguration();
        if (omekaConfDTO != null && omekaConfDTO.getIdentifier() != null) {
            final OmekaConfiguration omekaConf = omekaConfigurationService.findOne(omekaConfDTO.getIdentifier());
            lot.setOmekaConfiguration(omekaConf);
        } else {
            lot.setOmekaConfiguration(null);
        }

        final SimpleUserDTO providerDto = lotDTO.getProvider();
        if (providerDto != null && providerDto.getIdentifier() != null) {
            lot.setProvider(userService.findByIdentifier(providerDto.getIdentifier()));
        } else {
            lot.setProvider(null);
        }

    }
}
