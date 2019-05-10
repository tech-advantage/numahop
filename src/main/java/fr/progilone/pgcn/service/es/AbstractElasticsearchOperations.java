package fr.progilone.pgcn.service.es;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public abstract class AbstractElasticsearchOperations<T> {

    /**
     * Types d'éléments recherchés
     */
    public enum SearchEntity {
        CONDREPORT,
        DELIVERY,
        DOCUNIT,
        LOT,
        PROJECT,
        TRAIN
    }

    /**
     * Indexation d'une seule entité
     *
     * @param identifier
     * @return
     */
    public abstract T index(final String identifier);

    /**
     * Indexation de plusieurs entités
     *
     * @param identifiers
     * @return
     */
    public abstract Iterable<T> index(final List<String> identifiers);

    /**
     * Suppression d'une entité de l'index de recherche
     *
     * @param entity
     */
    public abstract void delete(final T entity);

    /**
     * Suppression d'entités de l'index de recherche
     *
     * @param entities
     */
    public abstract void delete(Collection<T> entities);

    /**
     * Indexation asynchrone
     *
     * @param identifier
     */
    @Async
    @Transactional(readOnly = true)
    public void indexAsync(final String identifier) {
        index(identifier);
    }

    /**
     * Indexation asynchrone de plusieurs entités
     *
     * @param identifiers
     */
    @Async
    @Transactional(readOnly = true)
    public void indexAsync(final List<String> identifiers) {
        index(identifiers);
    }

    /**
     * Suppression asynchrone
     *
     * @param entity
     */
    @Async
    public void deleteAsync(final T entity) {
        delete(entity);
    }

    /**
     * Suppression asynchrone
     *
     * @param entities
     */
    @Async
    public void deleteAsync(final Collection<T> entities) {
        delete(entities);
    }
}
