package com.metly.openfire.logic;


public interface MetlyServiceClient {
    public MetlyUser getMatchedStranger(String userJID, String systemJID);
    public MetlyUser getNewStranger(String userJID, String systemJID);
}
