package fr.progilone.pgcn.config;

import com.codahale.metrics.MetricRegistry;
import fr.progilone.pgcn.web.filter.CachingHttpHeadersFilter;
import fr.progilone.pgcn.web.filter.StaticResourcesProductionFilter;
import io.dropwizard.metrics.servlet.InstrumentedFilter;
import io.dropwizard.metrics.servlets.MetricsServlet;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import java.util.Arrays;
import java.util.EnumSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfigurer implements WebMvcConfigurer, ServletContextInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;
    private final MetricRegistry metricRegistry;

    public WebConfigurer(final Environment env, @Autowired(required = false) final MetricRegistry metricRegistry) {
        this.env = env;
        this.metricRegistry = metricRegistry;
    }

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {
        LOG.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
        final EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
        if (metricRegistry != null) {
            initMetrics(servletContext, disps);
        }
        if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_PRODUCTION))) {
            initCachingHttpHeadersFilter(servletContext, disps);
            initStaticResourcesProductionFilter(servletContext, disps);
        }
        LOG.info("Web application fully configured");
    }

    /**
     * Initializes the static resources production Filter.
     */
    private void initStaticResourcesProductionFilter(final ServletContext servletContext, final EnumSet<DispatcherType> disps) {

        LOG.debug("Registering static resources production Filter");
        final FilterRegistration.Dynamic staticResourcesProductionFilter = servletContext.addFilter("staticResourcesProductionFilter", new StaticResourcesProductionFilter());

        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/index.html");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/libs/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/i18n/*");
        staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        staticResourcesProductionFilter.setAsyncSupported(true);
    }

    /**
     * Initializes the cachig HTTP Headers Filter.
     */
    private void initCachingHttpHeadersFilter(final ServletContext servletContext, final EnumSet<DispatcherType> disps) {
        LOG.debug("Registering Caching HTTP Headers Filter");
        final FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter(env));

        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/assets/*");
        cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/scripts/*");
        cachingHttpHeadersFilter.setAsyncSupported(true);
    }

    /**
     * Initializes Metrics.
     */
    private void initMetrics(final ServletContext servletContext, final EnumSet<DispatcherType> disps) {
        LOG.debug("Initializing Metrics registries");
        servletContext.setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, metricRegistry);
        servletContext.setAttribute(MetricsServlet.METRICS_REGISTRY, metricRegistry);

        LOG.debug("Registering Metrics Filter");
        final FilterRegistration.Dynamic metricsFilter = servletContext.addFilter("webappMetricsFilter", new InstrumentedFilter());

        metricsFilter.addMappingForUrlPatterns(disps, true, "/*");
        metricsFilter.setAsyncSupported(true);

        LOG.debug("Registering Metrics Servlet");
        final ServletRegistration.Dynamic metricsAdminServlet = servletContext.addServlet("metricsServlet", new MetricsServlet());

        metricsAdminServlet.addMapping("/actuator/jhi-metrics/*");
        metricsAdminServlet.addMapping("/api_int/jhi-metrics/*");
        metricsAdminServlet.setAsyncSupported(true);
        metricsAdminServlet.setLoadOnStartup(2);
    }

}
