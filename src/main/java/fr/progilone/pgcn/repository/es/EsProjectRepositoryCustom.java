package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.project.Project;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;

public interface EsProjectRepositoryCustom {

    /**
     * Recherche paginée
     *
     * @param search
     * @param libraries
     * @param fuzzy
     * @param filters
     * @param pageable
     * @param facet
     * @return
     */
    Page<Project> search(final EsSearchOperation[] search,
                         final List<String> libraries,
                         final boolean fuzzy,
                         final EsSearchOperation[] filters,
                         final PageRequest pageable,
                         final boolean facet);

    /**
     * Indexation des unités documentaires, en spécifiant l'index
     *
     * @param index
     * @param Projects
     */
    void index(final String index, final Collection<Project> Projects);
}
