package Tetris;

import java.awt.Color;

import javax.security.auth.kerberos.KeyTab;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class GamePanel extends JPanel implements Runnable {
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 160;
    Thread gameThread;
    Playmanager pm;

    public GamePanel() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.BLACK);
        this.setLayout(null);
        // Implement Keylistener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        pm = new Playmanager(); // Khởi tạo Playmanager
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        //game Loop
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
      if (KeyHandler.pausedPressed == false){
        pm.update();
      }
        

    }

    @Override
    protected void paintComponent(Graphics g) {
        {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;

            pm.draw(g2);
        }
    }
}