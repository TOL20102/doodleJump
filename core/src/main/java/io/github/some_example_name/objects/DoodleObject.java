package io.github.some_example_name.objects;

import static java.lang.Math.abs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.MyGdxGame;

public class DoodleObject extends GameObject {
    int x;
    int y;
    int width;
    public int height;
    int livesLeft = 1;
    short cBits;
    private boolean isOnPlatform = false;
    private boolean canJump = false;
    private float jumpCooldown = 0;
    private boolean isAlive = true;
    private boolean wasOnPlatform = false;

    private boolean movingLeft = false;
    private boolean movingRight = false;
    private float moveSpeed = 25f;
    private float acceleration = 0.3f;
    private float deceleration = 0.15f;
    private float currentSpeed = 0f;

    private boolean facingRight = true;
    private float rotation = 0f;
    private float maxRotation = 15f;
    ScoreManager scoreManager;

    private float currentCameraY = 0;
    public int lastY;

    private MyGdxGame game;

    public DoodleObject(String texturePath, int x, int y, int width, int height, short cBits, World world, MyGdxGame game) {
        super(texturePath,x,y,width,height,cBits,world);
        this.width = width;
        this.height = height;
        this.cBits = cBits;
        this.x = x;
        this.y = y;
        this.isAlive = true;
        this.game = game;
        scoreManager = new ScoreManager();
        lastY =y;
        body.setGravityScale(GameSettings.GRAVITY_SCALE);
        body.setLinearDamping(0.3f);
    }

    public void updateCameraPosition(float cameraY) {
        this.currentCameraY = cameraY;
    }

    public void update(float delta) {
        if (!isAlive) return;

        checkDeath();
        handleAutoJump();
        updateMovement();
        updateRotation(delta);
        handleScreenWrap();

        if (jumpCooldown > 0) {
            jumpCooldown -= delta;
        }

        if (!isOnPlatform) {
            canJump = false;
        }
    }
    public void setLastY(int a) {lastY = a;}
    public void chekY(int a) {if (a<=1000 && a>=-1000) {
        scoreManager.saveScore(0);

    }
    }

    private void handleAutoJump() {
        if (!isAlive) return;

        if (isOnPlatform && canJump && jumpCooldown <= 0) {
            performJump();
        }
    }

    private void performJump() {
        Vector2 velocity = body.getLinearVelocity();
        body.setLinearVelocity(velocity.x, GameSettings.DOODLE_JUMP_FORCE);

        canJump = false;
        isOnPlatform = false;
        wasOnPlatform = true;
        jumpCooldown = 0.2f;

        if (game != null && game.soundManager != null) {
            game.soundManager.playJumpSound();
        }

        System.out.println("🦘 JUMP! Force: " + GameSettings.DOODLE_JUMP_FORCE);
    }

    private void checkDeath() {
        if (!isAlive) return;

        float screenBottom = currentCameraY;
        float deathThreshold = screenBottom - 200;

        if (getY() < deathThreshold) {
            die();
            System.out.println("💀 DIED - Fell below camera! Doodle Y: " + getY());
            return;
        }

        if (getY() < -1000) {
            die();
            return;
        }
    }

    private void handleScreenWrap() {
        if (!isAlive) return;

        if (getX() < (-width / 2f)) {
            setX(GameSettings.SCREEN_WIDTH - width / 2);
        }

        if (getX() > (GameSettings.SCREEN_WIDTH + width / 2f)) {
            setX(width / 2);
        }
    }

    private void updateRotation(float delta) {
        float targetRotation = 0f;

        if (movingLeft) {
            targetRotation = maxRotation;
            facingRight = false;
        } else if (movingRight) {
            targetRotation = -maxRotation;
            facingRight = true;
        }

        float rotationSpeed = 10f * delta;
        if (abs(targetRotation - rotation) > 0.1f) {
            if (rotation < targetRotation) {
                rotation = Math.min(rotation + rotationSpeed, targetRotation);
            } else {
                rotation = Math.max(rotation - rotationSpeed, targetRotation);
            }
        } else {
            rotation = targetRotation;
        }

        if (!movingLeft && !movingRight) {
            if (abs(rotation) > 0.1f) {
                if (rotation > 0) {
                    rotation = Math.max(0, rotation - rotationSpeed * 0.5f);
                } else {
                    rotation = Math.min(0, rotation + rotationSpeed * 0.5f);
                }
            } else {
                rotation = 0f;
            }
        }
    }

    public void setOnPlatform(boolean onPlatform) {
        if (!isAlive) return;

        if (onPlatform) {
            float velocityY = body.getLinearVelocity().y;
            if (velocityY <= 5f) {
                this.isOnPlatform = true;
                this.canJump = true;
                this.jumpCooldown = 0;
                this.wasOnPlatform = false;
                System.out.println("✅ Landed on platform at Y: " + getY() + ", VelY: " + velocityY);
            } else {
                System.out.println("🚫 Platform contact ignored - jumping up: " + velocityY);
            }
        } else {
            this.isOnPlatform = false;
        }
    }

    public void updateMovement() {
        if (!isAlive) return;

        Vector2 velocity = body.getLinearVelocity();
        float targetSpeed = 0;

        if (movingLeft) {
            targetSpeed = -moveSpeed;
        } else if (movingRight) {
            targetSpeed = moveSpeed;
        }

        if (targetSpeed != 0) {
            if (Math.signum(currentSpeed) == Math.signum(targetSpeed)) {
                currentSpeed = currentSpeed * (1 - acceleration) + targetSpeed * acceleration;
            } else {
                currentSpeed = currentSpeed * (1 - acceleration * 2f) + targetSpeed * acceleration * 2f;
            }
        } else {
            currentSpeed = currentSpeed * (1 - deceleration);

            if (abs(currentSpeed) < 1.0f) {
                currentSpeed = 0;
            }
        }

        if (abs(currentSpeed) > moveSpeed) {
            currentSpeed = Math.signum(currentSpeed) * moveSpeed;
        }

        body.setLinearVelocity(currentSpeed, velocity.y);
    }

    public void startMovingLeft() {
        movingLeft = true;
        movingRight = false;
    }

    public void startMovingRight() {
        movingRight = true;
        movingLeft = false;
    }

    public void stopMoving() {
        movingLeft = false;
        movingRight = false;
    }

    public void move(int xs, int ys, boolean check) {
        if (!isAlive) return;

        if (xs < 0) {
            startMovingLeft();
        } else if (xs > 0) {
            startMovingRight();
        } else {
            stopMoving();
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        if (isAlive) {
            float drawX = getX() - (width / 2f);
            float drawY = getY() - (height / 2f);

            if (facingRight) {
                batch.draw(texture,
                    drawX, drawY,
                    width / 2f, height / 2f,
                    width, height,
                    1f, 1f,
                    rotation,
                    0, 0,
                    texture.getWidth(), texture.getHeight(),
                    false, false
                );
            } else {
                batch.draw(texture,
                    drawX, drawY,
                    width / 2f, height / 2f,
                    width, height,
                    1f, 1f,
                    -rotation,
                    0, 0,
                    texture.getWidth(), texture.getHeight(),
                    true, false
                );
            }
        }
    }

    public int getLiveLeft() {return livesLeft;}

    public boolean isAlive() {
        return isAlive;
    }

    public void die() {
        if (!isAlive) return;

        isAlive = false;
        stopMoving();
        currentSpeed = 0;
        rotation = 0f;

        if (game != null && game.soundManager != null) {
            game.soundManager.playDeathSound();
        }

        System.out.println("💀 DOODLE DIED at position: (" + getX() + ", " + getY() + ")");
    }

    public void respawn() {
        isAlive = true;
        body.setTransform(GameSettings.SCREEN_WIDTH / 2 * GameSettings.SCALE,
            getStartY() * GameSettings.SCALE, 0);
        body.setLinearVelocity(0, 0);
        isOnPlatform = false;
        canJump = false;
        wasOnPlatform = false;
        stopMoving();
        currentSpeed = 0;
        jumpCooldown = 0;
        currentCameraY = 0;
        rotation = 0f;
        facingRight = true;
        System.out.println("🔄 Doodle respawned!");
    }

    public float getStartY() {
        return 350f;
    }

    public boolean isOnPlatform() {
        return isOnPlatform;
    }

    public boolean isMovingLeft() { return movingLeft; }
    public boolean isMovingRight() { return movingRight; }
    public float getCurrentSpeed() { return currentSpeed; }

    public float getRotation() { return rotation; }
    public boolean isFacingRight() { return facingRight; }

    public int getLastY() { return lastY; }
}
