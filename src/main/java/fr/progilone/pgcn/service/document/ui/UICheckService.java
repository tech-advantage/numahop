package fr.progilone.pgcn.service.document.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.progilone.pgcn.domain.document.Check;
import fr.progilone.pgcn.domain.dto.document.CheckDTO;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.document.CheckService;
import fr.progilone.pgcn.service.document.mapper.CheckMapper;
import fr.progilone.pgcn.service.document.mapper.UICheckMapper;
import fr.progilone.pgcn.service.util.transaction.VersionValidationService;

/**
 * Service dédié à les gestion des vues des contrôles (visuels)
 * @author jbrunet
 *
 */
@Service
public class UICheckService {

    private final CheckService checkService;
    private final UICheckMapper uiCheckMapper;

    @Autowired
    public UICheckService(final CheckService checkService,
                                          final UICheckMapper uiCheckMapper) {
        this.checkService = checkService;
        this.uiCheckMapper = uiCheckMapper;
    }
    
    @Transactional
    public CheckDTO create(final CheckDTO dto) throws PgcnValidationException {
        final Check check = new Check();
        uiCheckMapper.mapInto(dto, check);
        Check saved = checkService.save(check);
        return CheckMapper.INSTANCE.checkToCheckDTO(saved);
    }

    @Transactional
    public CheckDTO update(CheckDTO dto) {
        Check check = checkService.findOne(dto.getIdentifier());

        // Contrôle d'accès concurrents
        VersionValidationService.checkForStateObject(check, dto);
        
        uiCheckMapper.mapInto(dto, check);
        
        return CheckMapper.INSTANCE.checkToCheckDTO(checkService.save(check));
    }
}
