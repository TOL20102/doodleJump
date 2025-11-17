package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.some_example_name.objects.DoodleObject;


public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    int r = 0;
    Batch batch;
    boolean gyroscopeAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope);
    DoodleObject doodleObject;
    public GameScreen(MyGdxGame myGdxGame ) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        doodleObject = new DoodleObject(GameResorses.DOODLE_PATH,100,150,301/2,453/2,GameSettings.DOODLE_BIT, myGdxGame.world);
    }
    @Override
    public void render(float delta) {
        myGdxGame.stepWorld();
        draw();
    }
    private void draw() {
        myGdxGame.camera.update();
        myGdxGame.batch.setProjectionMatrix(myGdxGame.camera.combined);
        ScreenUtils.clear(Color.CLEAR);

        handleInput();
        doodleObject.jump();
        r++;


        batch.begin();
        doodleObject.draw(myGdxGame.batch);
        batch.end();
    }
    private void handleInput() {

        if (Gdx.input.isPeripheralAvailable(Input.Peripheral.Gyroscope)){
            if (r >= 100) {
                String gyroX = String.format("%.2f",Gdx.input.getGyroscopeX());
                String gyroY = String.format("%.2f",Gdx.input.getGyroscopeY());
                String gyroZ = String.format("%.2f",Gdx.input.getGyroscopeZ());
                Gdx.app.log("кординаты", "x - " + gyroX + "   y- " + gyroY + "   z - " + gyroZ);
                r -= 100;
            }
        }
    }
}
