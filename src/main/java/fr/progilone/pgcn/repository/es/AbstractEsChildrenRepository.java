package fr.progilone.pgcn.repository.es;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.ElasticsearchException;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractEsChildrenRepository<T extends AbstractDomainObject> extends AbstractEsRepository<T> {

    protected static final String NULL_PARENT = "__NULL__";

    public AbstractEsChildrenRepository(final Class<T> clazz, final EntityMapper entityMapper, final ElasticsearchTemplate elasticsearchTemplate) {
        super(clazz, elasticsearchTemplate, entityMapper);
    }

    /**
     * Indexation des entités avec spécification de l'index
     *
     * @param index
     * @param entities
     */
    @Override
    public void index(final String index, final Collection<T> entities) {
        if (entities.isEmpty()) {
            return;
        }
        final Client client = elasticsearchTemplate.getClient();
        final String type = entityInformation.getType();
        final BulkRequestBuilder bulkRequest = client.prepareBulk();

        // Indexation des entités mises à jour
        for (final T entity : entities) {
            try {
                bulkRequest.add(client.prepareIndex(index, type, entity.getIdentifier())
                                      .setParent(getParentId(entity))
                                      .setRouting(getRoutingId(entity))
                                      .setSource(entityMapper.mapToString(entity)));

            } catch (IOException e) {
                throw new ElasticsearchException("Échec de l'indexation de l'entité [id: " + entity.getIdentifier() + "]", e);
            }
        }
        // Exécution de la requête
        executeBulk(index, bulkRequest);
    }

    /**
     * Indexation d'une collection d'une entité
     * Contrairement à l'indexation par défaut, il s'agit ici d'une requête bulk consistant à une suppression + une indexation de l'entité.
     * En effet, une indexation seule peut mener à une duplication de l'entité si celle-ci change de parent, et que le nouveau parent est dans un shard différent.
     *
     * @param entity
     */
    public void indexEntity(final T entity) {
        indexEntities(Collections.singletonList(entity));
    }

    /**
     * Indexation d'une collection d'entités enfant.
     * Contrairement à l'indexation par défaut, il s'agit ici d'une requête bulk consistant à une suppression + une indexation de l'entité.
     * En effet, une indexation seule peut mener à une duplication de l'entité si celle-ci change de parent, et que le nouveau parent est dans un shard différent.
     *
     * @param entities
     */
    public void indexEntities(final Collection<T> entities) {
        final Client client = elasticsearchTemplate.getClient();
        final String index = entityInformation.getIndexName();
        final String type = entityInformation.getType();
        final BulkRequestBuilder bulkRequest = client.prepareBulk();

        // Suppression des entités existantes
        final List<T> existingEntities = findByIds(entities.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList()));
        for (final T entity : existingEntities) {
            bulkRequest.add(client.prepareDelete(index, type, entity.getIdentifier())
                                  .setParent(getParentId(entity))
                                  .setRouting(getRoutingId(entity)));
        }
        // Indexation des entités mises à jour
        for (final T entity : entities) {
            try {
                bulkRequest.add(client.prepareIndex(index, type, entity.getIdentifier())
                                      .setParent(getParentId(entity))
                                      .setRouting(getRoutingId(entity))
                                      .setSource(entityMapper.mapToString(entity)));

            } catch (IOException e) {
                throw new ElasticsearchException("Échec de l'indexation de l'entité [id: " + entity.getIdentifier() + "]", e);
            }
        }
        // Exécution de la requête
        executeBulk(index, bulkRequest);
    }

    /**
     * Suppression d'une entité enfant
     *
     * @param entity
     */
    public void deleteEntity(final T entity) {
        elasticsearchTemplate.getClient()
                             .prepareDelete(entityInformation.getIndexName(), entityInformation.getType(), entity.getIdentifier())
                             .setParent(getParentId(entity))
                             .setRouting(getRoutingId(entity))
                             // exécution
                             .execute()
                             .actionGet();
        // Refresh
        elasticsearchTemplate.refresh(entityInformation.getIndexName());
    }

    /**
     * Suppression d'une collectios d'entités enfant.
     *
     * @param entities
     */
    public void deleteEntities(final Collection<T> entities) {
        final Client client = elasticsearchTemplate.getClient();
        final String index = entityInformation.getIndexName();
        final String type = entityInformation.getType();
        final BulkRequestBuilder bulkRequest = client.prepareBulk();

        // Suppression des entités existantes
        final List<T> existingEntities = findByIds(entities.stream().map(AbstractDomainObject::getIdentifier).collect(Collectors.toList()));
        for (final T entity : existingEntities) {
            bulkRequest.add(client.prepareDelete(index, type, entity.getIdentifier())
                                  .setParent(getParentId(entity))
                                  .setRouting(getRoutingId(entity)));
        }
        // Exécution de la requête
        executeBulk(index, bulkRequest);
    }

    /**
     * Recherche d'entités à partir de leurs identifiants
     *
     * @param identifiers
     * @return
     */
    @Override
    public List<T> findByIds(final List<String> identifiers) {
        if (CollectionUtils.isEmpty(identifiers)) {
            return Collections.emptyList();
        }
        final SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.idsQuery(entityInformation.getType()).ids(identifiers))
                                                                      .withPageable(new PageRequest(0, identifiers.size()))
                                                                      .build();
        return elasticsearchTemplate.queryForList(searchQuery, clazz);
    }

    /**
     * Retourne l'id parent, ou l'id par défaut si il est non renseigné
     *
     * @param entity
     * @return
     */
    protected String getParentId(final T entity) {
        final String parentId = entityInformation.getParentId(entity);
        return StringUtils.isNotBlank(parentId) ? parentId : NULL_PARENT;
    }

    /**
     * Retourne l'id de routage de l'entité.
     * Dans le cas de relations hiérarchiques sur plus de 2 niveaux, le routing id fait référence à l'id de l'élément racine - et non au parent
     *
     * @param entity
     * @return
     */
    protected String getRoutingId(final T entity) {
        return getParentId(entity);
    }
}
