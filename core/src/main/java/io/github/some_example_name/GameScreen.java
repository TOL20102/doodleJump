package io.github.some_example_name;

import static io.github.some_example_name.GameState.PAUSED;
import static io.github.some_example_name.GameState.PLAYING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ScreenUtils;


import java.util.ArrayList;

import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;
import io.github.some_example_name.objects.DoodleObject;
import io.github.some_example_name.objects.PlateObject;


public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backGround, topBlackoutView, fullBlackoutView;
    ButtonView buttonView, buttonView1, pauseButton, homeButton, continueButton;
    TextView pauseTextView;
    int r = 0;
    Batch batch;
    GameSession gameSession;
    DoodleObject doodleObject;


    ArrayList<PlateObject> plates;
    float nextPlateY;
    final float PLATE_SPAWN_INTERVAL_Y = 200f;

    AchievementManager achievementManager;
    boolean firstLaunch = true;
    boolean gyroscopeAvail;
    boolean achievementShown = false;

    public GameScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        topBlackoutView = new ImageView(0, 1180, 720, 100, GameResources.BLACKOUT_TOP_IMG_PATH);
        fullBlackoutView = new ImageView(0, 0, 720, 1280, GameResources.PAUSE_SCREEN_IMG_PATH);
        doodleObject = new DoodleObject(GameResources.DOODLE_PATH, 100, 150, 920 / 3, 552 / 3, GameSettings.DOODLE_BIT, myGdxGame.world);
        backGround = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_PATH);
        pauseButton = new ButtonView(605, 1200, 46, 54, GameResources.PAUSE_IMG_PATH);
        buttonView = new ButtonView(0, 0, 630 / 3, 630 / 3, GameResources.BUTTON_FLIPED_PATH);
        gameSession = new GameSession();
        plates = new ArrayList<>();
        nextPlateY = 100f;
        buttonView1 = new ButtonView(510, 0, 630 / 3, 630 / 3, GameResources.BUTTON_PATH);
        pauseTextView = new TextView(myGdxGame.largeWhiteFont, 290, 950, "Pause");
        int screenHeight = Gdx.graphics.getHeight();
        while (nextPlateY < screenHeight * 3) {
            spawnNewPlate();
            homeButton = new ButtonView(190, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Home");
            continueButton = new ButtonView(390, 750, 160, 70, myGdxGame.commonWhiteFont, GameResources.BUTTON_SHORT_BG_IMG_PATH, "Continue");
        }
        gyroscopeAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);

        achievementManager = new AchievementManager();

        setupCollisionListener();
    }
    private void setupCollisionListener() {
        myGdxGame.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Object userDataA = contact.getFixtureA().getBody().getUserData();
                Object userDataB = contact.getFixtureB().getBody().getUserData();

                if ((userDataA != null && userDataB != null)) {
                    if ((userDataA.equals("doodle") && userDataB.equals("platform")) ||
                        (userDataA.equals("platform") && userDataB.equals("doodle"))) {

                        Vector2 doodleVelocity = doodleObject.body.getLinearVelocity();
                        if (doodleVelocity.y <= 0) {
                            doodleObject.onPlatformCollision();
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }
    @Override
    public void render(float delta) {

        myGdxGame.stepWorld();
        doodleObject.update(delta);

        achievementManager.update(delta);

        checkAchievements();

        float spawnThreshold = myGdxGame.camera.position.y + myGdxGame.camera.viewportHeight;
        while (nextPlateY < spawnThreshold + PLATE_SPAWN_INTERVAL_Y * 3) {
            spawnNewPlate();
        }

        for (int i = plates.size() - 1; i >= 0; i--) {
            PlateObject plate = plates.get(i);
            if (plate.getY() < myGdxGame.camera.position.y - myGdxGame.camera.viewportHeight - 100) {
                plate.dispose();
                plates.remove(i);
            } else {
                plate.update(delta);
            }
        }

        draw();
    }

    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);
        handleInput();

        r++;


        batch.begin();

        backGround.draw(myGdxGame.batch);
        doodleObject.draw(myGdxGame.batch);
        topBlackoutView.draw(myGdxGame.batch);
        pauseButton.draw(myGdxGame.batch);
        buttonView.draw(myGdxGame.batch);
        buttonView1.draw(myGdxGame.batch);
        if (gameSession.state == PLAYING) {

        } else if (gameSession.state == PAUSED) {
            fullBlackoutView.draw(myGdxGame.batch);
            pauseTextView.draw(myGdxGame.batch);
            homeButton.draw(myGdxGame.batch);
            continueButton.draw(myGdxGame.batch);
        }


        batch.end();


    }

    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            switch (gameSession.state) {
                case PLAYING:

                    if (buttonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        doodleObject.move(-100, 0);
                    }
                    if (buttonView1.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        doodleObject.move(100, 0);
                    }
                    if (pauseButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                        gameSession.pauseGame();
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
    private void spawnNewPlate() {
        float plateWidth = 120f;
        float plateHeight = 30f;

        PlateObject newPlate = new PlateObject(
            nextPlateY,
            plateWidth,
            plateHeight,
            GameSettings.PLATE_BIT,
            myGdxGame.world,
            Gdx.graphics.getWidth()
        );
        plates.add(newPlate);

        nextPlateY += PLATE_SPAWN_INTERVAL_Y;
    }
    private void checkAchievements() {
        if (firstLaunch && !achievementShown) {
            achievementManager.unlockAchievement("welcome");
            firstLaunch = false;
            achievementShown = true;
            Gdx.app.log("ACHIEVEMENT", "Welcome achievement unlocked");
        }

        float doodleY = doodleObject.getY();

        if (doodleY > 100f && !achievementManager.isAchievementUnlocked("first_jump")) {
            achievementManager.unlockAchievement("first_jump");
            Gdx.app.log("ACHIEVEMENT", "First jump achievement unlocked");
        }

        if (doodleY > 1000f && !achievementManager.isAchievementUnlocked("height_100")) {
            achievementManager.unlockAchievement("height_100");
            Gdx.app.log("ACHIEVEMENT", "Height 100 achievement unlocked");
        }

        if (doodleY > 5000f && !achievementManager.isAchievementUnlocked("height_500")) {
            achievementManager.unlockAchievement("height_500");
            Gdx.app.log("ACHIEVEMENT", "Height 500 achievement unlocked");
        }
    }
}
