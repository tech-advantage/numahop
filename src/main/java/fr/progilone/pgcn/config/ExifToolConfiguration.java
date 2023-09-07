package fr.progilone.pgcn.config;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import fr.progilone.pgcn.service.storage.ExifToolService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class ExifToolConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ExifToolConfiguration.class);

    private Environment environment;

    @Autowired
    private ExifToolService exifToolService;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Exif Tool");
        final String exifProcessPath = environment.getProperty("exifTool.process");
        exifToolService.initialize(exifProcessPath);
    }
}
