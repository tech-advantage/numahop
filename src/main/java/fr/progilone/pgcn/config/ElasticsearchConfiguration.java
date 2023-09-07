package fr.progilone.pgcn.config;

import java.util.stream.Stream;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = {"fr.progilone.pgcn.repository.es"})
@Import(ElasticsearchRestClientAutoConfiguration.class)
public class ElasticsearchConfiguration {

    private static final int STARTUP_INTERVAL = 1;
    private static final int STARTUP_TIMEOUT = 1800;

    private final ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchConfiguration(final ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    /**
     * Bean permettant d'attendre que Elasticsearch soit démarré.
     */
    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_TEST)
    public HttpServiceStartupValidator elasticsearchStartupValidator() {
        final var dsv = new HttpServiceStartupValidator(elasticsearchProperties.getUris().get(0));
        dsv.setInterval(STARTUP_INTERVAL);
        dsv.setTimeout(STARTUP_TIMEOUT);
        return dsv;
    }

    /**
     * On fait dépendre le client Elasticsearch de notre bean permettant d'attendre que Elasticsearch soit démarré pour que l'application attende
     * Elasticsearch avant de continuer à démarrer.
     */
    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_TEST)
    public static BeanFactoryPostProcessor dependsOnElasticsearchPostProcessor() {
        return bf -> {
            final String[] clientRegistrationRepository = bf.getBeanNamesForType(RestClientBuilder.class);
            Stream.of(clientRegistrationRepository).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("elasticsearchStartupValidator"));
        };
    }
}
