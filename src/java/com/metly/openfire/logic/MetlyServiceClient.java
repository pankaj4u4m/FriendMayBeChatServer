package com.metly.openfire.logic;

public interface MetlyServiceClient {
    public MetlyUser getMatchedStranger(String userJID);

    public MetlyUser getNewStranger(String userJID);

    void clearMapping(String userJID);
}
