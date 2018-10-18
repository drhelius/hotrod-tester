package me.ignaciosanchez.hotrodtester.service;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class Tester {

    @Autowired
    RemoteCacheManager rcm;


    @GetMapping("/api/health")
    public String health() {

        if (rcm.isStarted())
            return "Cache Manager is started! " + rcm.getConfiguration().toString();
        else
            return "Cache Manager is not started";
    }


    @GetMapping("/api/reset")
    public String reset() {

        rcm.stop();
        rcm.start();

        return "Cache Manager restarted";
    }


    @GetMapping("/api/cache")
    public String caches() {

        return rcm.getCacheNames().toString();
    }


    @GetMapping("/api/cache/{cache}/stats")
    public String stats(
            @PathVariable(value = "cache") String cacheName) {

        return rcm.getCache(cacheName).stats().getStatsMap().toString();
    }


    @GetMapping("/api/cache/{cache}/put")
    public String put(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "size", required=false) Integer entrySize,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey) {

        RemoteCache<String, byte[]> cache = rcm.getCache(cacheName);

        int min = 0;
        if (entryMinkey != null)
            min = entryMinkey;

        int size = 1024;
        if (entrySize != null)
            size = entrySize;

        byte[] bytes = new byte[size];
        Random rnd = new Random();

        for (int i=min; i<(min + numEntries) ; i++) {

            rnd.nextBytes(bytes);

            cache.put(Integer.toString(i), bytes);
        }

        return "OK " + numEntries + " " + entrySize + " " + entryMinkey;
    }

    @GetMapping("/api/cache/{cache}/get")
    public String get(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey) {

        RemoteCache<String, String> cache = rcm.getCache(cacheName);

        int min = 0;
        if (entryMinkey != null)
            min = entryMinkey;

        for (int i=min; i<(min + numEntries) ; i++) {
            cache.get(Integer.toString(i));
        }

        return "OK " + numEntries + " " + entryMinkey;
    }

    @GetMapping("/api/cache/{cache}/get-single")
    public String getSingle(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "key") int key) {

        RemoteCache<String, String> cache = rcm.getCache(cacheName);

        return cache.get(Integer.toString(key)).toString();
    }

    @GetMapping("/api/cache/{cache}/remove")
    public String remove(
            @PathVariable(value = "cache") String cacheName,
            @RequestParam(value = "entries") int numEntries,
            @RequestParam(value = "minkey", required=false) Integer entryMinkey) {

        RemoteCache<String, String> cache = rcm.getCache(cacheName);

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
