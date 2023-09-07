package fr.progilone.pgcn.web.rest.exchange.digitallibrary;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.digitallibrary.DigitalLibraryConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.administration.digitallibrary.DigitalLibraryConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.digitallibrary.DigitalLibraryDiffusionRequestHandlerService;
import fr.progilone.pgcn.web.rest.exchange.security.AuthorizationConstants;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur gérant la diffusion d'unités documentaires vers une bibliothèque numériqure.
 * <p>
 * Created by gaelle on 25/02/2021.
 */
@RestController
@RequestMapping(value = "/api/rest/export/digitalLibrary")
public class DigitalLibraryDiffusionController {

    private final DocUnitService docUnitService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final DigitalLibraryConfigurationService configurationService;
    private final DigitalLibraryDiffusionRequestHandlerService digitalLibraryDiffusionRequestHandlerService;
    private final AccessHelper accessHelper;

    @Autowired
    public DigitalLibraryDiffusionController(final DocUnitService docUnitService,
                                             final LibraryAccesssHelper libraryAccesssHelper,
                                             final DigitalLibraryConfigurationService configurationService,
                                             final DigitalLibraryDiffusionRequestHandlerService digitalLibraryDiffusionRequestHandlerService,
                                             final AccessHelper accessHelper) {
        this.docUnitService = docUnitService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.configurationService = configurationService;
        this.digitalLibraryDiffusionRequestHandlerService = digitalLibraryDiffusionRequestHandlerService;
        this.accessHelper = accessHelper;

    }

    /**
     * Export bib numérique unitaire.
     * Génération du répertoire d'export et upload sur le serveur SFTP pour bib numérique.
     *
     */
    @RequestMapping(method = RequestMethod.POST, params = {"send"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({AuthorizationConstants.EXPORT_DIFFUSION_DIGITAL_LIBRARY_HAB0})
    public ResponseEntity<?> exportDocUnitToDigitalLibrary(final HttpServletRequest request, @RequestParam(value = "docUnit") final String docUnitId) {

        final DocUnit docUnit = docUnitService.findOneWithAllDependencies(docUnitId);
        // Non trouvé
        if (docUnit == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérification de la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, docUnit, DocUnit::getLibrary)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        // Vérification présence d'une conf omeka
        final Set<DigitalLibraryConfiguration> confs = configurationService.findByLibraryAndActive(docUnit.getLibrary().getIdentifier(), true);
        final DigitalLibraryConfiguration conf = confs.stream().findFirst().orElse(null);
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final String currentUserId = SecurityUtils.getCurrentUserId();
        digitalLibraryDiffusionRequestHandlerService.exportDocToDigitalLibrary(docUnit, currentUserId, false, true, true);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Export bib numérique multiple.
     * Génération du répertoire d'export et upload sur le serveur SFTP pour bib numérique.
     **/
    @RolesAllowed({AuthorizationConstants.EXPORT_DIFFUSION_DIGITAL_LIBRARY_HAB0})
    @RequestMapping(method = RequestMethod.GET, params = {"mass_export"})
    @Timed
    public ResponseEntity<HttpStatus> massExport(final HttpServletRequest request, @RequestParam(name = "docs") final List<String> docs) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docs);
        if (filteredDocUnits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        int i = 0;
        boolean lastDoc = false;
        for (final DocUnit docUnit : filteredDocUnits) {
            if (++i == filteredDocUnits.size()) {
                lastDoc = true;
            }
            exportDigitalLibrary(request, docUnit, i == 1, lastDoc);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Export
     */
    private void exportDigitalLibrary(final HttpServletRequest request, final DocUnit docUnit, final boolean firstDoc, final boolean lastDoc) {

        // Vérification de la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, docUnit.getLibrary().getIdentifier())) {
            return;
        }
        // Vérification présence d'une conf bibliotheque numérique
        final Set<DigitalLibraryConfiguration> confs = configurationService.findByLibraryAndActive(docUnit.getLibrary().getIdentifier(), true);
        final DigitalLibraryConfiguration conf = confs.stream().findFirst().orElse(null);
        if (conf == null) {
            return;
        }

        final String currentUserId = SecurityUtils.getCurrentUserId();
        digitalLibraryDiffusionRequestHandlerService.exportDocToDigitalLibrary(docUnit, currentUserId, true, firstDoc, lastDoc);
    }
}
