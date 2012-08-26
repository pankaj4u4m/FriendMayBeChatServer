package com.metly.openfire.logic;

import org.xmpp.packet.JID;


public interface MetlyServiceClient {
    public MetlyUser getMatchedStranger(String userJID, String systemJID);
    public MetlyUser getNewStranger(String userJID, String systemJID);
	void clearMapping(String userJID, String systemJID);
}
