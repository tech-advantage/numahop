package fr.progilone.pgcn.config;

import fr.progilone.pgcn.service.es.jackson.PgcnEntityMapper;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"fr.progilone.pgcn.repository.es"})
public class ElasticsearchConfiguration {

    @Value("${elasticsearch.index.name}")
    private String elasticsearchIndexName;

    @Bean
    public String elasticsearchIndexName() {
        return elasticsearchIndexName;
    }

    @Bean
    public EntityMapper entityMapper() {
        return new PgcnEntityMapper();
    }

    /**
     * @param client
     * @param entityMapper
     * @return
     * @see <a href="https://github.com/spring-projects/spring-data-elasticsearch/wiki/Custom-ObjectMapper">Custom ObjectMapper</a>
     */
    @Bean
    @ConditionalOnBean({Client.class, EntityMapper.class})
    public ElasticsearchTemplate elasticsearchTemplate(Client client, EntityMapper entityMapper) {
        return new ElasticsearchTemplate(client, entityMapper);
    }
}
