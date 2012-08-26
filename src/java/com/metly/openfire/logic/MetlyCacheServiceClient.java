package com.metly.openfire.logic;

import org.apache.log4j.Logger;
import com.metly.openfire.cache.Cache;
import com.metly.openfire.cache.HashMapCache;

public class MetlyCacheServiceClient implements MetlyServiceClient{
    private static final Logger log = Logger.getLogger(MetlyCacheServiceClient.class);
    
    private MetlyServiceClient client;
    
    private static final Cache cache = new HashMapCache();
    

    
    public MetlyCacheServiceClient(MetlyServiceClient client){
        this.client = client; 
    }

    @Override
    public MetlyUser getMatchedStranger(String userJID, String systemJID) {
    	MetlyUser user = null;
        Object get = null;
        try {
            get = cache.get(userJID + systemJID);
        } catch (Exception e) {
            log.error("Error on cache GET key:" + userJID + systemJID);
        }
        if (get == null){
            //get = this.client.getMatchedStranger(jid);
            user = client.getMatchedStranger(userJID, systemJID);
            try {
                cache.set(userJID + systemJID , MetlyUser.getJSONString(user));
            } catch (Exception e) {
                log.error("Error on cache SET key:" + userJID + systemJID, e);
            }
        } else {
        	user = MetlyUser.getUserFromJSON((String) get);
        }
        return user;
    }

    @Override
    public MetlyUser getNewStranger(String userJID, String systemJID) {
        MetlyUser metlyUser = this.client.getNewStranger(userJID, systemJID);
        try {
            cache.set(userJID + systemJID, MetlyUser.getJSONString(metlyUser));
        } catch (Exception e) {
            log.error("Error on cache SET key:" + userJID + systemJID, e);
        }
        return metlyUser;
        
    }
}
