import java.awt.FlowLayout;
import javax.swing.JFrame;


public class MainClass extends TurtleGraphics {


    public static void main(String[] args) {
        new MainClass();
    }

    public MainClass() {
        JFrame mainFrame = new JFrame("Turtle Graphics");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new FlowLayout());
        mainFrame.add(this);
        mainFrame.pack();
        mainFrame.setVisible(true);
        about();
    }
}
