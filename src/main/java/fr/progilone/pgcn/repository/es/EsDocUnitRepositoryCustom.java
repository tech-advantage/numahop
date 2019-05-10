package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.DocUnit;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface EsDocUnitRepositoryCustom {

    /**
     * Recherche paginée
     *
     * @param searches
     * @param libraries
     * @param fuzzy
     * @param filters
     * @param pageable
     * @param facet
     * @return
     */
    Page<DocUnit> search(final EsSearchOperation[] searches,
                         final List<String> libraries,
                         final boolean fuzzy,
                         final EsSearchOperation[] filters,
                         final PageRequest pageable,
                         final boolean facet);

    /**
     * Suggestion
     *
     * @param text
     * @param size
     * @return
     */
    List<Map<String, Object>> suggest(String text, final int size, final List<String> libraries);

    /**
     * Recherche par identifiant
     *
     * @param identifiers
     * @return
     */
    List<DocUnit> findByIds(final List<String> identifiers);

    /**
     * Indexation des unités documentaires, en spécifiant l'index
     *
     * @param index
     * @param docUnits
     */
    void index(final String index, final Collection<DocUnit> docUnits);
}
