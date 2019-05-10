package fr.progilone.pgcn.web.rest.document;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.document.DocPropertyType;
import fr.progilone.pgcn.domain.dto.document.DocPropertyTypeDTO;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.document.DocPropertyTypeService;
import fr.progilone.pgcn.service.document.ui.UIDocPropertyTypeService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

@RestController
@RequestMapping(value = "/api/rest/docpropertytype")
public class DocPropertyTypeController extends AbstractRestController {

    private final DocPropertyTypeService docPropertyTypeService;
    private final UIDocPropertyTypeService uiDocPropertyTypeService;

    @Autowired
    public DocPropertyTypeController(final DocPropertyTypeService docPropertyTypeService, final UIDocPropertyTypeService uiDocPropertyTypeService) {
        super();
        this.docPropertyTypeService = docPropertyTypeService;
        this.uiDocPropertyTypeService = uiDocPropertyTypeService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<DocPropertyType>> findAll() {
        return new ResponseEntity<>(docPropertyTypeService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<DocPropertyTypeDTO>> findAllDto() {
        return new ResponseEntity<>(uiDocPropertyTypeService.findAllDTO(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"dto", "supertype"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB0)
    public ResponseEntity<List<DocPropertyTypeDTO>> findAllBySuperType(
        @RequestParam(value = "supertype") final DocPropertyType.DocPropertySuperType superType) {
        return new ResponseEntity<>(uiDocPropertyTypeService.findAllDTOBySuperType(superType), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB0})
    public ResponseEntity<DocPropertyType> getById(@PathVariable final String id) {
        return createResponseEntity(docPropertyTypeService.findOne(id));
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<DocPropertyType> create(@RequestBody final DocPropertyType type) throws PgcnValidationException {
        // On ne peut créer que des types personnalisés
        final DocPropertyType.DocPropertySuperType superType = type.getSuperType();
        if (superType == null || superType != DocPropertyType.DocPropertySuperType.CUSTOM) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        final DocPropertyType savedType = docPropertyTypeService.save(type);
        return new ResponseEntity<>(savedType, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<DocPropertyType> delete(@PathVariable final String id) throws PgcnValidationException {
        // On ne peut supprimer que des types personnalisés
        final DocPropertyType type = docPropertyTypeService.findOne(id);
        final DocPropertyType.DocPropertySuperType superType = type.getSuperType();
        if (superType == null || superType != DocPropertyType.DocPropertySuperType.CUSTOM) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        docPropertyTypeService.delete(type);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<DocPropertyType> update(@RequestBody final DocPropertyType type) throws PgcnValidationException {
        final DocPropertyType savedType = docPropertyTypeService.save(type);
        return new ResponseEntity<>(savedType, HttpStatus.OK);
    }
}
