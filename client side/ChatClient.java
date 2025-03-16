import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.Scanner;

public class ChatClient extends UnicastRemoteObject implements ChatClientInterface {

    private String name;
    private ChatServerInterface server;

    protected ChatClient(String name) throws RemoteException {
        this.name = name;
    }

    // Callback method invoked by the server.
    public void receiveMessage(String message) throws RemoteException {
        System.out.println(message);
    }

    public void startClient(String serverIP) {
        try {
            // Lookup the remote server using the provided IP address.
            server = (ChatServerInterface) Naming.lookup("rmi://" + serverIP + "/ChatServer");
            // Register this client with the server.
            server.registerClient(this);
            System.out.println("Connected to ChatServer at " + serverIP + ". Start chatting!");

            // Read user input and send messages.
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                server.broadcastMessage(name + ": " + message);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Expect command-line arguments: Client name and server IP.
            if (args.length < 2) {
                System.out.println("Usage: java ChatClient <ClientName> <ServerIPAddress>");
                System.exit(1);
            }
            String clientName = args[0];
            String serverIP = args[1];

            ChatClient client = new ChatClient(clientName);
            client.startClient(serverIP);
        } catch (RemoteException e) {
            System.err.println("RemoteException in client: " + e);
            e.printStackTrace();
        }
    }
}
