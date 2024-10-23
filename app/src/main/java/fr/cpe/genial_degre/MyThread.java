package fr.cpe.genial_degre;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class MyThread extends Thread {
    private String message;
    private String serverAddress;
    private int serverPort;
 2
    public MyThread(String message, String serverAddress, int serverPort) {
        this.message = message;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }
    @Override
    public void run() {
        try {
            InetAddress inetAddress = InetAddress.getByName(serverAddress);
            DatagramSocket socket = new DatagramSocket();
            byte[] messageBytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, inetAddress, serverPort);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}