package com.metly.openfire.cache;

import java.util.Date;

import org.jivesoftware.util.JiveGlobals;

import com.meetup.memcached.MemcachedClient;
import com.meetup.memcached.SockIOPool;

public class MemcachedCache implements Cache {
    private static final MemcachedClient cache = new MemcachedClient();
    static {

        // server list and weights
        String[] servers = JiveGlobals.getProperties("metly.memcacheservers").toArray(new String[0]);

        Integer[] weights = { 3, 3, 2 };

        // grab an instance of our connection pool
        SockIOPool pool = SockIOPool.getInstance();

        // set the servers and the weights
        pool.setServers(servers);
        pool.setWeights(weights);

        // set some basic pool settings
        // 5 initial, 5 min, and 250 max conns
        // and set the max idle time for a conn
        // to 6 hours
        pool.setInitConn(5);
        pool.setMinConn(5);
        pool.setMaxConn(250);
        pool.setMaxIdle(1000 * 60 * 60 * 6);

        // set the sleep for the maint thread
        // it will wake up every x seconds and
        // maintain the pool size
        pool.setMaintSleep(30);

        // set some TCP settings
        // disable nagle
        // set the read timeout to 3 secs
        // and don't set a connect timeout
        pool.setNagle(false);
        pool.setSocketTO(3000);
        pool.setSocketConnectTO(0);

        // initialize the connection pool
        pool.initialize();

        // lets set some compression on for the client
        // compress anything larger than 64k
        cache.setCompressEnable(true);
        cache.setCompressThreshold(64 * 1024);
        // use a compatible hashing algorithm
        pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);

        // store primitives as strings
        // the java client serializes primitives
        //
        // note: this will not help you when it comes to
        // storing non primitives
        cache.setPrimitiveAsString(true);

        // don't url encode keys
        // by default the java client url encodes keys
        // to sanitize them so they will always work on the server
        // however, other clients do not do this
        cache.setSanitizeKeys(false);
    }

    @Override
    public void set(String key, Object value) {
        cache.set(key, value);
    }

    @Override
    public void set(String key, Object value, Date expiry) {
        cache.set(key, value, expiry);
    }

    @Override
    public long addOrDecr(String key) {
        return cache.addOrDecr(key);
    }

    @Override
    public long addOrDecr(String key, long decr) {
        return addOrDecr(key, decr);
    }

    @Override
    public long addOrIncr(String key) {
        return addOrIncr(key);
    }

    @Override
    public long addOrIncr(String key, long inc) {
        return addOrIncr(key, inc);
    }

    @Override
    public boolean delete(String key) {
        return cache.delete(key);
    }

    @Override
    public boolean delete(String key, Date expiry) {
        return cache.delete(key, expiry);
    }

    @Override
    public Object get(String key) {
        return cache.get(key);
    }

    @Override
    public boolean keyExists(String key) {
        return cache.keyExists(key);
    }

    @Override
    public boolean replace(String key, Object value) {
        return cache.replace(key, value);
    }

    @Override
    public boolean replace(String key, Object value, Date expiry) {
        return cache.replace(key, value, expiry);
    }

}
