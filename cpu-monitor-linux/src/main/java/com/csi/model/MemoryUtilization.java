package com.csi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class MemoryUtilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generates ID
    private Long id;
    private double totalMemoryGB;
    private double usedMemoryGB;
    private double freeMemoryGB;
    private double percentUsedMemory;
    private double totalSwapGB;
    private double usedSwapGB;
    private double freeSwapGB;
    private String serverIp;

    // Getters and setters
	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalMemoryGB() {
        return totalMemoryGB;
    }

    public void setTotalMemoryGB(double totalMemoryGB) {
        this.totalMemoryGB = totalMemoryGB;
    }

    public double getUsedMemoryGB() {
        return usedMemoryGB;
    }

    public void setUsedMemoryGB(double usedMemoryGB) {
        this.usedMemoryGB = usedMemoryGB;
    }

    public double getFreeMemoryGB() {
        return freeMemoryGB;
    }

    public void setFreeMemoryGB(double freeMemoryGB) {
        this.freeMemoryGB = freeMemoryGB;
    }

    public double getPercentUsedMemory() {
        return percentUsedMemory;
    }

    public void setPercentUsedMemory(double percentUsedMemory) {
        this.percentUsedMemory = percentUsedMemory;
    }

    public double getTotalSwapGB() {
        return totalSwapGB;
    }

    public void setTotalSwapGB(double totalSwapGB) {
        this.totalSwapGB = totalSwapGB;
    }

    public double getUsedSwapGB() {
        return usedSwapGB;
    }

    public void setUsedSwapGB(double usedSwapGB) {
        this.usedSwapGB = usedSwapGB;
    }

    public double getFreeSwapGB() {
        return freeSwapGB;
    }

    public void setFreeSwapGB(double freeSwapGB) {
        this.freeSwapGB = freeSwapGB;
    }
}
