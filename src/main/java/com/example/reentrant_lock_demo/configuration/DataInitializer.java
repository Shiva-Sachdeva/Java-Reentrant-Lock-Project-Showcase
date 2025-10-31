package com.example.reentrant_lock_demo.configuration;

import com.example.reentrant_lock_demo.model.Device;
import com.example.reentrant_lock_demo.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final DeviceService deviceService;

    @Autowired
    public DataInitializer(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void run(String... args) {
        List<Device> initialDevices = List.of(
                new Device("D001", "Switch-1", true),
                new Device("D002", "Router-1", false),
                new Device("D003", "Camera-1", true)
        );
        deviceService.addDevices(initialDevices, "System");
        System.out.println("✅ Initial devices loaded.");
        System.out.println("📊 Summary: " + deviceService.getStatusSummary());
    }
}
