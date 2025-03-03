package Tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class Playmanager {
    // main play areas
    // tao areas 12x20
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_X;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    // Next mino
    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Other
    public static int dropInterval = 60; // mino drop in every 60 frames.
    boolean Gameover;

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    public ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int line;
    int score;

    public Playmanager() {
        // main play areas frame
        left_X = (GamePanel.WIDTH / 2) - (WIDTH / 2); // 1280/2 - 360/2 = 460
        right_x = left_X + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_X + (WIDTH / 2) - Block.Size;
        MINO_START_Y = top_y + Block.Size;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = left_X + 125;

        // Set the starting Mino
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

    }

    private Mino pickMino() {
        // pick a random mino
        Mino mino = null;
        int i = new Random().nextInt(7);
        switch (i) {
            case 0:
                mino = new Mino_L1();
                break;
            case 1:
                mino = new Mino_Bar();
                break;
            case 2:
                mino = new Mino_L2();
                break;
            case 3:
                mino = new Mino_Square();
                break;
            case 4:
                mino = new Mino_T();
                break;
            case 5:
                mino = new Mino_Z1();
                break;
            case 6:
                mino = new Mino_Z2();
                break;
        }
        return mino;

    }

    public void update() {
        // check if the current mino is active

        if (currentMino.active == false) {
            // if the Mino not active, put it into the staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // check if the game is over
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                // this meas the current mino immediately collied a block and couldn't move at
                // all
                // so it's xy are the same with the nextMino's

                Gameover = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);

            }

            currentMino.deactivating = false;

            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // when a mino becomes inactive, check if line(s) can be delete
            checkDelete();

        } else {
            currentMino.update();
        }

    }

    private void checkDelete() {
        final int NUMBER_OF_COLUMNS = WIDTH / Block.Size;
        ArrayList<Integer> rowsToDelete = new ArrayList<>();
    
        // Count the number of blocks in each row
        HashMap<Integer, Integer> rowBlockCount = new HashMap<>();
        for (int index = 0; index < staticBlocks.size(); index++) {
            Block block = staticBlocks.get(index);
            int rowPosition = block.y;
    
            if (rowBlockCount.containsKey(rowPosition)) {
                int currentCount = rowBlockCount.get(rowPosition);
                rowBlockCount.put(rowPosition, currentCount + 1);
            } else {
                rowBlockCount.put(rowPosition, 1);
            }
        }
    
        // Identify full rows that need to be deleted
        for (Map.Entry<Integer, Integer> entry : rowBlockCount.entrySet()) {
            int rowPosition = entry.getKey();
            int blockCount = entry.getValue();
    
            if (blockCount == NUMBER_OF_COLUMNS) {
                rowsToDelete.add(rowPosition);
            }
        }
    
        // If no rows need to be deleted, return immediately
        if (rowsToDelete.isEmpty()) {
            return;
        }
    
        // Play delete effect and sound
        effectCounterOn = true;
        effectY.addAll(rowsToDelete);
        GamePanel.se.play(1, false);
    
        // Sort deleted rows in ascending order (important for shifting)
        Collections.sort(rowsToDelete);
    
        // Remove blocks in deleted rows
        ArrayList<Block> remainingBlocks = new ArrayList<>();
        for (int index = 0; index < staticBlocks.size(); index++) {
            Block block = staticBlocks.get(index);
            if (!rowsToDelete.contains(block.y)) {
                remainingBlocks.add(block);
            }
        }
    
        // Shift down blocks above deleted rows
        int rowsDeletedBelow = 0;
        for (int rowIndex = 0; rowIndex < rowsToDelete.size(); rowIndex++) {
            int deletedRow = rowsToDelete.get(rowIndex);
    
            for (int blockIndex = 0; blockIndex < remainingBlocks.size(); blockIndex++) {
                Block block = remainingBlocks.get(blockIndex);
    
                if (block.y < deletedRow) {
                    block.y += Block.Size;  // Move block down by one row
                }
            }
            rowsDeletedBelow++; // Track how many rows have been removed
        }
    
        // Update staticBlocks with only the remaining blocks
        staticBlocks.clear();
        staticBlocks.addAll(remainingBlocks);
    
        // Update score and level
        int numberOfClearedLines = rowsToDelete.size();
        line += numberOfClearedLines;
        score += numberOfClearedLines * 100;
    
        if (line % 10 == 0) {
            level++;
            dropInterval = Math.max(1, (int) (dropInterval * 0.9)); // Reduce speed by 10% per level
        }
    }
    


    public void draw(Graphics2D g2) {
        // Draw play area
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_X - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // Draw next Mino Frame
        int x = right_x + 100;
        int y = bottom_y - 200;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("Next", x + 60, y + 60);

        // Draw the currentMino
        if (currentMino != null) {
            currentMino.draw(g2);

        }
        // Draw the next Mino
        nextMino.draw(g2);

        // Draw the static block
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }
        // draw effect
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.red);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_X, effectY.get(i), WIDTH, Block.Size);

            }
            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();

            }
        }

        // draw pause and game over
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (Gameover) {
            g2.setColor(Color.red);
            g2.setFont(new Font("Arial", Font.BOLD, 50));
            g2.drawString("GAME OVER", left_X + 50, top_y + 300);
        }

        if (KeyHandler.pausedPressed && !Gameover) {
            g2.setColor(Color.yellow);
            g2.setFont(new Font("Arial", Font.BOLD, 40));
            g2.drawString("PAUSED", left_X + 100, top_y + 350);
        }
        int scoreX = right_x + 50;
        g2.drawRect(scoreX, top_y, 250, 150);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("LEVEL: " + level, scoreX + 20, top_y + 50);
        g2.drawString("LINES: " + line, scoreX + 20, top_y + 90);
        g2.drawString("SCORE: " + score, scoreX + 20, top_y + 130);

        // draw game title
        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("Times New Roman", Font.ITALIC, 60));

        g2.drawString("Simple Tetris", x, y);
    }

}
