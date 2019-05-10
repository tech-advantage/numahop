package fr.progilone.pgcn.config;

import java.util.concurrent.TimeUnit;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {MetricsConfiguration.class, DatabaseConfiguration.class})
@Profile("!" + Constants.SPRING_PROFILE_FAST)
public class CacheConfiguration {

    private javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(final Environment environment) {
        this.jcacheConfiguration =
            Eh107Configuration.fromEhcacheCacheConfiguration(CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class,
                                                                                                                    Object.class,
                                                                                                                    ResourcePoolsBuilder.heap(
                                                                                                                        environment.getProperty(
                                                                                                                            "ehcache.max-entries",
                                                                                                                            Long.class,
                                                                                                                            1000L)))
                                                                                      .withExpiry(Expirations.timeToLiveExpiration(Duration.of(
                                                                                          environment.getProperty("ehcache.time-to-live-seconds",
                                                                                                                  Long.class,
                                                                                                                  3600L),
                                                                                          TimeUnit.SECONDS)))
                                                                                      .build());

    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache(fr.progilone.pgcn.domain.user.Authorization.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.user.User.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.security.PersistentToken.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.user.User.class.getName() + ".persistentTokens", jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.library.Library.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.user.Address.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.document.DocPropertyType.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.check.AutomaticCheckType.class.getName(), jcacheConfiguration);
            cm.createCache(fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage.class.getName(), jcacheConfiguration);
        };
    }
}
