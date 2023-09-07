package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.es.delivery.EsDelivery;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import fr.progilone.pgcn.repository.es.helper.SearchResultPage;
import java.util.List;
import org.springframework.data.domain.PageRequest;

public interface EsDeliveryRepositoryCustom {

    /**
     * Recherche paginée
     */
    SearchResultPage<EsDelivery> search(final EsSearchOperation[] search,
                                        final List<String> libraries,
                                        final boolean fuzzy,
                                        final EsSearchOperation[] filters,
                                        final PageRequest pageable,
                                        final boolean facet);

}
