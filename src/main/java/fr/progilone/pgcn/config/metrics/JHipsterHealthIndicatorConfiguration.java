package fr.progilone.pgcn.config.metrics;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JHipsterHealthIndicatorConfiguration {

    @Inject
    private DataSource dataSource;

    @Bean
    public HealthIndicator dbHealthIndicator() {
        return new DatabaseHealthIndicator(dataSource);
    }
}
