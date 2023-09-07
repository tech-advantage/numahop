package fr.progilone.pgcn.service.user;

import fr.progilone.pgcn.domain.dto.user.RoleDTO;
import fr.progilone.pgcn.domain.user.Authorization;
import fr.progilone.pgcn.domain.user.Role;
import fr.progilone.pgcn.exception.PgcnValidationException;
import fr.progilone.pgcn.exception.message.PgcnError;
import fr.progilone.pgcn.exception.message.PgcnErrorCode;
import fr.progilone.pgcn.exception.message.PgcnList;
import fr.progilone.pgcn.repository.library.LibraryRepository;
import fr.progilone.pgcn.repository.user.RoleRepository;
import fr.progilone.pgcn.repository.user.UserRepository;
import fr.progilone.pgcn.service.user.mapper.RoleMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

    private final LibraryRepository libraryRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleService(final LibraryRepository libraryRepository, final RoleRepository roleRepository, final UserRepository userRepository) {
        this.libraryRepository = libraryRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleDTO> findAllDTO() {
        final List<Role> roles = roleRepository.findAll();
        final RoleDTO.Builder builder = new RoleDTO.Builder();
        return roles.stream()
                    .map(role -> builder.reinit().setCode(role.getCode()).setIdentifier(role.getIdentifier()).setLabel(role.getLabel()).build())
                    .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepository.findAllWithAuthorizations();
    }

    @Transactional(readOnly = true)
    public Role findOne(final String identifier) {
        return roleRepository.findOneWithAuthorizations(identifier);
    }

    /**
     * Permet de savoir si l'autorisation est dans le role
     *
     * @param role
     * @param authorization
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isAuthorized(Role role, Authorization authorization) {
        return role != null && authorization != null
               && CollectionUtils.isNotEmpty(role.getAuthorizations())
               && role.getAuthorizations().contains(authorization);
    }

    @Transactional
    public void delete(final String identifier) throws PgcnValidationException {
        // Validation de la suppresion
        final Role role = roleRepository.getOne(identifier);
        validateDelete(role);

        // Suppression
        roleRepository.deleteById(identifier);
    }

    private void validateDelete(final Role role) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        // Bibliothèque
        final Long libCount = libraryRepository.countByDefaultRole(role);
        if (libCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.ROLE_DEL_EXITS_LIB).setAdditionalComplement(libCount).build());
        }
        // Utilisateur
        final Long userCount = userRepository.countByRole(role);
        if (userCount > 0) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.ROLE_DEL_EXITS_USER).setAdditionalComplement(userCount).build());
        }

        if (!errors.isEmpty()) {
            role.setErrors(errors);
            throw new PgcnValidationException(role, errors);
        }
    }

    @Transactional
    public Role save(final Role role) throws PgcnValidationException {
        setDefaultValues(role);
        validate(role);
        return roleRepository.save(role);
    }

    private void setDefaultValues(final Role role) {
        final String code = role.getCode();

        role.setSuperuser(false);
        // code en majuscules
        if (code != null) {
            role.setCode(StringUtils.upperCase(code));
        }
    }

    private PgcnList<PgcnError> validate(final Role role) throws PgcnValidationException {
        final PgcnList<PgcnError> errors = new PgcnList<>();
        final PgcnError.Builder builder = new PgcnError.Builder();

        /* Validation non paramétrable **/
        final String id = role.getIdentifier();
        final String code = role.getCode();
        final String label = role.getLabel();

        // le code est obligatoire
        if (StringUtils.isBlank(code)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_ROLE_CODE_MANDATORY).setField("code").build());
        }
        // le code est unique
        else {
            final Role dup = id != null ? roleRepository.findByCodeAndIdentifierNot(code, id)
                                        : roleRepository.findByCode(code);
            if (dup != null) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.USER_ROLE_UNIQUE_CODE_VIOLATION).setField("code").build());
            }
        }
        // le label est obligatoire
        if (StringUtils.isBlank(label)) {
            errors.add(builder.reinit().setCode(PgcnErrorCode.USER_ROLE_LABEL_MANDATORY).setField("label").build());
        }
        // le label est unique
        else {
            final Role dup = id != null ? roleRepository.findOneByLabelAndIdentifierNot(label, id)
                                        : roleRepository.findOneByLabel(label);
            if (dup != null) {
                errors.add(builder.reinit().setCode(PgcnErrorCode.USER_ROLE_UNIQUE_LABEL_VIOLATION).setField("label").build());
            }
        }

        /* Retour **/
        if (!errors.isEmpty()) {
            role.setErrors(errors);
            throw new PgcnValidationException(role, errors);
        }
        return errors;
    }

    public List<RoleDTO> search(String search, List<String> authorizations) {
        List<Role> roles = roleRepository.search(search, authorizations);
        return roles.stream().map(RoleMapper.INSTANCE::roleToRoleDTO).collect(Collectors.toList());
    }
}
