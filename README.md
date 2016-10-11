# newrelic_procmon_plugin
Process Monitor Plugin for New Relic

**Getting Started**

A New Relic Plugin for monitoring Windows processes and services availability and resource utilization using a PID File. The Plugin searches a directory and subdirectories, specified in the PID_Location property, for the listed PID files specified in the PID_Files array.

**Overview**

- The plugin is for Windows and uses Windows Powershell commands.

- It is written in Java and requires an installed JRE 1.6 or higher. 

- It assumes an existing PID (Process Identifier) file(s) created by the process launcher.

- The PID file contains a single row with the numeric identifier for the process. You can read more about that here, https://en.wikipedia.org/wiki/Process_identifier.

- When a matching PID File is found the PID value is read and a Windows command is issued to determine if the process with that ID is running and, if it is, statistics are collected for that process.

**Installing**

1. Download and install the Repo to your Windows file system
2. Edit the config/ProcmonPlugin.config file:
  1. specify the top-level directory to search in the PID_Location attribute. 
  2. specify a list of PID file names, in the PID_FILES[] attribute, that contain the PID values to use for process search
3. Create a config/newrelic.properties by copying the template_newrelic.properties.
  1. Edit the newrelic.properties and replace the YOUR_KEY_GOES_HERE with your New Relic license key which you can find on your New Relic Account Settings page. 
4. Start the monitor by running the executable JAR file, NewRelicProcMon.jar (e.g. java -jar NewRelicProcMon.jar) 
