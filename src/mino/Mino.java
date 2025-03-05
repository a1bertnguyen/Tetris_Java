package mino;

import java.awt.Color;
import java.awt.Graphics2D;

import Tetris.GamePanel;
import Tetris.KeyHandler;
import Tetris.Playmanager;

public class Mino {
    public Block b[] = new Block[4];
    public Block tempB[] = new Block[4];
    int autoDropCouter = 0;
    public int direction = 1; // there are 4 direction (1/2/3/4)
    boolean leftCollision, rightCollision, bottomCollision;
    public boolean active = true;
    public boolean deactivating;
    int deactivateCounter = 0;

    public void create(Color c) {
        b[0] = new Block(c);
        b[1] = new Block(c);
        b[2] = new Block(c);
        b[3] = new Block(c);
        tempB[0] = new Block(c);
        tempB[1] = new Block(c);
        tempB[2] = new Block(c);
        tempB[3] = new Block(c);
    }

    public void setXY(int x, int y) {

    }

    public void updateXY(int direction) {
        checkRotationCollision();
        if (leftCollision == false && rightCollision == false && bottomCollision == false) {
            this.direction = direction;
            b[0].x = tempB[0].x;
            b[0].y = tempB[0].y;
            b[1].x = tempB[1].x;
            b[1].y = tempB[1].y;
            b[2].x = tempB[2].x;
            b[2].y = tempB[2].y;
            b[3].x = tempB[3].x;
            b[3].y = tempB[3].y;

        }
    }

    public void getDirection1() {}

    public void getDirection2() {}

    public void getDirection3() {}

    public void getDirection4() {}

    public void checkMovementCollision() {
        leftCollision = false;
        rightCollision = false;
        bottomCollision = false;
        checkStaticcollision();
        // check frame collison
        // left wall
        for (int i = 0; i < b.length; i++) {
            if (b[i].x == Playmanager.left_X) {
                leftCollision = true;
            }

        }

        // right wall
        for (int i = 0; i < b.length; i++) {
            if (b[i].x + Block.Size == Playmanager.right_x) {
                rightCollision = true;
            }

        }
        for (int i = 0; i < b.length; i++) {
            if (b[i].y + Block.Size == Playmanager.bottom_y) {
                bottomCollision = true;
            }
        }

    }

    public void checkRotationCollision() {
        // left wall
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x < Playmanager.left_X) {
                leftCollision = true;
            }

        }

        // right wall
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].x + Block.Size > Playmanager.right_x) {
                rightCollision = true;
            }

        }
        for (int i = 0; i < b.length; i++) {
            if (tempB[i].y + Block.Size > Playmanager.bottom_y) {
                bottomCollision = true;
            }
        }

    }

    private void checkStaticcollision() {

        for (int i = 0; i < Playmanager.staticBlocks.size(); i++) {
            int targetX = Playmanager.staticBlocks.get(i).x;
            int targetY = Playmanager.staticBlocks.get(i).y;

            // check down
            for (int ii = 0; ii < b.length; ii++) {
                if (b[ii].y + Block.Size == targetY && b[ii].x == targetX) {
                    bottomCollision = true;
                }

            }
            // check left
            for (int ii = 0; ii < b.length; ii++) {
                if (b[ii].x - Block.Size == targetX && b[ii].y == targetY) {
                    leftCollision = true;
                }
            }

            // check right
            for (int ii = 0; ii < b.length; ii++) {
                if (b[ii].x + Block.Size == targetX && b[ii].y == targetY) {
                    rightCollision = true;
                }
            }

        }

    }

    public void update() {
        if (deactivating) {
            deactivating();
        }
        // Move the mino
        if (KeyHandler.upPressed) {
            switch (direction) {
                case 1:
                    getDirection2();
                    break;
                case 2:
                    getDirection3();
                    break;
                case 3:
                    getDirection4();
                    break;
                case 4:
                    getDirection1();
                    break;
            }
            KeyHandler.upPressed = false;
            GamePanel.se.play(3, false);

        }
        checkMovementCollision();
        if (KeyHandler.downPressed) {
            // if the mino's bottom is not hitting, it can go down
            if (bottomCollision == false) {
                b[0].y += Block.Size;
                b[1].y += Block.Size;
                b[2].y += Block.Size;
                b[3].y += Block.Size;
                // when moved down, reset the autoDropcouter
                autoDropCouter = 0;

            }

            KeyHandler.downPressed = false;

        }

        if (KeyHandler.leftPressed) {
            if (leftCollision == false) {
                b[0].x -= Block.Size;
                b[1].x -= Block.Size;
                b[2].x -= Block.Size;
                b[3].x -= Block.Size;
            }

            KeyHandler.leftPressed = false;
        }
        if (KeyHandler.rightPressed) {
            if (rightCollision == false) {
                b[0].x += Block.Size;
                b[1].x += Block.Size;
                b[2].x += Block.Size;
                b[3].x += Block.Size;
            }

            KeyHandler.rightPressed = false;
        }
        if (bottomCollision) {
            if (deactivating ==false){
                GamePanel.se.play(4, false);
            }
            deactivating = true;
        } else {
            autoDropCouter++;
            if (autoDropCouter == Playmanager.dropInterval) {
                // the mino goes down
                b[0].y += Block.Size;
                b[1].y += Block.Size;
                b[2].y += Block.Size;
                b[3].y += Block.Size;
                autoDropCouter = 0;
            }
        }

    }

    private void deactivating() {
        deactivateCounter++;
        // wait 45 frame until diactivate

        if (deactivateCounter == 45) {
            deactivateCounter = 0;
            checkMovementCollision(); // check if the bottom is still hiting

            // if the bottom is stil hitting afer 45 frame, deactivate the mino
            if (bottomCollision) {
                active = false;
            }
        }
    }

    public void draw(Graphics2D g2) {
        int margin = 2;

        g2.setColor(b[0].c);
        g2.fillRect(b[0].x + margin, b[0].y + margin, Block.Size - (margin * 2), Block.Size - (margin * 2));
        g2.fillRect(b[1].x + margin, b[1].y + margin, Block.Size - (margin * 2), Block.Size - (margin * 2));
        g2.fillRect(b[2].x + margin, b[2].y + margin, Block.Size - (margin * 2), Block.Size - (margin * 2));
        g2.fillRect(b[3].x + margin, b[3].y + margin, Block.Size - (margin * 2), Block.Size - (margin * 2));
    }

}
