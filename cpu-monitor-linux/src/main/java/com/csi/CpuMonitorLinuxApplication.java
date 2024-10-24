package com.csi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class CpuMonitorLinuxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CpuMonitorLinuxApplication.class, args);
    }

    @Override
    public void run(String... args) {
        
    }
}
