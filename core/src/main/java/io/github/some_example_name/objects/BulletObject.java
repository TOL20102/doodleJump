package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Static.GameSettings;


public class BulletObject extends GameObject {

    private boolean hasToBeDestroyed = false;
    private static final float BULLET_SPEED = 500f;
    private static final float DESPAWN_Y = 2000;

    public BulletObject(float x, float y, int width, int height, String texturePath, World world) {
        super(texturePath, (int)x, (int)y, width, height, GameSettings.BULLET_BIT, world);


        body.setGravityScale(0);
        body.setBullet(true);
        body.setLinearVelocity(new Vector2(0, BULLET_SPEED));
    }

    public void setHasToBeDestroyed(boolean destroy) {
        this.hasToBeDestroyed = destroy;
    }

    public boolean hasToBeDestroyed() {
        return hasToBeDestroyed || getY() > DESPAWN_Y;
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (!hasToBeDestroyed) {
            batch.draw(texture, getX() - (width / 2f), getY() - (height / 2f), width, height);
        }
    }

    public void update(float delta) {
    }
}
