package com.avinash.masterJava.virtualthreads;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class Task {

    public static void ioIntensive(int i){
        try {
            log.info("Task {} is starting", i);
            Thread.sleep(Duration.ofSeconds(2).toMillis());
            log.info("Task {} is completed", i);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
