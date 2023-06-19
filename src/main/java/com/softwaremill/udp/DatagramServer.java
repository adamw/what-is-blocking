package com.softwaremill.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class DatagramServer {
    public static void main(String[] args) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];

        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("RECEIVED: " + sentence);

            Thread.startVirtualThread(() -> {
                try {
                    Thread.sleep(3000L);

                    byte[] sendData = sentence.toUpperCase().getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    serverSocket.send(sendPacket);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
