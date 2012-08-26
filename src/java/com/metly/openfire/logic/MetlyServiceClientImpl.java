package com.metly.openfire.logic;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metly.openfire.exception.MetlyException;

@Deprecated
public class MetlyServiceClientImpl implements MetlyServiceClient{
    private static final MultiThreadedHttpConnectionManager connectionManager = 
        new MultiThreadedHttpConnectionManager();
    private static final HttpClient client = new HttpClient(connectionManager);
    
    
    @Override
    public MetlyUser getMatchedStranger(String userJID, String systemJID) {
        // and then from inside some thread executing method
        PostMethod post = new PostMethod("http://metly.com/");
        try {
            post.addParameter("userJID", userJID);
            post.addParameter("systemJID", systemJID);
            client.executeMethod(post);
            return new MetlyUser( post.getResponseBodyAsString());
        } catch (HttpException e) {
            throw new MetlyException(e);
        } catch (IOException e) {
            throw new MetlyException(e);
        } finally {
            // be sure the connection is released back to the connection 
            // manager
            post.releaseConnection();
        }
        
    }

    @Override
    public MetlyUser getNewStranger(String userJID, String systemJID) {
     // and then from inside some thread executing method
        PostMethod post = new PostMethod("http://metly.com/");
        try {
            post.addParameter("userJID", userJID);
            post.addParameter("systemJID", systemJID);
            client.executeMethod(post);
            return new MetlyUser(post.getResponseBodyAsString());
        } catch (HttpException e) {
            throw new MetlyException(e);
        } catch (IOException e) {
            throw new MetlyException(e);
        } finally {
            // be sure the connection is released back to the connection 
            // manager
            post.releaseConnection();
        }
    }

	@Override
	public void clearMapping(String userJID, String systemJID) {
		// TODO Auto-generated method stub
		
	}
}
