import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class PacketHelper {
    void PacketSender(DatagramSocket socket, String setUp, InetAddress serverAddress, int serverPort) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(
                setUp.getBytes(),
                setUp.length(),
                serverAddress,
                serverPort);
        socket.send(sendPacket);
    }

    boolean PacketSender(DatagramSocket socket, Scanner scanner, InetAddress serverAddress, int serverPort) throws IOException {
        System.out.print("Enter message: ");
        String input = scanner.nextLine();
        if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
            System.out.println("Closing Chat...");
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
        final String[] message = {new String(receivePacket[0].getData(), 0, receivePacket[0].getLength())};

        Thread receiveThread = new Thread(() -> {
            try {
                while (true) {
                    MessageFormat(socket, buf, receivePacket, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        receiveThread.start();
    }

    private void MessageFormat(DatagramSocket socket, byte[] buf, DatagramPacket[] receivePacket, String[] message) throws IOException {
        receivePacket[0] = new DatagramPacket(buf, buf.length);

        socket.receive(receivePacket[0]);

        message[0] = new String(receivePacket[0].getData(), 0, receivePacket[0].getLength());
        System.out.print("\r\033[2K");
        System.out.println("[Partner]: " + message[0]);
        System.out.print("Enter message: ");
    }
}
