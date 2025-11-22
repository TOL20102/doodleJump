package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

import io.github.some_example_name.Managers.ScoreManager;
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.components.TextView;


public class SettingsScreen extends ScreenAdapter {

    MyGdxGame myGdxGame;

    ImageView backgroundView;
    TextView titleTextView;
    ImageView blackoutImageView;
    ButtonView returnButton;
    TextView musicSettingView;
    TextView soundSettingView;
    TextView clearSettingView;

    public SettingsScreen(MyGdxGame myGdxGame) {
        this.myGdxGame = myGdxGame;

        backgroundView = new ImageView(0,0, GameSettings.SCREEN_WIDTH,GameSettings.SCREEN_HEIGHT, GameResources.BACKGROUND_IMG_PATH);
        titleTextView = new TextView(myGdxGame.largeWhiteFont, 256, 956, "Settings");
        blackoutImageView = new ImageView(85, 365,550,471, GameResources.BLACKOUT_MIDDLE_IMG_PATH);
        musicSettingView = new TextView(myGdxGame.commonWhiteFont, 173, 717, "music: " + translateStateToText(ScoreManager.loadIsMusicOn()));
        soundSettingView = new TextView(myGdxGame.commonWhiteFont, 173, 658, "sound: " + translateStateToText(ScoreManager.loadIsSoundOn()));
        clearSettingView = new TextView(myGdxGame.commonWhiteFont, 173, 599, "clear records");
        returnButton = new ButtonView(
            280, 447,
            160, 70,
            myGdxGame.commonBlackFont,
            GameResources.BUTTON_SHORT_BG_IMG_PATH,
            "return"
        );

    }

    @Override
    public void render(float delta) {
        handleInput();
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        myGdxGame.batch.begin();

        backgroundView.draw(myGdxGame.batch);
        titleTextView.draw(myGdxGame.batch);
        blackoutImageView.draw(myGdxGame.batch);
        returnButton.draw(myGdxGame.batch);
        musicSettingView.draw(myGdxGame.batch);
        soundSettingView.draw(myGdxGame.batch);
        clearSettingView.draw(myGdxGame.batch);

        myGdxGame.batch.end();
    }
    void handleInput() {
        if (Gdx.input.justTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));

            if (returnButton.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                myGdxGame.setScreen(myGdxGame.menuScreen);
            }
            if (clearSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                clearSettingView.setText("clear records (cleared)");
                ScoreManager.saveTableOfRecords(new ArrayList<>());
            }
            if (musicSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                ScoreManager.saveMusicSettings(!ScoreManager.loadIsMusicOn());
                musicSettingView.setText("music: " + translateStateToText(ScoreManager.loadIsMusicOn()));
                //myGdxGame.soundManager.updateMusicFlag();
            }
            if (soundSettingView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                ScoreManager.saveSoundSettings(!ScoreManager.loadIsSoundOn());
                soundSettingView.setText("sound: " + translateStateToText(ScoreManager.loadIsSoundOn()));
                //myGdxGame.soundManager.updateSoundFlag();
            }
        }
    }
    private String translateStateToText(boolean state) {
        return state ? "ON" : "OFF";
    }
    @Override
    public void dispose() {
        backgroundView.dispose();
        titleTextView.dispose();
        blackoutImageView.dispose();
        returnButton.dispose();
        musicSettingView.dispose();
        soundSettingView.dispose();
        clearSettingView.dispose();
    }
}
