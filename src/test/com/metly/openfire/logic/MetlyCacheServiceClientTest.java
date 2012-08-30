package com.metly.openfire.logic;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.easymock.EasyMock;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;

public class MetlyCacheServiceClientTest {

    @Test
    public void testGetMatchedStranger() {
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
        
        MetlyServiceClient mockClient = EasyMock.createMock(MetlyServiceClient.class);
        EasyMock.expect(mockClient.getMatchedStranger("metly@localhost")).andReturn(user);
        MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(mockClient);
        Assert.assertEquals(metlyCacheServiceClient.getMatchedStranger("metly@localhost"), user);
        
        EasyMock.verify(mockClient);
        EasyMock.reset(mockClient);
    }

    @Test
    public void testGetNewStranger() {
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

        MetlyServiceClient mockClient = EasyMock.createMock(MetlyServiceClient.class);
        EasyMock.expect(mockClient.getNewStranger("metly@localhost")).andReturn(user);
        MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(mockClient);
        Assert.assertEquals(metlyCacheServiceClient.getNewStranger("metly@localhost"), user);
        Assert.assertEquals(metlyCacheServiceClient.getMatchedStranger("metly@localhost"), user);

        EasyMock.verify(mockClient);
        EasyMock.reset(mockClient);
    }

    @Test
    public void testClearMapping() {
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

        
        MetlyServiceClient mockClient = EasyMock.createMock(MetlyServiceClient.class);
        mockClient.clearMapping("metly@localhost");
        MetlyCacheServiceClient metlyCacheServiceClient = new MetlyCacheServiceClient(mockClient);
        metlyCacheServiceClient.clearMapping("metly@localhost");
        Assert.assertEquals(metlyCacheServiceClient.getMatchedStranger("metly@localhost"), user);

    }

}
