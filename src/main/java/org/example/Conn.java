package org.example;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;



public class Conn extends WebSocketClient {

    private static JTextArea textArea;

     private static ImageIcon image1;
     private static ImageIcon image2;
    private static JFrame frame;
    public Conn(URI serverURI) {
        super(serverURI);
    }

    public static void main(String[] args) throws URISyntaxException {
        Conn c = new Conn(new URI(
                "ws://localhost:2828")); // more about drafts here: http://github.com/TooTallNate/Java-WebSocket/wiki/Drafts
        c.connect();
        createAndShowGUI(c);
    }
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }
    @Override
    public void onMessage(String message) {

        //System.out.println("DEBUG: received: " + message);

        String beginData = "data:image/jpeg;base64,";

        // "BeginAfterSign{\"signImage\":\"base64_sign_image\",\"fingerImage\":\"base64_finger_image\"}EndAfterSign";

        if (message.indexOf("BeginAfterSign") >= 0) {
            String newLogMsg = message.replace("BeginAfterSign", "").replace("EndAfterSign", "") + "\n";
            // Assuming you have a library or method to parse JSON in Java, similar to JSON.parse in JavaScript
            // Let's assume jsondata is an object with signImage and fingerImage properties
            // You should replace the placeholders with your actual JSON parsing code
            // For this example, we'll just create a sample object
            // String jsondata = "{\"signImage\":\"base64_sign_image\",\"fingerImage\":\"base64_finger_image\"}";
//
            JSONObject jsondata = new JSONObject(newLogMsg);
            String log = ""; // Initialize log
            log = log + jsondata.get("signImage") + jsondata.get("fingerImage");
            String signpicSrc = beginData + jsondata.get("signImage");

            String fingerpicSrc = beginData + jsondata.get("fingerImage");
            //System.out.println("THIS="+fingerpicSrc);

//           Signature img conversion
            if(jsondata.get("signImage").toString()!="") {
                String base64Image = signpicSrc.split(",")[1];
                //System.out.println(base64Image);
                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
                //System.out.println(imageBytes.toString());
                try {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
                    File file = new File("Assinatura.png");
                    ImageIO.write(img, "png", file);
                    file.createNewFile();
                    JLabel picLabel = new JLabel(new ImageIcon(img));
                    frame.add(picLabel);
                    // System.out.println("DEBUG");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


//            Finger img conversion
//            if(jsondata.get("fingerImage").toString()!="") {
//                String base64Image = fingerpicSrc.split(",")[1];
//                //System.out.println(base64Image);
//                byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
//                //System.out.println(imageBytes.toString());
//                try {
//                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
//                    File file = new File("Impressao.png");
//                    ImageIO.write(img, "png", file);
//                    file.createNewFile();
//                    JLabel picLabel = new JLabel(new ImageIcon(img));
//                    frame.add(picLabel);
//                   // System.out.println("DEBUG");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
    }
    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }
    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        /* if the error is fatal then onClose will be called additionally */
    }

    private static void createAndShowGUI(Conn c) {
        // Create the main frame
        frame = new JFrame("INATRO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);

        // Create a JTextArea
//        textArea = new JTextArea();
//        textArea.setEditable(false);
//        textArea.setSize(400,250);

//        // Load the first image
//        ImageIcon image1 = image1.loadImage(); // Replace with the path to your first image
//
//        // Create a label to display the first image
//        JLabel label1 = new JLabel(image1);
//
//        // Load the second image
//        //ImageIcon image2 = image2.loadImage(); // Replace with the path to your second image
//
//        // Create a label to display the second image
//        JLabel label2 = new JLabel(image2);
//
//        // Create a panel to hold the image labels
//        JPanel imagePanel = new JPanel();
//        imagePanel.setLayout(new GridLayout(1, 2)); // Two images side by side
//        imagePanel.add(label1);
//        imagePanel.add(label2);
//
//
//        imagePanel.setLayout(new GridLayout(1, 2)); // Two images side by side
//        imagePanel.add(label1);
//        imagePanel.add(label2);


        // Create two buttons
        JButton button1 = new JButton("Assinatura");
        JButton button2 = new JButton("Impressão digital");

        // Create a JPanel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(button1);
        buttonPanel.add(button2);

        // Add action listeners to the buttons

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Signature
                c.send("DoGWQ_StartSign2(\"\",d:\\\\1.xml,0.1.0.100,Please sign your name,0)");
                textArea.append("\n");
            }
        });

        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //Fingerprint
                c.send("DoGWQ_StartSign2(\"\",d:\\\\1.xml,1.1.0.100,Please sign your finger,0)");
                textArea.append("\n");
            }
        });

        // Add components to the frame
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Display the frame
        frame.setVisible(true);
    }
}