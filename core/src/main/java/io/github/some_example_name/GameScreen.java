package io.github.some_example_name;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;



public class GameScreen extends ScreenAdapter {
    MyGdxGame myGdxGame;
    Batch batch;
    Texture texture;
    public GameScreen(MyGdxGame myGdxGame, Batch batch) {
        this.myGdxGame = myGdxGame;
        this.batch = batch;
        texture = new Texture("i.png");
    }
    @Override
    public void render(float delta) {
        batch.begin();
        batch.draw(texture, 10, 15, 301, 453);
        batch.end();
    }
}
