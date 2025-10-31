package com.example.reentrant_lock_demo.service;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class CounterService {

    @Getter
    private int counter = 0;
    private final ReentrantLock lock = new ReentrantLock(true); //Threads acquire the lock in the order they requested it (first-come-first-serve).
    private static final Logger log = LoggerFactory.getLogger(CounterService.class);

    /**
     * Increment without lock (unsafe)
     */
    public int incrementWithoutLock() {
        String thread = Thread.currentThread().getName();
        log.info("[{}] Trying to increment (NO LOCK)...", thread);
        int current = counter;
        try{
            Thread.sleep(100);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        counter = current + 1;
        log.info("[{}] Incremented (no lock): counter = {}", thread, counter);
        return counter;
    }


    /**
      *Increment with lock (safe)
     */
    public int incrementWithLock() {
        String thread = Thread.currentThread().getName();
        log.info("[{}] Waiting to acquire lock...", thread);
        lock.lock();
        try{
            log.info("[{}] Acquired lock ✅", thread);
            int current = counter;
            Thread.sleep(100);
            counter = current + 1;
            log.info("[{}] Incremented (with lock): counter = {}", thread, counter);
            return counter;
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        finally {
            lock.unlock();
            log.info("[{}] Released lock 🔓", thread);
        }
    }

    public void reset() {
        counter = 0;
        log.info("Counter reset to 0");
    }

}
