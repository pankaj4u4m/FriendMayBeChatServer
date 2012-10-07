package com.friendmaybe.openfire.logic;

public interface FriendmaybeServiceClient {
    public FriendmaybeUser getMatchedStranger(String userJID);

    public FriendmaybeUser getNewStranger(String userJID);

    void clearMapping(String userJID);
}
