package com.friendmaybe.openfire.logic;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

public class FriendmaybeCacheServiceClientTest {

    @Test
    public void testGetMatchedStranger() {
        FriendmaybeUser user = new FriendmaybeUser();
        user.setConnectedWith("connected");
        user.setDOB("2000-12-02");
        user.setGender("Male");
        user.setId(123L);
        user.setJID("pankaj@friendmaybe.com");
        user.setLocation("location");
        user.setName("friendmaybe");
        user.setStatus("C");
        user.setSystemJID("friendmaybe@friendmaybe.com");
        
        FriendmaybeServiceClient mockClient = EasyMock.createMock(FriendmaybeServiceClient.class);
        EasyMock.expect(mockClient.getMatchedStranger("friendmaybe@friendmaybe.com")).andReturn(user);
        FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(mockClient);
        Assert.assertEquals(friendmaybeCacheServiceClient.getMatchedStranger("friendmaybe@friendmaybe.com"), user);
        
        EasyMock.verify(mockClient);
        EasyMock.reset(mockClient);
    }

    @Test
    public void testGetNewStranger() {
        FriendmaybeUser user = new FriendmaybeUser();
        user.setConnectedWith("connected");
        user.setDOB("2000-12-02");
        user.setGender("Male");
        user.setId(123L);
        user.setJID("pankaj@friendmaybe.com");
        user.setLocation("location");
        user.setName("friendmaybe");
        user.setStatus("C");
        user.setSystemJID("friendmaybe@friendmaybe.com");

        FriendmaybeServiceClient mockClient = EasyMock.createMock(FriendmaybeServiceClient.class);
        EasyMock.expect(mockClient.getNewStranger("friendmaybe@friendmaybe.com")).andReturn(user);
        FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(mockClient);
        Assert.assertEquals(friendmaybeCacheServiceClient.getNewStranger("friendmaybe@friendmaybe.com"), user);
        Assert.assertEquals(friendmaybeCacheServiceClient.getMatchedStranger("friendmaybe@friendmaybe.com"), user);

        EasyMock.verify(mockClient);
        EasyMock.reset(mockClient);
    }

    @Test
    public void testClearMapping() {
        FriendmaybeUser user = new FriendmaybeUser();
        user.setConnectedWith("connected");
        user.setDOB("2000-12-02");
        user.setGender("Male");
        user.setId(123L);
        user.setJID("pankaj@friendmaybe.com");
        user.setLocation("location");
        user.setName("friendmaybe");
        user.setStatus("C");
        user.setSystemJID("friendmaybe@friendmaybe.com");

        
        FriendmaybeServiceClient mockClient = EasyMock.createMock(FriendmaybeServiceClient.class);
        mockClient.clearMapping("friendmaybe@friendmaybe.com");
        FriendmaybeCacheServiceClient friendmaybeCacheServiceClient = new FriendmaybeCacheServiceClient(mockClient);
        friendmaybeCacheServiceClient.clearMapping("friendmaybe@friendmaybe.com");
        Assert.assertEquals(friendmaybeCacheServiceClient.getMatchedStranger("friendmaybe@friendmaybe.com"), user);

    }

}
