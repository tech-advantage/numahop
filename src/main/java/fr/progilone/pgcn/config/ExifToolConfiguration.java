package fr.progilone.pgcn.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;

import fr.progilone.pgcn.service.storage.ExifToolService;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class ExifToolConfiguration implements EnvironmentAware {

    private static final String ENV_EXIF = "exifTool.";
    private static final String PROP_PROCESS = "process";

    private static final Logger LOG = LoggerFactory.getLogger(ExifToolConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Inject
    private ExifToolService exifToolService;

    @Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_EXIF);
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Exif Tool");
        final String exifProcessPath = propertyResolver.getProperty(PROP_PROCESS);
        exifToolService.initialize(exifProcessPath);
    }
}
