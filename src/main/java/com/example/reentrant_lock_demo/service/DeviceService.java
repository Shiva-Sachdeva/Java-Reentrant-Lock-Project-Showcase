package com.example.reentrant_lock_demo.service;

import com.example.reentrant_lock_demo.model.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
public class DeviceService {

    private static final Logger log = LoggerFactory.getLogger(DeviceService.class);
    private final Map<String, Device> deviceRepo = new ConcurrentHashMap<>();
    private final LicenseService licenseService;
    private final ReentrantLock deviceLock = new ReentrantLock(true);

    public DeviceService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    // -------------------- ADD DEVICES --------------------
    public void addDevices(List<Device> devices, String user) {
        String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        
        log.info("[{} | {}] ⚙️ Trying to acquire lock for addDevices() - count={}", user, threadName, devices.size());
        deviceLock.lock();
        try {
            int enabledCount = (int) devices.stream().filter(Device::isEnabled).count();
            int available = licenseService.getAvailable();
            if (enabledCount > available) {
                log.warn("[{}] ❌ Not enough license slots. Requested={}, Available={}", user, enabledCount, available);
                throw new IllegalStateException("Insufficient license slots");
            }

            // Step 2: Add all devices
            for (Device d : devices) {
                deviceRepo.put(d.getDeviceId(), d);
                log.info("[{}] Added device {} ({})", user, d.getDeviceId(), d.isEnabled() ? "ENABLED" : "DISABLED");
            }

            // Step 3: Update license usage for enabled devices only
            if (enabledCount > 0) {
                licenseService.consume(enabledCount, user);
            }
        } finally {
            deviceLock.unlock();
            long end = System.currentTimeMillis();
            log.info("[{} | {}] 🔓 Released lock (duration={}ms)\n", user, threadName, (end - start));
        }
    }



    // -------------------- ENABLE DEVICES --------------------
    public void enableDevices(List<String> ids, String user) {
        String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        
        log.info("[{} | {}] ⚙️ Trying to acquire lock for enableDevices() - count={}", user, threadName, ids.size());
        deviceLock.lock();
        try {
            List<Device> devicesToEnable = ids.stream()
                    .map(deviceRepo::get)
                    .filter(Objects::nonNull)
                    .filter(d -> !d.isEnabled())
                    .collect(Collectors.toList());

            int requiredLicenses = devicesToEnable.size();
            int available = licenseService.getAvailable();
            if (requiredLicenses > available) {
                log.warn("[{}] ❌ Not enough license slots. Requested={}, Available={}", user, requiredLicenses, available);
                throw new IllegalStateException("Insufficient license slots");
            }

            devicesToEnable.forEach(d -> d.setEnabled(true));
            log.info("[{}] ✅ Enabled {} device(s): {}", user, requiredLicenses, ids);

            if (requiredLicenses > 0) {
                licenseService.consume(requiredLicenses, user);
            }
        } finally {
            deviceLock.unlock();
            long end = System.currentTimeMillis();
            log.info("[{} | {}] 🔓 Released lock (duration={}ms)\n", user, threadName, (end - start));
        }
    }



    // -------------------- DISABLE DEVICES --------------------
    public void disableDevices(List<String> ids, String user) {
        String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        
        log.info("[{} | {}] ⚙️ Trying to acquire lock for disableDevices() - count={}", user, threadName, ids.size());
        deviceLock.lock();
        try {
            List<Device> devicesToDisable = ids.stream()
                    .map(deviceRepo::get)
                    .filter(Objects::nonNull)
                    .filter(Device::isEnabled)
                    .collect(Collectors.toList());

            int toRelease = devicesToDisable.size();
            devicesToDisable.forEach(d -> d.setEnabled(false));

            log.info("[{}] ♻️ Disabled {} device(s): {}", user, toRelease, ids);
            
            if (toRelease > 0) {
                licenseService.release(toRelease, user);
            }
        } finally {
            deviceLock.unlock();
            long end = System.currentTimeMillis();
            log.info("[{} | {}] 🔓 Released lock (duration={}ms)\n", user, threadName, (end - start));
        }
    }



    // -------------------- DELETE DEVICES --------------------
    public void deleteDevices(List<String> ids, String user) {
        String threadName = Thread.currentThread().getName();
        long start = System.currentTimeMillis();
        
        log.info("[{} | {}] ⚙️ Trying to acquire lock for deleteDevices() - count={}", user, threadName, ids.size());
        deviceLock.lock();
        try {
            int enabledDeleted = 0;
            for (String id : ids) {
                Device d = deviceRepo.remove(id);
                if (d != null) {
                    if (d.isEnabled()) {
                        enabledDeleted++;
                        log.info("[{}] 🗑️ Deleted enabled device: {} (will release license)", user, id);
                    } else {
                        log.info("[{}] 🗑️ Deleted disabled device: {} (no license impact)", user, id);
                    }
                } else {
                    log.warn("[{}] ⚠️ Device not found for deletion: {}", user, id);
                }
            }

            // Release licenses only for enabled devices that were deleted
            if (enabledDeleted > 0) {
                licenseService.release(enabledDeleted, user);
                log.info("[{}] ♻️ Released {} license(s) due to enabled device deletion", user, enabledDeleted);
            }
        } finally {
            deviceLock.unlock();
            long end = System.currentTimeMillis();
            log.info("[{} | {}] 🔓 Released lock (duration={}ms)\n", user, threadName, (end - start));
        }
    }



    public Map<String, Long> getStatusSummary() {
        deviceLock.lock();
        try {
            long enabledCount = deviceRepo.values().stream().filter(Device::isEnabled).count();
            long disabledCount = deviceRepo.size() - enabledCount;

            Map<String, Long> summary = new HashMap<>();
            summary.put("enabledCount", enabledCount);
            summary.put("disabledCount", disabledCount);
            summary.put("currentLicenseUsage", (long) licenseService.getCurrentUsage());
            return summary;
        } finally {
            deviceLock.unlock();
        }
    }

    public Collection<Device> getAllDevices() {
        return deviceRepo.values();
    }

    public int getLicenseUsage() {
        deviceLock.lock();
        try {
            return licenseService.getCurrentUsage();
        } finally {
            deviceLock.unlock();
        }
    }
}
