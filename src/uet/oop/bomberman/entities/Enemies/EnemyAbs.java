package uet.oop.bomberman.entities.Enemies;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.Character.Vector;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.graphics.Sprite;

import java.util.ArrayList;
import java.util.Random;

public abstract class EnemyAbs extends Entity {

    protected int time;
    protected int timeDead = 0;
    protected Vector lastPosition;
    protected int speed = 2;
    protected Vector vel;
    protected int iMap;
    protected int jMap;
    protected boolean dr[] = {false, false, false, false};
    protected final int left = 0, right = 1, up = 2, down = 3;
    protected boolean dead = false;
    protected boolean completelyDead = false;
    protected ArrayList<Image> images;

    public EnemyAbs(Vector p, Image img) {
        super(p, img);
        images = new ArrayList<>();
        addImage();
        vel = new Vector(0,0);
    }

    public void update(GraphicsContext gc) {
        if (!dead) {
            if (dr[0]) {
                setLeftAnimation();
            } else {
                setRightAnimation();
            }
            move();
        } else {
            setDeadAnimation();
        }
        gc.clearRect(lastPosition.x, lastPosition.y, Sprite.SCALED_SIZE, Sprite.SCALED_SIZE);
        render(gc);
        lastPosition.setVector(position.x, position.y);
    }

    public void setLeftAnimation() {
        switch (time % 12) {
            case 0 :
                img = images.get(0);
                break;
            case 4 :
                img = images.get(1);
                break;
            case 8 :
                img = images.get(2);
                break;
        }
        time++;
    }

    public void setRightAnimation() {
        switch (time % 12) {
            case 0 :
                img = images.get(3);
                break;
            case 4 :
                img = images.get(4);
                break;
            case 8 :
                img = images.get(5);
                break;
        }
        time++;
    }

    public void setDeadAnimation() {
        if (timeDead <= 6) { img = images.get(6); }
        else if (timeDead <= 12) { img = images.get(7); }
        else if (timeDead <= 18) { img = images.get(8); }
        else {
            img = images.get(9);
            if (time >= 25) {
                completelyDead = true;
            }
        }
        timeDead++;
    }

    public void setFalse() {
        for (int i = 0; i < 4; i++) { dr[i] = false; }
    }

    public void setDirect() {
        int ran = new Random().nextInt(4);
        setFalse();
        dr[ran] = true;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isCompletelyDead() {
        return completelyDead;
    }

    public abstract void addImage();

    public abstract void move();

    public abstract void handleCollition();

    protected boolean isMovingVertical() {
        return ((int)Math.round(position.y)) % Sprite.SCALED_SIZE != 0;
    }

    protected boolean isMovingHorizontal() {
        return ((int) Math.round(position.x)) % Sprite.SCALED_SIZE != 0;
    }

}
