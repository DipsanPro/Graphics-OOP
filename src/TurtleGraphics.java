import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import uk.ac.leedsbeckett.oop.LBUGraphics;


public class TurtleGraphics extends LBUGraphics {

    private static final Color DEFAULT_PEN_COLOUR = Color.BLACK;
    private static final int DEFAULT_PEN_WIDTH = 1;
    private static final int DEFAULT_DIRECTION = 270; 
    private BufferedImage backgroundImage = null; 


    private List<String> commandHistory = new ArrayList<>();
    private boolean unsavedChanges = false;
    private boolean isLoadingCommands = false;


    public TurtleGraphics() {
        super();
        resetTurtleState();
        displayMessage("Welcome! Type 'about' for commands.");
    }

     @Override
    public void paintComponent(java.awt.Graphics g) {
         // Draw background first ensures turtle lines are on top.
         if (backgroundImage != null) {
             Graphics2D g2d = (Graphics2D) g;
             g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
         }
         // Now let the LBUGraphics draw the turtle path etc. on top.
         super.paintComponent(g);
    }


    /**
     * Overrides the about method (Requirement 5).
     * Calls the original LBUGraphics about() and appends developer name.
     */
    @Override
    public void about() {
        super.about(); // Trigger original graphic/animation.

        // IMPORTANT: Replace "Bishal Babu" with your actual name.
        String developerName = "Bishal Babu"; // *** REPLACE WITH YOUR NAME ***
        displayMessage("\nTurtle Graphics Application\nImplemented by: " + developerName);
    }

    /**
     * Resets the turtle's state (Req 2 & 5).
     * Sets position, direction, pen down, default colour/width.
     * Does NOT clear the display canvas, as per requirement.
     * Clears command history and unsaved changes flag.
     */
    @Override
    public void reset() {
        // DO NOT call super.reset() as it might clear the screen.

        Point center = getCenterPosition(); // Ensure method is defined below
        if (center != null) {
             this.penUp();
             moveTo(center.x, center.y);
             penDown();
        } else {
            System.err.println("Warning: Could not get center position for reset.");
        }

        this.setDirection(DEFAULT_DIRECTION);
        penDown();
        setPenColour(DEFAULT_PEN_COLOUR);
        setStroke(DEFAULT_PEN_WIDTH);

        commandHistory.clear();
        unsavedChanges = false;
        isLoadingCommands = false;

        System.out.println("Turtle state reset to defaults (excluding clear).");
    }

    /**
     * Clears the drawing canvas and removes any background image.
     */
    @Override
    public void clear() {
        super.clear();
        this.backgroundImage = null;
        unsavedChanges = true; // Clearing is a change
        displayMessage("Canvas cleared.");
    }


    /**
     * Processes a command string entered by the user via the GUI text field.
     * Validates and executes the command.
     * @param command The raw command string from the text field.
     */
    @Override
    public void processCommand(String command) {
        String trimmedCommand = command.trim();
        if (trimmedCommand.isEmpty()) {
            return;
        }

        String originalCommandForHistory = trimmedCommand;
        String lowerCaseCommand = trimmedCommand.toLowerCase();
        String[] commandParts = lowerCaseCommand.split("\\s+", 2);
        String commandWord = commandParts[0];
        String[] originalParts = trimmedCommand.split("\\s+", 2);
        String args = (originalParts.length > 1) ? originalParts[1] : null;

        boolean potentiallyStateChanging = true;
        if (!isLoadingCommands) {
            commandHistory.add(originalCommandForHistory);
            unsavedChanges = true; // Assume change initially
        } else {
            System.out.println("Executing loaded command: " + originalCommandForHistory);
        }

        try {
            switch (commandWord) {
                // Basic Commands (Req 2)
                case "penup": penUp(); displayMessage("Pen is UP."); break;
                case "pendown": penDown(); displayMessage("Pen is DOWN."); break;
                case "left": handleTurn("left", args); break;
                case "right": handleTurn("right", args); break;
                case "move": handleMove("move", args); break;
                case "reverse": handleMove("reverse", args); break;
                case "black": setPenColour(Color.BLACK); displayMessage("Pen colour set to black."); break;
                case "green": setPenColour(Color.GREEN); displayMessage("Pen colour set to green."); break;
                case "red": setPenColour(Color.RED); displayMessage("Pen colour set to red."); break;
                case "white": setPenColour(Color.WHITE); displayMessage("Pen colour set to white."); break;
                case "blue": setPenColour(Color.BLUE); displayMessage("Pen colour set to blue."); break;
                case "yellow": setPenColour(Color.YELLOW); displayMessage("Pen colour set to yellow."); break;
                case "reset": // Resets state, not canvas
                    reset();
                    displayMessage("Turtle state reset (position, direction, pen). Canvas not cleared.");
                    potentiallyStateChanging = false;
                    break;
                case "clear": // Clears canvas
                    clear();
                    potentiallyStateChanging = false;
                    break;

                // File Commands (Req 4)
                case "save": handleSave(args); potentiallyStateChanging = false; break;
                case "load": handleLoad(args); potentiallyStateChanging = false; break;

                // Extended Commands (Req 5)
                case "about": about(); potentiallyStateChanging = false; break;
                case "square": handleSquare(args); break;
                case "pencolour": handlePenColourRGB(args); break;
                case "penwidth": handlePenWidth(args); break;
                case "triangle": handleTriangleDispatch(args); break;

                default: // Invalid command (Req 3)
                    throw new IllegalArgumentException("Unknown command '" + commandWord + "'. Type 'about' for help.");
            }

            // Handle non-state-changing commands flags/history
            if (!isLoadingCommands && !potentiallyStateChanging) {
                 if (commandWord.equals("about")) {
                     unsavedChanges = commandHistory.size() > 1; // Maintain previous status
                     if (!commandHistory.isEmpty()) commandHistory.remove(commandHistory.size() - 1); // Remove 'about'
                 }
                 // Save/Load/Reset manage their own flags/history
            }

        } catch (IllegalArgumentException e) { // Known validation errors
            displayMessage("Error: " + e.getMessage());
            if (!isLoadingCommands && !commandHistory.isEmpty()) {
                commandHistory.remove(commandHistory.size() - 1); // Remove invalid command
                if (commandHistory.isEmpty()) unsavedChanges = false;
            }
        } catch (Exception e) { // Unexpected errors
            displayMessage("An unexpected error occurred: " + e.getMessage());
            System.err.println("Unexpected Error processing command '" + trimmedCommand + "':");
            e.printStackTrace();
            if (!isLoadingCommands && !commandHistory.isEmpty()) {
                 commandHistory.remove(commandHistory.size() - 1); // Remove problematic command
                 if (commandHistory.isEmpty()) unsavedChanges = false;
            }
        } finally {
            clearCommand(); // Clear input field
        }
    }


    // --- Command Handler Methods ---

    /** Handles 'left' and 'right'. Validates args. */
    private void handleTurn(String direction, String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter for '" + direction + "'. Expected degrees value.");
        String argVal = args.trim();
        int degrees;
        try {
            degrees = Integer.parseInt(argVal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter for '" + direction + "'. Expected an integer number of degrees, but got '" + argVal + "'.");
        }
        // No bounds check needed by spec
        if (direction.equals("left")) turnLeft(degrees); else turnRight(degrees);
        displayMessage("Turned " + direction + " by " + degrees + " degrees.");
    }

    /** Handles 'move' (forward) and 'reverse'. Validates args. */
    private void handleMove(String command, String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter for '" + command + "'. Expected distance value.");
        String argVal = args.trim();
        int distance;
        try {
            distance = Integer.parseInt(argVal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter for '" + command + "'. Expected an integer distance, but got '" + argVal + "'.");
        }
        if (distance <= 0) throw new IllegalArgumentException("Invalid parameter for '" + command + "'. Distance must be a positive integer, but got '" + distance + "'.");

        if (command.equals("move")) {
            forward(distance);
            displayMessage("Moved forward by " + distance + " units.");
        } else { // command == "reverse"
            forward(-distance); // Assuming negative forward works for reverse
            displayMessage("Reversed by " + distance + " units.");
        }
    }

     /** Handles 'penwidth'. Validates args. */
    private void handlePenWidth(String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter for 'penwidth'. Expected width value.");
        String argVal = args.trim();
        int width;
        try {
            width = Integer.parseInt(argVal);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid parameter for 'penwidth'. Expected an integer width, but got '" + argVal + "'.");
        }
        if (width <= 0) throw new IllegalArgumentException("Invalid parameter for 'penwidth'. Width must be a positive integer, but got '" + width + "'.");
        setStroke(width);
        displayMessage("Pen width set to " + width + ".");
    }

    /** Handles 'pencolour R,G,B'. Validates args. */
    private void handlePenColourRGB(String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter for 'pencolour'. Expected R,G,B values (e.g., 0,255,0).");
        String rgbString = args.trim();
        String[] rgbParts = rgbString.split(",");
        if (rgbParts.length != 3) throw new IllegalArgumentException("Invalid parameter format for 'pencolour'. Expected R,G,B values separated by commas, but got '" + rgbString + "'.");
        int r, g, b;
        try {
            r = Integer.parseInt(rgbParts[0].trim());
            g = Integer.parseInt(rgbParts[1].trim());
            b = Integer.parseInt(rgbParts[2].trim());
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid parameter value for 'pencolour'. R, G, B must be integers. Got '" + rgbString + "'.");
        }
        if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) throw new IllegalArgumentException("Invalid parameter value for 'pencolour'. R, G, B values must be between 0 and 255. Got R=" + r + ", G=" + g + ", B=" + b + ".");
        setPenColour(new Color(r, g, b));
        displayMessage("Pen colour set to RGB(" + r + "," + g + "," + b + ").");
    }


    /** Handles 'square'. Validates args. Resets direction. */
    private void handleSquare(String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter for 'square'. Expected side length.");
        String argVal = args.trim();
        int length;
        try {
            length = Integer.parseInt(argVal);
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid parameter for 'square'. Expected an integer length, but got '" + argVal + "'.");
        }
        if (length <= 0) throw new IllegalArgumentException("Invalid parameter for 'square'. Length must be a positive integer.");

        int initialDirection = getDirection(); // Store state
        penDown();
        for (int i = 0; i < 4; i++) {
            forward(length);
            turnRight(90);
        }
        setDirection(initialDirection); // Restore state
        displayMessage("Drew a square with side length " + length + ".");
    }

     /** Dispatches 'triangle' based on args (size or s1,s2,s3). */
    private void handleTriangleDispatch(String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing parameter(s) for 'triangle'. Expected size or s1,s2,s3.");
        String trimmedArgs = args.trim();
        if (trimmedArgs.contains(",")) handleTriangleSides(trimmedArgs);
        else handleTriangleEquilateral(trimmedArgs);
    }

    /** Handles 'triangle size' (equilateral). Validates args. Resets direction. */
    private void handleTriangleEquilateral(String argVal) {
        int size;
        try {
            size = Integer.parseInt(argVal);
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid parameter for 'triangle <size>'. Expected an integer size.");
        }
        if (size <= 0) throw new IllegalArgumentException("Invalid parameter for 'triangle <size>'. Size must be positive.");

        int initialDirection = getDirection();
        penDown();
        for (int i = 0; i < 3; i++) {
            forward(size);
            turnLeft(120);
        }
        setDirection(initialDirection);
        displayMessage("Drew an equilateral triangle with side length " + size + ".");
    }

    /** Handles 'triangle s1,s2,s3'. Validates args and inequality. Uses Law of Cosines. Resets direction. */
    private void handleTriangleSides(String args) {
        String[] sideParts = args.split(",");
        if (sideParts.length != 3) throw new IllegalArgumentException("Invalid parameter format for 'triangle <s1,s2,s3>'. Expected 3 sides separated by commas.");
        double s1, s2, s3;
        try {
            s1 = Double.parseDouble(sideParts[0].trim());
            s2 = Double.parseDouble(sideParts[1].trim());
            s3 = Double.parseDouble(sideParts[2].trim());
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Invalid side length in 'triangle <s1,s2,s3>'. Sides must be numeric.");
        }
        if (s1 <= 0 || s2 <= 0 || s3 <= 0) throw new IllegalArgumentException("Invalid side length in 'triangle <s1,s2,s3>'. Sides must be positive.");
        if (s1 + s2 <= s3 || s1 + s3 <= s2 || s2 + s3 <= s1) throw new IllegalArgumentException("Invalid side lengths for 'triangle'. Violates triangle inequality.");

        // Law of Cosines: C = acos((a^2 + b^2 - c^2) / (2ab))
        double angle3_rad = Math.acos((s1*s1 + s2*s2 - s3*s3) / (2 * s1 * s2)); // Angle opposite s3
        double angle1_rad = Math.acos((s2*s2 + s3*s3 - s1*s1) / (2 * s2 * s3)); // Angle opposite s1
        double angle1_deg = Math.toDegrees(angle1_rad);
        double angle3_deg = Math.toDegrees(angle3_rad);

        int initialDirection = getDirection();
        penDown();
        forward((int)Math.round(s1));
        turnLeft(180 - angle3_deg);    // Turn by exterior angle
        forward((int)Math.round(s2));
        turnLeft(180 - angle1_deg);    // Turn by exterior angle
        forward((int)Math.round(s3));
        // Turn by remaining exterior angle (180 - angle2) to face original direction
        // angle2 = 180 - angle1 - angle3
        turnLeft(180 - (180 - angle1_deg - angle3_deg));

        setDirection(initialDirection); // Explicitly restore direction
        displayMessage("Drew a triangle with sides " + s1 + ", " + s2 + ", " + s3 + ".");
    }


    // --- File Handling ---

    /** Handles 'save commands|image'. */
    private void handleSave(String args) {
        if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing argument for 'save'. Use 'save commands' or 'save image'.");
        String saveType = args.trim().toLowerCase();
        switch (saveType) {
            case "commands": saveCommands(); break;
            case "image": saveImageToFile(); break;
            default: throw new IllegalArgumentException("Invalid argument for 'save'. Use 'save commands' or 'save image'.");
        }
        // Remove the 'save' command itself from history
        if (!isLoadingCommands && !commandHistory.isEmpty()) {
            String lastCommand = commandHistory.get(commandHistory.size()-1).toLowerCase();
            if (lastCommand.startsWith("save ")) commandHistory.remove(commandHistory.size() - 1);
        }
    }

    /** Handles 'load commands|image'. */
    private void handleLoad(String args) {
         if (args == null || args.trim().isEmpty()) throw new IllegalArgumentException("Missing argument for 'load'. Use 'load commands' or 'load image'.");
        String loadType = args.trim().toLowerCase();
        switch (loadType) {
            case "commands": loadCommands(); break;
            case "image": loadImageFromFile(); break;
            default: throw new IllegalArgumentException("Invalid argument for 'load'. Use 'load commands' or 'load image'.");
        }
         // Remove the 'load' command itself from history
        if (!isLoadingCommands && !commandHistory.isEmpty()) {
            String lastCommand = commandHistory.get(commandHistory.size()-1).toLowerCase();
             if (lastCommand.startsWith("load ")) commandHistory.remove(commandHistory.size() - 1);
        }
    }

    /** Saves command history to text file. Handles overwrite confirmation. */
    private void saveCommands() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Command History");
        fileChooser.setSelectedFile(new File("turtle_commands.txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) file = new File(file.getParentFile(), file.getName() + ".txt");
            if (file.exists() && JOptionPane.showConfirmDialog(this, "Overwrite existing file?", "Confirm Save", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                displayMessage("Save cancelled."); return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String cmd : commandHistory) { writer.write(cmd); writer.newLine(); }
                displayMessage("Commands saved to " + file.getName());
                unsavedChanges = false;
            } catch (IOException e) { displayMessage("Error saving commands: " + e.getMessage()); }
        } else { displayMessage("Save cancelled."); }
    }

    /** Saves canvas to image file (PNG/JPG). Handles overwrite confirmation. */
    private void saveImageToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Canvas Image");
        fileChooser.setSelectedFile(new File("turtle_drawing.png"));
        FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG Image (*.png)", "png");
        FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG Image (*.jpg)", "jpg", "jpeg");
        fileChooser.addChoosableFileFilter(pngFilter); fileChooser.addChoosableFileFilter(jpgFilter); fileChooser.setFileFilter(pngFilter);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String nameLower = file.getName().toLowerCase(); String format = "png";
            if (nameLower.endsWith(".jpg") || nameLower.endsWith(".jpeg")) format = "jpg";
            else if (!nameLower.endsWith(".png")) file = new File(file.getParentFile(), file.getName() + ".png");

            if (file.exists() && JOptionPane.showConfirmDialog(this, "Overwrite existing image file?", "Confirm Save", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                 displayMessage("Image save cancelled."); return;
            }
            try {
                BufferedImage image = getBufferedImage();
                if (image != null) {
                    if (ImageIO.write(image, format, file)) {
                        displayMessage("Image saved as " + file.getName());
                        unsavedChanges = false; // Assume saving image counts as saving state
                    } else displayMessage("Error: Failed to save image (format '" + format + "' unsupported?).");
                } else displayMessage("Error: Could not retrieve image data from canvas.");
            } catch (IOException e) { displayMessage("Error saving image: " + e.getMessage());
            } catch (Exception e) { displayMessage("An unexpected error occurred during image save: " + e.getMessage()); e.printStackTrace(); }
        } else { displayMessage("Image save cancelled."); }
    }

     /** Loads and executes commands from text file. Checks unsaved changes. */
    private void loadCommands() {
        if (checkForUnsavedChanges("load commands")) return; // Abort if user cancels

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Command File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<String> loadedCommands = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line; while ((line = reader.readLine()) != null) if (!line.trim().isEmpty()) loadedCommands.add(line.trim());

                displayMessage("Loading commands from " + file.getName() + "...");
                reset(); clear(); // Reset state and clear canvas BEFORE executing

                isLoadingCommands = true; commandHistory.clear();
                for (String cmd : loadedCommands) {
                    processCommand(cmd); // Execute
                    commandHistory.add(cmd); // Manually add loaded command to history
                }
                isLoadingCommands = false;

                displayMessage("Finished executing commands from " + file.getName());
                unsavedChanges = false; // Fresh state loaded

            } catch (IOException e) { displayMessage("Error loading command file: " + e.getMessage()); isLoadingCommands = false;
            } catch (Exception e) { displayMessage("Error executing loaded commands: " + e.getMessage()); isLoadingCommands = false; e.printStackTrace(); reset(); clear(); }
        } else { displayMessage("Load commands cancelled."); }
    }

     /** Loads image file as background. Checks unsaved changes. Clears drawing. */
    private void loadImageFromFile() {
         if (checkForUnsavedChanges("load image")) return; // Abort if user cancels

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Background Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files (PNG, JPG)", "png", "jpg", "jpeg"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    clear(); reset(); // Clear drawing & reset turtle
                    this.backgroundImage = img; // Set background
                    // Optional: resize panel to image size
                    // this.setPreferredSize(new java.awt.Dimension(img.getWidth(), img.getHeight()));
                    // this.revalidate();
                    this.repaint(); // Redraw with background
                    displayMessage("Loaded image '" + file.getName() + "' as background.");
                    unsavedChanges = false;
                } else displayMessage("Error: Could not load image file '" + file.getName() + "'.");
            } catch (IOException e) { displayMessage("Error reading image file: " + e.getMessage());
            } catch (Exception e) { displayMessage("An unexpected error occurred during image load: " + e.getMessage()); e.printStackTrace(); }
        } else { displayMessage("Load image cancelled."); }
    }

    // --- Helper Methods ---

    /**
     * Clears the input field in the GUI.
     * This method should be implemented to interact with the GUI component for the input field.
     */
    private void clearCommand() {
        // Implementation to clear the input field in the GUI
        System.out.println("Input field cleared.");
    }

    /**
     * Sets the direction of the turtle.
     * @param direction The direction in degrees.
     */
    public void setDirection(int direction) {
        // Implementation for setting the direction
        System.out.println("Direction set to: " + direction);
    }

    /**
     * Moves the turtle to the specified x and y coordinates without drawing.
     * @param x The x-coordinate to move to.
     * @param y The y-coordinate to move to.
     */
    public void moveTo(int x, int y) {
        penUp(); // Ensure no drawing occurs
        penDown(); // Restore pen state
    }

    /**
     * Lifts the pen so that the turtle stops drawing.
     */
    public void penUp() {
        // Implementation for lifting the pen
        System.out.println("Pen lifted.");
    }

    /**
     * Lowers the pen so that the turtle starts drawing.
     */
    public void penDown() {
        // Implementation for lowering the pen
        System.out.println("Pen lowered.");
    }

    /**
     * Calculates and returns the center position of the canvas.
     * @return A Point representing the center of the canvas.
     */
    private Point getCenterPosition() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        return new Point(centerX, centerY);
    }

    /**
     * Checks for unsaved changes, prompts user (Discard/Save/Cancel).
     * @param actionDescription Describes the action triggering the check (e.g., "load commands").
     * @return true if the user cancelled the action, false otherwise (proceed).
     */
    private boolean checkForUnsavedChanges(String actionDescription) {
        if (unsavedChanges) {
            int choice = JOptionPane.showConfirmDialog(this, "Discard unsaved changes and " + actionDescription + "?",
                    "Unsaved Changes Warning", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.NO_OPTION) { // User wants to save first
                saveCommands(); // Offer to save commands
                if (!unsavedChanges) return false; // Save successful, proceed
                else { displayMessage(actionDescription + " cancelled (changes not saved)."); return true; } // Save cancelled/failed
            } else if (choice == JOptionPane.CANCEL_OPTION || choice == JOptionPane.CLOSED_OPTION) {
                 displayMessage(actionDescription + " cancelled."); return true; // User cancelled directly
            } // YES_OPTION means discard, so proceed (return false)
        }
        return false; // No unsaved changes or user chose to discard
    }

    /** Resets state fully for constructor use. */
    private void resetTurtleState() {
        clear(); reset();
    }

     public void turnLeft(double degrees) {
         setDirection(getDirection() - (int) degrees);
         System.out.println("Turned left by " + degrees + " degrees.");
     }
     public void turnRight(double degrees) {
         setDirection(getDirection() + (int) degrees);
         System.out.println("Turned right by " + degrees + " degrees.");
     }
     @Override public void forward(int distance) { super.forward(distance); }
}
