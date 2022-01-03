package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;

/**
 * A class of an avatar that can move around the world.
 */
public class Avatar extends GameObject {
    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -300;
    private static final float GRAVITY = 500;
    private static final String STANDING_IMAGE = "assets/avatar/sneaky-toast-preview.png";
    private static final int NUM_OF_WALKING_IMG = 8;
    private static final int NUM_OF_FLYING_IMG = 4;
    private static final float MAX_ENERGY = 100f;
    private static final float ENERGY_FACTOR = 0.5f;
    private static final double TIME_BETWEEN_CLIPS = 0.1;
    private static final String AVATAR_TAG = "Avatar";
    private static final String END_OF_IMG_DIR = "_delay-0.1s.gif";
    private static final String WALKING_ANIMATION_DIR = "assets/avatar/walk/frame_";
    private static final String FLYING_ANIMATION_DIR = "assets/avatar/fly/frame_";
    private static final int AVATAR_SIZE = 100;

    private static UserInputListener inputListener;
    private static ImageReader imageReader;
    private static Renderable standingRenderer;
    private static AnimationRenderable walkingAnimationRenderer;
    private static AnimationRenderable flyingAnimationRenderer;
    private float energy;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        energy = MAX_ENERGY;
    }

    /*
     * Adds to the avatar the animation of his walking.
     */
    private static void createRenderWalkAnimation() {
        Renderable[] walking = new Renderable[NUM_OF_WALKING_IMG];
        for (int i = 0; i < walking.length; i++) {
            walking[i] = imageReader.readImage(WALKING_ANIMATION_DIR + String.valueOf(i) +
                    END_OF_IMG_DIR, true);
        }
        walkingAnimationRenderer = new AnimationRenderable(walking, TIME_BETWEEN_CLIPS);
    }

    /*
    Adds to the avatar the animation of the way he flies.
     */
    private static void createRenderFlyAnimation() {
        Renderable[] flying = new Renderable[NUM_OF_FLYING_IMG];
        for (int i = 0; i < flying.length; i++) {
            flying[i] = imageReader.readImage(FLYING_ANIMATION_DIR + String.valueOf(i) + END_OF_IMG_DIR,
                    true);
        }
        flyingAnimationRenderer = new AnimationRenderable(flying, TIME_BETWEEN_CLIPS);
    }

    /**
     * This function creates an avatar that can travel the world and is followed by the camera. The can stand,
     * walk, jump and fly, and never reaches the end of the world.
     *
     * @param gameObjects   - The collection of all participating game objects.
     * @param layer         - The number of the layer to which the created avatar should be added.
     * @param topLeftCorner - The location of the top-left corner of the created avatar.
     * @param inputListener - Used for reading input from the user.
     * @param imageReader   - Used for reading images from disk or from within a jar.
     * @return A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader) {
        Avatar.inputListener = inputListener;
        Avatar.imageReader = imageReader;
        standingRenderer = imageReader.readImage(STANDING_IMAGE, true);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), standingRenderer);
        gameObjects.addGameObject(avatar, layer);
        avatar.setTag(AVATAR_TAG);
        createRenderWalkAnimation();
        createRenderFlyAnimation();
        return avatar;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            if (!renderer().isFlippedHorizontally()) {
                renderer().setIsFlippedHorizontally(true);
            }
            renderer().setRenderable(walkingAnimationRenderer);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            if (renderer().isFlippedHorizontally()) {
                renderer().setIsFlippedHorizontally(false);
            }
            renderer().setRenderable(walkingAnimationRenderer);
        }
        transform().setVelocityX(xVel);
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            if (energy > 0) {
                energy -= ENERGY_FACTOR;
                transform().setVelocityY(VELOCITY_Y);
                renderer().setRenderable(flyingAnimationRenderer);
            }
        } else if (this.transform().getVelocity().y() == 0 && energy < MAX_ENERGY) {
            energy += ENERGY_FACTOR;
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0) {
            transform().setVelocityY(VELOCITY_Y);
        }
        if (!(inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) &&
                !(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) &&
                !(inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                        inputListener.isKeyPressed(KeyEvent.VK_SHIFT))) {
            renderer().setRenderable(standingRenderer);
        }
    }
}
