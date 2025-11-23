package io.github.some_example_name;

import static io.github.some_example_name.Static.GameSettings.POSITION_ITERATIONS;
import static io.github.some_example_name.Static.GameSettings.STEP_TIME;
import static io.github.some_example_name.Static.GameSettings.VELOCITY_ITERATIONS;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.Managers.SoundManager;
import io.github.some_example_name.Managers.AchievementManager;
import io.github.some_example_name.Screens.GameScreen;
import io.github.some_example_name.Screens.MenuScreen;
import io.github.some_example_name.Screens.RecordsScreen;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.FontBuilder;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public World world;
    public Vector3 touch;
    int p = 0;
    float accumulator = 0;
    int acumm;
    GameSession gameSession;
    public BitmapFont commonWhiteFont,commonBlackFont,largeWhiteFont;

    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    public RecordsScreen recordsScreen;
    public ScoreManager scoreManager;
    public SoundManager soundManager;
    public AchievementManager achievementManager;

    @Override
    public void create() {
        Box2D.init();
        world = new World(new Vector2(0, -50), true);
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        gameSession = new GameSession();
        camera.setToOrtho(false, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        commonWhiteFont = FontBuilder.generate(24, Color.WHITE, GameResources.FONT_PATH);
        largeWhiteFont = FontBuilder.generate(48, Color.WHITE, GameResources.FONT_PATH);
        commonBlackFont = FontBuilder.generate(24, Color.BLACK, GameResources.FONT_PATH);

        scoreManager = new ScoreManager();
        soundManager = new SoundManager();
        achievementManager = new AchievementManager();

        if (!achievementManager.isAchievementUnlocked("welcome")) {
            achievementManager.unlockAchievement("welcome");
        }

        gameScreen = new GameScreen(this);
        menuScreen = new MenuScreen(this);
        recordsScreen = new RecordsScreen(this);

        setScreen(menuScreen);
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (soundManager != null) {
            soundManager.dispose();
        }
    }

    public void stepWorld() {
        float delta = Gdx.graphics.getDeltaTime();
        accumulator += delta;

        if (accumulator >= STEP_TIME) {
            accumulator -= STEP_TIME;
            world.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            acumm++;
            if (acumm >= 60) {
                gameScreen.setTr(false, true);
                acumm-=60;
            } else {
                gameScreen.setTr(false, false);
            }
        }
        else {
            gameScreen.setTr(false,false);
        }
    }
}
