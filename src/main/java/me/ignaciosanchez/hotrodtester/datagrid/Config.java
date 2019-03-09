package me.ignaciosanchez.hotrodtester.datagrid;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.NearCacheMode;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableCaching
@Configuration
public class Config {

    @Value("${datagrid.host}")
    private String host;

    @Value("${datagrid.port}")
    private String port;

    @Value("${hotrod.version}")
    private String version;

    @Value("${datagrid.near-cache}")
    private boolean nearCache;

    @Value("${datagrid.near-cache-size}")
    private int nearCacheSize;

    @Bean
    @Primary
    public CacheManager cacheManager() {
        return new SpringRemoteCacheManager(infinispanCacheManager());
    }

    @Bean
    public RemoteCacheManager infinispanCacheManager() {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.version(ProtocolVersion.parseVersion(version))
                .addServer()
                .host(host)
                .port(Integer.parseInt(port));

        if (nearCache) {
            builder.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(nearCacheSize);
        }

        return new RemoteCacheManager(builder.build());
    }
}
