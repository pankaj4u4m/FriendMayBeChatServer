package com.friendmaybe.openfire.business;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.dom4j.io.XMPPPacketReader;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.friendmaybe.openfire.dao.MessageDB;
import com.friendmaybe.openfire.dao.FriendmaybeAnonymousClient;
import com.friendmaybe.openfire.dao.FriendmaybeServiceDBClient;
import com.friendmaybe.openfire.exception.FriendmaybeHappyException;
import com.friendmaybe.openfire.logic.FriendmaybeCacheServiceClient;
import com.friendmaybe.openfire.logic.FriendmaybeUser;
import com.friendmaybe.openfire.utils.ApplicationProperties;
import com.friendmaybe.openfire.utils.Commands;

import de.javawi.jstun.attribute.MessageAttributeInterface.MessageAttributeType;

public class MessageProcessor {
    private static final Logger log = Logger.getLogger(MessageProcessor.class);

    private final MessageDB messageDB;

    private static final FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(
            new FriendmaybeServiceDBClient());
    private static final FriendmaybeCacheServiceClient friendmaybeCacheAnonymousServiceClient = new FriendmaybeCacheServiceClient(
            new FriendmaybeAnonymousClient());
    
    private final JID systemJID;

    public MessageProcessor() {
        this.messageDB = new MessageDB();
        systemJID = new JID(ApplicationProperties.getProperty("friendmaybe.systemJID"));
    }

    public void process(Message packet) {
        
        if(packet.getBody() != null && packet.getBody().length() > 1000){
            somethingWentWrong(packet, packet.getFrom(), packet.getTo(), "Your message has more than 1000 characters");
            return;
        }
        JID to = packet.getTo();
        boolean isAnonymous = to.equals(systemJID);
        
        String body = "";
        if(packet.getBody() != null){
            body = packet.getBody().trim().toLowerCase();
        }
        
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
            
            FriendmaybeUser stranger = null;
            if(fromJID.getNode().equals(fromJID.getResource())){
                stranger  = friendmaybeCacheAnonymousServiceClient.getMatchedStranger(fromJID.toString());
            } else {
                stranger = friendmaybeCacheServiceClient.getMatchedStranger(fromJID.toString());
            }
            if (stranger != null) {
                JID strangerJID = new JID(stranger.getJID());

                SessionManager sessionManager = SessionManager.getInstance();
                ClientSession clientSession = sessionManager.getSession(strangerJID);
                // acknowledge him that friendmaybeCacheServiceClient is connected

                if (clientSession != null) {
                    log.info("sending to stranger:" + packet);

                    packet.setTo(strangerJID);
                    packet.setFrom(systemJID);
                    clientSession.process(packet);
                }
                
                // acknowledge him that friendmaybeCacheServiceClient is connected
                packet.setTo(fromJID);
                packet.setFrom(toJID);
                packet.setBody(" -Disconnected");
            } else {
                somethingWentWrong(packet, fromJID, systemJID, " -You are already disconnected from Stranger. Try \\c");
            }
            if(fromJID.getNode().equals(fromJID.getResource())){
                friendmaybeCacheAnonymousServiceClient.clearMapping(fromJID.toString());
            } else {
                friendmaybeCacheServiceClient.clearMapping(fromJID.toString());
            }
        } else {
            
            // acknowledge him that friendmaybeCacheServiceClient is connected
            packet.setTo(fromJID);
            packet.setFrom(toJID);
            packet.setBody(" -You can't Disconnect from Remembered users");
        }
    }

    private void anonymousMessage(Message packet) {
        JID userJID = packet.getFrom(); 
        FriendmaybeUser stranger = null;
        if(userJID.getNode().equals(userJID.getResource())){
            stranger = friendmaybeCacheAnonymousServiceClient.getMatchedStranger(userJID.toString());
        } else {
            stranger = friendmaybeCacheServiceClient.getMatchedStranger(userJID.toString());
        }
        if (stranger != null) {
            JID strangerJID = new JID(stranger.getJID());

            SessionManager sessionManager = SessionManager.getInstance();
            ClientSession clientSession = sessionManager.getSession(strangerJID);
            // acknowledge him that friendmaybeCacheServiceClient is connected

            if (clientSession != null) {
                log.info("sending to stranger:" + packet);

                packet.setTo(strangerJID);
                packet.setFrom(systemJID);
                clientSession.process(packet);
                throw new FriendmaybeHappyException();
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

            FriendmaybeUser stranger = null;
            if(userJID.getNode().equals(userJID.getResource())){
                stranger = friendmaybeCacheAnonymousServiceClient.getNewStranger(userJID.toString());
            } else {
                stranger = friendmaybeCacheServiceClient.getNewStranger(userJID.toString());
            }
            // acknowledge him that friendmaybeCacheServiceClient is connected
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
                            + ApplicationProperties.getProperty("friendmaybe.jid", "friendmaybe") + '@'
                            + packet.getTo().getDomain() + " to use this command");

        }
    }

    private void somethingWentWrong(Message packet, JID toJID, JID fromJID, String message) {
        packet.setTo(toJID);
        packet.setFrom(fromJID);
        packet.setType(Message.Type.error);
        packet.setBody(message);
        if(toJID.getNode().equals(toJID.getResource())){
            friendmaybeCacheAnonymousServiceClient.clearMapping(toJID.toString());
        } else {
            friendmaybeCacheServiceClient.clearMapping(toJID.toString());
        }

    }

    private String getFormatedJSON(FriendmaybeUser stranger) {
        StringBuilder builder = new StringBuilder();

        if (stranger != null) {
            builder.append("You are now talking with Stranger:\n");
//            if(stranger.getName() != null){
//                StringBuffer name = new StringBuffer();
//                boolean space = true;
//                for(int i = 0; i< stranger.getName().length(); ++i){
//                    if(stranger.getName().charAt(i) == 32){
//                        space = true;
//                    } else if(space){
//                        name.append(stranger.getName().charAt(i));
//                        space = false;
//                    } else {
//                        name.append("*");
//                    }
//                }
//                builder.append("Name- ").append(name).append("\n");
//            }
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
                builder.append("Gender - ").append(stranger.getGender()).append("\n");
            }
        }
        return builder.toString();
    }

}
