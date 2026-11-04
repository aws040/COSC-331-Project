import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        String ip = getLocalNetworkIp();
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        System.out.print("Enter port to host on: ");
        int port = 0;
        while (!valid) {
            if (scanner.hasNextInt()) {
                port = scanner.nextInt();
                if (port < 0 || port > 65535) {
                    System.out.println("Invalid port");
                    System.out.print("Enter port to host on: ");
                } else {
                    valid = true;
                }
            } else {
                System.out.println("Invalid port");
                System.out.print("Enter port to host on: ");
                scanner.nextLine();
            }
        }
        scanner.nextLine();
        try (DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName(ip))) {
            byte[] buf = new byte[1024];

            System.out.printf("Server listening on IP: %s:%d%n", ip, port);
            final DatagramPacket[] receivePacket = {new DatagramPacket(buf, buf.length)};
            socket.receive(receivePacket[0]);
            InetAddress clientAddress = receivePacket[0].getAddress();
            int clientPort = receivePacket[0].getPort();
            System.out.printf("Client found at address: %s:%d%n", clientAddress.getHostAddress(), clientPort);
            PacketHelper helper = new PacketHelper();
            helper.PacketSender(socket, "Connection setup", clientAddress, clientPort);
            helper.PacketReceiver(socket, buf, receivePacket);
            while (true) {
                if (helper.PacketSender(socket, scanner, clientAddress, clientPort)) break;
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

    public static String getLocalNetworkIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // Skip loopback interfaces (127.0.0.1) and inactive connections
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // We only want IPv4 addresses (e.g., 192.168.x.x)
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching IP: " + e.getMessage());
        }
        return "127.0.0.1"; // Fallback if nothing else is found
    }
}