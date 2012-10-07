package com.friendmaybe.openfire.cache;

public class CacheFactory {

    public static Cache getCache() {
        return new MemcachedCache();
    }

}
