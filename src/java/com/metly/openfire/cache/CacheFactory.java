package com.metly.openfire.cache;

public class CacheFactory {

    public static Cache getCache() {
        return new MemcachedCache();
    }

}
