import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class Game extends JPanel implements ActionListener, KeyListener {
    int screenWidth;
    int screenHeight;

    Image dinoDeadImg;
    Image dinoRunImg;
    Image dinoJumpImg;
    Image dinoDuckImg;
    Image cactus1Img;
    Image cactus2Img;
    Image cactus3Img;
    Image cloudImg;
    Image trackImg;
    Image gameOverImg;
    Image resetImg;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;

        public Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    Block dino;
    ArrayList<Block> tracks;
    ArrayList<Block> cactus;
    ArrayList<Block> clouds;

    Timer gameLoop;
    Timer cactusGenerator;
    Timer cloudGenerator;
    int velocityX = -7;
    int velocityY = 0;
    int gravity = 1;
    boolean duck = false;
    boolean gameOver = false;
    int score = 0;

    public Game(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        addKeyListener(this);
        setFocusable(true);

        dinoDeadImg = new ImageIcon(getClass().getResource("/resources/dino-dead.png")).getImage();
        dinoRunImg = new ImageIcon(getClass().getResource("/resources/dino-run.gif")).getImage();
        dinoJumpImg = new ImageIcon(getClass().getResource("/resources/dino-jump.png")).getImage();
        dinoDuckImg = new ImageIcon(getClass().getResource("/resources/dino-duck.gif")).getImage();
        dinoDeadImg = new ImageIcon(getClass().getResource("/resources/dino-dead.png")).getImage();
        cactus1Img = new ImageIcon(getClass().getResource("/resources/cactus1.png")).getImage();
        cactus2Img = new ImageIcon(getClass().getResource("/resources/cactus2.png")).getImage();
        cactus3Img = new ImageIcon(getClass().getResource("/resources/cactus3.png")).getImage();
        cloudImg = new ImageIcon(getClass().getResource("/resources/cloud.png")).getImage();
        trackImg = new ImageIcon(getClass().getResource("/resources/track.png")).getImage();
        gameOverImg = new ImageIcon(getClass().getResource("/resources/game-over.png")).getImage();
        resetImg = new ImageIcon(getClass().getResource("/resources/reset.png")).getImage();

        tracks = new ArrayList<>();
        tracks.add(new Block(0, screenHeight-28, 2404, 28, trackImg));
        dino = new Block(30, screenHeight-94, 88, 94, dinoRunImg);
        cactus = new ArrayList<>();
        cactusGenerator = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCactus();
            }
        });
        clouds = new ArrayList<>();
        cloudGenerator = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateCloud();
            }
        });

        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
        cactusGenerator.start();
        cloudGenerator.start();
    }

    void generateCactus() {
        double percent = Math.random();
        Block cact;
        if(percent > 0.9) {
            cact = new Block(screenWidth, screenHeight-70-5, 102, 70, cactus3Img);
            cactus.add(cact);
        }
        else if(percent > 0.7) {
            cact = new Block(screenWidth, screenHeight-70-5, 69, 70, cactus2Img);
            cactus.add(cact);
        }
        else if(percent > 0.4) {
            cact = new Block(screenWidth, screenHeight-70-2, 34, 70, cactus1Img);
            cactus.add(cact);
        }

        if(cactus.size() > 5) {
            cactus.remove(0);
        }
    }

    void generateCloud() {
        Block cloud = new Block(screenWidth, (int)(Math.random()*screenHeight/2), 84, 101, null);
        clouds.add(cloud);
        if(clouds.size() > 5) {
            clouds.remove(0);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0, screenWidth, screenHeight);

        for(Block track: tracks) {
            g.drawImage(track.img, track.x, track.y, track.width, track.height, null);
        }
        for(Block c: cactus) {
            g.drawImage(c.img, c.x, c.y, c.width, c.height, null);
        }
        for(Block cloud: clouds) {
            g.drawImage(cloud.img, cloud.x, cloud.y, cloud.width, cloud.height, null);
        }
        g.drawImage(dino.img, dino.x, dino.y, dino.width, dino.height, null);

        g.setColor(Color.black);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString(String.valueOf(score), 10, 30);

        if(gameOver) {
            g.drawImage(gameOverImg, screenWidth/2-386/2, screenHeight/2-50, 382, 40, null);
            g.drawImage(resetImg, screenWidth/2-76/2, screenHeight/2+10, 76, 68, null);
        }
    }

    void move() {
        // track
        Block track = tracks.get(tracks.size()-1);
        if(track.x + track.width <= screenWidth + 100) {
            tracks.add(new Block(track.x + track.width - 5, track.y, track.width, track.height, track.img));
        }
        if(tracks.size() > 2) {
            tracks.remove(0);
        }
        for(Block t: tracks) {
            t.x += velocityX;
        }

        // cactus
        for (Block c: cactus) {
            c.x += velocityX;
            if(collide(dino, c)) {
                gameOver = true;
                return;
            }
        }

        // dino
        velocityY += gravity;
        dino.y += velocityY;
        dino.y = Math.min(dino.y, screenHeight-dino.height);
        if(!duck && dino.y == screenHeight-dino.height) {
            dino.img = dinoRunImg;
        }
    }

    boolean collide(Block k, Block b) {
        // formula for detecting collisions
        return  k.x < b.x + b.width &&      // k's top left corner doesn't reach b's top right corner
                k.x + k.width > b.x &&      // k's top right corner passes b's top left corner
                k.y < b.y + b.height &&     // k's top left corner doesn't reach b's bottom left corner
                k.y + k.height > b.y;       // k's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        Toolkit.getDefaultToolkit().sync();
        score++;

        if(gameOver) {
            cactusGenerator.stop();
            cloudGenerator.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_W) {
            if(dino.y == screenHeight-dino.height) {
                velocityY = -19;
                dino.img = dinoJumpImg;
                dino.width = 88;
                dino.height = 94;
                duck = false;
            }

            if(gameOver) {
                gameOver = false;
                score = 0;
                cactus.clear();
                tracks.clear();
                tracks.add(new Block(0, screenHeight-28, 2404, 28, trackImg));
                dino = new Block(30, screenHeight-94, 88, 94, dinoRunImg);
                cactusGenerator.start();
                cloudGenerator.start();
                gameLoop.start();
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_S) {
            if(dino.y == screenHeight-dino.height) {
                duck = true;
                dino.img = dinoDuckImg;
                dino.width = 118;
                dino.height = 60;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(dino.y == screenHeight-dino.height) {
            duck = false;
            dino.width = 88;
            dino.height = 94;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
