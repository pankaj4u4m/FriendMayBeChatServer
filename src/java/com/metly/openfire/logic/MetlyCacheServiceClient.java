package com.metly.openfire.logic;

import org.apache.log4j.Logger;
import org.xmpp.packet.JID;

import com.metly.openfire.cache.Cache;
import com.metly.openfire.cache.CacheFactory;
import com.metly.openfire.cache.HashMapCache;

public class MetlyCacheServiceClient implements MetlyServiceClient {
    private static final Logger log = Logger.getLogger(MetlyCacheServiceClient.class);

    private MetlyServiceClient client;

    private static final Cache cache = CacheFactory.getCache();

    public MetlyCacheServiceClient(MetlyServiceClient client) {
        this.client = client;
    }

    @Override
    public MetlyUser getMatchedStranger(String userJID) {
        MetlyUser user = null;
        Object get = null;
        try {
            get = cache.get(userJID);
        } catch (Exception e) {
            log.error("Error on cache GET key:" + userJID);
        }
        if (get == null || get.equals("null")) {
            user = client.getMatchedStranger(userJID);
            if (user != null) {
                try {
                    cache.set(userJID, MetlyUser.getJSONString(user));
                } catch (Exception e) {
                    log.error("Error on cache SET key:" + userJID, e);
                }
            }
        } else {
            user = MetlyUser.getUserFromJSON((String) get);
        }
        return user;
    }

    @Override
    public MetlyUser getNewStranger(String userJID) {
        MetlyUser metlyUser = this.client.getNewStranger(userJID);
        if (metlyUser != null) {
            try {
                cache.set(userJID, MetlyUser.getJSONString(metlyUser));
            } catch (Exception e) {
                log.error("Error on cache SET key:" + userJID, e);
            }
        }
        return metlyUser;

    }

    @Override
    public void clearMapping(String userJID) {
        try {
            Object get = cache.get(userJID);
            if (get != null && !get.equals("null")) {
                cache.delete(MetlyUser.getUserFromJSON((String) get).getJID());
                log.info("log cleared:" + MetlyUser.getUserFromJSON((String) get).getJID());
            }
            cache.delete(userJID);
            log.info("log cleared:" + userJID);
        } catch (Exception e) {
            log.error("Error on cache DELETE key:" + userJID, e);
        }
        this.client.clearMapping(userJID);
    }
}
