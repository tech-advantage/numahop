package fr.progilone.pgcn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

@Configuration
@EnableJpaRepositories(basePackages = {"fr.progilone.pgcn.repository"},
                       excludeFilters = @Filter(type = FilterType.ASPECTJ, pattern = "fr.progilone.pgcn.repository.es.*"))
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {


    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

}
