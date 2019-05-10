package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.dto.user.SimpleUserDTO;
import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {

    /**
     * récupère tous les usagers sous forme allégée
     *
     * @param fromDate
     *         date à partir de laquelle on veut faire un delta des modifications
     * @return liste contenant l'ensemble des BorrowerSimpleDTO
     */
    List<SimpleUserDTO> findAllSimpleDTO(final Optional<Date> fromDate);

    /**
     * Recherche rapide d'utilisateurs
     *
     * @param search
     * @param initiale
     * @param libraries
     * @param categories
     * @param pageable
     * @return
     */
    Page<User> search(String search,
                      final String initiale,
                      final boolean active,
                      final boolean filterProviders,
                      final List<String> libraries,
                      final List<Category> categories,
                      final List<String> roles,
                      Pageable pageable);

    /**
     * Nombres de users par bibliothèque
     *
     * @param libraries
     * @return
     */
    List<Object[]> getUsersGroupByLibrary(List<String> libraries);
}
