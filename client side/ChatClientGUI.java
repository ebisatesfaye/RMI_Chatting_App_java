import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClientGUI {
    // The ChatClient instance used to communicate with the server.
    private static ChatClient client;
    // The text area that displays chat messages.
    static JTextArea chatArea;
    // The text field for user input.
    private static JTextField messageField;
    
    public static void main(String[] args) {
        // Check for command-line arguments: username and server IP.
        if (args.length < 2) {
            System.out.println("Usage: java ChatClientGUI <username> <server-ip>");
            return;
        }
        String username = args[0];
        String serverIP = args[1];
        
        // Connect to the RMI server by creating an instance of ChatClient.
        try {
            client = new ChatClient(username, serverIP);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to server!");
            return;
        }
        
        // Build the GUI.
        JFrame frame = new JFrame("Chat - " + username);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Create and add the chat display area.
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        
        // Create a panel for message input.
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        // Action for the send button.
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Allow sending messages by pressing Enter in the text field.
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        frame.setVisible(true);
    }
    
    // Method to send a message.
    private static void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            // Append the sent message to the chat area.
            appendMessage("Me: " + message);
            messageField.setText("");
        }
    }
    
    // This static method is used by ChatClient's receiveMessage to update the chat window.
    public static void appendMessage(String message) {
        // Ensure thread safety using SwingUtilities.invokeLater.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatArea.append(message + "\n");
            }
        });
    }
}
