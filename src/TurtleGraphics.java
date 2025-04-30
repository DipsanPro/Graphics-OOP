import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import uk.ac.leedsbeckett.oop.LBUGraphics;

public class TurtleGraphics extends LBUGraphics {
    
    private static final Logger LOGGER = Logger.getLogger(TurtleGraphics.class.getName());
    
    public static void main(String[] args) {
        new TurtleGraphics();
    }
    
    public TurtleGraphics() {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);
        
        // Initialize turtle position
        reset();
        
        // Thread to handle console input
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final String command = line.trim();
                    SwingUtilities.invokeLater(() -> processCommand(command));
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading console input", e);
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
                    reset();
                    break;
                    
                case "clear":
                    clear();
                    break;
                    
                default:
                    displayMessage("Unknown command: " + command);
            }
        } catch (NumberFormatException e) {
            displayMessage("Invalid number format in command: " + command);
            LOGGER.log(Level.WARNING, "Number format error in command: " + command, e);
        } catch (Exception e) {
            displayMessage("Error processing command: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error processing command: " + command, e);
        }
    }
    
    @Override
    public void reset() {
        clear();  // Clear the display
        drawOn();  // Set the pen down
        setPenColour(Color.BLACK);  // Set the default color to black
        
        // Calculate the center of the canvas
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        // Reset position and direction (pointing down)
        setxPos(centerX);
        setyPos(centerY);
        pointTurtle(90);  // Point the turtle down (90 degrees)
    }
}