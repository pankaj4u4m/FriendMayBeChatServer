package com.metly.openfire.cache;

import static org.junit.Assert.*;

import org.junit.Test;

public class MemcachedCacheTest {
    Cache cache = new MemcachedCache();
    
    @Test
    public void testSetStringObject() {
        cache.set("key", "value");
        assertEquals(cache.get("key"), "value");
    }

    @Test
    public void testSetStringObjectDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddOrDecrString() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddOrDecrStringLong() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddOrIncrString() {
        fail("Not yet implemented");
    }

    @Test
    public void testAddOrIncrStringLong() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteString() {
        fail("Not yet implemented");
    }

    @Test
    public void testDeleteStringDate() {
        fail("Not yet implemented");
    }

    @Test
    public void testGet() {
        fail("Not yet implemented");
    }

    @Test
    public void testKeyExists() {
        fail("Not yet implemented");
    }

    @Test
    public void testReplaceStringObject() {
        fail("Not yet implemented");
    }

    @Test
    public void testReplaceStringObjectDate() {
        fail("Not yet implemented");
    }

}
