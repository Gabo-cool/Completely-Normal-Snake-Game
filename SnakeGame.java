import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
 
public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int SCREEN_WIDTH = 600;
    private static final int SCREEN_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100;
 
    private final int[] snakeX = new int[GAME_UNITS];
    private final int[] snakeY = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private Timer timer;
    private final Random random = new Random();
 
    public SnakeGame() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setupKeyBindings();
        startGame();
    }
 
    private void setupKeyBindings() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
 
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), "moveLeft");
        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "moveRight");
        inputMap.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "restart");
 
        actionMap.put("moveLeft", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (direction != 'R') direction = 'L';
            }
        });
        actionMap.put("moveRight", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (direction != 'L') direction = 'R';
            }
        });
        actionMap.put("moveUp", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (direction != 'D') direction = 'U';
            }
        });
        actionMap.put("moveDown", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (direction != 'U') direction = 'D';
            }
        });
        actionMap.put("restart", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                if (!running) restartGame();
            }
        });
    }
 
    private void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
 
    private void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawBackground(g2);
 
        if (running) {
            drawApple(g2);
            drawSnake(g2);
            drawScore(g2);
        } else {
            gameOver(g2);
        }
 
        g2.dispose();
    }
 
    private void drawBackground(Graphics2D g) {
        GradientPaint grass = new GradientPaint(
                0, 0, new Color(105, 190, 82),
                0, SCREEN_HEIGHT, new Color(35, 125, 55));
        g.setPaint(grass);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
 
        // Small grass pattern for a less plain background.
        g.setStroke(new BasicStroke(1.2f));
        g.setColor(new Color(255, 255, 255, 25));
        for (int x = 0; x < SCREEN_WIDTH; x += 50) {
            for (int y = 45; y < SCREEN_HEIGHT; y += 50) {
                g.drawLine(x, y, x + 3, y - 6);
                g.drawLine(x + 3, y - 6, x + 7, y);
            }
        }
 
        // Soft border around the playing area.
        g.setColor(new Color(20, 85, 35));
        g.setStroke(new BasicStroke(6));
        g.drawRect(3, 3, SCREEN_WIDTH - 6, SCREEN_HEIGHT - 6);
    }
 
    private void drawApple(Graphics2D g) {
        int padding = 3;
        int x = appleX + padding;
        int y = appleY + padding + 2;
        int size = UNIT_SIZE - padding * 2;
 
        // Apple shadow.
        g.setColor(new Color(0, 0, 0, 45));
        g.fillOval(x + 2, y + 3, size, size);
 
        // Apple body.
        g.setColor(new Color(205, 25, 35));
        g.fillOval(x, y, size, size);
        g.setColor(new Color(245, 55, 55));
        g.fillOval(x + 3, y + 2, size / 2, size / 2);
 
        // Stem and leaf.
        g.setColor(new Color(90, 55, 25));
        g.setStroke(new BasicStroke(3));
        g.drawLine(appleX + UNIT_SIZE / 2, appleY + 5,
                appleX + UNIT_SIZE / 2 + 2, appleY - 2);
        g.setColor(new Color(35, 115, 45));
        g.fillOval(appleX + UNIT_SIZE / 2 + 1, appleY - 3, 10, 6);
    }
 
    private void drawSnake(Graphics2D g) {
        for (int i = bodyParts - 1; i >= 0; i--) {
            int x = snakeX[i] + 2;
            int y = snakeY[i] + 2;
            int size = UNIT_SIZE - 4;
 
            g.setColor(new Color(0, 0, 0, 35));
            g.fillRoundRect(x + 2, y + 3, size, size, 12, 12);
 
            if (i == 0) {
                g.setColor(new Color(25, 125, 45));
            } else {
                g.setColor(new Color(55, 170, 65));
            }
            g.fillRoundRect(x, y, size, size, 12, 12);
 
            g.setColor(new Color(125, 220, 100, 150));
            g.fillOval(x + 5, y + 4, 5, 5);
        }
 
        drawSnakeFace(g);
    }
 
    private void drawSnakeFace(Graphics2D g) {
        int headX = snakeX[0];
        int headY = snakeY[0];
        int eyeSize = 4;
        int eye1X = headX + 7;
        int eye1Y = headY + 6;
        int eye2X = headX + 15;
        int eye2Y = headY + 6;
 
        if (direction == 'U' || direction == 'D') {
            eye1X = headX + 6;
            eye2X = headX + 15;
            eye1Y = eye2Y = headY + 7;
            if (direction == 'D') eye1Y = eye2Y = headY + 15;
        } else if (direction == 'L') {
            eye1X = eye2X = headX + 6;
            eye1Y = headY + 7;
            eye2Y = headY + 15;
        } else {
            eye1X = eye2X = headX + 15;
            eye1Y = headY + 7;
            eye2Y = headY + 15;
        }
 
        g.setColor(Color.WHITE);
        g.fillOval(eye1X, eye1Y, eyeSize, eyeSize);
        g.fillOval(eye2X, eye2Y, eyeSize, eyeSize);
        g.setColor(Color.BLACK);
        g.fillOval(eye1X + 1, eye1Y + 1, 2, 2);
        g.fillOval(eye2X + 1, eye2Y + 1, 2, 2);
 
        // Forked tongue.
        g.setColor(new Color(220, 35, 80));
        g.setStroke(new BasicStroke(2));
        int centerX = headX + UNIT_SIZE / 2;
        int centerY = headY + UNIT_SIZE / 2;
        if (direction == 'R') {
            g.drawLine(headX + UNIT_SIZE - 1, centerY, headX + UNIT_SIZE + 5, centerY);
            g.drawLine(headX + UNIT_SIZE + 5, centerY, headX + UNIT_SIZE + 9, centerY - 3);
            g.drawLine(headX + UNIT_SIZE + 5, centerY, headX + UNIT_SIZE + 9, centerY + 3);
        } else if (direction == 'L') {
            g.drawLine(headX + 1, centerY, headX - 5, centerY);
            g.drawLine(headX - 5, centerY, headX - 9, centerY - 3);
            g.drawLine(headX - 5, centerY, headX - 9, centerY + 3);
        } else if (direction == 'U') {
            g.drawLine(centerX, headY + 1, centerX, headY - 5);
            g.drawLine(centerX, headY - 5, centerX - 3, headY - 9);
            g.drawLine(centerX, headY - 5, centerX + 3, headY - 9);
        } else {
            g.drawLine(centerX, headY + UNIT_SIZE - 1, centerX, headY + UNIT_SIZE + 5);
            g.drawLine(centerX, headY + UNIT_SIZE + 5, centerX - 3, headY + UNIT_SIZE + 9);
            g.drawLine(centerX, headY + UNIT_SIZE + 5, centerX + 3, headY + UNIT_SIZE + 9);
        }
    }
 
    private void drawScore(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(10, 10, 125, 38, 15, 15);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 23));
        g.drawString("Score: " + applesEaten, 22, 37);
    }
 
    private void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }
 
    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }
 
        switch (direction) {
            case 'U':
                snakeY[0] -= UNIT_SIZE;
                break;
            case 'D':
                snakeY[0] += UNIT_SIZE;
                break;
            case 'L':
                snakeX[0] -= UNIT_SIZE;
                break;
            case 'R':
                snakeX[0] += UNIT_SIZE;
                break;
            default:
                break;
        }
    }
 
    private void checkApple() {
        if (snakeX[0] == appleX && snakeY[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
 
    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) {
                running = false;
            }
        }
 
        if (snakeX[0] < 0 || snakeX[0] >= SCREEN_WIDTH
                || snakeY[0] < 0 || snakeY[0] >= SCREEN_HEIGHT) {
            running = false;
        }
 
        if (!running) {
            timer.stop();
        }
    }
 
    private void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2,
                SCREEN_HEIGHT / 2 - 30);
 
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 25));
        String scoreText = "Score: " + applesEaten;
        metrics = getFontMetrics(g.getFont());
        g.drawString(scoreText, (SCREEN_WIDTH - metrics.stringWidth(scoreText)) / 2,
                SCREEN_HEIGHT / 2 + 20);
 
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String restartText = "Press ENTER to play again";
        metrics = getFontMetrics(g.getFont());
        g.drawString(restartText, (SCREEN_WIDTH - metrics.stringWidth(restartText)) / 2,
                SCREEN_HEIGHT / 2 + 60);
    }
 
    private void restartGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            snakeX[i] = 0;
            snakeY[i] = 0;
        }
        newApple();
        running = true;
        timer.start();
        requestFocusInWindow();
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }
 
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
            case KeyEvent.VK_ENTER:
                if (!running) restartGame();
                break;
            default:
                break;
        }
    }
 
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }
 
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        SwingUtilities.invokeLater(game::requestFocusInWindow);
    }
}
 
 