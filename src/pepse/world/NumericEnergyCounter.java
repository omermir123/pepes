package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

public class NumericEnergyCounter extends GameObject{
    private final TextRenderable text;
    private final Avatar avatar;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public NumericEnergyCounter(Vector2 topLeftCorner, Vector2 dimensions, GameObjectCollection gameObjects ,Avatar avatar, int layer) {
        super(topLeftCorner, dimensions, null);
        this.avatar = avatar;
        text = new TextRenderable(String.format("%.1f", this.avatar.getEnergy()));
        GameObject numericEnergy = new GameObject(topLeftCorner, dimensions, text);
        numericEnergy.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(numericEnergy, layer);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        text.setString(String.format("%.1f", this.avatar.getEnergy()));
    }
}
