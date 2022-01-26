package fr.progilone.pgcn.web.rest.exchange.omeka;

import static fr.progilone.pgcn.web.rest.document.security.AuthorizationConstants.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import com.codahale.metrics.annotation.Timed;

import fr.progilone.pgcn.domain.administration.omeka.OmekaConfiguration;
import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.exception.PgcnTechnicalException;
import fr.progilone.pgcn.security.SecurityUtils;
import fr.progilone.pgcn.service.administration.omeka.OmekaConfigurationService;
import fr.progilone.pgcn.service.document.DocUnitService;
import fr.progilone.pgcn.service.exchange.omeka.OmekaRequestHandlerService;
import fr.progilone.pgcn.web.util.AccessHelper;
import fr.progilone.pgcn.web.util.LibraryAccesssHelper;

/**
 * Contrôleur gérant l'export d'unités documentaires vers Omeka.
 * <p>
 * Created by manu on 14/09/2018.
 */
@RestController
@RequestMapping(value = "/api/rest/export/omeka")
public class ExportOmekaController {
    
    
    private final DocUnitService docUnitService;
    private final LibraryAccesssHelper libraryAccesssHelper;
    private final OmekaConfigurationService omekaConfigurationService;
    private final OmekaRequestHandlerService omekaRequestHandlerService;
    private final AccessHelper accessHelper;
    
    @Autowired
    public ExportOmekaController(final DocUnitService docUnitService,
                                 final LibraryAccesssHelper libraryAccesssHelper,
                                 final OmekaConfigurationService omekaConfigurationService,
                                 final OmekaRequestHandlerService omekaRequestHandlerService,
                                 final AccessHelper accessHelper) {
        this.docUnitService = docUnitService;
        this.libraryAccesssHelper = libraryAccesssHelper;
        this.omekaConfigurationService = omekaConfigurationService;
        this.omekaRequestHandlerService = omekaRequestHandlerService;
        this.accessHelper = accessHelper;

    }
    
    /**
     * Export Omeka unitaire.
     * Génération du répertoire d'export et upload sur le serveur SFTP pour OMEKA (avec les métadonnées Dublin Core).
     *
     * @param request
     * @param docUnitId
     * @param conf
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, params = {"sendomeka"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed({DOC_UNIT_HAB4})
    public ResponseEntity<?> exportDocUnitToOmekaWithDc(final HttpServletRequest request,
                                                                  @RequestParam(value = "docUnit") final String docUnitId) {
         
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
        final Set<OmekaConfiguration> confs = omekaConfigurationService.findByLibraryAndActive(docUnit.getLibrary(), true);
        final OmekaConfiguration conf = confs.stream().findFirst().orElse(null);
        if (conf == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
        final String currentUserId = SecurityUtils.getCurrentUserId(); 
        omekaRequestHandlerService.exportDocToOmeka(docUnit, currentUserId, false, true, true);
        return new ResponseEntity<>(HttpStatus.OK); 
    }
    
    /**
     * Export Omeka multiple.
     * Déclenche l'export OMEKA de la liste de documents spécifiés.
     *
     * @throws PgcnTechnicalException
     */
    @RolesAllowed({DOC_UNIT_HAB4})
    @RequestMapping(method = RequestMethod.GET, params = {"mass_export"})
    @Timed
    public ResponseEntity<HttpStatus> massExport(final HttpServletRequest request, 
                                        @RequestParam(name = "docs") final List<String> docs) throws PgcnTechnicalException {
        // droits d'accès à l'ud
        final Collection<DocUnit> filteredDocUnits = accessHelper.filterDocUnits(docs);
        if (filteredDocUnits.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        int i = 0;
        boolean lastDoc = false;
        for (final DocUnit docUnit : filteredDocUnits) {     
            if(++i == filteredDocUnits.size()) {
                lastDoc = true;
            } 
            exportOmeka(request, docUnit, lastDoc, i == 1);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 
     * @param request
     * @param docUnit
     * @param lastDoc
     * @param firstDoc
     */
    private void exportOmeka(final HttpServletRequest request, final DocUnit docUnit, final boolean lastDoc, final boolean firstDoc) {
        
        // Vérification de la bibliothèque de l'utilisateur
        if (!libraryAccesssHelper.checkLibrary(request, docUnit.getLibrary().getIdentifier())) {
            return;
        }
        // Vérification présence d'une conf omeka
        final Set<OmekaConfiguration> confs = omekaConfigurationService.findByLibraryAndActive(docUnit.getLibrary(), true);
        final OmekaConfiguration conf = confs.stream().findFirst().orElse(null);
        if (conf == null) {
            return; 
        }
        
        final String currentUserId = SecurityUtils.getCurrentUserId(); 
        omekaRequestHandlerService.exportDocToOmeka(docUnit, currentUserId, true, lastDoc, firstDoc);
    }
    

}
