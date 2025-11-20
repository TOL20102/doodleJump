package io.github.some_example_name;

import static io.github.some_example_name.GameState.PAUSED;
import static io.github.some_example_name.GameState.PLAYING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.ScreenUtils;


import java.util.ArrayList;

import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;
import io.github.some_example_name.objects.BulletObject;
import io.github.some_example_name.objects.DoodleObject;


public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backGround, topBlackoutView, fullBlackoutView;
    ButtonView buttonView, buttonView1, pauseButton, homeButton, continueButton;
    TextView pauseTextView;
    ArrayList<BulletObject> bulletArray;


    GameSession gameSession;
    Box2DDebugRenderer debugRenderer;
    int r = 0;
    boolean canShot, hasShot;
    Batch batch;
    public DoodleObject doodleObject;
    boolean tr;
    public GameScreen(MyGdxGame myGdxGame ) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        Box2D.init();
        bulletArray = new ArrayList<>();
        topBlackoutView = new ImageView(0, 1180, 720, 100, GameResources.BLACKOUT_TOP_IMG_PATH);
        fullBlackoutView = new ImageView(0, 0, 720, 1280, GameResources.PAUSE_SCREEN_IMG_PATH);
        doodleObject = new DoodleObject(GameResources.DOODLE_PATH, 100, 150, 920 / 3, 552 / 3, GameSettings.DOODLE_BIT, myGdxGame.world);
        backGround = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_PATH);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        buttonView = new ButtonView(0, 0, 630 / 3, 630 / 3, GameResources.BUTTON_FLIPED_PATH);
        gameSession = new GameSession();
        buttonView1 = new ButtonView(510, 0, 630 / 3, 630 / 3, GameResources.BUTTON_PATH);
        debugRenderer = new Box2DDebugRenderer();
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 290, 950, "Pause");
        homeButton = new ButtonView(190, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
        continueButton = new ButtonView(390, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        BulletObject laserBullet = new BulletObject(
            1000, 100 + doodleObject.height / 2,
            GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
            GameResources.BULLET_IMG_PATH,
            myGdxGame.world,
            0,
            100);
        bulletArray.add(laserBullet);

    }

    @Override
    public void render(float delta) {
        myGdxGame.stepWorld();
        if(tr) {
            doodleObject.jump();
        }
        draw();
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        updateBullets();

        r++;

        batch.begin();

        backGround.draw(myGdxGame.batch);
        doodleObject.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        buttonView.draw(myGdxGame.batch);
        buttonView1.draw(myGdxGame.batch);
        for (BulletObject bullet : bulletArray) bullet.draw(myGdxGame.batch);

        if (gameSession.state == PLAYING) {

        } else if (gameSession.state == PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        }

        batch.end();

        handleInput();
    }
    private void updateBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            if (bulletArray.get(i).hasToBeDestroyed()) {
                myGdxGame.world.destroyBody(bulletArray.get(i).body);
                bulletArray.remove(i--);
            }
        }
    }
    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            switch (gameSession.state) {
                case PLAYING:

                    if (buttonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        doodleObject.move(-100, 0, false);
                    }
                    if (buttonView1.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        doodleObject.move(100, 0, false);
                    }
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
                    }
                    if (!(pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) && !(buttonView1.isHit(myGdxGame.touch.x, myGdxGame.touch.y))
                        && !(buttonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) && !(topBlackoutView.isHit(myGdxGame.touch.x,myGdxGame.touch.y)) && !hasShot) {
                        BulletObject laserBullet = new BulletObject(
                            doodleObject.getX(), doodleObject.getY() + doodleObject.height / 2,
                            GameSettings.BULLET_WIDTH, GameSettings.BULLET_HEIGHT,
                            GameResources.BULLET_IMG_PATH,
                            myGdxGame.world,
                            myGdxGame.touch.x,
                            myGdxGame.touch.y);
                        bulletArray.add(laserBullet);
                        hasShot = true;

                }
                    break;

                case PAUSED:
                    if (homeButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        myGdxGame.setScreen(myGdxGame.menuScreen);
                    }
                    if (continueButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.resumeGame();
                    }
                    break;
            }
        }
    }
    public void setTr(boolean r, boolean t) { tr = r; if (t) { hasShot = false; } }
}
