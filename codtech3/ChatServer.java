import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 12345;
    private static Set<Socket> clientSockets = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server started. Waiting for clients on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Start a new thread for each connected client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    // Inner class to handle each client
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    broadcastMessage("Client [" + socket.getInetAddress() + "]: " + message);
                }
            } catch (IOException e) {
                System.err.println("Connection error with client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                    clientSockets.remove(socket);
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

        // Send message to all connected clients
        private void broadcastMessage(String message) {
            for (Socket s : clientSockets) {
                try {
                    PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
                    writer.println(message);
                } catch (IOException e) {
                    System.err.println("Broadcast failed: " + e.getMessage());
                }
            }
        }
    }
}
