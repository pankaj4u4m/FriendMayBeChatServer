package com.metly.openfire.cache;

import java.util.Date;
import java.util.Map;

/**
 * The Interface Cache.
 */
public interface Cache {
    
    /**
     * Sets the.
     *
     * @param key the key
     * @param value the value
     */
    public void set(String key, Object value);
    
    /**
     * Sets the.
     *
     * @param key the key
     * @param value the value
     * @param expiry the expiry
     */
    public void set(String key, Object value, Date expiry);
    
    /**
     * Adds the or decr.
     *
     * @param key the key
     * @return the long
     */
    public long addOrDecr( String key );
    
    /**
     * Adds the or decr.
     *
     * @param key the key
     * @param decr the decr
     * @return the long
     */
    public long addOrDecr( String key, long decr ) ;
    
    /**
     * Adds the or incr.
     *
     * @param key the key
     * @return the long
     */
    public long addOrIncr( String key ) ;
    
    /**
     * Adds the or incr.
     *
     * @param key the key
     * @param inc the inc
     * @return the long
     */
    public long addOrIncr( String key, long inc ) ;
    
    /**
     * Delete.
     *
     * @param key the key
     * @return true, if successful
     */
    public boolean delete( String key ) ;
    
    /**
     * Delete.
     *
     * @param key the key
     * @param expiry the expiry
     * @return true, if successful
     */
    public boolean delete( String key, Date expiry ) ;
    
    /**
     * Gets the.
     *
     * @param key the key
     * @return the object
     */
    public Object get( String key );
    
    /**
     * Key exists.
     *
     * @param key the key
     * @return true, if successful
     */
    public boolean keyExists( String key ) ;
    
    /**
     * Replace.
     *
     * @param key the key
     * @param value the value
     * @return true, if successful
     */
    public boolean replace( String key, Object value );
    
    /**
     * Replace.
     *
     * @param key the key
     * @param value the value
     * @param expiry the expiry
     * @return true, if successful
     */
    public boolean replace( String key, Object value, Date expiry ) ;
}
