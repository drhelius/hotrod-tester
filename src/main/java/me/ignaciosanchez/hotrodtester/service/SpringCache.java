package me.ignaciosanchez.hotrodtester.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class SpringCache {

    @Cacheable(value = "spring", key = "#key")
    public String getKey(String key) {

        return "the real value";
    }
}
