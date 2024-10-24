package com.csi.utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class Utility {
	
	public String serverIpCommand() {
		 String command = "sh -c \"ip -4 addr show | grep -oP '(?<=inet\\s)\\d+(\\.\\d+){3}' | grep -v '127.0.0.1'\"";
	        Process p;
	        String ipAddr="";
	        try {
	            p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", "ip -4 addr show | grep -oP '(?<=inet\\s)\\d+(\\.\\d+){3}' | grep -v '127.0.0.1'"});
	            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
	            String line;
	            StringBuilder ip = new StringBuilder();
	            
	            // Read the output and append each line
	            while ((line = br.readLine()) != null) {
	                ip.append(line);
	            }

	            // Update the cpuUsage object with the IP address
	            ipAddr =ip.toString();

	            p.waitFor();
	            p.destroy();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		return ipAddr;
	}

}
