package fr.progilone.pgcn.domain.dto.user;

import fr.progilone.pgcn.domain.dto.library.SimpleLibraryDTO;
import fr.progilone.pgcn.domain.user.Lang;
import fr.progilone.pgcn.domain.user.User;
import java.time.LocalDateTime;

/**
 * DTO représentant un usager et ses droits
 * Ajout du mot de passe en création uniquement
 *
 * @author jbrunet
 * @see User
 */
public class UserCreationDTO extends UserDTO {

    private String password;

    public UserCreationDTO(final String identifier,
                           final String login,
                           final String firstname,
                           final String surname,
                           final Boolean active,
                           final String category,
                           final Lang lang,
                           final RoleDTO role,
                           final AddressDTO address,
                           final SimpleLibraryDTO library,
                           final String phoneNumber,
                           final String email,
                           final String companyName,
                           final String function,
                           final Long version,
                           final String password,
                           String createdBy,
                           LocalDateTime createdDate,
                           String lastModifiedBy,
                           LocalDateTime lastModifiedDate) {
        super(identifier,
              login,
              firstname,
              surname,
              active,
              category,
              lang,
              role,
              address,
              library,
              phoneNumber,
              email,
              companyName,
              function,
              createdBy,
              createdDate,
              lastModifiedBy,
              lastModifiedDate);
        this.password = password;
    }

    public UserCreationDTO() {
    }

    public final String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
