package fr.progilone.pgcn.service.ocrlangconfiguration.ui;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLangConfigurationDTO;
import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.SimpleOcrLangConfigDTO;
import fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLangConfiguration;
import fr.progilone.pgcn.service.ocrlangconfiguration.OcrLangConfigurationService;
import fr.progilone.pgcn.service.ocrlangconfiguration.mapper.OcrLangConfigurationMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UIOcrLangConfigurationService {

    private final OcrLangConfigurationService ocrLangConfigurationService;
    private final UIOcrLangConfigMapper ocrConfigMapper;

    public UIOcrLangConfigurationService(final OcrLangConfigurationService ocrLangConfigurationService, final UIOcrLangConfigMapper ocrConfigMapper) {
        this.ocrLangConfigurationService = ocrLangConfigurationService;
        this.ocrConfigMapper = ocrConfigMapper;
    }

    @Transactional
    public OcrLangConfigurationDTO create(final OcrLangConfigurationDTO configDTO) {
        final OcrLangConfiguration newConf = new OcrLangConfiguration();
        ocrConfigMapper.mapDtoToConfig(configDTO, newConf);
        final OcrLangConfigurationDTO returnDto = new OcrLangConfigurationDTO();
        return ocrConfigMapper.mapConfigToDto(ocrLangConfigurationService.save(newConf), returnDto);
    }

    @Transactional
    public void delete(final String id) {
        ocrLangConfigurationService.delete(id);
    }

    @Transactional(readOnly = true)
    public OcrLangConfigurationDTO getOne(final String id) {
        final OcrLangConfigurationDTO retDto = new OcrLangConfigurationDTO();
        return ocrConfigMapper.mapConfigToDto(ocrLangConfigurationService.findOne(id), retDto);
    }

    @Transactional
    public OcrLangConfigurationDTO update(final OcrLangConfigurationDTO configDTO) {

        final OcrLangConfiguration conf = ocrLangConfigurationService.findOne(configDTO.getIdentifier());
        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(conf, configDTO);
        ocrConfigMapper.mapDtoToConfig(configDTO, conf);

        final OcrLangConfigurationDTO returnDto = new OcrLangConfigurationDTO();
        // persist object and return dto.
        return ocrConfigMapper.mapConfigToDto(conf, returnDto);
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
    public Page<SimpleOcrLangConfigDTO> search(final String search, final List<String> libraries, final Integer page, final Integer size) {
        final Pageable pageRequest = PageRequest.of(page, size);
        final Page<OcrLangConfiguration> ocrLangConfigs = ocrLangConfigurationService.search(search, libraries, pageRequest);

        return ocrLangConfigs.map(OcrLangConfigurationMapper.INSTANCE::ocrLangConfigToSimpleDto);

    }

}
