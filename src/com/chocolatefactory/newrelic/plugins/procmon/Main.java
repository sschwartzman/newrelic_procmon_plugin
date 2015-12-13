package com.chocolatefactory.newrelic.plugins.procmon;

import java.io.File;
import java.net.InetAddress;

import com.newrelic.metrics.publish.Runner;
import com.newrelic.metrics.publish.configuration.ConfigurationException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Main {	
	
	public static final String kProcmonGuid = "com.chocolatefactory.newrelic.plugins.procmon";
	public static final String kAgentVersion = "0.0.1";
	
    public static void main(String[] args) throws Exception {
    	Runner runner = new Runner();
    	
    	Config pluginConfig = ConfigFactory.parseFile(new File("config/ProcmonPlugin.config"));
		String thisOS, thisName, thisLocation;
		Boolean isDebug;
		
		if (pluginConfig.hasPath("debug")) {
			isDebug = pluginConfig.getBoolean("debug");
		} else {
			isDebug = false;
		}
		
		if (pluginConfig.hasPath("name") && !pluginConfig.getString("name").equals("auto")) {
			thisName = pluginConfig.getString("name").toLowerCase();
		} else {
			thisName = InetAddress.getLocalHost().getHostName();
		}
		
		if (pluginConfig.hasPath("OS") && !pluginConfig.getString("OS").equals("auto")) {
			thisOS = pluginConfig.getString("OS").toLowerCase();
		} else {
			thisOS = System.getProperty("os.name").toLowerCase();
		}

		
		if (!pluginConfig.atPath("PID_Location").isEmpty()) {
			thisLocation = pluginConfig.getString("PID_Location").toLowerCase();
		} else {
				thisLocation = "";
				System.out.println("Error with configuration: PID Files Location is not configured");
				System.exit (-1);
			}

		if (!pluginConfig.atPath("PID_Files").isEmpty()) {
			for (String thisCommand : pluginConfig.getStringList("PID_Files")) {
				runner.register(new Procmon(kProcmonGuid, kAgentVersion, thisName, thisOS, thisLocation, thisCommand, isDebug));
			}
		} else {
				System.out.println("Error with configuration: no processes or PID Files configured");
				System.exit (-1);
			}
    	
		if (isDebug) {
			System.out.println("OS: " + thisOS);
			System.out.println("Name: " + thisName);
		}
		
		try {
	    	//Never returns
	    	runner.setupAndRun();
		} catch (ConfigurationException e) {
			e.printStackTrace();
    		System.err.println("Error configuring");
    		System.exit(-1);
		}
    	
    }
    
}
