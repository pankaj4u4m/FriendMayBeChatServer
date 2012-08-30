package com.metly.openfire.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xmpp.packet.Message;

public class MessageDBTest {

    @Test
    public void testSave() {
        MessageDB messageDB = new MessageDB();
        Message packet = new Message();
        packet.setTo("pankaj@localhost");
        packet.setFrom("metly@localhost");
        packet.setType(Message.Type.chat);
        packet.setBody("hurry passed");
        messageDB.save(packet);
    }

}
