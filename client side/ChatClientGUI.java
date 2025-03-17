import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatClientGUI {
    private static ChatClient client;
    static JPanel messageContainer;
    static JScrollPane scrollPane;
    private static JTextField messageField;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ChatClientGUI <username> <server-ip>");
            return;
        }
        String username = args[0];
        String serverIP = args[1];

        try {
            client = new ChatClient(username, serverIP);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to server!");
            return;
        }

        // Create the chat window
        JFrame frame = new JFrame("Chat - " + username);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // Chat messages container
        messageContainer = new JPanel();
        messageContainer.setLayout(new BoxLayout(messageContainer, BoxLayout.Y_AXIS));
        messageContainer.setBorder(new EmptyBorder(5, 10, 5, 10));
        messageContainer.setBackground(new Color(254, 230, 205));

        // Scroll pane for messages
        scrollPane = new JScrollPane(messageContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Input Panel for sending messages
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageField = new JTextField();
        messageField.setBackground(new Color(254, 230, 205));
        ImageIcon sendIcon = new ImageIcon(ChatClientGUI.class.getResource("send.ico"));
        Image img = sendIcon.getImage();
        Image resizedImg = img.getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JButton sendButton = new JButton() {
            protected void paintComponent(Graphics g) {
                g.drawImage(getBackgroundImage(), 0, 0, getWidth(), getHeight(), this);
                super.paintComponent(g); // Paint the text and other button components
            }

            private Image getBackgroundImage() {
                ImageIcon icon = new ImageIcon("send.ico");
                return icon.getImage();
            }

        };

        sendButton.setBorder(BorderFactory.createEmptyBorder());
        sendButton.setIcon(new ImageIcon(resizedImg));

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Send button action
        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Send message on pressing "Enter"
        messageField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        frame.setVisible(true);
    }

    // Send message method
    private static void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message); // Send the message to the server
            messageField.setText(""); // Clear input field
        }
    }

    // Append received message to chat UI
    public static void appendMessage(String message, boolean isSender) {
        SwingUtilities.invokeLater(() -> {
            JPanel messagePanel = new JPanel(new FlowLayout(isSender ? FlowLayout.RIGHT : FlowLayout.LEFT, 5, 0));
            messagePanel.setBorder(new EmptyBorder(2, 5, 2, 5));

            JLabel messageLabel = new JLabel("<html><p style='width: 250px;'>" + message + "</p></html>");
            messageLabel.setOpaque(true);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            if (isSender) {
                messageLabel.setBackground(new Color(205, 230, 254)); // Blue for sender
                messageLabel.setForeground(Color.black);

            } else {
                messageLabel.setBackground(new Color(230, 230, 230)); // Gray for others
                messageLabel.setForeground(Color.BLACK);

            }

            messagePanel.add(messageLabel);
            messageContainer.add(messagePanel);
            messageContainer.revalidate();
            messageContainer.repaint();

            // Auto-scroll to latest message
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });
    }
}