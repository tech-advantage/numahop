package fr.progilone.pgcn.web.rest.exchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.exchange.cines.CinesLanguageCode;
import fr.progilone.pgcn.exception.PgcnException;
import fr.progilone.pgcn.service.exchange.cines.CinesLanguageCodeService;
import fr.progilone.pgcn.web.rest.AbstractRestController;


/**
 * Controleur basique pour la gestion des codes langue pour export cines.
 *
 */
@RestController
@RequestMapping(value = "/api/rest/conf/cineslangcode")
public class CinesLanguageCodeController extends AbstractRestController{

    private final CinesLanguageCodeService cinesLangCodeService;
    
    @Autowired
    public CinesLanguageCodeController(final CinesLanguageCodeService cinesLangCodeService) {
        this.cinesLangCodeService = cinesLangCodeService;
    }
    
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CinesLanguageCode>> getListByActive() {
        
        return createResponseEntity(cinesLangCodeService.findAllActive(true));
    }
    
    
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CinesLanguageCode>> update(@RequestBody final List<CinesLanguageCode> cinesCodes) throws PgcnException {
        
        final List<CinesLanguageCode> savedCodes = cinesLangCodeService.update(cinesCodes);
        return new ResponseEntity<>(savedCodes, HttpStatus.OK);
    }
}
