package com.metly.openfire.cache;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HashMapCache implements Cache{
    static class CacheValue{
        public Object object;
        public Date expiry;
        public CacheValue(Object obj){
            object = obj;
            expiry = new Date(new Date().getTime() + 100000000);
        }
        public CacheValue(Object obj, Date exp){
            object = obj;
            expiry = exp;
        }
    }
    private static final Map<String, CacheValue> cache = new ConcurrentHashMap< String, CacheValue >(100);
    
    @Override
    public void set(String key, Object value) {
        cache.put(key, new CacheValue(value));
    }

    @Override
    public void set(String key, Object value, Date expiry) {
        cache.put(key, new CacheValue(value, expiry));
    }

    @Override
    public long addOrDecr(String key) {
        long ret = 0;
        synchronized (cache) {
            CacheValue value = cache.get(key);
            if(value == null){
                value = new CacheValue(0);
                cache.put(key, value);
            }
            value.object = ret = new Integer(value.object.toString()) - 1;
        }
        return ret;
       
    }

    @Override
    public long addOrDecr(String key, long decr) {
        long ret = 0;
        synchronized (cache) {
            CacheValue value = cache.get(key);
            if(value == null){
                value = new CacheValue(0);
                cache.put(key, value);
            }
            value.object = ret = new Integer(value.object.toString()) - decr;
        }
        return ret;
    }

    @Override
    public long addOrIncr(String key) {
        long ret = 0;
        synchronized (cache) {
            CacheValue value = cache.get(key);
            if(value == null){
                value = new CacheValue(0);
                cache.put(key, value);
            }
            value.object = ret = new Integer(value.object.toString()) + 1;
        }
        return ret;
    }

    @Override
    public long addOrIncr(String key, long inc) {
        long ret = 0;
        synchronized (cache) {
            CacheValue value = cache.get(key);
            if(value == null){
                value = new CacheValue(0);
                cache.put(key, value);
            }
            value.object = ret = new Integer(value.object.toString()) + inc;
        }
        return ret;
    }

    @Override
    public boolean delete(String key) {
        CacheValue value = cache.remove(key);
        return value != null && value.expiry.compareTo(new Date()) >= 0;
    }

    @Override
    public boolean delete(String key, Date expiry) {
        CacheValue value = cache.get(key);
        if (value == null || value.expiry.compareTo(expiry) < 0){
            cache.remove(key);
            return false;
        }
        value.expiry = expiry;
        return true;
    }

    @Override
    public Object get(String key) {
        CacheValue value = cache.get(key);
        if(value == null){
            return null;
        }
        if (value.expiry.compareTo(new Date()) < 0){
            return null;
        }
        return value.object;
    }


    @Override
    public boolean keyExists(String key) {
        CacheValue value = cache.get(key);
        return value != null && value.expiry.compareTo(new Date()) >= 0;
    }

    @Override
    public boolean replace(String key, Object value) {
        cache.put(key, new CacheValue(value));
        return true;
    }

    @Override
    public boolean replace(String key, Object value, Date expiry) {
        cache.put(key, new CacheValue(value, expiry));
        return true;
    }

}
