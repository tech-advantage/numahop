package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.document.EsDocUnit;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.SearchResultPage;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;

public interface EsDocUnitRepositoryCustom {

    /**
     * Recherche paginée
     */
    SearchResultPage<EsDocUnit> search(final EsSearchOperation[] searches,
                                       final List<String> libraries,
                                       final boolean fuzzy,
                                       final EsSearchOperation[] filters,
                                       final PageRequest pageable,
                                       final boolean facet);

    /**
     * Suggestion
     */
    List<Map<String, Object>> suggest(String text, final int size, final List<String> libraries);

}
