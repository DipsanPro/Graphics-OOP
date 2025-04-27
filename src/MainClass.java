import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import uk.ac.leedsbeckett.oop.LBUGraphics;

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

    public void processCommand(String command) {
        if ("about".equals(command.trim())) {
            about();
        }
    }
}