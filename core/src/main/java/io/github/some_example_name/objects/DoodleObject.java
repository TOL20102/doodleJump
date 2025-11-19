package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.math.Vector2;
import io.github.some_example_name.GameSettings;

public class DoodleObject extends GameObject {

    private static final float GYRO_SENSITIVITY = 150f;
    private static final float JUMP_FORCE = 15f;
    private static final float MOVE_FORCE = 8f;
    private boolean isOnGround = false;
    private boolean canJump = false;
    private float jumpCooldown = 0f;
    private static final float JUMP_COOLDOWN_TIME = 0.1f;

    private boolean autoJumpEnabled = true;
    private float autoJumpTimer = 0f;
    private static final float AUTO_JUMP_INTERVAL = 0.3f;

    public DoodleObject(String texturePath, float x, float y, float width, float height, short cBits, World world) {
        super(texturePath, x, y, width, height, cBits, world, BodyDef.BodyType.DynamicBody);
        setupPhysics();
    }

    private void setupPhysics() {
        body.setFixedRotation(true);
        body.setLinearDamping(0.3f);
        body.setUserData("doodle");

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = (short) 1;
        fixtureDef.filter.maskBits = (short) 2;

        body.createFixture(fixtureDef);
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

        if (jumpCooldown > 0) {
            jumpCooldown -= delta;
        }

        if (autoJumpEnabled) {
            autoJumpTimer += delta;
            if (autoJumpTimer >= AUTO_JUMP_INTERVAL) {
                performAutoJump();
                autoJumpTimer = 0f;
            }
        }

        checkGroundStatus();

        Vector2 velocity = body.getLinearVelocity();
        if (velocity.y < -25f) {
            body.setLinearVelocity(velocity.x, -25f);
        }

        handleScreenWrapping();
    }

    public void jump() {
        if (canJump && jumpCooldown <= 0) {
            performJump();
        }
    }
    public void move(int xs,int ys) {

        body.applyForceToCenter(new Vector2(
                xs,
                ys ),
            true
        );
    }

    private void performJump() {
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        body.applyLinearImpulse(new Vector2(0, JUMP_FORCE), body.getWorldCenter(), true);
        jumpCooldown = JUMP_COOLDOWN_TIME;
        canJump = false;
        isOnGround = false;
    }

    private void performAutoJump() {
        Vector2 velocity = body.getLinearVelocity();
        if (velocity.y < -5f && canJump) {
            performJump();
        }
    }

    public void moveLeft() {
        Vector2 velocity = body.getLinearVelocity();
        float targetVelocityX = -MOVE_FORCE * 4;
        body.setLinearVelocity(targetVelocityX, velocity.y);
    }

    public void moveRight() {
        Vector2 velocity = body.getLinearVelocity();
        float targetVelocityX = MOVE_FORCE * 4;
        body.setLinearVelocity(targetVelocityX, velocity.y);
    }

    public void moveByGyroscope(float gyroscopeX, float delta) {
        Vector2 velocity = body.getLinearVelocity();
        float targetVelocityX = -gyroscopeX * GYRO_SENSITIVITY;
        body.setLinearVelocity(targetVelocityX, velocity.y);
    }

    private void handleScreenWrapping() {
        float screenWidth = 720f;
        Vector2 position = body.getPosition();

        if (position.x < -width / 2) {
            body.setTransform(screenWidth - width / 2, position.y, 0);
        } else if (position.x > screenWidth + width / 2) {
            body.setTransform(width / 2, position.y, 0);
        }
    }

    private void checkGroundStatus() {
        Vector2 velocity = body.getLinearVelocity();
        if (Math.abs(velocity.y) < 1f) {
            isOnGround = true;
            canJump = true;
        } else {
            isOnGround = false;
        }
    }

    public void onPlatformCollision() {
        Vector2 velocity = body.getLinearVelocity();
        if (velocity.y <= 0) {
            canJump = true;
            isOnGround = true;
            body.setLinearVelocity(velocity.x, 0);
        }
    }

    public void setAutoJumpEnabled(boolean enabled) {
        this.autoJumpEnabled = enabled;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public boolean canJump() {
        return canJump && jumpCooldown <= 0;
    }

    public float getVerticalVelocity() {
        return body.getLinearVelocity().y;
    }
}
