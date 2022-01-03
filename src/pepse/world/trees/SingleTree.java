package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class SingleTree {
    private static final int MINIMAL_TREE_HEIGHT = 5;
    private static final Color STUMP_COLOR = new Color(100, 50, 20);
    private static final String STUMP_TAG = "stump";
    private static final String LEAF_TAG = "leaf";
    private static final int RANDOM_TREE_HEIGHT_BOND = 11;

    private final int stumpLayer;
    private final int leafLayer;
    private final GameObjectCollection gameObjects;
    private final Random random;
    private final ArrayList<GameObject> treesBlocks = new ArrayList<>();

    public SingleTree(Random random, int stumpLayer, int leafLayer, GameObjectCollection gameObjects){
        this.random = random;
        this.stumpLayer = stumpLayer;
        this.leafLayer = leafLayer;
        this.gameObjects = gameObjects;
    }

    /**
     * This function builds a complete tree (stump and leaves) in the startOfTree location.
     * @param x
     * @param startOfTree
     */
    public void buildTree(float x, float startOfTree) {
        int treeHeight = MINIMAL_TREE_HEIGHT + random.nextInt(RANDOM_TREE_HEIGHT_BOND);
        for (int i = 1; i <= treeHeight; i++) {
            Block stump = new Block(new Vector2(x, startOfTree - Block.SIZE * i),
                    new RectangleRenderable(STUMP_COLOR));
            gameObjects.addGameObject(stump, stumpLayer);
            treesBlocks.add(stump);
            stump.setTag(STUMP_TAG);
        }
        Vector2 topLeftLeaf = new Vector2(x - 2 * Block.SIZE,
                startOfTree - Block.SIZE * (treeHeight + 2));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Leaf leaf = new Leaf(new Vector2(topLeftLeaf.x() + Block.SIZE * j,
                        topLeftLeaf.y() + Block.SIZE * i), gameObjects, leafLayer);
                gameObjects.addGameObject(leaf, leafLayer);
                treesBlocks.add(leaf);
                leaf.setTag(LEAF_TAG);

            }
        }

    }

    /**
     *
     */
    public void removeTree(){
        for (GameObject obj: treesBlocks) {
            gameObjects.removeGameObject(obj, stumpLayer);
            gameObjects.removeGameObject(obj, leafLayer);
        }
    }


}
