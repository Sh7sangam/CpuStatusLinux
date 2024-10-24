package com.csi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DiskUtilization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filesystem;
    private String size;
    private String used;
    private String available;
    private String usePercent;
    private String mountedOn;
    
    private String serverIp;

    // Getters and Setters
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

    public String getFilesystem() {
        return filesystem;
    }

    public void setFilesystem(String filesystem) {
        this.filesystem = filesystem;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getUsed() {
        return used;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getUsePercent() {
        return usePercent;
    }

    public void setUsePercent(String usePercent) {
        this.usePercent = usePercent;
    }

    public String getMountedOn() {
        return mountedOn;
    }

    public void setMountedOn(String mountedOn) {
        this.mountedOn = mountedOn;
    }
}
