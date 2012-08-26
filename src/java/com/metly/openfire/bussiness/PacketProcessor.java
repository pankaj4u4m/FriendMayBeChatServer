package com.metly.openfire.bussiness;

import org.apache.log4j.Logger;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.ClientSession;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.IQ;
import org.xmpp.packet.IQ.Type;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.Presence;

import com.metly.openfire.plugin.PacketReceiver;

/**
 * The Class PacketProcessor.
 * @author kpankaj
 */
public class PacketProcessor implements PacketReceiver {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(PacketProcessor.class);
    
    /** The iq processor. */
    private final IQProcessor iqProcessor;
    
    /** The message processor. */
    private final MessageProcessor messageProcessor;
    
    /** The presence processor. */
    private final PresenceProcessor presenceProcessor;
    
    /**
     * Instantiates a new packet receiver processor.
     */
    public PacketProcessor(){
        iqProcessor = new IQProcessor();
        messageProcessor = new MessageProcessor();
        presenceProcessor = new PresenceProcessor();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void receivePacket(Packet packet, Session session, boolean incoming, boolean processed) {
        
    	if(processed){
            return;
        }
    	log.info("{Packet Received:" + packet + ", incoming:" + incoming + " processed:"
                + processed + "}");
    	
        if (packet instanceof Message) {
            messageProcessor.process((Message)packet);
        }
        else if (packet instanceof Presence) {
            presenceProcessor.process((Presence)packet);
        }
        else if (packet instanceof IQ) {
            iqProcessor.process((IQ)packet);
        }
        else {
            throw new IllegalArgumentException("For:{Packet Received:" + packet + ", incoming:" + incoming + " processed:"
                    + processed + "}");
        }
    }
}
