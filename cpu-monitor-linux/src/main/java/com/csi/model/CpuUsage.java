package com.csi.model;

import javax.persistence.*;

@Entity
@Table(name = "cpu_usage")
public class CpuUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numberOfCups;
	private double userPerc;
    private double systemPerc;
    private double idlePerc;
    private double averageLoad;
    
    private double peakLoad;
    
    private String timestamp;
    private String serverIp;

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

	public int getNumberOfCups() {
		return numberOfCups;
	}

	public void setNumberOfCups(int numberOfCups) {
		this.numberOfCups = numberOfCups;
	}

	public double getUserPerc() {
		return userPerc;
	}

	public void setUserPerc(double userPerc) {
		this.userPerc = userPerc;
	}

	public double getSystemPerc() {
		return systemPerc;
	}

	public void setSystemPerc(double systemPerc) {
		this.systemPerc = systemPerc;
	}

	public double getIdlePerc() {
		return idlePerc;
	}

	public void setIdlePerc(double idlePerc) {
		this.idlePerc = idlePerc;
	}

	public double getAverageLoad() {
		return averageLoad;
	}

	public void setAverageLoad(double averageLoad) {
		this.averageLoad = averageLoad;
	}

	public double getPeakLoad() {
		return peakLoad;
	}

	public void setPeakLoad(double peakLoad) {
		this.peakLoad = peakLoad;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
    
    

}