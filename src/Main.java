import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        int width = 800;
        int height = 300;

        JFrame frame = new JFrame();
        frame.setTitle("dinosaur game");
        frame.setSize(width, height);
        frame.setIconImage(new ImageIcon((new Main()).getClass().getResource("/resources/dino-jump.png")).getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        Game game = new Game(width, height);
        game.requestFocus();
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }
}
