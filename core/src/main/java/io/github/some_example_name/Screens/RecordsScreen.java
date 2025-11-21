package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;

public class RecordsScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backgroundView;
    TextView titleView, highScoreView, lastScoreView, minScoreView, totalGamesView;
    ButtonView backButton;

    public RecordsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0, 0, 720, 1280, GameResources.BACKGROUND_IMG_PATH);
        titleView = new TextView(myGdxGame.largeWhiteFont, 250, 1100, "Records");


        highScoreView = new TextView(myGdxGame.commonWhiteFont, 200, 900, "");
        lastScoreView = new TextView(myGdxGame.commonWhiteFont, 200, 800, "");
        minScoreView = new TextView(myGdxGame.commonWhiteFont, 200, 700, "");
        totalGamesView = new TextView(myGdxGame.commonWhiteFont, 200, 600, "");

        backButton = new ButtonView(140, 400, 440, 70, myGdxGame.commonBlackFont,
            GameResources.BUTTON_LONG_BG_IMG_PATH, "Back");

        updateScoresDisplay();
    }

    @Override
    public void show() {

        updateScoresDisplay();


        myGdxGame.camera.position.set(
            GameSettings.SCREEN_WIDTH / 2,
            GameSettings.SCREEN_HEIGHT / 2,
            0
        );
        myGdxGame.camera.update();
    }

    private void updateScoresDisplay() {
        ScoreManager.ScoreData scores = myGdxGame.scoreManager.getScoreData();

        highScoreView.setText("High Score: " + scores.highScore);
        lastScoreView.setText("Last Score: " + scores.lastScore);
        minScoreView.setText("Min Score: " + (scores.totalGames > 0 ? scores.minScore : 0));
        totalGamesView.setText("Total Games: " + scores.totalGames);
    }

    @Override
    public void render(float delta) {
        handleInput();

        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleView.draw(myGdxGame.batch);
        highScoreView.draw(myGdxGame.batch);
        lastScoreView.draw(myGdxGame.batch);
        minScoreView.draw(myGdxGame.batch);
        totalGamesView.draw(myGdxGame.batch);
        backButton.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (backButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
        }
    }

    @Override
    public void dispose() {
        backgroundView.dispose();
        titleView.dispose();
        highScoreView.dispose();
        lastScoreView.dispose();
        minScoreView.dispose();
        totalGamesView.dispose();
        backButton.dispose();
    }
}
