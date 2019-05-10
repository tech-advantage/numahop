package fr.progilone.pgcn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

import fr.progilone.pgcn.config.Constants;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationWebXml.class);

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.profiles(addDefaultProfile()).bannerMode(Mode.OFF).sources(Application.class);
    }

    /**
     * Set a default profile if it has not been set.
     * <p/>
     * <p>
     * Please use -Dspring.profiles.active=dev
     * </p>
     */
    private String addDefaultProfile() {
        final String profile = System.getProperty("spring.profiles.active");
        if (profile != null) {
            LOG.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        LOG.warn("No Spring profile configured, running with default configuration");
        return Constants.SPRING_PROFILE_DEVELOPMENT;
    }
}
