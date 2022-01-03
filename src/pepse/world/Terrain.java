package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

/**
 * A class responsible for the creation and management of terrain.
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 30;
    private static final float NOISE_FACTOR = 200;
    private static final String TERRAIN_TAG = "Ground";
    private static final float AVERAGE_HEIGHT_FACTOR = (float) (2.0 / 3);
    private static final int ALTITUDE_FACTOR = 2;
    private static final int LAYER_DIFFERENCE = 5;

    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private final int seed;
    private final Random random;
    private final float sinFactor;
    private final float piFactor;
    private final int start_height;


    /**
     * A constructor for a new Terrain instance.
     * @param gameObjects      - The collection of all participating game objects.
     * @param groundLayer      - The number of the layer to which the created ground objects should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param seed             - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        random = new Random(seed);
        sinFactor = random.nextFloat() / NOISE_FACTOR;
        piFactor = random.nextFloat() / NOISE_FACTOR;
        start_height = (int) (windowDimensions.y() * AVERAGE_HEIGHT_FACTOR);
    }

    /**
     * This method return the ground height at a given location.
     *
     * @param x - A number.
     * @return The ground height at the given location
     */
    public float groundHeightAt(float x) {
        float height = (float) (start_height + (Block.SIZE * ALTITUDE_FACTOR * (Math.sin(sinFactor * x) +
                Math.sin(piFactor * Math.PI * x))));
        return (float) ((int) (height / Block.SIZE) * Block.SIZE);
    }

    /**
     * This method creates terrain in a given range of x-values.
     *
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX; i += Block.SIZE) {
            int startOfCol = (int) groundHeightAt((float) i);
            createColumnOfDirt(i, startOfCol);
        }
    }


    /*
    Creates a column if the ground starting from the (x, y) location on the screen.
     */
    private void createColumnOfDirt(int x, int y) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block block = new Block(new Vector2(x, y + i * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
            if (i <= 1) {
                gameObjects.addGameObject(block, groundLayer + LAYER_DIFFERENCE);
            } else {
                gameObjects.addGameObject(block, groundLayer);
            }
            block.setTag(TERRAIN_TAG);
        }
    }

}
