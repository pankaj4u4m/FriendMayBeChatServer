package com.metly.openfire.bussiness;

import org.apache.log4j.Logger;
import org.xmpp.packet.Presence;

import com.metly.openfire.dao.PresenceDB;

public class PresenceProcessor {
    private static final Logger log = Logger.getLogger(PresenceProcessor.class);

    private PresenceDB presenceDB;

    public PresenceProcessor() {
        this.presenceDB = new PresenceDB();
    }

    public void process(Presence packet) {
        this.presenceDB.save(packet);

    }

}
