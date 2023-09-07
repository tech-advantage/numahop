package fr.progilone.pgcn.repository.library;

import fr.progilone.pgcn.domain.library.Library;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LibraryRepositoryCustom {

    /**
     * Recherche rapide des bibliothèques
     *
     * @param search
     * @param libraries
     * @param initiale
     * @param institutions
     * @param pageable
     * @return
     */
    Page<Library> search(String search, List<String> libraries, String initiale, List<String> institutions, boolean isActive, Pageable pageable);
}
