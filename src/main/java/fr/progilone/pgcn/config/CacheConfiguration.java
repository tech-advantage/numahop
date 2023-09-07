package fr.progilone.pgcn.config;

import java.time.Duration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("!" + Constants.SPRING_PROFILE_TEST)
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(final Environment environment) {
        this.jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class,
                                                                                                                                           Object.class,
                                                                                                                                           ResourcePoolsBuilder.heap(environment.getProperty("cache.ehcache.max-entries",
                                                                                                                                                                                             Long.class,
                                                                                                                                                                                             1000L)))
                                                                                                             .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(environment.getProperty("cache.ehcache.time-to-live-seconds",
                                                                                                                                                                                                             Long.class,
                                                                                                                                                                                                             3600L))))
                                                                                                             .build());

    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(final javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, fr.progilone.pgcn.domain.user.Authorization.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.user.User.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.security.PersistentToken.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.user.User.class.getName() + ".persistentTokens");
            createCache(cm, fr.progilone.pgcn.domain.library.Library.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.user.Address.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.document.DocPropertyType.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.ocrlangconfiguration.OcrLanguage.class.getName());
            createCache(cm, fr.progilone.pgcn.domain.check.AutomaticCheckType.class.getName());
        };
    }

    private void createCache(final javax.cache.CacheManager cm, final String cacheName) {
        final javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
