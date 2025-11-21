package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import java.util.Random;

public class PlateObject extends GameObject {
    private static Random random = new Random();


    public PlateObject(float y, float width, float height, short cBits, World world, float screenWidth) {
        super("i1.png",
            random.nextFloat() * (screenWidth - width) + width/2,
            y,
            width,
            height,
            cBits,
            world,
            BodyDef.BodyType.StaticBody);
    }

    @Override
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
