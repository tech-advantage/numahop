package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.document.BibliographicRecord;
import fr.progilone.pgcn.repository.es.helper.EsSearchOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;

public interface EsBibliographicRecordRepositoryCustom {

    /**
     * Recherche paginée
     *
     * @param search
     * @param libraries
     * @param fuzzy
     * @param pageable
     * @param filters
     * @param facet
     * @return
     */
    Page<BibliographicRecord> search(final EsSearchOperation[] search,
                                     final List<String> libraries,
                                     final boolean fuzzy,
                                     final EsSearchOperation[] filters,
                                     final PageRequest pageable,
                                     final boolean facet);

    /**
     * Indexation des notices, en spécifiant l'index
     *
     * @param index
     * @param records
     */
    void index(final String index, final Collection<BibliographicRecord> records);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param record
     */
    void indexEntity(BibliographicRecord record);

    /**
     * Indexation (avec gestion de la relation parent/enfant)
     *
     * @param records
     */
    void indexEntities(Collection<BibliographicRecord> records);

    /**
     * Suppression d'une notice de l'index
     *
     * @param record
     */
    void deleteEntity(BibliographicRecord record);

    /**
     * Suppression d'une collection de notices de l'index
     *
     * @param records
     */
    void deleteEntities(Collection<BibliographicRecord> records);
}
