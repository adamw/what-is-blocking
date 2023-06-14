package com.softwaremill;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.softwaremill.Stats.sleep;
import static com.softwaremill.Stats.startStatsThread;

public class Locks {
    public static void main(String[] args) {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());
        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 1000; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                new Test().test();
            });
        }

        sleep(5000);
    }

    static class Test {
        private ReentrantLock lock = new ReentrantLock();
        void test() {
            lock.tryLock();
            try {
                sleep(4000);
            } finally {
                lock.unlock();
            }
        }
    }
}
