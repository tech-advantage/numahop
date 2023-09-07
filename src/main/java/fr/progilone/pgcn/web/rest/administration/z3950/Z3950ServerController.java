package fr.progilone.pgcn.web.rest.administration.z3950;

import static fr.progilone.pgcn.web.rest.administration.security.AuthorizationConstants.*;

import com.codahale.metrics.annotation.Timed;
import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.service.administration.z3950.Z3950ServerService;
import fr.progilone.pgcn.web.rest.AbstractRestController;
import jakarta.annotation.security.RolesAllowed;
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

/**
 * Controller permettant de gérer les web services des serveurs Z39.50
 */
@RestController
@RequestMapping(value = "/api/rest/z3950Server")
public class Z3950ServerController extends AbstractRestController {

    /**
     * Service gérant les serveurs Z39.50.
     */
    private final Z3950ServerService z3950ServerService;

    @Autowired
    public Z3950ServerController(final Z3950ServerService z3950ServerService) {
        this.z3950ServerService = z3950ServerService;
    }

    /**
     * Création d'une Serveur Z39.50.
     *
     * @param z3950Server
     *            le serveur à créer (objet <code>Z3950Server</code>).
     * @return l'objet <code>Z3950Server</code> créé.
     * @throws PgcnValidationException
     *             si erreur lors du create
     */
    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({Z3950_HAB1})
    public ResponseEntity<Z3950Server> create(@RequestBody final Z3950Server z3950Server) throws PgcnValidationException {
        return new ResponseEntity<>(z3950ServerService.save(z3950Server), HttpStatus.CREATED);
    }

    /**
     * Récupération de l'ensemble des serveurs Z39.50
     *
     * @param dto
     *            si on souhaite des objets réduits.
     * @return liste de <code>Z3950Server</code> contenant l'ensemble des serveurs Z39.50 (identifiant + nom + version).
     */
    @RequestMapping(method = RequestMethod.GET, params = {"dto"}, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({Z3950_HAB0})
    public ResponseEntity<List<Z3950ServerDTO>> findAll(@RequestParam("dto") final Boolean dto) {
        return new ResponseEntity<>(z3950ServerService.findAllDTO(), HttpStatus.OK);
    }

    /**
     * Récupération de l'ensemble des serveurs Z39.50.
     *
     * @return liste de <code>Z3950Server</code> contenant l'ensemble des serveurs Z39.50
     */
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({Z3950_HAB0})
    public ResponseEntity<List<Z3950Server>> findAll() {
        return new ResponseEntity<>(z3950ServerService.findAll(), HttpStatus.OK);
    }

    /**
     * Récupération d'un serveur Z39.50 en fonction de son identifiant.
     *
     * @param id
     *            identifiant du serveur à récupérer.
     * @return un objet <code>Z3950Server</code> correspondant à l'identifiant.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({Z3950_HAB0})
    public ResponseEntity<Z3950Server> getById(@PathVariable final String id) {
        return createResponseEntity(z3950ServerService.findOne(id));
    }

    /**
     * Modification d'un serveur Z39.50 en fonction de l'objet passé en paramètre.
     *
     * @param z3950Server
     *            serveur à modifier.
     * @return l'objet <code>Z3950Server</code> modifié.
     * @throws PgcnValidationException
     *             si erreur lors du save
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed({Z3950_HAB1})
    public ResponseEntity<Z3950Server> udpate(@RequestBody final Z3950Server z3950Server) throws PgcnValidationException {
        return new ResponseEntity<>(z3950ServerService.save(z3950Server), HttpStatus.OK);
    }

    /**
     * Suppression d'un serveur Z39.50 en fonction de son identifiant.
     *
     * @param id
     *            identifiant du serveur à supprimer.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Timed
    @RolesAllowed({Z3950_HAB2})
    public void delete(@PathVariable final String id) {
        z3950ServerService.delete(id);
    }

}
