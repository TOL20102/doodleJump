package io.github.some_example_name.objects;

import com.badlogic.gdx.graphics.Texture; // Необходимый импорт для работы с текстурами
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import io.github.some_example_name.Static.GameSettings;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Color;

public class EnemyObject extends GameObject {
    public static final int ENEMY_WIDTH = 120;
    public static final int ENEMY_HEIGHT = 120;
    public static final short ENEMY_BIT = 4;
    private static final String TRASH_PATH = "trash.png";
    private static final String TRASH_PATH2 = "trash2.png";

    private boolean isAlive;
    private int health = 10;
    private boolean wasHit;

    private Texture aggroTexture;

    private static final float TEXTURE_SWITCH_INTERVAL = 0.25f;
    private float textureTimer = 0f;
    private boolean useAlternateTexture = false;

    private float moveSpeed = 5f;
    private float currentSpeed = 0f;
    private boolean movingRight = true;
    private static final float MOVE_CHANGE_INTERVAL = 2.0f;
    private float moveTimer = 0f;

    private boolean isAggro = false;
    private static final float AGGRO_TIME = 10.0f;
    private float aggroTimer = AGGRO_TIME;
    private static final float AGGRO_SPEED = 50f;
    private static final float EDGE_BUFFER = 10f;

    public EnemyObject(String texturePath, float x, float y, World world) {
        super(TRASH_PATH, (int)x, (int)y, ENEMY_WIDTH, ENEMY_HEIGHT, ENEMY_BIT, world);
        this.isAlive = true;
        this.wasHit = false;

        try {
            this.aggroTexture = new Texture(TRASH_PATH2);
            if (this.aggroTexture == this.texture) {
                System.err.println("WARNING: aggroTexture is identical to default texture. Check loading path or error handling.");
            }
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR loading aggro texture: " + TRASH_PATH2 + ". Check if the file exists and is in the correct assets folder.");
            this.aggroTexture = this.texture;
        }

        body.setGravityScale(0);
        body.setType(com.badlogic.gdx.physics.box2d.BodyDef.BodyType.KinematicBody);

        this.movingRight = MathUtils.randomBoolean();
        this.aggroTimer = AGGRO_TIME;
    }

    public void update(float delta, float doodleX, float doodleY) {
        if (!isAlive) return;
        textureTimer += delta;
        if (textureTimer >= TEXTURE_SWITCH_INTERVAL) {
            useAlternateTexture = !useAlternateTexture;
            textureTimer = 0;
        }

        checkAggroConditions(delta, doodleX, doodleY);

        handleMovement(delta, doodleX, doodleY);

        if (wasHit) {
            if (health <= 0) {
                die();
            }
            wasHit = false;
        }
    }

    private void checkAggroConditions(float delta, float doodleX, float doodleY) {
        if (isAggro) return;

        if (doodleY > getY() + ENEMY_HEIGHT * 0.75f) {
            isAggro = true;
            aggroTimer = 0;
            System.out.println("Enemy AGGRO: Doodle passed by Y. Starting chase.");
            return;
        }

        aggroTimer -= delta;
        if (aggroTimer <= 0) {
            isAggro = true;
            System.out.println("Enemy AGGRO: Time limit expired. Starting chase.");
        }
    }

    private void handleMovement(float delta, float doodleX, float doodleY) {
        Vector2 targetVelocity = new Vector2(0, 0);

        if (isAggro) {
            float dx = doodleX - getX();
            float dy = doodleY - getY();

            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 1f) {
                float speedX = (dx / distance) * AGGRO_SPEED;
                float speedY = (dy / distance) * AGGRO_SPEED;
                targetVelocity.set(speedX, speedY);
            } else {
                targetVelocity.set(0, AGGRO_SPEED);
            }

        } else {
            moveTimer -= delta;
            if (moveTimer <= 0) {
                movingRight = !movingRight;
                moveTimer = MOVE_CHANGE_INTERVAL;
            }

            currentSpeed = movingRight ? moveSpeed : -moveSpeed;
            targetVelocity.set(currentSpeed, 0);

            float xPos = getX();
            float halfWidth = ENEMY_WIDTH / 2f;
            float screenRight = GameSettings.SCREEN_WIDTH - halfWidth - EDGE_BUFFER;
            float screenLeft = halfWidth + EDGE_BUFFER;

            if ((xPos <= screenLeft && !movingRight) || (xPos >= screenRight && movingRight)) {

                movingRight = !movingRight;
                moveTimer = MOVE_CHANGE_INTERVAL;
                currentSpeed = movingRight ? moveSpeed : -moveSpeed;
                targetVelocity.set(currentSpeed, 0);
            }
        }

        body.setLinearVelocity(targetVelocity.x, targetVelocity.y);
    }


    @Override
    public void draw(SpriteBatch batch) {
        if (isAlive) {
            Color oldColor = batch.getColor();

            Texture currentTexture;

            if (useAlternateTexture) {
                currentTexture = this.aggroTexture;
            } else {
                currentTexture = this.texture;
            }

            if (isAggro) {
                batch.setColor(1f, 0.5f, 0.5f, 1f);
            } else {
                batch.setColor(Color.WHITE);
            }

            batch.draw(currentTexture,
                getX() - ENEMY_WIDTH/1.6f ,
                getY() - ENEMY_HEIGHT/1.6f ,
                ENEMY_WIDTH *1.25f, ENEMY_HEIGHT*1.25f);

            batch.setColor(oldColor);
        }
    }

    public void die() {
        if (!isAlive) return;
        isAlive = false;
        System.out.println("Enemy destroyed");
    }

    public void hit(int damage) {
        this.health -= damage;
        this.wasHit = true;



        System.out.println("Enemy hit HP left: " + this.health);
    }

    public void setWasHit(boolean hit) {
        this.wasHit = hit;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean hasToBeDestroyed() {
        return !isAlive || getY() < -100;
    }
}
