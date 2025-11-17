package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.GameSettings;

public class DoodleObject extends GameObject{
    int x, y, width, height, livesLeft = 1;
    short cBits;
    int lastX=x;

    public void jump() {
        if (Math.abs(x-lastX)<100) {
            body.applyForceToCenter(new Vector2(
                    0,
                    500 * GameSettings.SHIP_FORCE_RATIO),
                true
            );
            lastX=x;
        }
    }
    public DoodleObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        super(texturePath,x,y,width,height,cBits,world);
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.x = x;
        this.y = y;
    }

    public void move() {

        body.applyForceToCenter(new Vector2(
                    0,
                500 * GameSettings.SHIP_FORCE_RATIO),
            true
        );
    }

    @Override
    public void draw(SpriteBatch batch) {
        putInFrame();
        super.draw(batch);
    }

    public int getLiveLeft() {return livesLeft;}
    private void putInFrame() {
        if (getY() <= (height / 2f)) {
            setY(height / 2);
        }
        if (getX() < (-width / 2f)) {
            setX(GameSettings.SCREEN_WIDTH);
        }
        if (getX() > (GameSettings.SCREEN_WIDTH + width / 2f)) {
            setX(0);
        }
    }

}
