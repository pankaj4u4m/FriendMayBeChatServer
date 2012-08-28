package com.metly.openfire.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class ApplicationProperties {
    private static final Properties PROPERTIES = new Properties();
    static {
        PROPERTIES.setProperty("metly.systemJID", "metly@localhost");
        PROPERTIES.setProperty("metly.memcacheservers.host1", "localhost:20020");
        PROPERTIES.setProperty("metly.memcacheservers.host2", "localhost:20020");
        PROPERTIES.setProperty("metly.users_db", "metly_development.users");
        PROPERTIES.setProperty("metly.user_details_db", "metly_development.user_details");
        PROPERTIES.setProperty("metly.stanza_archives_db", "metly_development.stanza_archives");
        PROPERTIES.setProperty("metly.messages_archives_db", "metly_development.message_archives");
        PROPERTIES.setProperty("metly.login_locations_db", "metly_development.login_locations");
        PROPERTIES.setProperty("metly.user_connection_statuses_db", "metly_development.user_connection_statuses");
    }

    public static String getProperty(String property) {
        return PROPERTIES.getProperty(property);
    }

    public static String getProperty(String property, String value) {
        String ret = PROPERTIES.getProperty(property);
        if (ret == null) {
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
