package com.friendmaybe.openfire.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmpp.packet.IQ;

public class IQDBTest {

    @Test
    public void testSave() {
        IQDB iqdb = new IQDB();
        IQ packet = new IQ();
        packet.setTo("pankaj@friendmaybe.com");
        packet.setType(IQ.Type.set);
        iqdb.save(packet);
    }

}
