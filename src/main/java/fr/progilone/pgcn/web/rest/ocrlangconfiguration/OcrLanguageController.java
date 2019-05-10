package fr.progilone.pgcn.web.rest.ocrlangconfiguration;

import static fr.progilone.pgcn.web.rest.ocrlangconfiguration.security.AuthorizationConstants.OCR_LANG_HAB0;

import java.util.List;

import javax.annotation.security.RolesAllowed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.dto.ocrlangconfiguration.OcrLanguageDTO;
import fr.progilone.pgcn.service.library.LibraryService;
import fr.progilone.pgcn.service.ocrlangconfiguration.OcrLanguageService;
import fr.progilone.pgcn.web.rest.AbstractRestController;

@RestController
@RequestMapping(value = "/api/rest/ocrlanguages")
public class OcrLanguageController extends AbstractRestController {

    private final OcrLanguageService ocrLanguageService;
    private final LibraryService libraryService;
    
    @Autowired
    public OcrLanguageController(final OcrLanguageService ocrLanguageService, final LibraryService libraryService) {
        this.ocrLanguageService = ocrLanguageService;
        this.libraryService = libraryService;
    }
        
        
    @RequestMapping(method = RequestMethod.GET, params = {"languages"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({OCR_LANG_HAB0})
    public ResponseEntity<List<OcrLanguageDTO>> search() {
             
        return new ResponseEntity<>(ocrLanguageService.findAll(), HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, params = {"langs"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({OCR_LANG_HAB0})
    public ResponseEntity<List<OcrLanguageDTO>> getLangs(@RequestParam(name = "library", required = false) final String libraryId) {
        
            return new ResponseEntity<>(libraryService.findActifsOcrLanguagesByLibrary(libraryId), HttpStatus.OK);
    }
    
}
