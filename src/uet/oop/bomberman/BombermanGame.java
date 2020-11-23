package uet.oop.bomberman;

import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import uet.oop.bomberman.Character.Vector;
import uet.oop.bomberman.entities.*;
import uet.oop.bomberman.entities.Item.BombItem;
import uet.oop.bomberman.entities.Item.FlameItem;
import uet.oop.bomberman.entities.Item.Portal;
import uet.oop.bomberman.entities.Item.SpeedItem;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.Camera.GameCamera;
import uet.oop.bomberman.notEntity.Bomb;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.*;

public class BombermanGame extends Application {
    
    public static final int WIDTH = 20;
    public static final int HEIGHT = 16;
    private static int realHeight;
    private static int realWidth;
    private long lastTime = 0;
    private int gameTime = 0;


    private GraphicsContext gc;
    private Canvas canvas;
    private Group root;

    private List<Entity> items = new ArrayList<>();
    public static List<Enemy> enemies = new ArrayList<>();
    private List<Entity> walls = new ArrayList<>();
    private static Map<Vector, Brick> bricks = new HashMap<>();
    public static String input = "";
    public static char[][] map;

    public static Bomber bomberman;
    public static List<Bomb> bombs = new ArrayList<>();
    GameCamera gameCamera = new GameCamera(0,0);


    public static void main(String[] args) {
        Application .launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        // Tao Canvas
        createMap("levels/Level2.txt");
        canvas = new Canvas(Sprite.SCALED_SIZE * realWidth, Sprite.SCALED_SIZE * realHeight);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.rgb(80, 160,0));

        // Tao root container
        root = new Group();
        root.getChildren().add(canvas);

        // Tao scene
        Scene scene = new Scene(root);
        scene.setFill(Color.rgb(80, 160,0));

        // Them scene vao stage
        stage.setScene(scene);
        stage.setHeight(HEIGHT * Sprite.SCALED_SIZE);
        stage.setWidth(WIDTH * Sprite.SCALED_SIZE);
        stage.show();

        scene.setOnKeyPressed(
                (javafx.scene.input.KeyEvent event) -> {
                    String keyName = event.getCode().toString();
                    if ( !input.equals(keyName) ) {
                        input = keyName;
                    }
                }
        );

        scene.setOnKeyReleased(
                (javafx.scene.input.KeyEvent event) -> {
                    input = "";
                }
        );


        bomberman = new Bomber(new Vector(1,1), Sprite.player_right.getFxImage());
        gameTime = 0;
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                if (l - lastTime > 100000000 / bomberman.getMaxSpeed()) {
                    update(l);
                    updateOther(l);
                    setLastTime(l);
                }
                handleCollision();
            }
        };

        render();
        timer.start();
    }

    public void handleCollision() {
        bomberman.handleCollision();
        for (Enemy enemy : enemies) {
            if (bomberman.handle_1_Collision(enemy)) {
                bomberman.setDead(true);
                break;
            }
        }

        int i = 0;
        while (i < items.size()) {
            if (bomberman.handle_1_Collision(items.get(i))) {
                if (items.get(i) instanceof BombItem) { bomberman.setMaxBomb(bomberman.getMaxBomb() + 1); }
                if (items.get(i) instanceof FlameItem) { bomberman.setMaxBombLength(bomberman.getMaxBombLength() + 1); }
                if (items.get(i) instanceof SpeedItem) { bomberman.setMaxSpeed(bomberman.getMaxSpeed() * 2); }
                items.get(i).clear(gc);
                items.remove(i);
            } else { i++; }

        }
    }

    public void createMap(String filePath) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            File myObj = new File(cl.getResource(filePath).getFile());
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                int level = myReader.nextInt();
                int height= myReader.nextInt();
                realHeight = height;
                int width = myReader.nextInt();
                realWidth = width;
                map = new char[height][width];
                String row = myReader.nextLine();
                for (int i = 0; i < height; i++ ) {
                    row = myReader.nextLine();
                    for (int j = 0; j < width; j++) {
                        map[i][j] = row.charAt(j);
                        switch (map[i][j]) {
                            case '#' :
                                Wall wall = new Wall(new Vector(j, i), Sprite.wall.getFxImage());
                                walls.add(wall);
                                break;
                            case '*' :
                                Brick brick = new Brick(new Vector(j, i), Sprite.brick.getFxImage());
                                bricks.put(new Vector(j, i), brick);
                                break;
                            case '1' :
                                Enemy enemy = new Enemy(new Vector(j, i), Sprite.balloom_left1.getFxImage());
                                enemies.add(enemy);
                                map[i][j] = ' ';
                                break;
                            case '2' :
                                Brick brick1 = new Brick(new Vector(j, i), Sprite.brick.getFxImage());
                                bricks.put(new Vector(j, i), brick1);
                                SpeedItem speedItem = new SpeedItem(bricks.get(new Vector(j, i)));
                                bricks.get(new Vector(j, i)).setContain(speedItem);
                                items.add(speedItem);
                                map[i][j] = '*';
                                break;
                            case '3' :
                                Brick brick2 = new Brick(new Vector(j, i), Sprite.brick.getFxImage());
                                bricks.put(new Vector(j, i), brick2);
                                BombItem bombItem = new BombItem(bricks.get(new Vector(j, i)));
                                bricks.get(new Vector(j, i)).setContain(bombItem);
                                items.add(bombItem);
                                map[i][j] = '*';
                                break;
                            case '4' :
                                Brick brick3 = new Brick(new Vector(j, i), Sprite.brick.getFxImage());
                                bricks.put(new Vector(j, i), brick3);
                                FlameItem flameItem = new FlameItem(bricks.get(new Vector(j, i)));
                                bricks.get(new Vector(j, i)).setContain(flameItem);
                                items.add(flameItem);
                                map[i][j] = '*';
                                break;
                            case '5' :
                                Brick brick4 = new Brick(new Vector(j, i), Sprite.brick.getFxImage());
                                bricks.put(new Vector(j, i), brick4);
                                Portal portal = new Portal(bricks.get(new Vector(j, i)));
                                bricks.get(new Vector(j, i)).setContain(portal);
                                items.add(portal);
                                map[i][j] = '*';
                                break;
                            default:
                                //Grass grass = new Grass(new Vector(j, i), Sprite.grass.getFxImage());
                                //stillObjects.put(grass.getPosition(), grass);
                        }
                    }
                }

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void update(long time) {
        gameCamera.update(bomberman);
        canvas.setTranslateX(-gameCamera.getxOffset());
        canvas.setTranslateY(-gameCamera.getyOffset());
        bomberman.position.add(gameCamera.getxOffset() - gameCamera.getLastXOffset(),
                gameCamera.getyOffset() - gameCamera.getLastYOffset());
        bomberman.update(gc);
    }

    public void updateOther(long time) {
        enemies.forEach(g -> g.update(gc));
        int j = 0;
        while (j < enemies.size()) {
            if (enemies.get(j).isDead()) {
                enemies.remove(j);
            } else { j++; }
        }
        bombs.forEach(g -> g.update(time, gc));
        int i = 0;
        while (i < bombs.size()) {
            if (bombs.get(i).isExploded()) {
                bombs.remove(i);
            } else { i++; }
        }
    }



    public void render() {
        walls.forEach(g -> g.render(gc));
        for (Map.Entry<Vector, Brick> entry : bricks.entrySet()) {
            entry.getValue().render(gc);
        }
    }

    public static int getRealHeight() {
        return realHeight;
    }

    public static int getRealWidth() {
        return realWidth;
    }

    public void setLastTime(long l) {
        lastTime = l;
    }

    public static Map<Vector, Brick> getBricks() {
        return bricks;
    }
}
