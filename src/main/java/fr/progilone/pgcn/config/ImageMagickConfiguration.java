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

import fr.progilone.pgcn.service.storage.ImageMagickService;

@Configuration
@EnableMetrics(proxyTargetClass = true)
public class ImageMagickConfiguration implements EnvironmentAware {

    private static final String ENV_IM = "imageMagick.";
    private static final String PROP_CONVERT = "convert";
    private static final String PROP_IDENTIFY = "identify";

    private static final Logger LOG = LoggerFactory.getLogger(ImageMagickConfiguration.class);

    private RelaxedPropertyResolver propertyResolver;

    @Inject
    private ImageMagickService imageMagickService;


	@Override
    public void setEnvironment(final Environment environment) {
        this.propertyResolver = new RelaxedPropertyResolver(environment, ENV_IM);
    }

    @PostConstruct
    public void init() throws IOException {
        LOG.debug("Configuring Image Magick");
        final String IMConvertPath = propertyResolver.getProperty(PROP_CONVERT);
        final String IMIdentifyPath = propertyResolver.getProperty(PROP_IDENTIFY);
        LOG.debug("Image Magick convert path : {}", IMConvertPath);
        LOG.debug("Image Magick identify path : {}", IMIdentifyPath);
        imageMagickService.initialize(IMConvertPath, IMIdentifyPath);
    }
}
