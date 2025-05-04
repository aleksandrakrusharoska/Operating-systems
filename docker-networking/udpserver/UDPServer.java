

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDPServer extends Thread {

    private int port;

    public UDPServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {

        try {
            // tuka se startuva serverot
            DatagramSocket datagramSocket = new DatagramSocket(port);
            byte[] buffer = new byte[256];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);

            while (true) {
                // Prv slucaj - login
                // Vtor slucaj - logout
                // Tret slucaj - bilo kakva poraka

                datagramSocket.receive(datagramPacket);
                String received = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println(received);

                if (received.equals("login")) {
                    String line = "logged in";
                    buffer = line.getBytes();
                } else if (received.equals("logout")) {
                    String line = "logged out";
                    buffer = line.getBytes();
                } else {
                    String line = "echo- " + received;
                    buffer = line.getBytes();
                }

                DatagramPacket datagramPacketToSend = new DatagramPacket(buffer, buffer.length, datagramPacket.getAddress(), datagramPacket.getPort());
                datagramSocket.send(datagramPacketToSend);

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public static void main(String[] args) {
        UDPServer udpServer = new UDPServer(9090);
        udpServer.start();
    }
}
