package fr.progilone.pgcn.service.administration.z3950;

import fr.progilone.pgcn.domain.administration.exchange.z3950.Z3950Server;
import fr.progilone.pgcn.domain.dto.administration.z3950.Z3950ServerDTO;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.administration.z3950.Z3950ServerRepository;
import fr.progilone.pgcn.service.administration.mapper.Z3950ServerMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service permettant d'administrer les serveurs Z39.50
 * <p>
 * Created by Sébastien on 21/12/2016.
 */
@Service
public class Z3950ServerService {

    private final Z3950ServerRepository z3950ServerRepository;

    @Autowired
    public Z3950ServerService(final Z3950ServerRepository z3950ServerRepository) {
        this.z3950ServerRepository = z3950ServerRepository;
    }

    /**
     * Récupération d'un serveur Z39.50 en fonction de son identifiant.
     *
     * @param id identifiant du serveur
     * @return un objet <code>Z3950Server</code> correspondant à l'identifiant.
     */
    @Transactional(readOnly = true)
    public Z3950Server findOne(final String id) {
        return z3950ServerRepository.findOne(id);
    }

    /**
     * Récupération de l'ensemble des serveurs Z 39.50
     *
     * @return une liste de <code>Z3950Server</code> correspondant à l'ensemble des bibliothèques.
     */
    @Transactional(readOnly = true)
    public List<Z3950Server> findAll() {
        return z3950ServerRepository.findAll();
    }

    /**
     * Récupération de l'ensemble des serveurs Z39.50.
     *
     * @return liste de <code>Z3950ServerDTO</code> contenant l'ensemble des serveurs Z39.50 (identifiant + nom + version)
     */
    @Transactional(readOnly = true)
    public List<Z3950ServerDTO> findAllDTO() {
        final List<Z3950Server> servers = z3950ServerRepository.findAll(new Sort("name"));
        return Z3950ServerMapper.INSTANCE.z3950ServerToDtos(servers);
    }

    /**
     * Modification d'un serveur Z 39.50 en fonction de l'objet passé en paramètre.
     *
     * @param z3950Server serveur à modifier.
     * @return l'objet <code>Z3950Server</code> modifié.
     * @throws PgcnValidationException si le nom est déjà pris
     */
    @Transactional
    public Z3950Server save(final Z3950Server z3950Server) throws PgcnValidationException {
        validate(z3950Server);
        final List<Z3950Server> activeServers = z3950ServerRepository.findByActive(true);
        if (!activeServers.isEmpty() && z3950Server.isActive()) {
            for (final Z3950Server activeServer : activeServers) {
                activeServer.setActive(false);
            }
            z3950ServerRepository.save(activeServers);
        }
        return z3950ServerRepository.save(z3950Server);
    }

    /**
     * Suppression d'une serveur Z39.50 en fonction de son identifiant.
     *
     * @param id identifiant du serveur à supprimer.
     */
    @Transactional
    public void delete(final String id) {
        z3950ServerRepository.delete(id);
    }

    /**
     * Validation d'un serveur Z39.50
     *
     * @param z3950Server
     */
    private void validate(final Z3950Server z3950Server) {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        final String id = z3950Server.getIdentifier();
        final String name = z3950Server.getName();

        // le nom est obligatoire
        if (StringUtils.isBlank(name)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.Z3950SERVER_NAME_MANDATORY).setField("name").build());
        }
        // le nom est unique
        else {
            final Z3950Server dup = id != null
                                    ? z3950ServerRepository.findOneByNameAndIdentifierNot(name, id)
                                    : z3950ServerRepository.findOneByName(name);
            if (dup != null) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.Z3950SERVER_UNIQUE_NAME_VIOLATION).setField("name").build());
            }
        }

        if (!errors.isEmpty()) {
            z3950Server.setErrors(errors);
            throw new PgcnValidationException(z3950Server, errors);
        }
    }
}
