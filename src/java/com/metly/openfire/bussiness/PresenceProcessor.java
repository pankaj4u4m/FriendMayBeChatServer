package com.metly.openfire.bussiness;

import org.apache.log4j.Logger;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

import com.metly.openfire.dao.MetlyServiceDBClient;
import com.metly.openfire.dao.PresenceDB;
import com.metly.openfire.logic.MetlyCacheServiceClient;
import com.metly.openfire.logic.MetlyUser;
import com.metly.openfire.utils.ApplicationProperties;

public class PresenceProcessor {
    private static final Logger log = Logger.getLogger(PresenceProcessor.class);

    private PresenceDB presenceDB;

    private static final MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(
            new MetlyServiceDBClient());

    private final String systemJID;

    public PresenceProcessor() {
        this.presenceDB = new PresenceDB();
        systemJID = ApplicationProperties.getProperty("metly.systemJID");
    }

    public void process(Presence packet) {
        JID toJID = packet.getTo();
        if (toJID != null && toJID.toString().equals(systemJID)) {
            JID userJID = packet.getFrom();
            if (userJID != null) {
                MetlyUser stranger = metlyCacheServiceClient.getMatchedStranger(userJID.toString());
//          log.info("user:" + MetlyUser.getJSONString(stranger));
                if (stranger != null) {
                    packet.setTo(new JID(stranger.getJID()).toBareJID());
                } else {
                    packet.setType(Presence.Type.error);
                }
            }
        }
//        this.presenceDB.save(packet);

    }

}
