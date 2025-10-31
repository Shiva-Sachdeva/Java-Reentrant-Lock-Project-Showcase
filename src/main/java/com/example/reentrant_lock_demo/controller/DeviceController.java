package com.example.reentrant_lock_demo.controller;

import com.example.reentrant_lock_demo.model.Device;
import com.example.reentrant_lock_demo.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;
    private final ExecutorService executor = Executors.newFixedThreadPool(7);

    @GetMapping("/status")
    public Object getStatus() {
        return deviceService.getAllDevices();
    }

    @GetMapping("/usage")
    public int getLicenseUsage() {
        return deviceService.getLicenseUsage();
    }

    @PostMapping("/enable")
    public String enableDevices(@RequestBody List<String> ids, @RequestParam String user) {
        deviceService.enableDevices(ids, user);
        return "Enabled devices: " + ids + " | Current usage=" + deviceService.getLicenseUsage();
    }

    @PostMapping("/disable")
    public String disableDevices(@RequestBody List<String> ids, @RequestParam String user) {
        deviceService.disableDevices(ids, user);
        return "Disabled devices: " + ids + " | Current usage=" + deviceService.getLicenseUsage();
    }

    @PostMapping("/delete")
    public String deleteDevices(@RequestBody List<String> ids, @RequestParam String user) {
        deviceService.deleteDevices(ids, user);
        return "Deleted devices: " + ids + " | Current usage=" + deviceService.getLicenseUsage();
    }

    @PostMapping("/add")
    public ResponseEntity<String> addDevices(@RequestBody Map<String, List<Device>> request, @RequestParam String user) {
        List<Device> devices = request.get("devices");
        deviceService.addDevices(devices, user);
        return ResponseEntity.ok("Devices added successfully.");
    }


    @GetMapping("/statusSummary")
    public ResponseEntity<Map<String, Long>> getStatusSummary() {
        return ResponseEntity.ok(deviceService.getStatusSummary());
    }

    @GetMapping("/testConcurrentOps")
    public String testConcurrentOps() {
        List<String> userADevices = Arrays.asList("D1", "D2");
        List<String> userBDevices = Arrays.asList("D2");
        List<String> userCDevices = Arrays.asList("D3");

        executor.submit(() -> deviceService.addDevices(Arrays.asList(
                new Device("D10", "NewDevice-1", true),
                new Device("D11", "NewDevice-2", false)
        ), "UserF"));
        executor.submit(() -> deviceService.enableDevices(userADevices, "UserA"));
        executor.submit(() -> deviceService.disableDevices(userBDevices, "UserB"));
        executor.submit(() -> deviceService.deleteDevices(userCDevices, "UserC"));
        executor.submit(() -> deviceService.enableDevices(Arrays.asList("D4"), "UserD"));
        executor.submit(() -> deviceService.disableDevices(Arrays.asList("D1"), "UserE"));
        executor.submit(() -> deviceService.disableDevices(Arrays.asList("D1"), "UserE"));
        executor.submit(() -> deviceService.addDevices(Arrays.asList(
                new Device("D12", "NewDevice-3", true),
                new Device("D13", "NewDevice-4", false),
                new Device("D14", "NewDevice-5", true)
        ), "UserG"));

        return "✅ Concurrent simulation started — check console logs for thread-safe behavior.";
    }
}
