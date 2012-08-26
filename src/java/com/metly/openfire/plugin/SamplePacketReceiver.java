
package com.metly.openfire.plugin;

import org.apache.log4j.Logger;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

/**
 * The Class SamplePacketReceiver.
 * @author kpankaj
 */
public class SamplePacketReceiver implements PacketReceiver {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(SamplePacketReceiver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void receivePacket(Packet packet, Session session, boolean incoming, boolean processed) {
        log.info("{Packet Received:" + packet + ", Session:" + session + ", incoming:" + incoming + " processed:"
                + processed + "}");
    }

}
