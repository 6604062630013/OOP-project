import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;
import javax.swing.*;

public class DragonPP extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Image dragonImageRight, dragonImageLeft, tigerImageRight, tigerImageLeft, orbImage, gameOverImage, winImage, rockImg;
    private Image level1Background, level2Background, level3Background, currentBackground,greenorbImg,blueorbImg;
    private int backgroundWidth, backgroundHeight;
    private int dragonX = 200, dragonY = 300;
    private int directionX = 0, directionY = 0;
    private int dragonDirectionX = 1;
    private boolean spacePressed = false;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private long lastShotTime = 0;
    private final int SHOOT_DELAY = 300;
    private JButton playAgainButton, quitButton,startButton;
    private ScheduledExecutorService executor;
    private ArrayList<Fireball> fireballs;
    private ArrayList<Tiger> tigers;
    private ArrayList<Rock> rocks;
    private ArrayList<Gjade> greenorbs;
    private ArrayList<Bjade> blueorbs;
    private long gameStartTime;
    private int tigerSpeed = 5;
    private int score = 0,speedup = 0,firesp = 0;
    private int dragonhealth = 3;
    public DragonPP() {
        dragonImageRight = new ImageIcon("./D.png").getImage();
        dragonImageLeft = new ImageIcon("./DL.png").getImage();
        tigerImageRight = new ImageIcon("./tigerL.png").getImage();
        tigerImageLeft = new ImageIcon("./tiger.png").getImage();
        orbImage = new ImageIcon("./Gorb.png").getImage();
        gameOverImage = new ImageIcon("./loss.png").getImage();
        winImage = new ImageIcon("./winbn.png").getImage();
        level1Background = new ImageIcon("./bg1.png").getImage();
        level2Background = new ImageIcon("./bg2.png").getImage();
        level3Background = new ImageIcon("./bg3.png").getImage();
        rockImg = new ImageIcon("./rock.png").getImage();
        greenorbImg = new  ImageIcon("./greenjade.png").getImage();
        blueorbImg = new  ImageIcon("./bluejade.png").getImage();
        currentBackground = level1Background;
        fireballs = new ArrayList<>();
        tigers = new ArrayList<>();
        rocks = new ArrayList<>();
        greenorbs = new ArrayList<>();
        blueorbs = new ArrayList<>();
        
        timer = new Timer(20, this);
        
        setFocusable(true);
        addKeyListener(this);

        
        startButton = new JButton("Start Game");
        startButton.setBounds(450, 300, 120, 50);
        startButton.addActionListener(e -> startGame());
        setLayout(null);
        add(startButton);
    }
    
    private void startGame() {
        gameStartTime = System.currentTimeMillis();
      
        timer.start();
        remove(startButton);
        setFocusable(true);
        requestFocusInWindow();
        executor = Executors.newScheduledThreadPool(3);
        executor.scheduleAtFixedRate(this::spawnTiger, 0, 5, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::spawnRock, 10, 15, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::spawnb, 30, 70, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(this::spawng, 15, 55, TimeUnit.SECONDS); 
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(currentBackground, 0, 0, null);

        if (gameOver) {
            showGameOverScreen(g);
            return;
        }

        if (gameWon) {
            showWinScreen(g);
            return;
        }

        g.drawImage(orbImage, 450, 400, null);
        if (dragonDirectionX > 0) {
            g.drawImage(dragonImageRight, dragonX, dragonY, null);
        } else {
            g.drawImage(dragonImageLeft, dragonX, dragonY, null);
        }

        for (Tiger tiger : tigers) {
            if (tiger.directionX > 0) {
                g.drawImage(tigerImageRight, tiger.x, tiger.y, null);
            } else {
                g.drawImage(tigerImageLeft, tiger.x, tiger.y, null);
            }
        }

        for (Fireball fireball : fireballs) {
            fireball.draw(g);
        }

        for (Rock rock : rocks) {
           g.drawImage(rockImg, rock.x, rock.y, null);;
        }
        for (Gjade greenorb : greenorbs) {
            g.drawImage(greenorbImg, greenorb.x, greenorb.y, null);;
        }
        for (Bjade blueorb : blueorbs) {
            g.drawImage(blueorbImg, blueorb.x, blueorb.y, null);;
        }

        long elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000;
        long remainingTime = Math.max(300 - elapsedTime, 0); 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Time: " + remainingTime + "s", getWidth() - 150, 30);
        g.drawString("Score: " + score, getWidth() - 150, 60);
        g.setColor(Color.RED);
        g.drawString("Health: " + dragonhealth, 20, getHeight() - 20);
    }
     
    private void showGameOverScreen(Graphics g) {
        g.drawImage(gameOverImage, 20, 120, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, getWidth()/2-45, 300);
        addGameOverButtons();
    }

    private void showWinScreen(Graphics g) {
        g.drawImage(winImage, 65, 120, null);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, getWidth()/2-45, 300);
        addGameOverButtons();
    }

    private void addGameOverButtons() {
        if (playAgainButton == null && quitButton == null) {
            playAgainButton = new JButton("Play Again");
            quitButton = new JButton("Quit");

            playAgainButton.setBounds(350, 400, 150, 50);
            quitButton.setBounds(550, 400, 150, 50);

            playAgainButton.addActionListener(e -> restartGame());
            quitButton.addActionListener(e -> System.exit(0));

            setLayout(null);
            add(playAgainButton);
            add(quitButton);
        }
    }

    private void restartGame() {
        gameOver = false;
        gameWon = false;
        tigers.clear();
        fireballs.clear();
        rocks.clear();
        dragonX = 200;
        dragonY = 300;
        directionX = 0;
        directionY = 0;
        dragonDirectionX = 1;
        currentBackground = level1Background;
        tigerSpeed = 5;
        dragonhealth = 3;
        score = 0;
        speedup = 0;
        firesp = 0;
        gameStartTime = System.currentTimeMillis();

        setFocusable(true);
        requestFocusInWindow();
        
        remove(playAgainButton);
        remove(quitButton);
        playAgainButton = null;
        quitButton = null;

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gameWon) {
            long elapsedTime = (System.currentTimeMillis() - gameStartTime) / 1000;

            if (elapsedTime >= 120 && elapsedTime < 240) {
                currentBackground = level2Background;
                tigerSpeed = 7;
            } else if (elapsedTime >= 240 && elapsedTime < 300) {
                currentBackground = level3Background;
                tigerSpeed = 9;
            } else if (elapsedTime >= 300) {
                gameWon = true;
                timer.stop();
            }

            moveDragon();
            moveFireballs();
            moveTigers();
            moveRocks();
            moveb();
            moveg();
            checkCollisions();
            checkOrbCollision();
            checkRockCollision();
            checkBCollision();
            checkGCollision();
            if (spacePressed) {
                shootFireball();
            }
            repaint();
        }
    }

    private void moveDragon() {
        dragonX += directionX;
        dragonY += directionY;
        if (dragonX < 0) dragonX = 0;
        if (dragonX > getWidth() - dragonImageRight.getWidth(null)) dragonX = getWidth() - dragonImageRight.getWidth(null);
        if (dragonY < 0) dragonY = 0;
        if (dragonY > getHeight() - dragonImageRight.getHeight(null)) dragonY = getHeight() - dragonImageRight.getHeight(null);
    }

    private void shootFireball() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShotTime >= SHOOT_DELAY) {
            int fireballStartX = dragonX + (dragonDirectionX > 0 ? dragonImageRight.getWidth(null) : 0);
            int fireballStartY = dragonY + dragonImageRight.getHeight(null) / 4;
            fireballs.add(new Fireball(fireballStartX, fireballStartY, dragonDirectionX, 0));
            lastShotTime = currentTime;
        }
    }

    private void moveFireballs() {
        for (int i = 0; i < fireballs.size(); i++) {
            Fireball fireball = fireballs.get(i);
            fireball.move();
            if (fireball.isOutOfBounds()) {
                fireballs.remove(i);
                i--;
            }
        }
    }

    private void spawnTiger() {
        Random random = new Random();
        int spawnSide = random.nextBoolean() ? -164 : 1024;
        int tigerY = random.nextInt(600);
        int tigerDirectionX = spawnSide == -164 ? 1 : -1;
        tigers.add(new Tiger(spawnSide, tigerY, tigerDirectionX));
    }
    private void moveTigers() {
        for (Tiger tiger : tigers) {
            double angleToOrb = Math.atan2(400 - tiger.y, 450 - tiger.x);
            tiger.x += (int) (tigerSpeed * Math.cos(angleToOrb));
            tiger.y += (int) (tigerSpeed * Math.sin(angleToOrb));
        }
    }

    private void spawnRock() {
        Random random = new Random();
        int rockX = random.nextInt(900);
        rocks.add(new Rock(rockX, 0)); 
    }

    private void moveRocks() {
        for (Rock rock : rocks) {
            rock.y += 5;
        }
    }
    private void spawnb() {
        Random random = new Random();
        int bX = random.nextInt(900);
        blueorbs.add(new Bjade(bX, 0)); 
    }

    private void moveb() {
        for (Bjade blueorb : blueorbs) {
            blueorb.y += 3;
        }
    }
    private void spawng() {
        Random random = new Random();
        int gX = random.nextInt(900);
        greenorbs.add(new Gjade(gX, 0)); 
    }

    private void moveg() {
        for (Gjade greenorb : greenorbs) {
            greenorb.y += 3;
        }
    }

    private void checkCollisions() {
        for (int i = 0; i < fireballs.size(); i++) {
            Fireball fireball = fireballs.get(i);
            for (int j = 0; j < tigers.size(); j++) {
                Tiger tiger = tigers.get(j);
                if (fireball.x < tiger.x + tigerImageRight.getWidth(null) - 50 && fireball.x + 10 > tiger.x &&
                    fireball.y < tiger.y + tigerImageRight.getHeight(null) && fireball.y + 10 > tiger.y) {
                    fireballs.remove(i);
                    tigers.remove(j);
                    i--;
                    score += 10;
                    break;
                }
            }
        }
    }

    private void checkRockCollision() {
        for (int i = 0; i < rocks.size(); i++) {
            Rock rock = rocks.get(i);
            if (dragonX < rock.x + rockImg.getWidth(null) && dragonX + dragonImageRight.getWidth(null) > rock.x &&
                dragonY < rock.y + rockImg.getHeight(null) && dragonY + dragonImageRight.getHeight(null) > rock.y) {
                rocks.remove(i);
                dragonhealth--;
                if (dragonhealth <= 0) {
                    gameOver = true;
                    timer.stop();
                }
                break;
            }
        }
    }
    private void checkBCollision() {
        for (int i = 0; i < blueorbs.size(); i++) {
            Bjade blueorb = blueorbs.get(i);
            if (dragonX < blueorb.x + blueorbImg.getWidth(null) && dragonX + dragonImageRight.getWidth(null) > blueorb.x &&
                dragonY < blueorb.y + blueorbImg.getHeight(null) && dragonY + dragonImageRight.getHeight(null) > blueorb.y) {
                blueorbs.remove(i);
                speedup += 2;
            }
        }
    }
    private void checkGCollision() {
        for (int i = 0; i < greenorbs.size(); i++) {
            Gjade greenorb = greenorbs.get(i);
            if (dragonX < greenorb.x + greenorbImg.getWidth(null) && dragonX + dragonImageRight.getWidth(null) > greenorb.x &&
                dragonY < greenorb.y + greenorbImg.getHeight(null) && dragonY + dragonImageRight.getHeight(null) > greenorb.y) {
                greenorbs.remove(i);
                firesp += 5;
            }
        }
    }

    private void checkOrbCollision() {
        for (Tiger tiger : tigers) {
            if (tiger.x + tigerImageRight.getWidth(null) - 50 > 450 && tiger.x < 450 + orbImage.getWidth(null) &&
                tiger.y + tigerImageRight.getHeight(null) - 30 > 400 && tiger.y < 400 + orbImage.getHeight(null)) {
                gameOver = true;
                timer.stop();
                break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) directionY = -5-speedup; 
        if (key == KeyEvent.VK_S) directionY = 5+speedup;  
        if (key == KeyEvent.VK_A) {
            directionX = -5-speedup;
            dragonDirectionX = -1;
        }
        if (key == KeyEvent.VK_D) {
            directionX = 5+speedup; 
            dragonDirectionX = 1; 
        }
        if (key == KeyEvent.VK_SPACE) {
            spacePressed = true; 
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_S) directionY = 0;  
        if (key == KeyEvent.VK_A || key == KeyEvent.VK_D) directionX = 0;  
        if (key == KeyEvent.VK_SPACE) {
            spacePressed = false; 
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    class Fireball {
        int x, y;
        int directionX, directionY;

        public Fireball(int x, int y, int directionX, int directionY) {
            this.x = x;
            this.y = y;
            this.directionX = directionX;
            this.directionY = directionY;
        }
        public void move() {
            x += directionX * (10 + firesp);  
        }
        public boolean isOutOfBounds() {
            return x < 0 || x > 1024; 
        }
        public void draw(Graphics g) {
            g.setColor(Color.RED);
            g.fillOval(x, y, 10, 10);  
        }
    }

    class Tiger {
        int x, y;
        int directionX;

        public Tiger(int x, int y, int directionX) {
            this.x = x;
            this.y = y;
            this.directionX = directionX;
        }
    }

    class Rock {
        int x, y;
        public Rock(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    class Gjade{
        int x, y;
        public Gjade(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    class Bjade {
        int x, y;
        public Bjade(int x, int y) {
            this.x = x;
            this.y = y;
        }  
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dragon Pew Pew");
        DragonPP game = new DragonPP();
        frame.add(game);
        frame.setSize(1024, 768);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }
}                                                                       