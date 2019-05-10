package fr.progilone.pgcn;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import fr.progilone.pgcn.config.Constants;


@SpringBootApplication(exclude = {MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class})
@ServletComponentScan
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);


    @Inject
    private Environment env;

    @Value("${nativeLibraries.path}")
    private String nativeLibrariesPath;
    
    @Value("${storage.binaries}")
    private String binariesStoragePath;

    /**
     * Initializes numahop.
     * <p/>
     * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
     * <p/>
     */
    @PostConstruct
    public void initApplication() throws IOException {
        if (env.getActiveProfiles().length == 0) {
            LOG.warn("No Spring profile configured, running with default configuration");
        } else {
            LOG.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
        }
        System.setProperty("java.library.path", nativeLibrariesPath);
        
        LOG.debug("java.library.path = {}", System.getProperty("java.library.path"));

        try {
            final Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);

        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * Main method, used to run the application.
     */
    public static void main(final String[] args) {
        final SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Mode.OFF);

        final SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        addDefaultProfile(app, source);

        app.run(args);
    }

    /**
     * Set a default profile if it has not been set
     */
    private static void addDefaultProfile(final SpringApplication app, final SimpleCommandLinePropertySource source) {
        if (!source.containsProperty("spring.profiles.active")) {
            app.setAdditionalProfiles(Constants.SPRING_PROFILE_DEVELOPMENT);
        }
    }
}
