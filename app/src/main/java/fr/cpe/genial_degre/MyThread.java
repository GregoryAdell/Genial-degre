package fr.cpe.genial_degre;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThread extends Thread {
    private MyThreadEventListener listener;
    private String message;
    private String serverAddress;
    private int serverPort;
    private DatagramSocket socket;
    private InetAddress inetAddress;
    private volatile boolean running = true; // Flag pour contrôler la fermeture du thread
    // Queue pour gérer les messages à envoyer
    private BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

    // Constructeur avec le listener
    public MyThread(String serverAddress, int serverPort, MyThreadEventListener listener) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.listener = listener;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        try {
            inetAddress = InetAddress.getByName(serverAddress);
            socket = new DatagramSocket();

            listener.onEventInMyThread("Connected to server.");

            byte[] buffer = new byte[1024];
            while (running) {

                // 1. Vérifie s'il y a un message à envoyer dans la queue
                String messageToSend = messageQueue.poll();
                if (messageToSend != null) {
                    byte[] messageBytes = messageToSend.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, inetAddress, serverPort);
                    socket.send(sendPacket);
                    listener.onEventInMyThread("Message sent: " + messageToSend);

                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket); // Bloque jusqu'à la réception d'un message
                    listener.onEventInMyThread("PROUUUUUUUUUUT.");

                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    listener.onEventInMyThread("Received from server: " + receivedMessage);
                }

                // 2. Écoute pour les réponses du serveur


            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    // Méthode pour ajouter un message à la queue d'envoi
    public void sendMessage(String message) {
        messageQueue.add(message);
    }

    // Méthode pour arrêter le thread proprement
    public void stopThread() {
        running = false;
        this.interrupt(); // Interrompt le thread s'il est bloqué
    }
}
