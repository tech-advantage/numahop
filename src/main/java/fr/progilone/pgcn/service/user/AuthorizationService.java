package fr.progilone.pgcn.service.user;

import fr.progilone.pgcn.domain.dto.user.AuthorizationDTO;
import fr.progilone.pgcn.domain.user.Authorization;
import fr.progilone.pgcn.repository.user.AuthorizationRepository;
import fr.progilone.pgcn.web.util.AuthorizationManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService {

    private final AuthorizationRepository authorizationRepository;

    @Autowired
    public AuthorizationService(final AuthorizationRepository authorizationRepository) {
        this.authorizationRepository = authorizationRepository;
    }

    @Transactional(readOnly = true)
    public List<AuthorizationDTO> findAllDTO() {
        final List<Authorization> authorizations = authorizationRepository.findAll();
        final AuthorizationDTO.Builder authBuilder = new AuthorizationDTO.Builder();
        return authorizations.stream()
                             .map(auth -> authBuilder.reinit()
                                                     .setCode(auth.getCode())
                                                     .setIdentifier(auth.getIdentifier())
                                                     .setLabel(auth.getLabel())
                                                     .setModule(auth.getModule())
                                                     .setDescription(auth.getDescription())
                                                     .setRequirements(AuthorizationManager.getRequirements(auth.getCode()))
                                                     .setDependencies(AuthorizationManager.getDependencies(auth.getCode()))
                                                     .build())
                             .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Authorization> findAll() {
        return authorizationRepository.findAllWithRoles();
    }

    @Transactional(readOnly = true)
    public Authorization findOne(final String identifier) {
        return authorizationRepository.findOneWithRoles(identifier);
    }
}
