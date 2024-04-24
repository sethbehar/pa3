import java.net.Socket;
import java.io.*;

public class Client {
    private Socket socket;
    private DataInputStream serverInput;
    private DataOutputStream serverOutput;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            System.out.println("Connected to the server.");

            serverInput = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            serverOutput = new DataOutputStream(socket.getOutputStream());

            interactWithServer();

            serverInput.close();
            serverOutput.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void interactWithServer() throws IOException {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        while (!line.equals("exit")) {
            line = serverInput.readUTF();
            if (line.toLowerCase().contains("hit or stand")) {
                System.out.println(line);
                line = console.readLine();
                serverOutput.writeUTF(line);
            } else {
                System.out.println(line);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Client <address> <port>");
        } else {
            new Client(args[0], Integer.parseInt(args[1]));
        }
    }
}
