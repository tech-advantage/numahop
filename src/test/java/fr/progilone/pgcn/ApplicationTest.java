package fr.progilone.pgcn;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableAutoConfiguration(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
public class ApplicationTest implements EnvironmentAware {

    private RelaxedPropertyResolver propertyResolver;

    @Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, "spring.jpa.properties.");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource datasource, final JpaVendorAdapter jpaVendorAdapter) {
        final LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(datasource);
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan("fr.progilone.pgcn.domain");

        Stream.of("hibernate.cache.use_second_level_cache",
                  "hibernate.cache.use_query_cache",
                  "hibernate.generate_statistics",
                  "hibernate.cache.region.factory_class",
                  "hibernate.cache.use_minimal_puts",
                  "hibernate.cache.hazelcast.use_lite_member",
                  "hibernate.default_batch_fetch_size")
              .filter(property -> this.propertyResolver.containsProperty(property))
              .forEach(property -> bean.getJpaPropertyMap().put(property, this.propertyResolver.getProperty(property)));
        return bean;
    }

    @Bean
    public CacheManager cacheManager() {
        return new GuavaCacheManager();
    }
}
