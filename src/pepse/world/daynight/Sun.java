package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Represents the sun - moves across the sky in an elliptical path.
 */
public class Sun {
    private static final float INIT_ANGLE = 0f;
    private static final float FINAL_ANGLE = 360f;
    private static final float RADIUS = 300f;
    private static final String SUN_TAG = "sun";
    private static final float PART_OF_WINDOW_SIZE = 0.5f;
    private static final float SUN_SIZE = 100f;
    private static final int SUN_ANGLE_FACTOR = 90; // A factor used to calculate the next position of the sun
    private static final int POWER_VALUE = 2; // the value used in the Math.pow

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera coordinates).
     *
     * @param gameObjects      - The collection of all participating game objects.
     * @param layer            - The number of the layer to which the created sun should be added.
     * @param windowDimensions - The dimensions of the windows.
     * @param cycleLength      - The amount of seconds it should take the created game object to complete a full cycle.
     * @return A new game object representing the sun.
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {
        GameObject sun = new GameObject(windowDimensions.mult(PART_OF_WINDOW_SIZE),
                                        Vector2.ONES.mult(SUN_SIZE), new OvalRenderable(Color.YELLOW));
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        Consumer<Float> consumer = num -> sun.setCenter(calcSunPosition(windowDimensions, num,
                getOvalRadius(num)));
        new Transition<Float>(sun,
                consumer,
                INIT_ANGLE,
                FINAL_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }

    //todo check how to make it ellipse
    /*
    Calculates ellipse route
     */
    private static float getOvalRadius(float angleInSky) {
        return (float) ((RADIUS * RADIUS * 1.5f) /
                Math.sqrt(Math.pow(RADIUS *1.5f* Math.sin(Math.toRadians(angleInSky)), POWER_VALUE)
                        + Math.pow(RADIUS * Math.cos(Math.toRadians(angleInSky)), POWER_VALUE)));
    }

    /*
    Calculates the location of the sun according to the center of the window and an angle from the center.
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky, float radius) {
        Vector2 center = windowDimensions.mult(0.5f);
        return new Vector2(center.x() -
                (float) Math.cos(Math.toRadians(angleInSky + SUN_ANGLE_FACTOR)) * radius,
                center.y() - (float) Math.sin(Math.toRadians(angleInSky + SUN_ANGLE_FACTOR)) * radius);
    }
}
