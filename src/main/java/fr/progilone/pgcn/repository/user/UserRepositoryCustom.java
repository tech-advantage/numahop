package fr.progilone.pgcn.repository.user;

import fr.progilone.pgcn.domain.user.User;
import fr.progilone.pgcn.domain.user.User.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {

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
