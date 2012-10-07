package com.friendmaybe.openfire.business;

import org.apache.log4j.Logger;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

import com.friendmaybe.openfire.dao.FriendmaybeAnonymousClient;
import com.friendmaybe.openfire.dao.FriendmaybeServiceDBClient;
import com.friendmaybe.openfire.dao.PresenceDB;
import com.friendmaybe.openfire.exception.FriendmaybeHappyException;
import com.friendmaybe.openfire.logic.FriendmaybeCacheServiceClient;
import com.friendmaybe.openfire.logic.FriendmaybeUser;
import com.friendmaybe.openfire.utils.ApplicationProperties;

public class PresenceProcessor {
    private static final Logger log = Logger.getLogger(PresenceProcessor.class);

    private PresenceDB presenceDB;

    private static final FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(
            new FriendmaybeServiceDBClient());
    
    private final String systemJID;

    public PresenceProcessor() {
        this.presenceDB = new PresenceDB();
        systemJID = ApplicationProperties.getProperty("friendmaybe.systemJID");
    }

    public void process(Presence packet) {
        JID toJID = packet.getTo();
        if (toJID != null && toJID.toString().equals(systemJID)) {
            JID userJID = packet.getFrom();
            if (userJID != null) {
                FriendmaybeUser stranger = null;
                if(!userJID.getNode().equals(userJID.getResource())){
                    stranger = friendmaybeCacheServiceClient.getMatchedStranger(userJID.toString());
                }
//          log.info("user:" + FriendmaybeUser.getJSONString(stranger));
                if (stranger != null) {
                    JID jid = new JID(stranger.getJID());
                    if(!jid.getNode().equals(jid.getResource())){
                        packet.setTo(jid.toBareJID());
                    } else {
                        throw new FriendmaybeHappyException("Sending request to anonymous");
                    }
                } else {
                    throw new FriendmaybeHappyException("Sending request to anonymous");
                }
            }
        }
//        this.presenceDB.save(packet);

    }

}
