package fr.progilone.pgcn.web.rest.audit;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.audit.AuditRevision;
import fr.progilone.pgcn.domain.exchange.Mapping;
import fr.progilone.pgcn.service.audit.AuditMappingService;
import fr.progilone.pgcn.service.exchange.MappingService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants.*;

/**
 * Created by Sébastien on 27/06/2017.
 */
@RestController
@RequestMapping(value = "/api/rest/audit/mapping")
public class AuditMappingController extends AbstractRestController {

    private final LibraryAccesssHelper libraryAccesssHelper;
    private final AuditMappingService auditMappingService;
    private final MappingService mappingService;

    @Autowired
    public AuditMappingController(LibraryAccesssHelper libraryAccesssHelper, AuditMappingService auditMappingService, MappingService mappingService) {
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.auditMappingService = auditMappingService;
        this.mappingService = mappingService;
    }

    @RequestMapping(value = "/{id}", params = {"rev"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<Mapping> getEntity(final HttpServletRequest request,
                                             @PathVariable(value = "id") final String id,
                                             @RequestParam(value = "rev", defaultValue = "1") final int rev) {
        // Chargement du mapping existant
        final Mapping dbMapping = mappingService.findOne(id);
        // Non trouvé
        if (dbMapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (!libraryAccesssHelper.checkLibrary(request, dbMapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return createResponseEntity(auditMappingService.getEntity(id, rev));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({MAP_HAB0})
    public ResponseEntity<List<AuditRevision>> getRevisions(final HttpServletRequest request, @PathVariable(value = "id") final String id) {
        // Chargement du mapping existant
        final Mapping dbMapping = mappingService.findOne(id);
        // Non trouvé
        if (dbMapping == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification des droits d'accès par rapport à la bibliothèque de l'utilisateur, pour le mapping existant
        if (!libraryAccesssHelper.checkLibrary(request, dbMapping, Mapping::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Réponse
        return new ResponseEntity<>(auditMappingService.getRevisions(id), HttpStatus.OK);
    }
}
