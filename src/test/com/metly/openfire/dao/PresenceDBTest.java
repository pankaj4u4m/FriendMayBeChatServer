package com.metly.openfire.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmpp.packet.Presence;

public class PresenceDBTest {

    @Test
    public void testSave() {
        PresenceDB presenceDB = new PresenceDB();
        Presence packet = new Presence();
        packet.setFrom("metly@localhost");
        packet.setTo("pankaj@localhost");
        packet.setType(Presence.Type.subscribe);
        presenceDB.save(packet);
    }

}
