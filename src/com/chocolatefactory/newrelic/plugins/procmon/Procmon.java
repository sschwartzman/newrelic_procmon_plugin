package com.chocolatefactory.newrelic.plugins.procmon;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.newrelic.metrics.publish.Agent;

public class Procmon extends Agent {
	
	public static final String kProcCount = "Process Count";
	public static final String kMemUsage = "Memory Usage";
	public static final String kCPUTime = "CPU Time";
	public static final String kProcStatus = "Process Status";
	public static final String kProcCountType = "processes";
	public static final String kMemUsageType = "kb";
	public static final String kCPUTimeType = "sec";
	public static final String kProcStatusType = "status";
	// public static final String kMetricPrefix = "Component/";
	
	public static final Map<String, String> kMetricTypes = new HashMap<String,String>() {
		private static final long serialVersionUID = 1L;
		@Override
	    public String get(Object key) {
	        if(!containsKey(key))
	            return "value";
	        return super.get(key);
	    }
	{
	    put(kProcCount, "processes");
	    put(kMemUsage, "kb");
	    put(kCPUTime, "seconds");
	    put(kProcStatus, "status");
	}};
			
	String command, hostname, ostype, name;
	Boolean debug;
    
	public Procmon(String guid, String agentversion, String hostname, String ostype, String command, Boolean debug) {
		super(guid, agentversion);
		this.command = command;
		this.ostype = ostype;
		this.debug = debug;
		this.hostname = hostname;
		this.name = hostname + " - " + command;
	}

	@Override
	public String getComponentHumanLabel() {
		return this.name;
	}
	
	@Override
	public void pollCycle() {
		if(this.ostype.indexOf("win") >= 0) {
			if (this.debug) {
				System.out.println("Currently checking Windows process: " + this.command);
			}
			HashMap <String, Number> outputHash = winProcCommand();
			for (Entry<String, Number> thisMetric : outputHash.entrySet()) {
			    String key = thisMetric.getKey();
			    Number value = thisMetric.getValue();
			    String type = kMetricTypes.get(key);
			    //reportMetric(kMetricPrefix + key, type, value); 
			    reportMetric(key, type, value);  
			}
		}
	}
	
	private HashMap <String, Number> winProcCommand() {
		int procCount = 0;
		int memUsage = 0;
		int cpuTime = 0;
		
		String line;
		String[] headers = null;
		HashMap<String, Number> results = new HashMap<String, Number>();
		Boolean firstLine = true;
		String taskString = "tasklist /fo CSV /v /fi \"imagename eq " + this.command + "\"";
		try {
			Process tasklist = Runtime.getRuntime().exec(taskString);
			BufferedReader tasklistOutput = new BufferedReader(new InputStreamReader(tasklist.getInputStream()));
			
			while ((line = tasklistOutput.readLine()) != null) {
				line = line.replaceAll("\",\"", "%").replaceAll("\"", "");
				if(firstLine) {
					if(line.startsWith("INFO: No tasks")) {
						firstLine = false;
						break;
					} else {
						headers = line.split("%");
						firstLine = false;
					}
				} else {
					procCount++;
					String[] thisProc = line.split("%");
					for(int i = 0; i < thisProc.length; i++) {
						if(this.debug) {
							System.out.println(headers[i] + ": " + thisProc[i]);
						}
						if(headers[i].equals("Mem Usage")) {
							String thisMem = thisProc[i].substring(0, thisProc[i].length() - 2);
							memUsage += NumberFormat.getNumberInstance(java.util.Locale.US).parse(thisMem).intValue();
						} else if(headers[i].equals("CPU Time")) {
							String[] splitTime = thisProc[i].split(":");
							int hours_sec = Integer.valueOf(splitTime[0]) * 3600;
							int minutes_sec = Integer.valueOf(splitTime[1]) * 60;
							int seconds = Integer.valueOf(splitTime[2]);
							cpuTime += (hours_sec + minutes_sec + seconds);
						}
					}
				}
		    }
			
			if (this.debug) {
				System.out.println(kProcCount + ": " + procCount);
				System.out.println(kMemUsage + ": " + memUsage);
				System.out.println(kCPUTime + ": " + cpuTime);
			}
			
			results.put(kProcCount, procCount);
			results.put(kMemUsage, memUsage);
			results.put(kCPUTime, cpuTime);
			if (procCount > 0) {
				results.put(kProcStatus, 1);
			} else {
				results.put(kProcStatus, 3);
			}
			
			tasklistOutput.close();
		} catch (Exception e) {
			System.out.println("Encountered an error attempting to check process " + this.command);
			e.printStackTrace();
		} 
		
		return results;
	}	
}

