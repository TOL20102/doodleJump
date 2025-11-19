package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.GameSettings;

public class DoodleObject extends GameObject {
    int x, y, width, height, livesLeft = 1;
    short cBits;
    private float previousY = body.getPosition().y; // Инициализация в начале
    private final float threshold = 0.02f; // Настройте по необходимости
    // В методе update или render (например, в render() или update(float delta))
    float currentY = body.getPosition().y;

    public DoodleObject(String texturePath, int x, int y, int width, int height, short cBits, World world) {
        super(texturePath,x,y,width,height,cBits,world);
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.x = x;
        this.y = y;
    }
    public void jump() {
        currentY = body.getPosition().y;

        if (Math.abs(currentY - previousY) < threshold && body.getLinearVelocity().y < -5) {
            float x = body.getPosition().x;
            float y = body.getPosition().y;
            body.applyLinearImpulse(0, 1000, x, y, true);
            System.out.println(Math.abs(currentY - previousY));
        }

        previousY = currentY;
    }
    public void move(int xs,int ys, boolean check) {
        if(check) {
            body.applyForceToCenter(xs,ys, true);
        }

        else {
            body.applyLinearImpulse(xs,ys,x,y,true);
        }


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
