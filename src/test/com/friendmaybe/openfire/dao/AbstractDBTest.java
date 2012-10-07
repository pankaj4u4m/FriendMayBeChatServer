package com.friendmaybe.openfire.dao;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;
import org.xmpp.packet.JID;

public class AbstractDBTest {

    @Test
    public void testGetUserId() {
        AbstractDB abstractDB = new AbstractDB() {
        };
        JID jid = new JID("pankaj@friendmaybe.com");
        Assert.assertEquals(abstractDB.getUserId(jid), new Long(1));
        
    }

    @Test
    public void testGetUserXmpp() {
        AbstractDB abstractDB = new AbstractDB() {
        };
        Assert.assertEquals(abstractDB.getUserXmpp(1L), "pankaj@friendmaybe.com");
    }

}
