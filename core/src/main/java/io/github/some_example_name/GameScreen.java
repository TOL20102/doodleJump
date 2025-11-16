package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import objects.DoodleObject;


public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    Batch batch;
    boolean gyroscopeAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
    DoodleObject doodleObject;
    public GameScreen(MyGdxGame myGdxGame ) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        doodleObject = new DoodleObject(GameResorses.DOODLE_PATH,100,150,301,453,GameSettings.DOODLE_BIT, myGdxGame.world);
    }
    @Override
    public void render(float delta) {
        draw();
    }
    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        handleInput();


        batch.begin();
        doodleObject.draw(myGdxGame.batch);
        batch.end();
    }
    private void handleInput() {
        float gyroX = Gdx.input.getGyroscopeX();
        float gyroY = Gdx.input.getGyroscopeY();
        float gyroZ = Gdx.input.getGyroscopeZ();
        Gdx.app.log("кординаты","x - "+ gyroX + "   y- " + gyroY+ "   z - "+ gyroZ);
    }
}
