package io.github.some_example_name.components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ImageView extends View {
    Texture texture;

    public ImageView(float x, float y,int width, int height, String imagePath) {
        super(x, y, 0, 0);
        texture = new Texture(imagePath);
        this.width = width;
        this.height = height;
    }
    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }
    @Override
    public void dispose(){
        texture.dispose();
    }
}
