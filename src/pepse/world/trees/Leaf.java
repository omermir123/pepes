package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * A class responsible for the creation and management of a leaf.
 */
public class Leaf extends Block {

    private static final int FADEOUT_TIME = 10;
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final Random random = new Random();
    private static final int RANDOM_TIME_ON_TREE_BOUND = 120;
    private static final int RANDOM_TIME_ON_GROUND_BOUND = 100;
    private static final int FALLING_LEAF_VELOCITY = 50;
    private static final float HORIZONTAL_LEAF_MOVEMENT_BOUND = 50f;
    private static final float HORIZONTAL_TRANSITION_TIME = 1f;
    private static final float ANGLE_VALUE_FOR_TRANSITION_RANGE = 5f;
    private static final float ANGLE_CHANGE_TRANSITION_TIME = 0.5f;
    private static final float CHANGE_WIDTH_FACTOR = 2f;
    private static final float CHANGE_WIDTH_TRANSITION_TIME = 1f;

    private float leafTimeOnTree;
    private float timeOnGround;
    private Vector2 topLeftCorner;
    private GameObjectCollection gameObjects;
    private int layer;
    private boolean isOnGround = false;
    private Transition<Float> horizontalTransition;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    public Leaf(Vector2 topLeftCorner, GameObjectCollection gameObjects, int layer) {
        super(topLeftCorner, new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.topLeftCorner = topLeftCorner;
        this.gameObjects = gameObjects;
        this.layer = layer;
        leafTimeOnTree = (float) random.nextInt(RANDOM_TIME_ON_TREE_BOUND);
        timeOnGround = (float) random.nextInt(RANDOM_TIME_ON_GROUND_BOUND) + FADEOUT_TIME;
        MakeLeafMove();


    }

    /*
    This function creates a scheduled task that makes the leaf move and fall after some time.
     */
    private void MakeLeafMove() {
        new ScheduledTask(this,
                (this.hashCode() % 10) * 0.5f,
                false,
                this::shakeInTheWindMovement);
        new ScheduledTask(this, leafTimeOnTree, false, this::startFallingSequence);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, null);
        this.setVelocity(Vector2.ZERO);
        removeComponent(horizontalTransition);
        isOnGround = true;
        new ScheduledTask(this, timeOnGround, false, this::afterTimeOnGround);
    }

    /*
    This function makes the leaf fall from the tree.
     */
    private void startFallingSequence() {
        this.renderer().fadeOut(FADEOUT_TIME);
        this.transform().setVelocityY(FALLING_LEAF_VELOCITY);
        horizontalTransition = new Transition<Float>(this,
                transform()::setVelocityX,
                HORIZONTAL_LEAF_MOVEMENT_BOUND,
                -HORIZONTAL_LEAF_MOVEMENT_BOUND,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                HORIZONTAL_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /*
    This function manages what to do when the leaf life cycle is over.
     */
    private void afterTimeOnGround() {
        gameObjects.removeGameObject(this, layer);
        gameObjects.addGameObject(new Leaf(topLeftCorner, gameObjects, layer), layer);
    }

    /*
    This function starts the transitions responsible for making the leaves change width and angle so they
    will shake in the wind.
     */
     private void shakeInTheWindMovement(){
         new Transition<Float>(this,
                 this.renderer()::setRenderableAngle,
                 this.renderer().getRenderableAngle() - ANGLE_VALUE_FOR_TRANSITION_RANGE,
                 this.renderer().getRenderableAngle() + ANGLE_VALUE_FOR_TRANSITION_RANGE,
                 Transition.CUBIC_INTERPOLATOR_FLOAT,
                 ANGLE_CHANGE_TRANSITION_TIME,
                 Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                 null);
         new Transition<Float>(this,
                 num -> this.setDimensions(new Vector2(Block.SIZE + num, Block.SIZE - num)),
                 -CHANGE_WIDTH_FACTOR,
                 CHANGE_WIDTH_FACTOR,
                 Transition.CUBIC_INTERPOLATOR_FLOAT,
                 CHANGE_WIDTH_TRANSITION_TIME,
                 Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                 null);
     }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (isOnGround){
            this.setVelocity(Vector2.ZERO);
        }
    }
}
