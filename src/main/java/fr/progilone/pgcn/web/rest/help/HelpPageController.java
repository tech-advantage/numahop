package fr.progilone.pgcn.web.rest.help;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.dto.help.HelpPageDto;
import fr.progilone.pgcn.domain.dto.help.ModuleDto;
import fr.progilone.pgcn.domain.help.HelpPage;
import fr.progilone.pgcn.domain.help.HelpPageType;
import fr.progilone.pgcn.service.help.HelpPageService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rest/help")
@PermitAll
public class HelpPageController extends AbstractRestController {

    private final HelpPageService helpPageService;

    @Autowired
    public HelpPageController(final HelpPageService helpPageService) {
        super();
        this.helpPageService = helpPageService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ModuleDto>> search(@RequestParam(required = false, value = "modules") final List<String> modules,
                                                  @RequestParam(required = false, value = "types") final List<HelpPageType> types,
                                                  @RequestParam(required = false, value = "search") final String search) {
        return createResponseEntity(helpPageService.search(modules, types, search));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"tag"})
    @Timed
    public ResponseEntity<HelpPageDto> searchByTag(@RequestParam final String tag) {
        return new ResponseEntity<>(helpPageService.searchByTag(tag), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<HelpPage> getById(@PathVariable final String id) {
        return createResponseEntity(helpPageService.findOneByIdentifier(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @Timed
    public ResponseEntity<HelpPage> save(@RequestBody final HelpPage helpPage) {
        return createResponseEntity(helpPageService.save(helpPage));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    public void delete(@PathVariable final String id) {
        helpPageService.delete(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    @Timed
    public ResponseEntity<HelpPage> create(@RequestBody final HelpPage helpPage) {
        return createResponseEntity(helpPageService.save(helpPage));
    }

    @RequestMapping(method = RequestMethod.GET, params = {"modulelist"})
    @Timed
    public ResponseEntity<List<String>> findAllLevels() {
        return createResponseEntity(helpPageService.findAllModules());
    }
}
