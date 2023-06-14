package com.softwaremill;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.softwaremill.Stats.sleep;
import static com.softwaremill.Stats.startStatsThread;

public class VirtualThreads {
    public static void main(String[] args) {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());
        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 1000; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                sleep(4000);});
        }

        sleep(5000);
    }
}
