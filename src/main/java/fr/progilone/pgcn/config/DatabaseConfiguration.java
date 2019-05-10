package fr.progilone.pgcn.config;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"fr.progilone.pgcn.repository"},
                       excludeFilters = @Filter(type = FilterType.ASPECTJ, pattern = "fr.progilone.pgcn.repository.es.*"))
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfiguration.class);
    private static final String CONNECTION_POOL_NAME = "HikariPool-PGCN";

    private Environment environment;

    private RelaxedPropertyResolver dataSourcePropertyResolver;

    private RelaxedPropertyResolver liquiBasePropertyResolver;

    @Autowired(required = false)
    private MetricRegistry metricRegistry;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
        this.dataSourcePropertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
        this.liquiBasePropertyResolver = new RelaxedPropertyResolver(environment, "liquiBase.");
    }

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        LOG.debug("Configuring Datasource");
        final HikariConfig config = new HikariConfig();
        config.setPoolName(CONNECTION_POOL_NAME);

        if (StringUtils.isNotEmpty(dataSourcePropertyResolver.getProperty("dataSourceClassName"))) {
            config.setDataSourceClassName(dataSourcePropertyResolver.getProperty("dataSourceClassName"));
        }
        if (StringUtils.isNotEmpty(dataSourcePropertyResolver.getProperty("driverClassName"))) {
            config.setDriverClassName(dataSourcePropertyResolver.getProperty("driverClassName"));
        }

        final String url = dataSourcePropertyResolver.getProperty("url");
        if (StringUtils.isEmpty(url)) {
            config.addDataSourceProperty("databaseName", dataSourcePropertyResolver.getProperty("databaseName"));
            config.addDataSourceProperty("serverName", dataSourcePropertyResolver.getProperty("serverName"));
        } else {
            config.setJdbcUrl(url);
            config.addDataSourceProperty("url", url);
        }

        config.setUsername(dataSourcePropertyResolver.getProperty("username"));
        config.setPassword(dataSourcePropertyResolver.getProperty("password"));

        config.setLeakDetectionThreshold(dataSourcePropertyResolver.getProperty("leakDetectionThreshold", Long.class, 0L));
        config.setMaximumPoolSize(dataSourcePropertyResolver.getProperty("maximumPoolSize", Integer.class));
        config.setIdleTimeout(dataSourcePropertyResolver.getProperty("idleTimeout", Long.class, 60000L));
        config.setMaxLifetime(dataSourcePropertyResolver.getProperty("maxLifetime", Long.class, 600000L));

        // MySQL optimizations, see https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        final boolean isMySql =
            StringUtils.equals("com.mysql.jdbc.jdbc2.optional.MysqlDataSource", dataSourcePropertyResolver.getProperty("dataSourceClassName"))
            || StringUtils.equals("org.mariadb.jdbc.Driver", dataSourcePropertyResolver.getProperty("driverClassName"));
        if (isMySql) {
            config.addDataSourceProperty("cachePrepStmts", dataSourcePropertyResolver.getProperty("cachePrepStmts", "true"));
            config.addDataSourceProperty("prepStmtCacheSize", dataSourcePropertyResolver.getProperty("prepStmtCacheSize", "250"));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", dataSourcePropertyResolver.getProperty("prepStmtCacheSqlLimit", "2048"));
            config.addDataSourceProperty("useServerPrepStmts", dataSourcePropertyResolver.getProperty("useServerPrepStmts", "true"));

            // UTF-8 forcé
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.addDataSourceProperty("useUnicode", "true");
        }
        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }
        return new HikariDataSource(config);
    }

    @Bean
    public SpringLiquibase liquibase(final DataSource dataSource) {
        final SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
        liquibase.setContexts(liquiBasePropertyResolver.getProperty("context"));
        if (environment.acceptsProfiles(Constants.SPRING_PROFILE_FAST)) {
            if ("org.h2.jdbcx.JdbcDataSource".equals(dataSourcePropertyResolver.getProperty("dataSourceClassName"))) {
                liquibase.setShouldRun(true);
                LOG.warn(
                    "Using '{}' profile with H2 database in memory is not optimal, you should consider switching to MySQL or Postgresql to avoid rebuilding your database upon each start.",
                    Constants.SPRING_PROFILE_FAST);
            } else {
                liquibase.setShouldRun(false);
            }
        } else {
            LOG.debug("Configuring Liquibase");
        }
        return liquibase;
    }

    @Bean
    public Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

}
