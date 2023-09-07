package fr.progilone.pgcn.web.rest.imagemetadata;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB5;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.imagemetadata.ImageMetadataValuesDTO;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataProperty;
import fr.progilone.pgcn.domain.imagemetadata.ImageMetadataValue;
import fr.progilone.pgcn.exception.PgcnBusinessException;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.imagemetadata.ImageMetadataService;
import fr.progilone.pgcn.service.imagemetadata.ImageMetadataValuesService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rest/imagemetadata")
public class ImageMetadataController extends AbstractRestController {

    private final ImageMetadataService imageMetadataService;
    private final ImageMetadataValuesService imageMetadataValuesService;

    @Autowired
    public ImageMetadataController(final ImageMetadataService imageMetadataService, final ImageMetadataValuesService imageMetadataValuesService) {
        super();
        this.imageMetadataService = imageMetadataService;
        this.imageMetadataValuesService = imageMetadataValuesService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(DOC_UNIT_HAB5)
    public ResponseEntity<List<ImageMetadataProperty>> findAll() {
        return new ResponseEntity<>(imageMetadataService.findAll(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<ImageMetadataProperty> create(@RequestBody final ImageMetadataProperty metadata) throws PgcnValidationException {
        final ImageMetadataProperty savedMetadata = imageMetadataService.save(metadata);
        return new ResponseEntity<>(savedMetadata, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"saveList"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<List<ImageMetadataProperty>> saveList(@RequestBody final List<ImageMetadataProperty> metadataList) throws PgcnBusinessException {
        // Ok, so create/edit metadata
        final List<ImageMetadataProperty> savedMetadata = imageMetadataService.saveList(metadataList);
        return new ResponseEntity<>(savedMetadata, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<ImageMetadataProperty> update(@RequestBody final ImageMetadataProperty metadata) throws PgcnValidationException {
        final ImageMetadataProperty savedType = imageMetadataService.save(metadata);
        return new ResponseEntity<>(savedType, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, params = {"saveValues"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<List<ImageMetadataValue>> saveValues(@RequestBody final List<ImageMetadataValuesDTO> valuesDto) throws PgcnValidationException {
        List<ImageMetadataValue> valuesSaved = imageMetadataValuesService.saveList(valuesDto);

        return new ResponseEntity<>(valuesSaved, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"getMetaValues"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({DOC_UNIT_HAB5})
    public ResponseEntity<List<ImageMetadataValue>> getMetaValues(@RequestParam(value = "docUnitId") final String docUnitIdentifier) throws PgcnValidationException {

        List<ImageMetadataValue> valuesSaved = imageMetadataValuesService.getValuesByDocUnit(docUnitIdentifier);

        return new ResponseEntity<>(valuesSaved, HttpStatus.OK);
    }
}
