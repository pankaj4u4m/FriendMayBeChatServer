package com.metly.openfire.business;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.metly.openfire.dao.MessageDB;
import com.metly.openfire.dao.MetlyServiceDBClient;
import com.metly.openfire.exception.MetlyHappyException;
import com.metly.openfire.logic.MetlyCacheServiceClient;
import com.metly.openfire.logic.MetlyUser;
import com.metly.openfire.utils.ApplicationProperties;
import com.metly.openfire.utils.Commands;

import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;

public class MessageProcessor {
    private static final Logger log = Logger.getLogger(MessageProcessor.class);

    private final MessageDB messageDB;

    private static final MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(
            new MetlyServiceDBClient());
    
    private final JID systemJID;

    public MessageProcessor() {
        this.messageDB = new MessageDB();
        systemJID = new JID(ApplicationProperties.getProperty("metly.systemJID"));
    }

    public void process(Message packet) {
        if (packet.getBody() == null) {
            return;
        }
        if(packet.getBody().length() > 1000){
            somethingWentWrong(packet, packet.getFrom(), packet.getTo(), "Your message has more than 1000 characters");
            return;
        }
        JID to = packet.getTo();

        String body = packet.getBody().trim().toLowerCase();
        boolean isAnonymous = to.equals(systemJID);
        boolean isCommand = Commands.isCommand(body);
        log.info("isAnonymous:" + isAnonymous + " isCommand: " + isCommand);

        if (isCommand) {
            String command = Commands.getCommand(body);
            log.info("Command received:" + command + " with package:" + packet);
            if (command == Commands.CONNECT) {
                connectCommand(packet, isAnonymous);

            } else if (command == Commands.DISCONNECT) {
                disconnectCommand(packet, isAnonymous);
            }
        } else if (isAnonymous) {
            anonymousMessage(packet);
        } else if(packet.getType() != Message.Type.error){
            this.messageDB.save(packet);
        }
    }

    private void disconnectCommand(Message packet, boolean isAnonymous) {
        JID fromJID = packet.getFrom();
        JID toJID = packet.getTo();
        
        packet.setType(Message.Type.error);

        if (isAnonymous) {
            packet.setBody(" -User has Disconnected");
            MetlyUser stranger = metlyCacheServiceClient.getMatchedStranger(fromJID.toString());
            if (stranger != null) {
                JID strangerJID = new JID(stranger.getJID());

                SessionManager sessionManager = SessionManager.getInstance();
                ClientSession clientSession = sessionManager.getSession(strangerJID);
                // acknowledge him that metlyCacheServiceClient is connected

                if (clientSession != null) {
                    log.info("sending to stranger:" + packet);

                    packet.setTo(strangerJID);
                    packet.setFrom(systemJID);
                    clientSession.process(packet);
                }
                
                // acknowledge him that metlyCacheServiceClient is connected
                packet.setTo(fromJID);
                packet.setFrom(toJID);
                packet.setBody(" -Disconnected");
            } else {
                somethingWentWrong(packet, fromJID, systemJID, " -You are already disconnected from Stranger. Try \\c");
            }
            
            metlyCacheServiceClient.clearMapping(fromJID.toString());
        } else {
            
            // acknowledge him that metlyCacheServiceClient is connected
            packet.setTo(fromJID);
            packet.setFrom(toJID);
            packet.setBody(" -You can't Disconnect from Remembered users");
        }
    }

    private void anonymousMessage(Message packet) {
        JID userJID = packet.getFrom(); 
        MetlyUser stranger = metlyCacheServiceClient.getMatchedStranger(userJID.toString());
        if (stranger != null) {
            JID strangerJID = new JID(stranger.getJID());

            SessionManager sessionManager = SessionManager.getInstance();
            ClientSession clientSession = sessionManager.getSession(strangerJID);
            // acknowledge him that metlyCacheServiceClient is connected

            if (clientSession != null) {
                log.info("sending to stranger:" + packet);

                packet.setTo(strangerJID);
                packet.setFrom(systemJID);
                clientSession.process(packet);
                throw new MetlyHappyException();
            } else {
                log.error("no session for user:" + userJID + " stanger: " + stranger);
                somethingWentWrong(packet, userJID, systemJID, " -You have been disconnected from Stranger. Try \\c");

            }
        } else {
            log.error("no  user matching found:" + userJID);
            somethingWentWrong(packet, userJID, systemJID, " -You have been disconnected from Stranger. Try \\c");

        }
    }

    private void connectCommand(Message packet, boolean isAnonymous) {
        if (isAnonymous) {
            JID userJID = packet.getFrom();

            MetlyUser stranger = metlyCacheServiceClient.getNewStranger(userJID.toString());

            // acknowledge him that metlyCacheServiceClient is connected
            packet.setTo(userJID);
            packet.setFrom(systemJID);
            packet.setBody(getFormatedJSON(stranger));
            
            log.info("Connected with: "+ packet);
            
            if(stranger == null){
                somethingWentWrong(packet, userJID, systemJID, "We are not able to connect any Stranger. Please try again!. \\c");
            }else {
                SessionManager sessionManager = SessionManager.getInstance();
                ClientSession clientSession = sessionManager.getSession(new JID(stranger.getJID()));
                if(clientSession == null) {
                    somethingWentWrong(packet, userJID, systemJID, "We are not able to connect any Stranger. Please try again!. \\c");
                }
            }

        } else {
            somethingWentWrong(packet, packet.getFrom(), packet.getTo(),
                    "You are Chatting with a Remembered user, you have to move to Stranger chat or ping to "
                            + ApplicationProperties.getProperty("metly.jid", "metly") + '@'
                            + packet.getTo().getDomain() + " to use this command");

        }
    }

    private void somethingWentWrong(Message packet, JID toJID, JID fromJID, String message) {
        packet.setTo(toJID);
        packet.setFrom(fromJID);
        packet.setType(Message.Type.error);
        packet.setBody(message);
        metlyCacheServiceClient.clearMapping(toJID.toString());

    }

    private String getFormatedJSON(MetlyUser stranger) {
        StringBuilder builder = new StringBuilder();

        if (stranger != null) {
            builder.append("Now you can talk with Stranger:\n");
            if(stranger.getName() != null){
                builder.append("Name- ").append(stranger.getName()).append("\n");
            }
//            if(stranger.getDOB() != null){
//                try {
//                    int year = Calendar.getInstance().get(Calendar.YEAR);
//                    int userYear = Integer.parseInt(stranger.getDOB().split("-")[0]);
//                    builder.append("Age- ").append(year - userYear).append("\n");
//                } catch (NumberFormatException e) {
//                    log.error(e);
//                }
//            }
            if(stranger.getGender() != null){
                builder.append("Gender- ").append(stranger.getGender()).append("\n");
            }
        }
        return builder.toString();
    }

}