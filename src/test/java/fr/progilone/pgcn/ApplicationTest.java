package fr.progilone.pgcn;

import java.util.stream.Stream;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@EnableAutoConfiguration()
public class ApplicationTest implements EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource datasource, final JpaVendorAdapter jpaVendorAdapter) {
        final LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(datasource);
        bean.setJpaVendorAdapter(jpaVendorAdapter);
        bean.setPackagesToScan("fr.progilone.pgcn.domain");

        Stream.of("spring.jpa.properties.hibernate.cache.use_second_level_cache",
                  "spring.jpa.properties.hibernate.cache.use_query_cache",
                  "spring.jpa.properties.hibernate.generate_statistics",
                  "spring.jpa.properties.hibernate.cache.region.factory_class",
                  "spring.jpa.properties.hibernate.cache.use_minimal_puts",
                  "spring.jpa.properties.hibernate.cache.hazelcast.use_lite_member",
                  "spring.jpa.properties.hibernate.default_batch_fetch_size")
              .filter(property -> this.environment.containsProperty(property))
              .forEach(property -> bean.getJpaPropertyMap().put(property, this.environment.getProperty(property)));
        return bean;
    }
}
