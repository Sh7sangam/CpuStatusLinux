package com.csi.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.csi.model.MemoryUtilization;

@Repository
public interface MemoryUtilizationRepository extends JpaRepository<MemoryUtilization, Long> {
}
