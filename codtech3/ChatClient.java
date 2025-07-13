import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {

    private static final String SERVER_IP = "localhost"; // Use "127.0.0.1" or server IP
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Connected to chat server.");

            // Thread to read messages from the server
            Thread readerThread = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.err.println("Disconnected from server.");
                }
            });

            readerThread.start();

            // Main thread reads user input and sends to server
            while (true) {
                String input = scanner.nextLine();
                out.println(input);
                if (input.equalsIgnoreCase("exit")) break;
            }

            socket.close();
            System.out.println("Disconnected.");
        } catch (IOException e) {
            System.err.println("Client Error: " + e.getMessage());
        }
    }
}
