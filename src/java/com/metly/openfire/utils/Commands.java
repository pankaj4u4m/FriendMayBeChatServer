package com.metly.openfire.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

public class Commands {
	
    private static final Map<String, String> DESCRIPTION = new HashMap< String, String >();
    private static final Map<String, String> COMMANDS = new HashMap< String, String >();
    
//    public static final String HELP = "help";
    public static final String CONNECT = "connect";
//    public static final String BLOCK = "block";
//    public static final String REMEMBER = "remember";
//    public static final String FORGET = "forget";
    public static final String DISCONNECT = "disconnect";
    
    
    static {
//        COMMANDS.put("\\help", Commands.HELP);
//        COMMANDS.put("\\h", Commands.HELP);
//        DESCRIPTION.put(Commands.HELP, "\\h -List all available commands");
        COMMANDS.put("\\C", Commands.CONNECT);
        COMMANDS.put("\\c", Commands.CONNECT);
        DESCRIPTION.put(Commands.CONNECT, "\\c -Connect to an anonymous user");
//        COMMANDS.put("\\block", Commands.BLOCK); //removed from list as it needs reason too
//        COMMANDS.put("\\b", Commands.BLOCK);
//        DESCRIPTION.put(Commands.BLOCK, "\\b:\"reason\" -Block this user");
//        COMMANDS.put("\\remember", Commands.REMEMBER); //removed from here as it need name
//        COMMANDS.put("\\r", Commands.REMEMBER);
//        DESCRIPTION.put(Commands.REMEMBER, "\\r -Remember this user");
//        COMMANDS.put("\\forget", Commands.FORGET);
//        COMMANDS.put("\\f", Commands.FORGET);
//        DESCRIPTION.put(Commands.FORGET, "\\f -Forget this user");
        COMMANDS.put("\\D", Commands.DISCONNECT);
        COMMANDS.put("\\d", Commands.DISCONNECT);
        DESCRIPTION.put(Commands.DISCONNECT, "\\d -Disconnect this user");
        
    }

    public static boolean isCommand(String command) {
        return getCommand(command) != null;
    }
    
    public static String getCommand(String command){
        String cmd = COMMANDS.get(command);
//        if( cmd == null){
//        	if(command.startsWith("\\r")){
//        		return Commands.REMEMBER;
//        	} else if (command.startsWith("\\b" )){
//        		return Commands.BLOCK;
//        	}
//        }
        return cmd;
    }
    
    public static String getCommandHelp(){
        StringBuilder builder = new StringBuilder("\n");
        for(Entry< String, String > entry: DESCRIPTION.entrySet()){
            builder.append(entry.getValue()).append("\n");
        }
        return builder.toString();
    }
}
