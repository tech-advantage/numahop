package fr.progilone.pgcn.web.rest.check;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.DOC_UNIT_HAB4;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fr.progilone.pgcn.domain.check.AutomaticCheckType.AutoCheckType;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.service.check.AutomaticCheckService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

/**
 * Contrôleur gérant les contrôles automatiques
 *
 * @author jbrunet
 * Créé le 14 févr. 2017
 */
@RestController
@RequestMapping(value = "/api/rest/check/auto")
public class AutomaticCheckController {

    private final AutomaticCheckService autoCheckService;
    private final DocUnitService docUnitService;
    private final LibraryAccesssHelper libraryAccesssHelper;

    @Autowired
    public AutomaticCheckController(final AutomaticCheckService autoCheckService,
                                    final DocUnitService docUnitService,
                                    final LibraryAccesssHelper libraryAccesssHelper) {
        this.autoCheckService = autoCheckService;
        this.docUnitService = docUnitService;
        this.libraryAccesssHelper = libraryAccesssHelper;
    }

    /**
     * Lance un traitement asynchrone vers FACILE du Cines
     *
     * @param request
     * @param docUnitId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, params = {"facile"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> checkFacile(final HttpServletRequest request, @RequestParam(value = "docUnit") final String docUnitId) {
        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);
        // Non trouvé
        if (docUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification de la bibliothèque de l'utilisateur
        // 1/ sur l'unité documentaire
        if (!libraryAccesssHelper.checkLibrary(request, docUnit, DocUnit::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Traitement
        autoCheckService.asyncUnitCheck(AutoCheckType.FACILE, docUnit, docUnit.getLibrary().getIdentifier());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
