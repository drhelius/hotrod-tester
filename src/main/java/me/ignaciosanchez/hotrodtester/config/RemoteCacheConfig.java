package me.ignaciosanchez.hotrodtester.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RemoteCacheConfig {

    @Bean
    public RemoteCacheManager remoteCacheManager() {
        return new RemoteCacheManager();
    }
}
