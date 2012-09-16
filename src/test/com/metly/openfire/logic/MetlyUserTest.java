package com.metly.openfire.logic;

import junit.framework.Assert;

import org.junit.Test;

public class MetlyUserTest {
    
    @Test
    public void testMetlyUser() {
        MetlyUser user = new MetlyUser();
        user.setConnectedWith("connected");
        user.setDOB("2000-12-02");
        user.setGender("Male");
        user.setId(123L);
        user.setJID("pankaj@localhost");
        user.setLocation("location");
        user.setName("metly");
        user.setStatus("C");
        user.setSystemJID("metly@localhost");
        
        Assert.assertEquals("connected", user.getConnectedWith());
        Assert.assertEquals("2000-12-02", user.getDOB());
        Assert.assertEquals("Male", user.getGender());
        Assert.assertEquals(new Long(123), user.getId());
        Assert.assertEquals("pankaj@localhost", user.getJID());
        Assert.assertEquals("location", user.getLocation());
        Assert.assertEquals("metly", user.getName());
        Assert.assertEquals("C", user.getStatus());
        Assert.assertEquals("metly@localhost", user.getSystemJID());
        
        String name = "Pankaj asdkf d r";

    }

    @Test
    public void testGetJSONString() {
        MetlyUser user = new MetlyUser();
        user.setConnectedWith("connected");
        user.setDOB("2000-12-02");
        user.setGender("Male");
        user.setId(123L);
        user.setJID("pankaj@localhost");
        user.setLocation("location");
        user.setName("metly");
        user.setStatus("C");
        user.setSystemJID("metly@localhost");
        
        MetlyUser userFromJSON = MetlyUser.getUserFromJSON(MetlyUser.getJSONString(user));
        
        Assert.assertEquals("connected", userFromJSON.getConnectedWith());
        Assert.assertEquals("2000-12-02", userFromJSON.getDOB());
        Assert.assertEquals("Male", userFromJSON.getGender());
        Assert.assertEquals(new Long(123), userFromJSON.getId());
        Assert.assertEquals("pankaj@localhost", userFromJSON.getJID());
        Assert.assertEquals("location", userFromJSON.getLocation());
        Assert.assertEquals("metly", userFromJSON.getName());
        Assert.assertEquals("C", userFromJSON.getStatus());
        Assert.assertEquals("metly@localhost", userFromJSON.getSystemJID());
    }

}
