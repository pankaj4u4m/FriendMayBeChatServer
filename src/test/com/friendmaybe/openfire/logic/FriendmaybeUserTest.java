package com.friendmaybe.openfire.logic;

import junit.framework.Assert;

import org.junit.Test;

public class FriendmaybeUserTest {
    
    @Test
    public void testFriendmaybeUser() {
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
        
        Assert.assertEquals("connected", user.getConnectedWith());
        Assert.assertEquals("2000-12-02", user.getDOB());
        Assert.assertEquals("Male", user.getGender());
        Assert.assertEquals(new Long(123), user.getId());
        Assert.assertEquals("pankaj@friendmaybe.com", user.getJID());
        Assert.assertEquals("location", user.getLocation());
        Assert.assertEquals("friendmaybe", user.getName());
        Assert.assertEquals("C", user.getStatus());
        Assert.assertEquals("friendmaybe@friendmaybe.com", user.getSystemJID());
        
        String name = "Pankaj asdkf d r";

    }

    @Test
    public void testGetJSONString() {
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
        
        FriendmaybeUser userFromJSON = FriendmaybeUser.getUserFromJSON(FriendmaybeUser.getJSONString(user));
        
        Assert.assertEquals("connected", userFromJSON.getConnectedWith());
        Assert.assertEquals("2000-12-02", userFromJSON.getDOB());
        Assert.assertEquals("Male", userFromJSON.getGender());
        Assert.assertEquals(new Long(123), userFromJSON.getId());
        Assert.assertEquals("pankaj@friendmaybe.com", userFromJSON.getJID());
        Assert.assertEquals("location", userFromJSON.getLocation());
        Assert.assertEquals("friendmaybe", userFromJSON.getName());
        Assert.assertEquals("C", userFromJSON.getStatus());
        Assert.assertEquals("friendmaybe@friendmaybe.com", userFromJSON.getSystemJID());
    }

}
