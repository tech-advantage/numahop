package fr.progilone.pgcn.config.jcache;

import java.util.Properties;

import javax.cache.Cache;

import org.hibernate.cache.jcache.JCacheRegionFactory;
import org.hibernate.cache.spi.CacheDataDescription;

public class NoDefaultJCacheRegionFactory extends JCacheRegionFactory {

    @Override
    protected Cache<Object, Object> createCache(final String regionName, final Properties properties, final CacheDataDescription metadata) {
//        throw new IllegalStateException("All Hibernate caches should be created upfront. " + "Please update CacheConfiguration.java to add "
//                                        + regionName);
        return null;
    }
    
}
