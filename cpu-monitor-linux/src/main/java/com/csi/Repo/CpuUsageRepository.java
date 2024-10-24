package com.csi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csi.model.CpuUsage;

public interface CpuUsageRepository extends JpaRepository<CpuUsage, Long> {
}
