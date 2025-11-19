package io.github.some_example_name;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;

public class Achievement {
    public String id;
    public String title;
    public String description;
    public boolean unlocked;
    public float showTimer;
    public boolean isShowing;

    private static final float SHOW_DURATION = 3.0f;
    private static final float ANIMATION_DURATION = 0.5f;
    private float startX;
    private float currentX;
    private float y;

    public Achievement(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.unlocked = false;
        this.showTimer = 0;
        this.isShowing = false;
    }

    public void show() {
        this.unlocked = true;
        this.isShowing = true;
        this.showTimer = SHOW_DURATION;
        this.startX = 800f;
        this.currentX = startX;
        this.y = 700f;
    }

    public void update(float delta) {
        if (isShowing) {
            showTimer -= delta;


            float targetX = 50f;
            float progress = 1.0f - (showTimer / SHOW_DURATION);
            currentX = Interpolation.bounceOut.apply(startX, targetX,
                Math.min(1.0f, progress * 2f));

            if (showTimer <= 0) {
                isShowing = false;
            }
        }
    }

    public void draw(Batch batch, BitmapFont font) {
        if (!isShowing || font == null) return;

        float alpha = 1.0f;


        if (showTimer > SHOW_DURATION - ANIMATION_DURATION) {

            float animProgress = (SHOW_DURATION - showTimer) / ANIMATION_DURATION;
            alpha = Interpolation.pow2Out.apply(animProgress);
        } else if (showTimer < ANIMATION_DURATION) {

            float animProgress = showTimer / ANIMATION_DURATION;
            alpha = Interpolation.pow2Out.apply(animProgress);
        }


        batch.setColor(0.2f, 0.2f, 0.8f, alpha * 0.8f);



        font.setColor(1, 1, 1, alpha);
        font.draw(batch, "★ Достижение разблокировано!", currentX, y + 60);
        font.draw(batch, title, currentX, y + 30);
        font.draw(batch, description, currentX, y);


        batch.setColor(1, 1, 1, 1);
        font.setColor(1, 1, 1, 1);
    }

    @Override
    public String toString() {
        return String.format("Achievement{id='%s', title='%s', unlocked=%s}", id, title, unlocked);
    }
}
