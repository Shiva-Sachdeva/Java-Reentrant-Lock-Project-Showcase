package com.example.reentrant_lock_demo.controller;

import com.example.reentrant_lock_demo.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/counter")
public class CounterController {

    @Autowired
    private CounterService counterService;

    @GetMapping("/unsafe-test")
    public String testWithoutLock(@RequestParam(defaultValue = "10") int threads) throws InterruptedException {
        counterService.reset();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> counterService.incrementWithoutLock());
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        return "Final Counter (no lock) : " + counterService.getCounter();
    }


    @GetMapping("/safe-test")
    public String testWithLock(@RequestParam(defaultValue = "10") int threads) throws InterruptedException {
        counterService.reset();
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            executor.submit(counterService::incrementWithLock);
        }
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        return "Final counter (with lock): " + counterService.getCounter();
    }


    @GetMapping("/status")
    public Map<String, Object> getCounterStatus() {
        return Map.of(
                "counterValue", counterService.getCounter(),
                "thread", Thread.currentThread().getName(),
                "timestamp", System.currentTimeMillis()
        );
    }
}
