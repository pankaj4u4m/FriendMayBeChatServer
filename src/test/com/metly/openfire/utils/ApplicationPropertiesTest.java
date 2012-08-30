package com.metly.openfire.utils;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

public class ApplicationPropertiesTest {

    @Test
    public void testGetPropertyString() {
        Assert.assertNotNull(ApplicationProperties.getProperty("metly.systemJID"));
        Assert.assertNull(ApplicationProperties.getProperty("nullValue"));
        Assert.assertEquals("metly@localhost", ApplicationProperties.getProperty("metly.systemJID"));
    }

    @Test
    public void testGetPropertyStringString() {
        Assert.assertNull(ApplicationProperties.getProperty("nullValue1"));
        Assert.assertEquals( "metly", ApplicationProperties.getProperty("nullValue1", "metly"));
        Assert.assertEquals( "metly", ApplicationProperties.getProperty("nullValue1"));
    }

    @Test
    public void testGetProperties() {
        Set<String> list = new HashSet< String >(){{
            add("child1");
            add("child2");
            add("child3");
            add("child4");
        }} ;
        ApplicationProperties.getProperty("parent.c1", "child1");
        ApplicationProperties.getProperty("parent.ch2", "child2");
        ApplicationProperties.getProperty("parent.chi3", "child3");
        ApplicationProperties.getProperty("parent.chil4", "child4");
        List< String > properties = ApplicationProperties.getProperties("parent");
        Assert.assertEquals(properties.size(), 4);
        Assert.assertEquals(new HashSet<String>(properties), list);
    }

    @Test
    public void testGetPropetriesNames() {
        Set<String> list = new HashSet< String >(){{
            add("parent.c1");
            add("parent.ch2");
            add("parent.chi3");
            add("parent.chil4");
        }} ;
        ApplicationProperties.getProperty("parent.c1", "child1");
        ApplicationProperties.getProperty("parent.ch2", "child2");
        ApplicationProperties.getProperty("parent.chi3", "child3");
        ApplicationProperties.getProperty("parent.chil4", "child4");
        List< String > properties = ApplicationProperties.getPropetriesNames("parent");
        Assert.assertEquals(properties.size(), 4);
        Assert.assertEquals(new HashSet<String>(properties), list);
    }

}
