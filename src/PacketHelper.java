import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class PacketHelper {
    boolean running = true;
    void PacketSender(DatagramSocket socket, String input, InetAddress serverAddress, int serverPort) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(
                input.getBytes(),
                input.length(),
                serverAddress,
                serverPort);
        socket.send(sendPacket);
    }

    boolean PacketSender(DatagramSocket socket, Scanner scanner, InetAddress serverAddress, int serverPort) throws IOException {
        System.out.print("Enter message: ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
            System.out.println("Closing Chat...");
            DatagramPacket closingPacket = new DatagramPacket(
                    "@closingPacket@".getBytes(),
                    "@closingPacket@".length(),
                    serverAddress,
                    serverPort);
            socket.send(closingPacket);
            running = false;
            socket.close();
            return true;
        }
        DatagramPacket sendPacket = new DatagramPacket(
                input.getBytes(),
                input.length(),
                serverAddress,
                serverPort);
        socket.send(sendPacket);
        return false;
    }

    void PacketReceiver(DatagramSocket socket, byte[] buf, DatagramPacket[] receivePacket) throws IOException {

        Thread receiveThread = new Thread(() -> {
            try {
                while (running) {
                    MessageFormat(socket, buf, receivePacket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    private void MessageFormat(DatagramSocket socket, byte[] buf, DatagramPacket[] receivePacket) throws IOException {
        receivePacket[0] = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(receivePacket[0]);
           String message = new String(receivePacket[0].getData(), 0, receivePacket[0].getLength());
            if (message.equals("@closingPacket@")) {
                System.out.print("\r\033[2K");
                System.out.println("Partner has closed connection...");
                System.out.println("Shutting down program...");
                running = false;
                socket.close();
                System.exit(0);
            } else {
                System.out.print("\r\033[2K");
                System.out.println("[Partner]: " + message);
                System.out.print("Enter message: ");

            }
        } catch (SocketException e) {
            System.out.println("The connection has been closed");
        }
    }
}
