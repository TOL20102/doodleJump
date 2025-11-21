package io.github.some_example_name.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Static.GameSettings;

public class BulletObject extends GameObject {

    public boolean wasHit;

    public BulletObject(int x, int y, int width, int height, String texturePath, World world) {
        super(texturePath, x, y, width, height, (short) 2, world);

        body.setLinearVelocity(new Vector2(0, 300));
        body.setBullet(true);
        wasHit = false;
    }

    public boolean hasToBeDestroyed() {
        return wasHit || (getY() - height / 2 > GameSettings.SCREEN_HEIGHT);
    }

    @Override
    public void hit() {
        wasHit = true;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
