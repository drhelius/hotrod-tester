package me.ignaciosanchez.hotrodtester.config;

import org.infinispan.client.hotrod.ProtocolVersion;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.configuration.NearCacheMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteCacheConfig {

    @Value("${datagrid.host}")
    private String host;

    @Value("${datagrid.port}")
    private String port;

    @Value("${hotrod.protocol}")
    private String protocol;

    @Value("${datagrid.near-cache}")
    private boolean nearCache;

    @Value("${datagrid.near-cache-size}")
    private int nearCacheSize;

    @Bean
    public RemoteCacheManager remoteCacheManager() {

        ConfigurationBuilder config = new ConfigurationBuilder();
        config.statistics().enable().jmxEnable()
                .version(ProtocolVersion.parseVersion(protocol))
                .addServer().host(host).port(Integer.parseInt(port));

        if (nearCache) {
            config.nearCache().mode(NearCacheMode.INVALIDATED).maxEntries(nearCacheSize);
        }

        return new RemoteCacheManager(config.build());
    }
}
