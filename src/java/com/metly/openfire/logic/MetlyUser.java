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

    private Long id;
    private String jid;
    private String name;
    private String DOB;
    private String location;
    private String gender;
    private String connectedWith;
    private String status;
    private String systemJID;

    public MetlyUser() {

    }

    public MetlyUser(String jid) {
        this.jid = jid;
    }

    public static MetlyUser getUserFromJSON(String jsonString) {
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

    public void setJID(String jid) {
        this.jid = jid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String dOB) {
        DOB = dOB;
    }

    public String getSystemJID() {
        return systemJID;
    }

    public void setSystemJID(String systemJID) {
        this.systemJID = systemJID;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((DOB == null) ? 0 : DOB.hashCode());
        result = prime * result + ((connectedWith == null) ? 0 : connectedWith.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((jid == null) ? 0 : jid.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((systemJID == null) ? 0 : systemJID.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof MetlyUser)) {
            return false;
        }
        MetlyUser other = (MetlyUser) obj;
        if (DOB == null) {
            if (other.DOB != null) {
                return false;
            }
        } else if (!DOB.equals(other.DOB)) {
            return false;
        }
        if (connectedWith == null) {
            if (other.connectedWith != null) {
                return false;
            }
        } else if (!connectedWith.equals(other.connectedWith)) {
            return false;
        }
        if (gender == null) {
            if (other.gender != null) {
                return false;
            }
        } else if (!gender.equals(other.gender)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (jid == null) {
            if (other.jid != null) {
                return false;
            }
        } else if (!jid.equals(other.jid)) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (systemJID == null) {
            if (other.systemJID != null) {
                return false;
            }
        } else if (!systemJID.equals(other.systemJID)) {
            return false;
        }
        return true;
    }

}
