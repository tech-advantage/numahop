package fr.progilone.pgcn.web.rest.document;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.document.ListPhysicalDocumentDTO;
import fr.progilone.pgcn.domain.dto.document.PhysicalDocumentDTO;
import fr.progilone.pgcn.service.document.ui.UIPhysicalDocumentService;
import fr.progilone.pgcn.service.es.EsDocUnitService;
import fr.progilone.pgcn.web.rest.AbstractRestController;

@RestController
@RequestMapping(value = "/api/rest/physicaldocument")
public class PhysicalDocumentController extends AbstractRestController {

    private final EsDocUnitService esDocUnitService;
    private final UIPhysicalDocumentService uiPhysicalDocumentService;

    @Autowired
    public PhysicalDocumentController(final EsDocUnitService esDocUnitService,
                                      final UIPhysicalDocumentService uiPhysicalDocumentService) {
        super();
        this.esDocUnitService = esDocUnitService;
        this.uiPhysicalDocumentService = uiPhysicalDocumentService;
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PhysicalDocumentDTO> getById(@PathVariable final String identifier) {
        final PhysicalDocumentDTO pddto = uiPhysicalDocumentService.findByIdentifier(identifier);
        return createResponseEntity(pddto);
    }

    @RequestMapping(value = "/{identifier}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<PhysicalDocumentDTO> update(@RequestBody final PhysicalDocumentDTO doc) {
        final PhysicalDocumentDTO savedPhysDoc = uiPhysicalDocumentService.update(doc);
        esDocUnitService.indexPhysicalDocumentAsync(savedPhysDoc.getIdentifier());
        return new ResponseEntity<>(savedPhysDoc, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, params = {"train"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<List<ListPhysicalDocumentDTO>> findByTrainIdentifier(@RequestParam(value = "train") final String trainId) {
        final List<ListPhysicalDocumentDTO> documents = uiPhysicalDocumentService.findByTrainIdentifier(trainId);
        return createResponseEntity(documents);
    }
    
    @RequestMapping(method = RequestMethod.GET, params = {"trainDocUnits"})
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public ResponseEntity<List<ListPhysicalDocumentDTO>> findByDocUnitIdentifiers(@RequestParam(name = "docUnitIds") final List<String> docUnitIds) {
        final List<ListPhysicalDocumentDTO> documents = uiPhysicalDocumentService.findByDocUnitIds(docUnitIds);
        return createResponseEntity(documents);
    }
}
