package fr.progilone.pgcn.web.rest.filesgestion;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.FILES_GEST_HAB0;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.filesgestion.FilesGestionConfigDTO;
import fr.progilone.pgcn.domain.filesgestion.FilesGestionConfig;
import fr.progilone.pgcn.service.filesgestion.FilesGestionConfigMapper;
import fr.progilone.pgcn.service.filesgestion.FilesGestionConfigService;
import fr.progilone.pgcn.web.rest.AbstractRestController;

@RestController
@RequestMapping(value = "/api/rest/filesgestionconfig")
public class FilesGestionConfigController extends AbstractRestController {

    private final FilesGestionConfigService filesGestionConfigService;

    @Autowired
    public FilesGestionConfigController(final FilesGestionConfigService filesGestionConfigService) {
        this.filesGestionConfigService = filesGestionConfigService;
    }

    @RequestMapping(value = "/{idLibrary}", method = RequestMethod.GET, params = {}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FILES_GEST_HAB0})
    public ResponseEntity<FilesGestionConfigDTO> getByLibrary(@PathVariable final String idLibrary) {

        final FilesGestionConfigDTO dto = filesGestionConfigService.getConfigByLibrary(idLibrary);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, params = {}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FILES_GEST_HAB0})
    public ResponseEntity<FilesGestionConfigDTO> saveConfig(@PathVariable final String id,
                                                            @RequestBody final FilesGestionConfigDTO config) {

        final FilesGestionConfigDTO saved = filesGestionConfigService.save(FilesGestionConfigMapper.INSTANCE.configDtoToObj(config));
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({FILES_GEST_HAB0})
    public ResponseEntity<FilesGestionConfigDTO> createConfig(@RequestBody final FilesGestionConfigDTO config) {

        final FilesGestionConfigDTO saved = filesGestionConfigService.save(FilesGestionConfigMapper.INSTANCE.configDtoToObj(config));
        return new ResponseEntity<>(saved, HttpStatus.OK);
    }


}
