package com.softwaremill;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.softwaremill.Stats.sleep;
import static com.softwaremill.Stats.startStatsThread;

public class Synchronized {
    public static void main(String[] args) throws InterruptedException {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());
        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 1000; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                new Test().test();
            });
        }

        e.shutdown();
        e.awaitTermination(1, TimeUnit.DAYS);
    }

    static class Test {
        synchronized void test() {
            sleep(1000);
        }
    }
}
