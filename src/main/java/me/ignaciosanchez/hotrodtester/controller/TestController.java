package me.ignaciosanchez.hotrodtester.controller;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Random;

@RestController
public class TestController {

    @Autowired
    RemoteCacheManager rcm;

    Logger logger = LoggerFactory.getLogger(TestController.class);


    @GetMapping("/api/health")
    public String health() {

        if (rcm.isStarted())
            return "SpringCache Manager is started! " + rcm.getConfiguration().toString();
        else
            return "SpringCache Manager is not started";
    }


    @GetMapping("/api/reset")
    public String reset() {

        rcm.stop();
        rcm.start();

        return "SpringCache Manager restarted";
    }


    @GetMapping("/api/cache")
    public String caches() {

        return rcm.getCacheNames().toString();
    }


    @GetMapping("/api/cache/{cache}/stats")
    public String stats(
            @PathVariable(value = "cache") String cacheName) {

        return rcm.getCache(cacheName).serverStatistics().getStatsMap().toString();
    }

    @GetMapping("/api/cache/{cache}/create")
    public String create(
            @PathVariable(value = "cache") String cacheName) {
/*
        Configuration config = new ConfigurationBuilder()
                .clustering().cacheMode(CacheMode.DIST_ASYNC)
                .memory()
                    .size(20000)
                .expiration()
                    .wakeUpInterval(5000L)
                    .maxIdle(120000L)
                .build();

        //rcm.administration().getOrCreateCache(cacheName, config);
        rcm.administration().getOrCreateCache(cacheName, new XMLStringConfiguration(config.toXMLString()));
*/
        return "ok";
    }


    @GetMapping("/api/cache/{cache}/put")
    public String put(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "size", required=false) Integer entrySize,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey,
            @RequestParam(value = "keyrange", required=false) Integer entryKeyRange) {

        RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

        int min = 0;
        if (entryMinkey != null)
            min = entryMinkey;

        int size = 1024;
        if (entrySize != null)
            size = entrySize;

        int keyrange = numEntries;
        if (entryKeyRange != null)
            keyrange = entryKeyRange;

        byte[] bytes = new byte[size];
        Random rnd = new Random();

        int key = 0;

        for (int i=min; i<(min + numEntries) ; i++) {

            rnd.nextBytes(bytes);

            try {
                cache.put(Integer.toString(key + min), bytes);
                logger.info("put ok " + i);
            }
            catch (Exception e) {
                logger.error("Exception in put " + i, e);
            }

            key++;
            key%=keyrange;
        }

        return "OK " + numEntries + " " + size + " " + min;
    }

    @GetMapping("/api/cache/{cache}/get")
    public String get(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey) {

        RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

        int min = 0;
        if (entryMinkey != null)
            min = entryMinkey;

        for (int i=min; i<(min + numEntries) ; i++) {
            cache.get(Integer.toString(i));
        }

        return "OK " + numEntries + " " + min;
    }

    @GetMapping("/api/cache/{cache}/get-single")
    public String getSingle(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "key") int key) {

        RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

        return Arrays.toString(cache.get(Integer.toString(key)));
    }

    @GetMapping("/api/cache/{cache}/remove")
    public String remove(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey) {

        RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

        int min = 0;
        if (entryMinkey != null)
            min = entryMinkey;

        for (int i=min; i<(min + numEntries) ; i++) {
            cache.remove(Integer.toString(i));
        }

        return "OK " + numEntries + " " + entryMinkey;
    }

    // putcron, cron, n, minkey, maxkey
}
