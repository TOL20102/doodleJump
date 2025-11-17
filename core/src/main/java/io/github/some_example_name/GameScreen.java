package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;


import io.github.some_example_name.components.ButtonView;
import io.github.some_example_name.components.ImageView;
import io.github.some_example_name.objects.DoodleObject;


public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    ImageView backGround, backGround1, backGround2;
    ButtonView buttonView, buttonView1;
    int r = 0;
    Batch batch;
    DoodleObject doodleObject;
    public GameScreen(MyGdxGame myGdxGame ) {
        this.myGdxGame = myGdxGame;
        batch = myGdxGame.batch;
        doodleObject = new DoodleObject(GameResorses.DOODLE_PATH,100,150,920/3,552/3,GameSettings.DOODLE_BIT, myGdxGame.world);
        backGround = new ImageView(0,0, (int) (900), (int) (506),GameResorses.BACKGROUND_PATH);
        backGround1 = new ImageView(0,506, (int) (900), (int) (506),GameResorses.BACKGROUND_PATH);
        backGround2 = new ImageView(0,1012, (int) (900), (int) (506),GameResorses.BACKGROUND_PATH);
        buttonView = new ButtonView(0,0,630/3,630/3,GameResorses.BUTTON_FLIPED_PATH);
        buttonView1 = new ButtonView(510,0,630/3,630/3,GameResorses.BUTTON_PATH);
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


        doodleObject.jump();
        r++;


        batch.begin();
        backGround.draw(myGdxGame.batch);
        backGround1.draw(myGdxGame.batch);
        backGround2.draw(myGdxGame.batch);
        doodleObject.draw(myGdxGame.batch);
        buttonView.draw(myGdxGame.batch);
        buttonView1.draw(myGdxGame.batch);

        batch.end();

        handleInput();
    }
    private void handleInput() {
        if (Gdx.input.isTouched()) {
            myGdxGame.touch = myGdxGame.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            if(buttonView.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                doodleObject.move(-100,0);
            }
            if(buttonView1.isHit(myGdxGame.touch.x, myGdxGame.touch.y)) {
                doodleObject.move(100,0);
            }
        }
    }
}
