package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Responsible for the creation and management of trees.
 */
public class Tree {
    private static final int MINIMAL_TREE_HEIGHT = 5;
    private static final Color STUMP_COLOR = new Color(100, 50, 20);
    private static final String STUMP_TAG = "stump";
    private static final String LEAF_TAG = "leaf";
    private static final int RANDOM_TREE_HEIGHT_BOND = 11;
    private static final int RANDOM_BOND_TO_PLANT_TREE = 9;

    private final GameObjectCollection gameObjects;
    private final Function<Float, Float> heightFunc;
    private final int stumpLayer;
    private final int leafLayer;
    private int seed;
    private Random random;
    private Map<Integer, SingleTree> treeMap = new HashMap<>();

    /**
     * A constructor to create a new Tree instance.
     * @param gameObjects - The collection of all participating game objects.
     * @param heightFunc - A callback function used to calculate the ground height in a given location.
     * @param stumpLayer - The layer to add the stump of the tree.
     * @param leafLayer - The layer to add the leaves of the tree.
     * @param seed - The seed in which to create the random instance with.
     */
    public Tree(GameObjectCollection gameObjects, Function<Float, Float> heightFunc, int stumpLayer,
                                                                            int leafLayer, int seed) {
        this.gameObjects = gameObjects;
        this.heightFunc = heightFunc;
        this.stumpLayer = stumpLayer;
        this.leafLayer = leafLayer;
        this.seed = seed;
    }

    /**
     * This method creates trees in a given range of x-values.
     * @param minX - The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxX - The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minX, int maxX) {
        for (int i = minX; i < maxX; i += Block.SIZE) {
            random = new Random(Objects.hash(i , seed));
            if (random.nextInt(RANDOM_BOND_TO_PLANT_TREE) == 0) {
                buildTree(i, heightFunc.apply((float)i));
            }
        }
    }

    public void deleteInRange(int minX, int maxX){
        for (int i = minX; i < maxX; i += Block.SIZE){
            if (treeMap.containsKey(i)) {
                treeMap.get(i).removeTree();
            }
        }
    }



}
