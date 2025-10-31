
package com.example.reentrant_lock_demo.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LicenseService {

    private static final Logger log = LoggerFactory.getLogger(LicenseService.class);
    @Getter
    private int currentUsage = 0;
    private final int totalLicenseCapacity = 10;


    public void consume(int count, String user) {
        String threadName = Thread.currentThread().getName();

        int available = totalLicenseCapacity - currentUsage;
        if (available < count) {
            log.warn("[{}] ❌ Not enough licenses. Requested={}, Available={}", user, count, available);
            throw new IllegalStateException("Insufficient license slots");
        }
        currentUsage += count;
        log.info("[{} | {}] ✅ Consumed {} licenses. Current usage = {}", user, threadName, count, currentUsage);
    }


    public void release(int count, String user) {
        String threadName = Thread.currentThread().getName();
        currentUsage -= count;
        if (currentUsage < 0) currentUsage = 0;
        log.info("[{} | {}] ♻️ Released {} license(s). Current usage={}", user, threadName, count, currentUsage);
    }


    public int getAvailable() {
        return totalLicenseCapacity - currentUsage;
    }

}
