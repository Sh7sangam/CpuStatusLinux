package com.csi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.csi.Repo.MemoryUtilizationRepository;
import com.csi.model.MemoryUtilization;
import com.csi.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryUtilizationService {

    private static final Logger logger = LoggerFactory.getLogger(MemoryUtilizationService.class);

    private final MemoryUtilizationRepository memoryUtilizationRepository;
    private final List<MemoryUtilization> memoryUtilizationBatch = new ArrayList<>();
    
    @Autowired
    private Utility utility;

    @Autowired
    public MemoryUtilizationService(MemoryUtilizationRepository memoryUtilizationRepository) {
        this.memoryUtilizationRepository = memoryUtilizationRepository;
    }

    // Run every 3 seconds to collect data
    @Scheduled(fixedRate = 3000)
    public void collectMemoryData() {
        logger.info("Starting memory data collection...");
        getMemoryUtilization();
    }

    // Run every 60 seconds to insert the collected data in bulk
    @Scheduled(fixedRate = 60000)
    public void bulkInsertMemoryData() {
        if (!memoryUtilizationBatch.isEmpty()) {
            try {
                memoryUtilizationRepository.saveAll(memoryUtilizationBatch); // Bulk insert
                logger.info("Bulk insert successful: {} entries inserted.", memoryUtilizationBatch.size());
            } catch (Exception e) {
                logger.error("Error during bulk insert: ", e);
            } finally {
                memoryUtilizationBatch.clear(); // Clear the batch after insert
                logger.info("Memory utilization batch cleared after insert.");
            }
        } else {
            logger.info("No data to insert. Batch is empty.");
        }
    }

    // Method to get memory utilization data
    public void getMemoryUtilization() {
        String line = "";
        try {
            logger.info("Executing 'free -m' command to retrieve memory utilization...");
            // Execute the 'free -m' command to get memory usage data
            Process process = Runtime.getRuntime().exec(new String[]{"bash", "-c", "free -m"});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Skip the header line
            reader.readLine();

            // Read memory data (Mem: line)
            if ((line = reader.readLine()) != null) {
                String[] memoryData = line.split("\\s+");
                if (memoryData.length >= 4) { // Ensure enough data is present
                    double totalMemoryGB = Double.parseDouble(memoryData[1]) / 1024; // Convert MB to GB
                    double usedMemoryGB = Double.parseDouble(memoryData[2]) / 1024;  // Convert MB to GB
                    double freeMemoryGB = Double.parseDouble(memoryData[3]) / 1024;  // Convert MB to GB
                    
                    // Calculate percentage of used memory
                    double percentUsedMemory = (usedMemoryGB / totalMemoryGB) * 100;

                    // Read swap memory data (Swap: line)
                    String swapLine = reader.readLine(); // This should be the swap memory line
                    if (swapLine != null && swapLine.contains("Swap:")) {
                        String[] swapData = swapLine.split("\\s+");
                        if (swapData.length >= 4) {
                            double totalSwapGB = Double.parseDouble(swapData[1]) / 1024; // Convert MB to GB
                            double usedSwapGB = Double.parseDouble(swapData[2]) / 1024;  // Convert MB to GB
                            double freeSwapGB = Double.parseDouble(swapData[3]) / 1024;  // Convert MB to GB

                            // Create and populate MemoryUtilization object
                            MemoryUtilization memoryUtilization = new MemoryUtilization();
                            memoryUtilization.setTotalMemoryGB(totalMemoryGB);
                            memoryUtilization.setUsedMemoryGB(usedMemoryGB);
                            memoryUtilization.setFreeMemoryGB(freeMemoryGB);
                            memoryUtilization.setPercentUsedMemory(percentUsedMemory);
                            memoryUtilization.setTotalSwapGB(totalSwapGB);
                            memoryUtilization.setUsedSwapGB(usedSwapGB);
                            memoryUtilization.setFreeSwapGB(freeSwapGB);
                            memoryUtilization.setServerIp(utility.serverIpCommand());

                            // Add the memory utilization data to the batch
                            memoryUtilizationBatch.add(memoryUtilization);
                            logger.info("Memory utilization data added to batch.");

                        } else {
                            logger.warn("Insufficient data in swap memory output.");
                        }
                    } else {
                        logger.warn("Unable to read swap memory information.");
                    }
                } else {
                    logger.warn("Insufficient data in memory output.");
                }
            } else {
                logger.error("Unable to read memory information.");
            }

            process.waitFor();
            process.destroy();
            logger.info("Memory utilization data collection completed.");

        } catch (Exception e) {
            logger.error("Error during memory utilization data retrieval: ", e);
        }
    }
}
