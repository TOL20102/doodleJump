package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import java.util.Random;

public class PlateObject extends GameObject {
    private static Random random = new Random();

    public PlateObject(float y, float width, float height, short cBits, World world, float screenWidth) {
        super("i1.png", random.nextFloat() * (screenWidth - width), y, width, height, cBits, world, BodyDef.BodyType.StaticBody);
        setupOneWayPlatform();
    }

    private void setupOneWayPlatform() {
        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = (short) 2;
        fixtureDef.filter.maskBits = (short) 1;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        body.setUserData("platform");

        shape.dispose();
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }
}
