package com.csi.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.csi.Repo.CpuUsageRepository;
import com.csi.model.CpuUsage;
import com.csi.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TestProg {

    private static final Logger logger = LoggerFactory.getLogger(TestProg.class);

    private final CpuUsageRepository cpuUsageRepository;
    private final List<CpuUsage> cpuUsageBatch = new ArrayList<>();
    
    @Autowired
    private Utility utility;

    @Autowired
    public TestProg(CpuUsageRepository cpuUsageRepository) {
        this.cpuUsageRepository = cpuUsageRepository;
    }

    // Method to execute all operations and add to the list
    public void monitorCpu() {
        CpuUsage cpuUsage = new CpuUsage(); 
        
        // Set timestamp once
        cpuUsage.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        logger.info("Starting CPU monitoring at {}", cpuUsage.getTimestamp());

        // Execute command and update the cpuUsage object
        executeCommand("nproc", cpuUsage);

        // Calculate CPU usage and update the same cpuUsage object
        calculateCPUUsage(cpuUsage);

        // Calculate load average and update the same cpuUsage object
        calculateLoadAverage(cpuUsage);

        // Add the CpuUsage object to the batch list
        cpuUsageBatch.add(cpuUsage);
        logger.info("CPU monitoring data added to batch.");
    }

    // Run every 3 seconds to collect data
    @Scheduled(fixedRate = 3000)
    public void collectCpuData() {
        logger.info("Collecting CPU data...");
        monitorCpu();
    }

    // Run every 60 seconds to insert the collected data in bulk
    @Scheduled(fixedRate = 60000)
    public void bulkInsertCpuData() {
        if (!cpuUsageBatch.isEmpty()) {
            try {
                cpuUsageRepository.saveAll(cpuUsageBatch); // Bulk insert
                logger.info("Bulk insert successful: {} entries inserted.", cpuUsageBatch.size());
            } catch (Exception e) {
                logger.error("Error during bulk insert: ", e);
            } finally {
                cpuUsageBatch.clear(); // Clear the batch after insert
                logger.info("CPU usage batch cleared after insert.");
            }
        } else {
            logger.info("No data to insert. Batch is empty.");
        }
    }

    // Method to execute a command and print its output
    public void executeCommand(String command, CpuUsage cpuUsage) {
        String outputLine = "";
        Process p;
        try {
            logger.info("Executing command: {}", command);
            p = Runtime.getRuntime().exec(new String[]{"bash", "-c", command});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while ((outputLine = br.readLine()) != null) {
                logger.info("Command output: {}", outputLine);
                int numCores = Integer.parseInt(outputLine);
                cpuUsage.setNumberOfCups(numCores);  // Update cpuUsage object with the number of CPUs
            }

            // Set server IP using utility
            cpuUsage.setServerIp(utility.serverIpCommand());
            logger.info("Server IP set: {}", cpuUsage.getServerIp());

            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            logger.error("Error executing command: {}", command, e);
        }
    }

    // Method to calculate CPU Idle %, User %, and System % from /proc/stat
    public void calculateCPUUsage(CpuUsage cpuUsage) {
        String cpuStatLine = "";
        try {
            logger.info("Calculating CPU usage from /proc/stat...");
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "cat /proc/stat | grep '^cpu '"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            if ((cpuStatLine = br.readLine()) != null) {
                String[] cpuData = cpuStatLine.split("\\s+");

                long user = Long.parseLong(cpuData[1]);
                long system = Long.parseLong(cpuData[3]);
                long idle = Long.parseLong(cpuData[4]);
                long total = user + system + idle;

                double userPerc = (user / (double) total) * 100;
                double systemPerc = (system / (double) total) * 100;
                double idlePerc = (idle / (double) total) * 100;

                // Update the cpuUsage object
                cpuUsage.setUserPerc(userPerc);
                cpuUsage.setSystemPerc(systemPerc);
                cpuUsage.setIdlePerc(idlePerc);

                logger.info("CPU User %: {:.2f}, System %: {:.2f}, Idle %: {:.2f}", userPerc, systemPerc, idlePerc);
            }

            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            logger.error("Error calculating CPU usage", e);
        }
    }

    // Method to calculate the Average Load and Peak Load
    public void calculateLoadAverage(CpuUsage cpuUsage) {
        String loadAvgLine = "";
        try {
            logger.info("Calculating load average from uptime...");
            Process p = Runtime.getRuntime().exec(new String[]{"bash", "-c", "uptime"});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            if ((loadAvgLine = br.readLine()) != null) {
                String[] parts = loadAvgLine.split("load average: ");
                if (parts.length > 1) {
                    String[] loadAverages = parts[1].split(", ");
                    double avg1 = Double.parseDouble(loadAverages[0].trim());
                    double avg5 = Double.parseDouble(loadAverages[1].trim());
                    double avg15 = Double.parseDouble(loadAverages[2].trim());

                    double avgLoad = (avg1 + avg5 + avg15) / 3;
                    double peakLoad = Math.max(avg1, Math.max(avg5, avg15));

                    // Update the cpuUsage object
                    cpuUsage.setAverageLoad(avgLoad);
                    cpuUsage.setPeakLoad(peakLoad);

                    logger.info("Average Load (15 min): {:.2f}, Peak Load: {:.2f}", avgLoad, peakLoad);
                }
            }

            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            logger.error("Error calculating load average", e);
        }
    }
}
