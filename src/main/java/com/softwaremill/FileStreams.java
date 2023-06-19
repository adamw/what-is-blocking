package com.softwaremill;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.softwaremill.Stats.startStatsThread;

public class FileStreams {
    public static void main(String[] args) throws Exception {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());

        var f = Files.createTempFile("xyz", "txt").toFile();
        var output = new FileOutputStream(f);
        output.write(10);
        output.flush();

        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 50; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                try (var s = new FileInputStream(f)) {
                    System.out.println("GOT1: " + s.read());
                    System.out.println("GOT2: " + s.read());
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        Thread.sleep(3000);
        output.write(54);
        output.flush();
    }
}
