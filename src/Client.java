import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
        Scanner scanner = new Scanner(System.in)) {
            byte[] buf = new byte[1024];

            System.out.print("Please enter your IP address: ");
            String ip = scanner.nextLine();
            InetAddress serverAddress;
            int serverPort;
            if (ip.contains(":")) {
                serverAddress = InetAddress.getByName(ip.split(":")[0]);
                serverPort = Integer.parseInt(ip.split(":")[1]);
            } else {
                serverAddress = InetAddress.getByName(ip);
                System.out.print("Please enter your port number: ");
                serverPort = scanner.nextInt();
            }
            PacketHelper helper = new PacketHelper();
            helper.PacketSender(socket, "Connection setup", serverAddress, serverPort);
            final DatagramPacket[] receivePacket = {new DatagramPacket(buf, buf.length)};
            helper.PacketReceiver(socket, buf, receivePacket);
            while (true) {
                if (helper.PacketSender(socket, scanner, serverAddress, serverPort)) break;
            }
        } catch (SocketException e) {
            System.out.printf("Socket Exception %s", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
}