package com.friendmaybe.openfire.business;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.lf5.PassingLogRecordFilter;
import org.apache.tools.ant.taskdefs.Definer.OnError;
import org.dom4j.Element;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendmaybe.openfire.dao.IQDB;
import com.friendmaybe.openfire.dao.FriendmaybeAnonymousClient;
import com.friendmaybe.openfire.dao.FriendmaybeServiceDBClient;
import com.friendmaybe.openfire.exception.FriendmaybeHappyException;
import com.friendmaybe.openfire.logic.FriendmaybeCacheServiceClient;
import com.friendmaybe.openfire.logic.FriendmaybeUser;
import com.friendmaybe.openfire.utils.ApplicationProperties;

public class IQProcessor {
    private static final Logger log = Logger.getLogger(IQProcessor.class);

    private IQDB iqdb;

    private static final FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(
            new FriendmaybeServiceDBClient());

    private final String systemJID;

    public IQProcessor() {
        iqdb = new IQDB();
        systemJID = ApplicationProperties.getProperty("friendmaybe.systemJID");
    }

    public void process(IQ packet) {
        try {
            Element childElement = packet.getChildElement();
            if (childElement != null) {
                String attributeValue = childElement.getNamespaceURI();

                if (attributeValue != null && attributeValue.equals("jabber:iq:roster")) {
                    JID userJID = packet.getFrom();
                    if (userJID != null) {

                        for (Object object : childElement.elements()) {
                            Element element = (Element) object;
                            String to = element.attributeValue("jid");
//                            String subscription = element.attributeValue("subscription");
                            if (to != null && to.equals(systemJID)) {
                                FriendmaybeUser stranger = null;
                                if(!userJID.getNode().equals(userJID.getResource())){
                                    stranger = friendmaybeCacheServiceClient.getMatchedStranger(userJID.toString());
                                }
                                if (stranger != null) {
                                    JID jid = new JID(stranger.getJID());
                                    if(!jid.getNode().equals(jid.getResource())) {
                                        element.addAttribute("jid", jid.toBareJID());
                                    } else {
                                        throw new FriendmaybeHappyException("Sending request to anonymous");
                                    }
//                                    element.addAttribute("name", stranger.getName());
//                                    element.addAttribute("subscription", "both");
                                } else {
                                    throw new FriendmaybeHappyException("Sending request to anonymous");
                                }
                            } 
//                            else if(subscription != null && subscription.equals("remove") && to != null){
//                            IQ forget = packet.createCopy();
//                            forget.setFrom(to);
//                            forget.getChildElement().clearContent();
//                            forget.getChildElement().add(element);
//                            element.addAttribute("jid", userJID.toBareJID());
//                            SessionManager sessionManager = SessionManager.getInstance();
//                            ClientSession clientSession = sessionManager.getSession(strangerJID);
//                        }
                        }
                        log.info("Changed to:" + packet);
                    }
                }
            }

//            this.iqdb.save(packet);
        } catch (Exception e) {
            log.error("IQ processing problem:" + packet, e);
        }
    }

}
