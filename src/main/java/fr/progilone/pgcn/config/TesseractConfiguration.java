package fr.progilone.pgcn.config;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import fr.progilone.pgcn.service.storage.TesseractService;
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
public class TesseractConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(TesseractConfiguration.class);

    private Environment environment;

    @Autowired
    private TesseractService tesseractService;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Tesseract");
        final String tessProcessPath = environment.getProperty("tesseract.process");
        tesseractService.initialize(tessProcessPath);
    }
}
