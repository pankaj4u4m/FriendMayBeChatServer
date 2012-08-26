package com.metly.openfire.bussiness;

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

public class MessageProcessor {
	private static final Logger log = Logger.getLogger(MessageProcessor.class);

	private final MessageDB messageDB;

	private static final MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(
			new MetlyServiceDBClient());

	XMPPPacketReader reader =  new XMPPPacketReader();
	public MessageProcessor() {
		this.messageDB = new MessageDB();

	}

	public void process(Message packet) {
		
		this.messageDB.save(packet);
		JID to = packet.getTo();
		
		String body = packet.getBody().trim().toLowerCase();
		String metly = ApplicationProperties.getProperty("metly.jid", "metly") + '@'
				+ to.getDomain();
		boolean isAnonymous = to.toBareJID().equals(metly);
		boolean isCommand = Commands.isCommand(body);
		log.info("isAnonymous:" + isAnonymous +" isCommand: " + isCommand);
		
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
		}
	}

	private void disconnectCommand(Message packet, boolean isAnonymous) {
		if(isAnonymous){
			JID userJID = packet.getFrom();

			// acknowledge him that metlyCacheServiceClient is connected
			packet.setTo(userJID);
			packet.setFrom(userJID);

		}
		log.info("disconnected!");
	}

	private void anonymousMessage(Message packet) {
		JID systemJID = packet.getTo();
		JID userJID = packet.getFrom();
		MetlyUser stranger = metlyCacheServiceClient.getMatchedStranger(userJID.toString(), systemJID.toString());
		JID strangerJID = new JID(stranger.getJID());
		
		SessionManager sessionManager = SessionManager.getInstance();
		ClientSession clientSession = sessionManager.getSession(strangerJID);

		// acknowledge him that metlyCacheServiceClient is connected
		packet.setTo(strangerJID);
		packet.setFrom(systemJID);

		if (clientSession != null) {
			log.info("sending to stranger:" + packet);
			clientSession.process(packet);
		} else {
			log.error("no session for user:" + stranger);
			disconnectCommand(packet, true);
		}
		throw new MetlyHappyException();
	}

	private void connectCommand(Message packet, boolean isAnonymous) {
		if (isAnonymous) {
			JID systemJID = packet.getTo();
			JID userJID = packet.getFrom();
			
			MetlyUser stranger = metlyCacheServiceClient.getNewStranger(userJID.toString(), systemJID.toString());
			

			// acknowledge him that metlyCacheServiceClient is connected
			packet.setTo(userJID);
			packet.setFrom(systemJID);

			packet.setBody(getFormatedJSON(stranger));

		} else {
			somethingWentWrong(
					packet,
					"You are Chatting with a Remembered user, you have to move to Stranger chat or ping to "
							+ ApplicationProperties.getProperty("metly.jid", "metly") + '@'
							+ packet.getTo().getDomain() + " to use this command");

		}
	}

	private void somethingWentWrong(Message packet, String message) {
		JID toJID = packet.getTo();
		packet.setTo(packet.getFrom());
		packet.setFrom(toJID);
		packet.setType(Message.Type.error);
		packet.setBody(message);

	}

	private String getFormatedJSON(MetlyUser stranger) {
		StringBuilder builder = new StringBuilder();

		if (stranger != null) {
			builder.append("\nAge:").append(stranger.getAge()).append("\n");
			builder.append("\nSex:").append(stranger.getGender()).append("\n");
		}
		return builder.toString();
	}

}
