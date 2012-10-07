package com.friendmaybe.openfire.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ApplicationProperties {
    private static final Properties PROPERTIES = new Properties();
    static {
        PROPERTIES.setProperty("friendmaybe.systemJID", "friendmaybe@friendmaybe.com");
        PROPERTIES.setProperty("friendmaybe.memcacheservers.host1", "friendmaybe.com:20020");
        PROPERTIES.setProperty("friendmaybe.memcacheservers.host2", "friendmaybe.com:20020");
        PROPERTIES.setProperty("friendmaybe.users_db", "friendmaybe_production.users");
        PROPERTIES.setProperty("friendmaybe.user_details_db", "friendmaybe_production.user_details");
        PROPERTIES.setProperty("friendmaybe.stanza_archives_db", "friendmaybe_production.stanza_archives");
        PROPERTIES.setProperty("friendmaybe.messages_archives_db", "friendmaybe_production.message_archives");
        PROPERTIES.setProperty("friendmaybe.login_locations_db", "friendmaybe_production.login_locations");
        PROPERTIES.setProperty("friendmaybe.user_connection_statuses_db", "friendmaybe_production.user_connection_statuses");
    }

    public static String getProperty(String property) {
        return PROPERTIES.getProperty(property);
    }

    public static String getProperty(String property, String value) {
        String ret = PROPERTIES.getProperty(property);
        if (ret == null) {
            PROPERTIES.setProperty(property, value);
            return value;
        }
        return ret;
    }

    public static List< String > getProperties(String parent) {

        Collection< String > propertyNames = getPropetriesNames(parent);
        List< String > values = new ArrayList< String >();
        for (String propertyName : propertyNames) {
            String value = getProperty(propertyName);
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }

    public static List< String > getPropetriesNames(String parent) {
        List< String > propertiesName = new ArrayList< String >();
        for (Object key : PROPERTIES.keySet()) {
            if (((String) key).startsWith(parent)) {
                propertiesName.add((String) key);
            }
        }
        return propertiesName;
    }

}
