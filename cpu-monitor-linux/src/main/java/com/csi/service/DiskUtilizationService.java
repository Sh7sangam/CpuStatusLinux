package com.csi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.csi.Repo.DiskUtilizationRepository;
import com.csi.model.DiskUtilization;
import com.csi.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class DiskUtilizationService {

    private static final Logger logger = LoggerFactory.getLogger(DiskUtilizationService.class);

    @Autowired
    private DiskUtilizationRepository diskUtilizationRepository; // Inject the repository
    
    private final List<DiskUtilization> diskUtilizationBatch = new ArrayList<>();
    
    @Autowired
    private Utility utility;

    // Run every 3 seconds to collect data
    @Scheduled(fixedRate = 3000)
    public void collectDiskData() {
        logger.info("Starting disk data collection...");
        
        List<DiskUtilization> currentDiskData = getDiskUtilization(); // Fetch disk utilization data
        if (!currentDiskData.isEmpty()) {
            diskUtilizationBatch.addAll(currentDiskData); // Add the fetched data to the batch
            logger.info("Disk data collected: {} entries added to batch.", currentDiskData.size());
        } else {
            logger.warn("No disk data collected in this iteration.");
        }
    }

    // Run every 60 seconds to insert the collected data in bulk
    @Scheduled(fixedRate = 60000)
    public void bulkInsertDiskData() {
        if (!diskUtilizationBatch.isEmpty()) {
            try {
                diskUtilizationRepository.saveAll(diskUtilizationBatch); // Bulk insert
                logger.info("Bulk insert successful: {} entries inserted.", diskUtilizationBatch.size());
            } catch (Exception e) {
                logger.error("Error during bulk insert: ", e);
            } finally {
                diskUtilizationBatch.clear(); // Clear the batch after insert
                logger.info("Disk utilization batch cleared after insert.");
            }
        } else {
            logger.info("No data to insert. Batch is empty.");
        }
    }

    // Method to fetch disk utilization data
    public List<DiskUtilization> getDiskUtilization() {
        List<DiskUtilization> diskUtilizationList = new ArrayList<>();

        try {
            logger.info("Executing 'df -kh' command to retrieve disk utilization...");
            // Create process to execute `df -kh` command
            ProcessBuilder processBuilder = new ProcessBuilder("df", "-kh");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Skip the first header line
            reader.readLine();

            // Process each line to extract disk utilization information
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 6) {
                    DiskUtilization diskUtilization = new DiskUtilization();
                    diskUtilization.setFilesystem(parts[0]);
                    diskUtilization.setSize(parts[1]);
                    diskUtilization.setUsed(parts[2]);
                    diskUtilization.setAvailable(parts[3]);
                    diskUtilization.setUsePercent(parts[4]);
                    diskUtilization.setMountedOn(parts[5]);
                    diskUtilization.setServerIp(utility.serverIpCommand());
                    
                    // Add each disk utilization record to the list
                    diskUtilizationList.add(diskUtilization);
                }
            }

            process.waitFor(); // Ensure the process completes
            process.destroy();  // Clean up the process
            logger.info("Disk utilization data successfully retrieved.");

        } catch (Exception e) {
            logger.error("Error during disk utilization data retrieval: ", e);
        }

        return diskUtilizationList; // Return the collected data
    }
}
