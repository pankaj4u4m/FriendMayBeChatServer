package com.metly.openfire.plugin;

import com.metly.openfire.bussiness.PacketProcessor;

/**
 * A factory for creating PacketReceiver objects.
 * 
 * @author kpankaj
 */
public class PacketReceiverFactory {

    /**
     * Gets the packet receiver instance.
     * 
     * @return the packet receiver
     */
    public static PacketReceiver getPacketReceiver() {
        return new PacketProcessor();
        // return new SamplePacketReceiver();
    }
}
