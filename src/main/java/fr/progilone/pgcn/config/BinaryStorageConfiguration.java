package fr.progilone.pgcn.config;

import fr.progilone.pgcn.service.storage.BinaryStorageManager;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class BinaryStorageConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(BinaryStorageConfiguration.class);

    private Environment environment;

    @Value("${instance.libraries}")
    private String[] instanceLibraries;

    @Autowired
    private BinaryStorageManager bm;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Binaries Storage Engine");
        final String binaries = environment.getProperty("storage.binaries");
        final String digest = environment.getProperty("storage.digest");
        final int depth = environment.getProperty("storage.depth", Integer.class, 2);
        LOG.debug("binaries folder : {}", binaries);
        LOG.debug("choosen digest : {}", digest);
        LOG.debug("depth : {}", depth);
        bm.initialize(binaries, depth, digest, instanceLibraries);
    }
}
