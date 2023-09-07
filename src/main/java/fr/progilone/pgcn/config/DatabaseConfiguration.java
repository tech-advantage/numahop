package fr.progilone.pgcn.config;

import java.util.stream.Stream;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {"fr.progilone.pgcn.repository"}, excludeFilters = @Filter(type = FilterType.ASPECTJ, pattern = "fr.progilone.pgcn.repository.es.*"))
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {

    private static final int STARTUP_INTERVAL = 1;
    private static final int STARTUP_TIMEOUT = 1800;

    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_TEST)
    public DatabaseStartupValidator databaseStartupValidator(final DataSource dataSource) {
        final var dsv = new DatabaseStartupValidator();
        dsv.setDataSource(dataSource);
        dsv.setInterval(STARTUP_INTERVAL);
        dsv.setTimeout(STARTUP_TIMEOUT);
        return dsv;
    }

    @Bean
    @Profile("!" + Constants.SPRING_PROFILE_TEST)
    public static BeanFactoryPostProcessor dependsOnPostProcessor() {
        return bf -> {
            final String[] liquibase = bf.getBeanNamesForType(SpringLiquibase.class);
            Stream.of(liquibase).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
        };
    }
}
