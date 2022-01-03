package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 */
public class PepseGameManager extends GameManager {

    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    private static final int LOWER_TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    private static final int UPPER_TERRAIN_LAYER = Layer.STATIC_OBJECTS + 5;
    private static final int STUMP_LAYER = Layer.STATIC_OBJECTS + 10;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 20;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    private static final int RADIUS_FACTOR = 180;
    private static final float DAY_LENGTH = 30f;
    private static final int RANDOM_SEED_BOUND = 100;
    private static final float HALF_VALUE_FACTOR = 0.5f;



    private ImageReader imageReader;
    private SoundReader soundReader;
    private UserInputListener inputListener;
    private WindowController windowController;
    private int seed;
    private Vector2 windowDimensions;
    private Random random;
    private Terrain terrain;
    private Tree tree;
    private Avatar avatar;
    private int leftRenderBorder;
    private int rightRenderBorder;
    private int halfWindowX;

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        this.windowDimensions = windowController.getWindowDimensions();
        windowController.setTargetFramerate(80);
        seed = new Random().nextInt(RANDOM_SEED_BOUND);
        random = new Random(seed);
        createSky();

        leftRenderBorder = -RADIUS_FACTOR;
        rightRenderBorder = (int) Math.floor(windowDimensions.x() / Block.SIZE) * Block.SIZE + RADIUS_FACTOR;
        halfWindowX = (int) Math.floor(windowDimensions.x() / (2 * Block.SIZE)) * Block.SIZE;

        Night.create(this.gameObjects(), Layer.FOREGROUND, windowDimensions, DAY_LENGTH);

        GameObject sun = Sun.create(this.gameObjects(), Layer.BACKGROUND, windowDimensions, DAY_LENGTH);

        SunHalo.create(this.gameObjects(), SUN_HALO_LAYER, sun, HALO_COLOR);



        terrain = new Terrain(this.gameObjects(), LOWER_TERRAIN_LAYER, windowDimensions, seed);
        terrain.createInRange(leftRenderBorder, rightRenderBorder);

        tree = new Tree(this.gameObjects(), terrain::groundHeightAt, STUMP_LAYER, LEAF_LAYER, seed);
        tree.createInRange(leftRenderBorder, rightRenderBorder);

        gameObjects().layers().shouldLayersCollide(UPPER_TERRAIN_LAYER, LEAF_LAYER, true);

        avatar = Avatar.create(gameObjects(),
                Layer.DEFAULT,
                new Vector2(windowDimensions.x() * 0.5f, terrain.groundHeightAt(windowDimensions.x() * 0.5f) - 100),
                inputListener, imageReader);

        gameObjects().layers().shouldLayersCollide(UPPER_TERRAIN_LAYER, Layer.DEFAULT, true);
        gameObjects().layers().shouldLayersCollide(STUMP_LAYER, Layer.DEFAULT, true);

        setCamera(new Camera(avatar,
                windowController.getWindowDimensions().mult(0.5f).add(avatar.getTopLeftCorner().mult(-1)),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));

    }


    /*
     * Creates the sky in the game.
     */
    private void createSky() {
        Sky.create(this.gameObjects(), windowDimensions, Layer.BACKGROUND);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (avatar.getCenter().x() + halfWindowX + RADIUS_FACTOR - rightRenderBorder > Block.SIZE ){
            terrain.createInRange(rightRenderBorder, rightRenderBorder + Block.SIZE);
            tree.createInRange(rightRenderBorder, rightRenderBorder + Block.SIZE);
            for (GameObject obj: gameObjects()) {
                if (obj.getCenter().x() < leftRenderBorder + Block.SIZE){
                    gameObjects().removeGameObject(obj, LOWER_TERRAIN_LAYER);
                    gameObjects().removeGameObject(obj, UPPER_TERRAIN_LAYER);
                    gameObjects().removeGameObject(obj, STUMP_LAYER);
                    gameObjects().removeGameObject(obj, LEAF_LAYER);
                }
            }
            rightRenderBorder += Block.SIZE;
            leftRenderBorder += Block.SIZE;
        } else if (leftRenderBorder - avatar.getCenter().x() + halfWindowX + RADIUS_FACTOR > Block.SIZE){
            terrain.createInRange(leftRenderBorder - Block.SIZE, leftRenderBorder);
            tree.createInRange(leftRenderBorder - Block.SIZE, leftRenderBorder);
            for (GameObject obj: gameObjects()) {
                if (obj.getCenter().x() > rightRenderBorder - Block.SIZE){
                    gameObjects().removeGameObject(obj, LOWER_TERRAIN_LAYER);
                    gameObjects().removeGameObject(obj, UPPER_TERRAIN_LAYER);
                    gameObjects().removeGameObject(obj, STUMP_LAYER);
                    gameObjects().removeGameObject(obj, LEAF_LAYER);
                }
            }
            rightRenderBorder -= Block.SIZE;
            leftRenderBorder -= Block.SIZE;
        }
    }

    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
