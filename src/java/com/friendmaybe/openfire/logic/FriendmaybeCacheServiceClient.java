package com.friendmaybe.openfire.logic;

import org.apache.log4j.Logger;
import org.xmpp.packet.JID;

import com.friendmaybe.openfire.cache.Cache;
import com.friendmaybe.openfire.cache.CacheFactory;
import com.friendmaybe.openfire.cache.HashMapCache;

public class FriendmaybeCacheServiceClient implements FriendmaybeServiceClient {
    private static final Logger log = Logger.getLogger(FriendmaybeCacheServiceClient.class);

    private FriendmaybeServiceClient client;

    private static final Cache cache = CacheFactory.getCache();

    public FriendmaybeCacheServiceClient(FriendmaybeServiceClient client) {
        this.client = client;
    }

    @Override
    public FriendmaybeUser getMatchedStranger(String userJID) {
        FriendmaybeUser user = null;
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
                    cache.set(userJID, FriendmaybeUser.getJSONString(user));
                } catch (Exception e) {
                    log.error("Error on cache SET key:" + userJID, e);
                }
            }
        } else {
            user = FriendmaybeUser.getUserFromJSON((String) get);
        }
        return user;
    }

    @Override
    public FriendmaybeUser getNewStranger(String userJID) {
        FriendmaybeUser friendmaybeUser = this.client.getNewStranger(userJID);
        if (friendmaybeUser != null) {
            try {
                cache.set(userJID, FriendmaybeUser.getJSONString(friendmaybeUser));
            } catch (Exception e) {
                log.error("Error on cache SET key:" + userJID, e);
            }
        }
        return friendmaybeUser;

    }

    @Override
    public void clearMapping(String userJID) {
        try {
            Object get = cache.get(userJID);
            if (get != null && !get.equals("null")) {
                cache.delete(FriendmaybeUser.getUserFromJSON((String) get).getJID());
                log.info("log cleared:" + FriendmaybeUser.getUserFromJSON((String) get).getJID());
            }
            cache.delete(userJID);
            log.info("log cleared:" + userJID);
        } catch (Exception e) {
            log.error("Error on cache DELETE key:" + userJID, e);
        }
        this.client.clearMapping(userJID);
    }
}
