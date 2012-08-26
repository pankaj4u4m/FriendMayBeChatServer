package com.metly.openfire.plugin;

import java.io.File;

import org.apache.log4j.Logger;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.session.Session;
import org.xmpp.packet.Packet;

import com.metly.openfire.exception.MetlyException;

/**
 * The Class MetlyPlugin.
 * @author kpankaj
 */
public final class MetlyPlugin implements Plugin, PacketInterceptor {
    
    /** The Constant log. */
    private static final Logger log = Logger.getLogger(MetlyPlugin.class);

    /** The plugin manager. */
    private static PluginManager pluginManager;

    /** The interceptor manager. */
    private final InterceptorManager interceptorManager;

    /** The packet packetReceiver. */
    private final PacketReceiver packetReceiver;

    /**
     * Instantiates a new metly plugin.
     */
    public MetlyPlugin() {
        this.interceptorManager = InterceptorManager.getInstance();
        this.packetReceiver = PacketReceiverFactory.getPacketReceiver();
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void initializePlugin(final PluginManager manager, final File pluginDirectory) {
        // register with interceptor manager
        MetlyPlugin.log.info("Packet Filter loaded...");
        this.interceptorManager.addInterceptor(this);
        MetlyPlugin.pluginManager = manager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyPlugin() {
        // unregister with interceptor manager
        this.interceptorManager.removeInterceptor(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interceptPacket(final Packet packet, final Session session, final boolean incoming,
            final boolean processed) throws PacketRejectedException {
        try {
            this.packetReceiver.receivePacket(packet, session, incoming, processed);
        } catch (Exception e) {
        	
        		log.error(e.getMessage(), e);
        	
            throw new PacketRejectedException(e);
        }

    }

    /**
     * Gets the plugin manager.
     *
     * @return the plugin manager
     */
    public static PluginManager getPluginManager() {
        return MetlyPlugin.pluginManager;
    }
}
