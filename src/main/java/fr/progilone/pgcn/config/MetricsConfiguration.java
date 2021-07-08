package fr.progilone.pgcn.config;

import java.lang.management.ManagementFactory;

import javax.annotation.PostConstruct;
import javax.cache.CacheManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableMetrics(proxyTargetClass = true)
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class MetricsConfiguration extends MetricsConfigurerAdapter implements EnvironmentAware {

    private static final String ENV_METRICS = "metrics.";
    private static final String PROP_JMX_ENABLED = "jmx.enabled";
    private static final String PROP_METRIC_REG_JVM_MEMORY = "jvm.memory";
    private static final String PROP_METRIC_REG_JVM_GARBAGE = "jvm.garbage";
    private static final String PROP_METRIC_REG_JVM_THREADS = "jvm.threads";
    private static final String PROP_METRIC_REG_JVM_FILES = "jvm.files";
    private static final String PROP_METRIC_REG_JVM_BUFFERS = "jvm.buffers";

    private static final Logger LOG = LoggerFactory.getLogger(MetricsConfiguration.class);

    private final MetricRegistry metricRegistry = new MetricRegistry();

    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    private RelaxedPropertyResolver propertyResolver;
    
    private HikariDataSource hikariDataSource;
    
    public MetricsConfiguration(final CacheManager cacheManager) {
        // On injecte juste le cacheManager ici pour qu'il soit créé avant et que les métrics de cache fonctionnent...
    }

    @Autowired(required = false)
    public void setHikariDataSource(final HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    @Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_METRICS);
    }

    @Override
    @Bean
    public MetricRegistry getMetricRegistry() {
        return metricRegistry;
    }

    @Override
    @Bean
    public HealthCheckRegistry getHealthCheckRegistry() {
        return healthCheckRegistry;
    }

    @PostConstruct
    public void init() {
        LOG.debug("Registering JVM gauges");
        metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_FILES, new FileDescriptorRatioGauge());
        metricRegistry.register(PROP_METRIC_REG_JVM_BUFFERS, new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
        if (propertyResolver.getProperty(PROP_JMX_ENABLED, Boolean.class, false)) {
            LOG.info("Initializing Metrics JMX reporting");
            final JmxReporter jmxReporter = JmxReporter.forRegistry(metricRegistry).build();
            jmxReporter.start();
        }
        if (hikariDataSource != null) {
            LOG.debug("Monitoring the datasource");
            // remove the factory created by HikariDataSourceMetricsPostProcessor until JHipster migrate to Micrometer
            hikariDataSource.setMetricsTrackerFactory(null);
            hikariDataSource.setMetricRegistry(metricRegistry);
        }
    }

}
