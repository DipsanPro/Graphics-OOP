import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;

public class MainClass {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TurtleGraphics turtlePanel = new TurtleGraphics();

                JFrame mainFrame = new JFrame("OOP Assignment 1: Turtle Graphics");
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                mainFrame.setLayout(new FlowLayout());
                mainFrame.add(turtlePanel);

                mainFrame.pack();
                mainFrame.setLocationRelativeTo(null);
                mainFrame.setVisible(true);
            }
        });
    }
}
