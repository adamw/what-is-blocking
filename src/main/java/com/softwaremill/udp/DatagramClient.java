package com.softwaremill.udp;

import com.softwaremill.Synchronized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.softwaremill.Stats.sleep;
import static com.softwaremill.Stats.startStatsThread;

public class DatagramClient {
    private final static Logger log = LoggerFactory.getLogger("Datagram");

    public static void main(String[] args) throws Exception {
        var started = new AtomicInteger(0);
        startStatsThread(unused -> "started: " + started.get());

        InetAddress IPAddress = InetAddress.getByName("localhost");

        var e = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 50; i++) {
            e.submit(() -> {
                started.incrementAndGet();
                try {
                    doSend(IPAddress);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }

        sleep(20000);
    }

    private static void doSend(InetAddress IPAddress) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        byte[] sendData;
        byte[] receiveData = new byte[1024];

        String sentence = "Hello from client!";
        sendData = sentence.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
        clientSocket.send(sendPacket);

        log.info("Sent ... ");

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        log.info("Received: " + modifiedSentence);
        clientSocket.close();
    }
}
