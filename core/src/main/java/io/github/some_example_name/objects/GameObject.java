package io.github.some_example_name.objects;

import static io.github.some_example_name.GameSettings.SCALE;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class GameObject {
    public short cBits;

    public float width;
    public float height;

    public Body body;
    Texture texture;
    World world;


    public GameObject(String texturePath, float x, float y, float width, float height, short cBits, World world, BodyDef.BodyType bodyType) {
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.world = world;
        this.texture = new Texture(texturePath);
        this.body = createBody(x, y, bodyType);
    }

    public void draw(SpriteBatch batch) {

        batch.draw(texture,
            (body.getPosition().x * SCALE) - (width / 2f),
            (body.getPosition().y * SCALE) - (height / 2f),
            width,
            height);
    }


    public float getX() {
        return (body.getPosition().x * SCALE) - (width / 2f);
    }

    public float getY() {
        return (body.getPosition().y * SCALE) - (height / 2f);
    }


    public void setX(float x) {
        body.setTransform((x + width / 2f) / SCALE, body.getPosition().y, 0);
    }

    public void setY(float y) {
        body.setTransform(body.getPosition().x, (y + height / 2f) / SCALE, 0);
    }

    private Body createBody(float x, float y, BodyDef.BodyType bodyType) {
        BodyDef def = new BodyDef();
        def.type = bodyType;
        def.fixedRotation = true;
        Body newBody = world.createBody(def);

        CircleShape circleShape = new CircleShape();

        circleShape.setRadius(Math.max(width, height) / 2f / SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 0.1f;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = cBits;

        Fixture fixture = newBody.createFixture(fixtureDef);
        fixture.setUserData(this);
        circleShape.dispose();


        newBody.setTransform((x + width / 2f) / SCALE, (y + height / 2f) / SCALE, 0);
        return newBody;
    }

    public void update(float delta) {

    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
        if (body != null && world != null && !world.isLocked()) {
            world.destroyBody(body);
            body = null;
        }
    }
}
