package com.softwaremill;

import com.sun.management.HotSpotDiagnosticMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class Stats {
    private final static Logger log = LoggerFactory.getLogger("VirtualThreads");

    private final static Path dumpsDir;

    static {
        try {
            dumpsDir = Files.createTempDirectory("dumps");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void threadStats(Function<Void, String> custom) {
        Path dumpPath = dumpsDir.resolve("dump");

        try {
            var platform = ManagementFactory.getThreadMXBean().getThreadCount();

            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
            mxBean.dumpThreads(dumpPath.toString(), HotSpotDiagnosticMXBean.ThreadDumpFormat.TEXT_PLAIN);

            var virtual = Files.readAllLines(dumpPath).stream().filter(s -> s.startsWith("#") && s.contains("virtual")).count();
            //var virtual = new String(Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", String.format("cat %s | grep virtual | wc -l", dumpPath)}).getInputStream().readAllBytes()).trim();

            log.info("Platform threads: " + platform + "; virtual threads: " + virtual + "; " + custom.apply(null));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            dumpPath.toFile().delete();
        }
    }

    public static void startStatsThread(Function<Void, String> custom) {
        log.info("Starting the stats thread ...");
        threadStats(custom);
        var t = new Thread(() -> {
            while (true) {
                threadStats(custom);
                sleep(500L);
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
