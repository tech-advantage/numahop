package fr.progilone.pgcn.config;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;

import fr.progilone.pgcn.service.storage.BinaryStorageManager;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class BinaryStorageConfiguration implements EnvironmentAware {

    private static final String ENV_STORAGE = "storage.";
    private static final String PROP_BINARIES = "binaries";
    private static final String PROP_DEPTH = "depth";
    private static final String PROP_DIGEST = "digest";

    private static final Logger LOG = LoggerFactory.getLogger(BinaryStorageConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;
    
    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Inject
    private BinaryStorageManager bm;

	@Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_STORAGE);
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Binaries Storage Engine");
        final String binaries = propertyResolver.getProperty(PROP_BINARIES);
        final String digest = propertyResolver.getProperty(PROP_DIGEST);
        final int depth = propertyResolver.getProperty(PROP_DEPTH, Integer.class, 2);
        LOG.debug("binaries folder : {}", binaries);
        LOG.debug("choosen digest : {}", digest);
        LOG.debug("depth : {}", depth);
        bm.initialize(binaries, depth, digest, instanceLibraries);
    }
}
