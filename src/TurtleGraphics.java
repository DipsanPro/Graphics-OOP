import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import uk.ac.leedsbeckett.oop.LBUGraphics;

/**
 * TurtleGraphics extends LBUGraphics to implement a turtle graphics system
 * This class contains all the command processing functionality
 */
public class TurtleGraphics extends LBUGraphics {
    
    private final Map<String, String> commandSuggestions = new HashMap<>();
    private JTextArea commandHistoryArea;
    private List<String> commandHistory = new ArrayList<>();
    private boolean imageSaved = true;
    private boolean commandsSaved = true;
    private JFrame historyFrame;
    private JFrame fileOperationsFrame;
    private int defaultPenWidth = 1;
    private Color defaultPenColor = Color.BLACK;
    
    /**
     * Constructor - initializes the turtle graphics
     */
    public TurtleGraphics() {
        super();
        reset();
        createCommandHistoryPanel();
        createFileOperationsPanel();
        initializeCommandSuggestions();
        
        displayMessage("Welcome! Type 'help' to see available commands.");
        
        SwingUtilities.invokeLater(() -> {
            if (getTopLevelAncestor() instanceof JFrame) {
                JFrame frame = (JFrame) getTopLevelAncestor();
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        exitApplication();
                    }
                });
            }
        });
    }
    
    /**
     * Override the about method to add a personal message
     */
    @Override
    public void about() {
        super.about();
        displayMessage("Turtle Graphics by Dipsan - OOP Assignment");
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "Turtle Graphics by Dipsan\nOOP Assignment", 
                "About This Program", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    

    private void createCommandHistoryPanel() {
        historyFrame = new JFrame("Command History");
        historyFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        commandHistoryArea = new JTextArea(10, 40);
        commandHistoryArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(commandHistoryArea);
        
        historyFrame.add(scrollPane);
        historyFrame.pack();
        historyFrame.setLocationRelativeTo(this);
    }
    
    /**
     * Create the file operations panel but don't show it yet
     */
    private void createFileOperationsPanel() {
        fileOperationsFrame = new JFrame("File Operations");
        fileOperationsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton loadImageBtn = new JButton("Load Image");
        JButton saveImageBtn = new JButton("Save Image");
        JButton loadCommandsBtn = new JButton("Load Commands");
        JButton saveCommandsBtn = new JButton("Save Commands");
        
        loadImageBtn.addActionListener(_ -> loadImage());
        saveImageBtn.addActionListener(_ -> saveImage());
        loadCommandsBtn.addActionListener(_ -> loadCommands());
        saveCommandsBtn.addActionListener(_ -> saveCommands());
        
        panel.add(loadImageBtn);
        panel.add(saveImageBtn);
        panel.add(loadCommandsBtn);
        panel.add(saveCommandsBtn);
        
        fileOperationsFrame.add(panel);
        fileOperationsFrame.pack();
        fileOperationsFrame.setLocationRelativeTo(this);
    }
    
    /**
     * Show the command history window
     */
    private void showCommandHistory() {
        updateCommandHistoryDisplay();
        
        if (!historyFrame.isVisible()) {
            historyFrame.setVisible(true);
        } else {
            historyFrame.toFront();
        }
        displayMessage("Command history displayed");
    }
    
    /**
     * Show the file operations window
     */
    private void showFileOperations() {
        if (!fileOperationsFrame.isVisible()) {
            fileOperationsFrame.setVisible(true);
        } else {
            fileOperationsFrame.toFront();
        }
        displayMessage("File operations panel displayed");
    }
    
    /**
     * Initialize command suggestions for typo correction
     */
    private void initializeCommandSuggestions() {
        commandSuggestions.put("penp", "penup");
        commandSuggestions.put("pen", "pendown");
        commandSuggestions.put("pend", "pendown");
        commandSuggestions.put("penu", "penup");
        commandSuggestions.put("lft", "left");
        commandSuggestions.put("rght", "right");
        commandSuggestions.put("mov", "move");
        commandSuggestions.put("forward", "move");
        commandSuggestions.put("rev", "reverse");
        commandSuggestions.put("back", "reverse");
        commandSuggestions.put("backwards", "reverse");
        commandSuggestions.put("rd", "red");
        commandSuggestions.put("grn", "green");
        commandSuggestions.put("blu", "blue");
        commandSuggestions.put("wht", "white");
        commandSuggestions.put("blk", "black");
        commandSuggestions.put("orng", "orange");
        commandSuggestions.put("purp", "purple");
        commandSuggestions.put("sqr", "square");
        commandSuggestions.put("squ", "square");
        commandSuggestions.put("squar", "square");
        commandSuggestions.put("suqare", "square");
        commandSuggestions.put("cir", "circle");
        commandSuggestions.put("circ", "circle");
        commandSuggestions.put("tri", "triangle");
        commandSuggestions.put("hex", "hexagon");
        commandSuggestions.put("fop", "foperation");
        commandSuggestions.put("fileop", "foperation");
        commandSuggestions.put("fileoper", "foperation");
        commandSuggestions.put("penc", "pencolour");
        commandSuggestions.put("pencolor", "pencolour");
        commandSuggestions.put("penw", "penwidth");
        commandSuggestions.put("tri", "triangle");
    }
    

    @Override
    public void processCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }
        
        try {
            addToCommandHistory(command);
            
            if (!command.startsWith("load commands")) {
                imageSaved = false;
                commandsSaved = false;
            }
            
            String[] parts = command.trim().toLowerCase().split("\\s+", 2);
            String commandName = parts[0];
            String parameter = parts.length > 1 ? parts[1] : null;
            
            if (!isValidCommand(commandName) && commandSuggestions.containsKey(commandName)) {
                String suggestion = commandSuggestions.get(commandName);
                String message = "Did you mean '" + suggestion + "'? Using that instead.";
                showErrorDialog(message);
                commandName = suggestion;
            }
            
            switch (commandName) {
                case "about":
                    about();
                    displayMessage("About information displayed");
                    break;
                    
                case "penup":
                    drawOff();
                    displayMessage("Pen is now up");
                    break;
                    
                case "pendown":
                    drawOn();
                    displayMessage("Pen is now down");
                    break;
                    
                case "left":
                    handleLeftCommand(parameter);
                    break;
                    
                case "right":
                    handleRightCommand(parameter);
                    break;
                    
                case "move":
                    handleMoveCommand(parameter);
                    break;
                    
                case "reverse":
                    handleReverseCommand(parameter);
                    break;
                    
                case "black":
                    setPenColour(Color.BLACK);
                    displayMessage("Pen color set to black");
                    break;
                    
                case "green":
                    setPenColour(Color.GREEN);
                    displayMessage("Pen color set to green");
                    break;
                    
                case "red":
                    setPenColour(Color.RED);
                    displayMessage("Pen color set to red");
                    break;
                    
                case "white":
                    setPenColour(Color.WHITE);
                    displayMessage("Pen color set to white");
                    break;
                    
                case "blue":
                    setPenColour(Color.BLUE);
                    displayMessage("Pen color set to blue");
                    break;
                    
                case "yellow":
                    setPenColour(Color.YELLOW);
                    displayMessage("Pen color set to yellow");
                    break;
                    
                case "orange":
                    setPenColour(Color.ORANGE);
                    displayMessage("Pen color set to orange");
                    break;
                    
                case "purple":
                    setPenColour(new Color(128, 0, 128));
                    displayMessage("Pen color set to purple");
                    break;
                    
                case "pink":
                    setPenColour(Color.PINK);
                    displayMessage("Pen color set to pink");
                    break;
                    
                case "cyan":
                    setPenColour(Color.CYAN);
                    displayMessage("Pen color set to cyan");
                    break;
                
                case "square":
                    handleSquareCommand(parameter);
                    break;
                    
                case "star":
                    handleStarCommand(parameter);
                    break;
                    
                case "circle":
                    handleCircleCommand(parameter);
                    break;
                    
                case "triangle":
                    handleTriangleCommand(parameter);
                    break;
                    
                case "hexagon":
                    handleHexagonCommand(parameter);
                    break;
                    
                case "reset":
                    resetAll();
                    displayMessage("Reset to initial position and default settings");
                    break;
                    
                case "clear":
                    clear();
                    displayMessage("Display cleared");
                    break;
                    
                case "history":
                    showCommandHistory();
                    break;
                    
                case "foperation":
                    showFileOperations();
                    break;
                    
                case "save":
                    if (parameter == null) {
                        showErrorDialog("Missing parameter. Usage: save image|commands");
                        return;
                    }
                    if (parameter.equals("image")) {
                        saveImage();
                    } else if (parameter.equals("commands")) {
                        saveCommands();
                    } else {
                        showErrorDialog("Invalid parameter. Use 'save image' or 'save commands'");
                    }
                    break;
                    
                case "load":
                    if (parameter == null) {
                        showErrorDialog("Missing parameter. Usage: load image|commands");
                        return;
                    }
                    if (parameter.equals("image")) {
                        loadImage();
                    } else if (parameter.equals("commands")) {
                        loadCommands();
                    } else {
                        showErrorDialog("Invalid parameter. Use 'load image' or 'load commands'");
                    }
                    break;
                    
                case "exit":
                    exitApplication();
                    break;
                    
                case "help":
                    showHelpDialog();
                    break;
                    
                case "pencolour":
                    handlePenColourCommand(parameter);
                    break;
                    
                case "penwidth":
                    handlePenWidthCommand(parameter);
                    break;
                    
                default:
                    showErrorDialog("Invalid command '" + commandName + "'. Type 'help' for a list of commands.");
                    break;
            }
        } catch (Exception e) {
            showErrorDialog("Error: " + e.getMessage());
        }
    }
    
    /**
     * Check if a command is valid
     */
    private boolean isValidCommand(String command) {
        return command.equals("about") || command.equals("penup") || command.equals("pendown") ||
               command.equals("left") || command.equals("right") || command.equals("move") ||
               command.equals("reverse") || command.equals("black") || command.equals("green") ||
               command.equals("red") || command.equals("white") || command.equals("blue") ||
               command.equals("yellow") || command.equals("orange") || command.equals("purple") ||
               command.equals("pink") || command.equals("cyan") || command.equals("square") ||
               command.equals("star") || command.equals("circle") || command.equals("triangle") ||
               command.equals("hexagon") || command.equals("reset") || command.equals("clear") ||
               command.equals("help") || command.equals("save") || command.equals("load") ||
               command.equals("exit") || command.equals("history") || command.equals("foperation") ||
               command.equals("pencolour") || command.equals("penwidth");
    }
    
    /**
     * Handle the pencolour command with validation
     * Format: pencolour <red>,<green>,<blue>
     */
    private void handlePenColourCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameters for 'pencolour'. Usage: pencolour <red>,<green>,<blue>\nExample: pencolour 255,0,0");
            return;
        }
        
        try {
            String[] colorParams = parameter.split(",");
            
            if (colorParams.length != 3) {
                showErrorDialog("Invalid number of parameters. Usage: pencolour <red>,<green>,<blue>\nExample: pencolour 255,0,0");
                return;
            }
            
            int red = Integer.parseInt(colorParams[0].trim());
            int green = Integer.parseInt(colorParams[1].trim());
            int blue = Integer.parseInt(colorParams[2].trim());
            
            if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                showErrorDialog("RGB values must be between 0 and 255. Example: pencolour 255,0,0");
                return;
            }
            
            Color newColor = new Color(red, green, blue);
            setPenColour(newColor);
            displayMessage("Pen color set to RGB(" + red + "," + green + "," + blue + ")");
        } catch (NumberFormatException e) {
            showErrorDialog("RGB values must be numbers. Usage: pencolour <red>,<green>,<blue>\nExample: pencolour 255,0,0");
        }
    }
    
    /**
     * Handle the penwidth command with validation
     * Format: penwidth <width>
     */
    private void handlePenWidthCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'penwidth'. Usage: penwidth <width>\nExample: penwidth 3");
            return;
        }
        
        try {
            int width = Integer.parseInt(parameter.trim());
            
            if (width <= 0) {
                showErrorDialog("Width must be a positive number. Example: penwidth 3");
                return;
            }
            
            setStroke(width);
            displayMessage("Pen width set to " + width);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'penwidth' must be a number. Example: penwidth 3");
        }
    }
    
    /**
     * Handle the square command with validation - keeps turtle at original position
     * Format: square <length>
     */
    private void handleSquareCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'square'. Usage: square <size>\nExample: square 100");
            return;
        }
        
        try {
            int size = Integer.parseInt(parameter.trim());
            
            if (size <= 0) {
                showErrorDialog("Size must be a positive number. Example: square 100");
                return;
            }
            
            prepareForShapeDrawing();
            
            int startX = getxPos();
            int startY = getyPos();
            int startDirection = getDirection();
            
            drawSquare(size);
            
            setPenState(false);
            setxPos(startX);
            setyPos(startY);
            pointTurtle(startDirection);
            setPenState(true);
            
            displayMessage("Drew a square with size " + size);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'square' must be a number. Example: square 100");
        }
    }
    
    /**
     * Handle the triangle command with validation
     * Supports both triangle <size> and triangle <side1>,<side2>,<side3> formats
     */
    private void handleTriangleCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'triangle'. Usage: triangle <size> or triangle <side1>,<side2>,<side3>");
            return;
        }
        
        if (parameter.contains(",")) {
            handleThreeSidedTriangle(parameter);
        } else {
            handleEquilateralTriangle(parameter);
        }
    }
    
    /**
     * Handle equilateral triangle with one size parameter
     */
    private void handleEquilateralTriangle(String parameter) {
        try {
            int size = Integer.parseInt(parameter.trim());
            
            if (size <= 0) {
                showErrorDialog("Size must be a positive number. Example: triangle 100");
                return;
            }
            
            prepareForShapeDrawing();
            
            int startX = getxPos();
            int startY = getyPos();
            int startDirection = getDirection();
            
            drawTriangle(size);
            
            setPenState(false);
            setxPos(startX);
            setyPos(startY);
            pointTurtle(startDirection);
            setPenState(true);
            
            displayMessage("Drew an equilateral triangle with side length " + size);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'triangle' must be a number. Example: triangle 100");
        }
    }
    
    /**
     * Handle triangle with three side parameters
     */
    private void handleThreeSidedTriangle(String parameter) {
        try {
            String[] sideParams = parameter.split(",");
            
            if (sideParams.length != 3) {
                showErrorDialog("Invalid number of parameters. Usage: triangle <side1>,<side2>,<side3>");
                return;
            }
            
            int side1 = Integer.parseInt(sideParams[0].trim());
            int side2 = Integer.parseInt(sideParams[1].trim());
            int side3 = Integer.parseInt(sideParams[2].trim());
            
            if (side1 <= 0 || side2 <= 0 || side3 <= 0) {
                showErrorDialog("Side lengths must be positive numbers.");
                return;
            }
            
            if (side1 + side2 <= side3 || side1 + side3 <= side2 || side2 + side3 <= side1) {
                showErrorDialog("Invalid triangle: The sum of any two sides must be greater than the third side.");
                return;
            }
            
            prepareForShapeDrawing();
            
            int startX = getxPos();
            int startY = getyPos();
            int startDirection = getDirection();
            
            drawArbitraryTriangle(side1, side2, side3);
            
            setPenState(false);
            setxPos(startX);
            setyPos(startY);
            pointTurtle(startDirection);
            setPenState(true);
            
            displayMessage("Drew a triangle with sides " + side1 + ", " + side2 + ", " + side3);
        } catch (NumberFormatException e) {
            showErrorDialog("Side lengths must be numbers. Usage: triangle <side1>,<side2>,<side3>");
        }
    }
    
    /**
     * Reset all settings including pen color, pen width, position, and direction
     */
    private void resetAll() {
        reset();
        setPenColour(defaultPenColor);
        setStroke(defaultPenWidth);
    }
    
    /**
     * Draw an equilateral triangle
     * @param size Size of each side
     */
    private void drawTriangle(int size) {
        for (int i = 0; i < 3; i++) {
            forward(size);
            right(120);
        }
    }
    
    /**
     * Draw a triangle with arbitrary side lengths
     * Implements the law of cosines to calculate angles
     * @param a First side length
     * @param b Second side length
     * @param c Third side length
     */
    private void drawArbitraryTriangle(int a, int b, int c) {
        double angleC = Math.acos((a*a + b*b - c*c) / (2.0 * a * b));
        double angleA = Math.acos((b*b + c*c - a*a) / (2.0 * b * c));
        
        int degreeC = (int) Math.round(Math.toDegrees(angleC));
        int degreeA = (int) Math.round(Math.toDegrees(angleA));
        
        forward(a);
        right(180 - degreeC);
        forward(b);
        right(180 - degreeA);
        forward(c);
    }
    
    /**
     * Show help information in a popup dialog
     */
    private void showHelpDialog() {
        StringBuilder help = new StringBuilder();
        help.append("Available commands:\n\n");
        help.append("about - Show about information\n\n");
        help.append("penup - Lift pen up\n");
        help.append("pendown - Put pen down\n");
        help.append("pencolour <red>,<green>,<blue> - Set custom RGB pen color\n");
        help.append("penwidth <width> - Set pen width\n\n");
        help.append("left <degrees> - Turn left\n");
        help.append("right <degrees> - Turn right\n");
        help.append("move <distance> - Move forward\n");
        help.append("reverse <distance> - Move backward\n\n");
        help.append("Colors: black, white, red, green, blue, yellow, orange, purple, pink, cyan\n\n");
        help.append("Shapes:\n");
        help.append("square <size> - Draw square and return to original position\n");
        help.append("triangle <size> - Draw equilateral triangle\n");
        help.append("triangle <side1>,<side2>,<side3> - Draw any triangle\n");
        help.append("star <size>, circle <radius>, hexagon <size>\n\n");
        help.append("reset - Reset position, pen color and width\n");
        help.append("clear - Clear the canvas\n\n");
        help.append("history - Show command history\n");
        help.append("foperation - Show file operations panel\n\n");
        help.append("File Operations:\n");
        help.append("save image - Save the current image\n");
        help.append("save commands - Save the command history\n");
        help.append("load image - Load an image from a file\n");
        help.append("load commands - Load and execute commands from a file\n");
        help.append("exit - Exit the application");
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, help.toString(), "Turtle Graphics Help", JOptionPane.INFORMATION_MESSAGE);
        });
        
        displayMessage("Help information displayed");
    }
    
    /**
     * Set drawing state before drawing shapes
     */
    private void prepareForShapeDrawing() {
        drawOn();
        setPenColour(Color.RED);
    }
    
    /**
     * Handle the left command with validation
     */
    private void handleLeftCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'left'. Usage: left <degrees>");
            return;
        }
        
        try {
            int degrees = Integer.parseInt(parameter);
            left(degrees);
            displayMessage("Turned left " + degrees + " degrees");
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'left' must be a number. Example: left 90");
        }
    }
    
    /**
     * Handle the right command with validation
     */
    private void handleRightCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'right'. Usage: right <degrees>");
            return;
        }
        
        try {
            int degrees = Integer.parseInt(parameter);
            right(degrees);
            displayMessage("Turned right " + degrees + " degrees");
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'right' must be a number. Example: right 90");
        }
    }
    
    /**
     * Handle the move command with validation and bounds checking
     */
    private void handleMoveCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'move'. Usage: move <distance>");
            return;
        }

        try {
            int distance = Integer.parseInt(parameter);

            if (distance <= 0) {
                showErrorDialog("Distance must be a positive number. Example: move 100");
                return;
            }

            // Calculate the new position
            int newX = getxPos() + (int) (distance * Math.cos(Math.toRadians(getDirection())));
            int newY = getyPos() + (int) (distance * Math.sin(Math.toRadians(getDirection())));

            // Check if the new position is within bounds
            if (newX < 0 || newX > getWidth() || newY < 0 || newY > getHeight()) {
                showErrorDialog("Move out of bounds! The turtle cannot move off the screen.");
                return;
            }

            forward(distance); // Move the turtle forward
            displayMessage("Moved forward " + distance + " units");
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'move' must be a number. Example: move 100");
        }
    }

    /**
     * Handle the reverse command with validation and bounds checking
     */
    private void handleReverseCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'reverse'. Usage: reverse <distance>");
            return;
        }

        try {
            int distance = Integer.parseInt(parameter);

            if (distance <= 0) {
                showErrorDialog("Distance must be a positive number. Example: reverse 100");
                return;
            }

            // Calculate the new position
            int newX = getxPos() - (int) (distance * Math.cos(Math.toRadians(getDirection())));
            int newY = getyPos() - (int) (distance * Math.sin(Math.toRadians(getDirection())));

            // Check if the new position is within bounds
            if (newX < 0 || newX > getWidth() || newY < 0 || newY > getHeight()) {
                showErrorDialog("Reverse out of bounds! The turtle cannot move off the screen.");
                return;
            }

            forward(-distance); // Move the turtle backward
            displayMessage("Moved backward " + distance + " units");
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'reverse' must be a number. Example: reverse 100");
        }
    }
    
    /**
     * Handle the star command with validation
     */
    private void handleStarCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'star'. Usage: star <size>\nExample: star 100");
            return;
        }
        
        try {
            int size = Integer.parseInt(parameter);
            
            if (size <= 0) {
                showErrorDialog("Size must be a positive number. Example: star 100");
                return;
            }
            
            prepareForShapeDrawing();
            
            drawStar(size);
            displayMessage("Drew a star with size " + size);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'star' must be a number. Example: star 100");
        }
    }
    
    /**
     * Handle the circle command with validation
     */
    private void handleCircleCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'circle'. Usage: circle <radius>\nExample: circle 50");
            return;
        }
        
        try {
            int radius = Integer.parseInt(parameter);
            
            if (radius <= 0) {
                showErrorDialog("Radius must be a positive number. Example: circle 50");
                return;
            }
            
            prepareForShapeDrawing();
            
            circle(radius);
            displayMessage("Drew a circle with radius " + radius);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'circle' must be a number. Example: circle 50");
        }
    }
    
    /**
     * Handle the hexagon command with validation
     */
    private void handleHexagonCommand(String parameter) {
        if (parameter == null) {
            showErrorDialog("Missing parameter for 'hexagon'. Usage: hexagon <size>\nExample: hexagon 50");
            return;
        }
        
        try {
            int size = Integer.parseInt(parameter);
            
            if (size <= 0) {
                showErrorDialog("Size must be a positive number. Example: hexagon 50");
                return;
            }
            
            prepareForShapeDrawing();
            
            drawRegularPolygon(6, size);
            displayMessage("Drew a hexagon with size " + size);
        } catch (NumberFormatException e) {
            showErrorDialog("Parameter for 'hexagon' must be a number. Example: hexagon 50");
        }
    }
    
    /**
     * Draw a square pattern
     */
    private void drawSquare(int size) {
        for (int i = 0; i < 4; i++) {
            forward(size);
            right(90);
        }
    }
    
    /**
     * Draw a star pattern
     */
    private void drawStar(int size) {
        for (int i = 0; i < 5; i++) {
            forward(size);
            right(144);
        }
    }
    
    /**
     * Draw a regular polygon with specified number of sides
     */
    private void drawRegularPolygon(int sides, int size) {
        int angle = 360 / sides;
        for (int i = 0; i < sides; i++) {
            forward(size);
            right(angle);
        }
    }
    
    /**
     * Show an error message in a popup dialog
     */
    private void showErrorDialog(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        });
        displayMessage(message);
    }
    
    /**
     * Add a command to the command history
     */
    private void addToCommandHistory(String command) {
        commandHistory.add(command);
        updateCommandHistoryDisplay();
    }
    
    /**
     * Update the command history display
     */
    private void updateCommandHistoryDisplay() {
        StringBuilder historyText = new StringBuilder();
        for (String cmd : commandHistory) {
            historyText.append(cmd).append("\n");
        }
        commandHistoryArea.setText(historyText.toString());
        commandHistoryArea.setCaretPosition(commandHistoryArea.getDocument().getLength());
    }
    
    /**
     * Save the current image to a file
     */
    private void saveImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            
            try {
                BufferedImage image = getBufferedImage();
                ImageIO.write(image, "png", file);
                imageSaved = true;
                displayMessage("Image saved to " + file.getName());
            } catch (IOException e) {
                showErrorDialog("Error saving image: " + e.getMessage());
            }
        }
    }
    
    /**
     * Load an image from a file
     */
    private void loadImage() {
        if (!imageSaved) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Current image is not saved. Do you want to save it first?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                saveImage();
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PNG Images", "png"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                BufferedImage image = ImageIO.read(file);
                
                if (image != null) {
                    setBufferedImage(image);
                    imageSaved = true;
                    displayMessage("Image loaded from " + file.getName());
                } else {
                    showErrorDialog("Error: Could not load image. Invalid format.");
                }
            } catch (IOException e) {
                showErrorDialog("Error loading image: " + e.getMessage());
            }
        }
    }
    
    /**
     * Save the command history to a file
     */
    private void saveCommands() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Commands");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (String cmd : commandHistory) {
                    writer.println(cmd);
                }
                commandsSaved = true;
                displayMessage("Commands saved to " + file.getName());
            } catch (IOException e) {
                showErrorDialog("Error saving commands: " + e.getMessage());
            }
        }
    }
    
    /**
     * Load and execute commands from a file
     */
    private void loadCommands() {
        if (!commandsSaved) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "Current commands are not saved. Do you want to save them first?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                saveCommands();
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Commands");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text Files", "txt"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                int clearResponse = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to clear the current command history?",
                    "Clear History",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (clearResponse == JOptionPane.YES_OPTION) {
                    commandHistory.clear();
                    updateCommandHistoryDisplay();
                }
                
                String line;
                ProgressMonitor progressMonitor = new ProgressMonitor(
                    this, 
                    "Executing commands from file...", 
                    "", 0, 100);
                progressMonitor.setMillisToDecideToPopup(10);
                
                int totalLines = countLines(file);
                int processedLines = 0;
                
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        if (!line.toLowerCase().startsWith("load commands")) {
                            processCommand(line);
                            processedLines++;
                            int progress = (processedLines * 100) / totalLines;
                            progressMonitor.setProgress(progress);
                            progressMonitor.setNote("Processed " + processedLines + " of " + totalLines + " commands");
                            
                            if (progressMonitor.isCanceled()) {
                                break;
                            }
                            
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        } else {
                            showErrorDialog("Nested command loading is not allowed.");
                        }
                    }
                }
                
                progressMonitor.close();
                displayMessage("Commands loaded from " + file.getName());
                commandsSaved = true;
            } catch (IOException e) {
                showErrorDialog("Error loading commands: " + e.getMessage());
            }
        }
    }
    
    /**
     * Count lines in a file
     */
    private int countLines(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            int lines = 0;
            while (reader.readLine() != null) lines++;
            return lines;
        }
    }
    
    /**
     * Exit the application with confirmation if work is not saved
     */
    private void exitApplication() {
        boolean needsSave = !imageSaved || !commandsSaved;
        
        if (needsSave) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "You have unsaved work. Do you want to save before exiting?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (response == JOptionPane.YES_OPTION) {
                String[] options = {"Image", "Commands", "Both", "Cancel"};
                int saveChoice = JOptionPane.showOptionDialog(
                    this,
                    "What would you like to save?",
                    "Save Options",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                
                switch (saveChoice) {
                    case 0:
                        saveImage();
                        break;
                    case 1:
                        saveCommands();
                        break;
                    case 2:
                        saveImage();
                        saveCommands();
                        break;
                    case 3:
                        return;
                }
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        System.exit(0);
    }
    
    /**
     * Clear the display with a warning if the current image is not saved
     */
    @Override
    public void clear() {
        if (!imageSaved) {
            int response = JOptionPane.showConfirmDialog(
                this,
                "The current image is not saved. Do you want to save it before clearing?",
                "Unsaved Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                saveImage(); // Save the image if the user chooses to save
            } else if (response == JOptionPane.CANCEL_OPTION) {
                return; // Do not clear the display if the user cancels
            }
        }

        super.clear(); // Clear the display
        displayMessage("Display cleared");
    }
}