package fr.progilone.pgcn.config;

import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import fr.progilone.pgcn.service.storage.ImageMagickService;
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
public class ImageMagickConfiguration implements EnvironmentAware {

    private static final Logger LOG = LoggerFactory.getLogger(ImageMagickConfiguration.class);

    private Environment environment;

    @Autowired
    private ImageMagickService imageMagickService;

    @Override
    public void setEnvironment(final Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Image Magick");
        final String IMConvertPath = environment.getProperty("imageMagick.convert");
        final String IMIdentifyPath = environment.getProperty("imageMagick.identify");
        LOG.debug("Image Magick convert path : {}", IMConvertPath);
        LOG.debug("Image Magick identify path : {}", IMIdentifyPath);
        imageMagickService.initialize(IMConvertPath, IMIdentifyPath);
    }
}
