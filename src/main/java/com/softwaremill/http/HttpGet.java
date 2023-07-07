package com.softwaremill.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.softwaremill.Stats.startStatsThread;

public class HttpGet {
    private final static Logger log = LoggerFactory.getLogger("Sockets");

    public static void main(String[] args) throws Exception {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());

        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 50; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                sendHttpThroughSocket();
            });
        }

        e.shutdown();
        e.awaitTermination(1, TimeUnit.DAYS);
    }

    private static void sendHttpThroughSocket() {
        // The hostname and port where the server is located
        String hostname = "127.0.0.1";
        int port = 8080;

        try {
            // Create a socket
            Socket socket = new Socket(hostname, port);

            // Create the output stream to send the HTTP request
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // Write the HTTP request to the output stream
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            wr.write("GET / HTTP/1.1\r\n");
            wr.write("Connection: Close\r\n");
            wr.write("\r\n");
            wr.flush();

            // Create the input stream to read the HTTP response
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Print out the response
            String line;
            while ((line = reader.readLine()) != null) {
                line.length(); // ignoring
                //log.info("GOT: " + line);
            }

            // Close the streams and the socket
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
