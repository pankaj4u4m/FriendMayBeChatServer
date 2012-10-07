package com.friendmaybe.openfire.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmpp.packet.Presence;

public class PresenceDBTest {

    @Test
    public void testSave() {
        PresenceDB presenceDB = new PresenceDB();
        Presence packet = new Presence();
        packet.setFrom("friendmaybe@friendmaybe.com");
        packet.setTo("pankaj@friendmaybe.com");
        packet.setType(Presence.Type.subscribe);
        presenceDB.save(packet);
    }

}
