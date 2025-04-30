import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import uk.ac.leedsbeckett.oop.LBUGraphics;
import java.awt.Color;

public class MainClass extends LBUGraphics {
    public static void main(String[] args) {
        new MainClass();
    }

    public MainClass() {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);

        // Thread to handle console input
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if ("about".equals(line.trim())) {
                        SwingUtilities.invokeLater(() -> about());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void processCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            displayMessage("Please enter a command");
            return;
        }

        String[] parts = command.trim().toLowerCase().split("\\s+");
        String cmd = parts[0];

        try {
            switch (cmd) {
            case "about":
                about();
                break;
                
            case "penup":
                drawOff();
                break;
                
            case "pendown":
                drawOn();
                break;
                
            case "left":
                if (parts.length != 2) {
                    displayMessage("Usage: left <degrees>");
                    return;
                }
                left(Integer.parseInt(parts[1]));
                break;
                
            case "right":
                if (parts.length != 2) {
                    displayMessage("Usage: right <degrees>");
                    return;
                }
                right(Integer.parseInt(parts[1]));
                break;
                
            case "move":
                if (parts.length != 2) {
                    displayMessage("Usage: move <distance>");
                    return;
                }
                forward(Integer.parseInt(parts[1]));
                break;
                
            case "reverse":
                if (parts.length != 2) {
                    displayMessage("Usage: reverse <distance>");
                    return;
                }
                forward(-Integer.parseInt(parts[1]));
                break;
                
            case "black":
                setPenColour(Color.BLACK);
                break;
                
            case "green":
                setPenColour(Color.GREEN);
                break;
                
            case "red":
                setPenColour(Color.RED);
                break;
                
            case "white":
                setPenColour(Color.WHITE);
                break;
                
            case "reset":
                clear();
                drawOn();
                setPenColour(Color.BLACK);
                setxPos(getWidth() / 2);
                setyPos(getHeight() / 2);
                pointTurtle(90);
                break;
                
            case "clear":
                clear();
                break;
                
            default:
                displayMessage("Unknown command: " + command);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid number format in command: " + command);
        } catch (Exception e) {
            displayMessage("Error processing command: " + e.getMessage());
        }
    }
}