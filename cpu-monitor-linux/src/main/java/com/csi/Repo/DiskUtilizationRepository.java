package com.csi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.csi.model.DiskUtilization;

public interface DiskUtilizationRepository extends JpaRepository<DiskUtilization, Long> {
}
