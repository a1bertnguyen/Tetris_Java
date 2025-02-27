package Tetris;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.lang.module.FindException;
import java.util.ArrayList;
import java.util.Random;

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

    //Next mino
    Mino nextMino;
   final int NEXTMINO_X;
 final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();


    // Other
    public static int dropInterval = 60; // mino drop in every 60 frames.

    public Playmanager() {
        // main play areas frame
        left_X = (GamePanel.WIDTH / 2) - (WIDTH / 2); // 1280/2 - 360/2 = 460
        right_x = left_X + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_X + (WIDTH / 2) - Block.Size;
        MINO_START_Y = top_y + Block.Size;

        NEXTMINO_X = right_x +175;
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
     
        if (currentMino.active ==false){
            // if the Mino not active, put it into the staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            currentMino.deactivating =false;

            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino =pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // when a mino becomes inactive, check if line(s) can be delete
            checkDelete();




        }
        else{
            currentMino.update();
        }

    }
    private void checkDelete (){
        int x =left_X;
        int y =top_y;
        int blockCount =0;
        
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

        //Draw the static block
        for (int i = 0; i<staticBlocks.size(); i++){
            staticBlocks.get(i).draw(g2);
        }



        // draw pause
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));
        if (KeyHandler.pausedPressed) {
            x = left_X + 70;
            y = top_y + 320;
            g2.drawString("Paused", x, y);

        }
    }

}
