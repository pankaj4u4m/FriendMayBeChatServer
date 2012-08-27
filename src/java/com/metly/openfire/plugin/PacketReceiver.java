package com.metly.openfire.plugin;

import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

/**
 * The Interface PacketReceiver.
 */
public interface PacketReceiver {

    /**
     * Receive packet and do further processes on these packages.
     * 
     * @param packet
     *            the packet
     * @param session
     *            the session
     * @param incoming
     *            the incoming
     * @param processed
     *            the processed
     */
    void receivePacket(Packet packet, Session session, boolean incoming, boolean processed);
}
