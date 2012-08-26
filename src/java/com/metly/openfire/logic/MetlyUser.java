package com.metly.openfire.logic;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metly.openfire.exception.MetlyException;

public class MetlyUser {
    private static final ObjectMapper mapper = new ObjectMapper();

    private String jid;
    private String name;
    private String age;
    private String location;
    private String gender;
    private String connectedWith;
    private String status;

    public MetlyUser(){
    	
    }
    
    public MetlyUser(String jid){
        this.jid = jid;
    }
    
    public static MetlyUser getUserFromJSON(String jsonString){
        try {
           return mapper.readValue(jsonString, MetlyUser.class);
        } catch (JsonProcessingException e) {
            throw new MetlyException(e);
        } catch (IOException e) {
            throw new MetlyException(e);
        }
    }

    public static String getJSONString(MetlyUser user) {
        try {
            return mapper.writeValueAsString(user);
        } catch (JsonGenerationException e) {
            throw new MetlyException(e);
        } catch (JsonMappingException e) {
            throw new MetlyException(e);
        } catch (IOException e) {
            throw new MetlyException(e);
        }
    }

    public void setConnectedWith(String connectedWith) {
        this.connectedWith = connectedWith;
    }

    public String getConnectedWith() {
        return connectedWith;
    }

    public String getJID() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
