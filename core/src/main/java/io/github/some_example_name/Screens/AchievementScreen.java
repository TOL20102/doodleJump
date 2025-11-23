package io.github.some_example_name.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

// ИМПОРТЫ (Убедитесь, что пути совпадают с вашим проектом)
import io.github.some_example_name.MyGdxGame;
import io.github.some_example_name.Achievement;
// Обратите внимание: пакет GameResources должен совпадать с тем, где он у вас лежит (Static или utils)
import io.github.some_example_name.Static.GameResources;
import io.github.some_example_name.Static.GameSettings;
import io.github.some_example_name.components.ButtonView;

public class AchievementScreen extends ScreenAdapter {

    private final MyGdxGame game;
    private Stage stage;
    private Texture backgroundTexture;

    // Кастомная кнопка
    private ButtonView exitButton;

    // Стили для текста
    private Label.LabelStyle titleStyle;
    private Label.LabelStyle nameStyle;
    private Label.LabelStyle descStyle;

    public AchievementScreen(final MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 0. Инициализируем Stage каждый раз при показе экрана
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // 1. Загружаем фон
        try {
            backgroundTexture = new Texture(Gdx.files.internal(GameResources.BACKGROUND_IMG_PATH));
        } catch (Exception e) {
            Gdx.app.error("AchievementScreen", "Не удалось загрузить фон: " + e.getMessage());
        }

        prepareStyles();

        // 2. Создаем кнопку выхода
        float btnWidth = 250;
        float btnHeight = 80;
        float btnX = (Gdx.graphics.getWidth() - btnWidth) / 2;
        float btnY = 30;

        exitButton = new ButtonView(
            btnX, btnY,
            btnWidth, btnHeight,
            game.commonWhiteFont,
            GameResources.BUTTON_LONG_BG_IMG_PATH,
            "BACK"
        );

        // 3. Главная таблица
        Table mainTable = new Table();
        mainTable.setFillParent(true);

        Label titleLabel = new Label("ACHIEVEMENTS", titleStyle);
        titleLabel.setAlignment(Align.center);
        mainTable.add(titleLabel).padTop(50).padBottom(30).row();

        // Таблица контента (внутри скролла)
        Table contentTable = new Table();
        contentTable.top();

        // --- Загрузка данных ---
        if (game.achievementManager != null) {
            List<Achievement> achievements = game.achievementManager.getAchievements();

            Collections.sort(achievements, new Comparator<Achievement>() {
                @Override
                public int compare(Achievement a1, Achievement a2) {
                    if (a1.unlocked && !a2.unlocked) return -1;
                    if (!a1.unlocked && a2.unlocked) return 1;
                    return 0;
                }
            });

            for (Achievement ach : achievements) {
                addAchievementRow(contentTable, ach.title, ach.description, ach.unlocked);
            }
        }

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        ScrollPane scrollPane = new ScrollPane(contentTable, scrollStyle);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        mainTable.add(scrollPane).expand().fill().pad(10).padBottom(120).row();

        stage.addActor(mainTable);
    }

    private void addAchievementRow(Table parentTable, String name, String desc, boolean unlocked) {
        Color titleColor = unlocked ? Color.GOLD : Color.GRAY;
        Color descColor = unlocked ? Color.WHITE : Color.DARK_GRAY;
        String prefix = unlocked ? "[Complete] " : "[Incomplete] ";

        Table entryTable = new Table();
        entryTable.top().left();

        float textWidth = Gdx.graphics.getWidth() - 100f;

        Label nameLabel = new Label(prefix + name, nameStyle);
        nameLabel.setColor(titleColor);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.left);

        entryTable.add(nameLabel).width(textWidth).left().row();

        Label descLabel = new Label(desc, descStyle);
        descLabel.setColor(descColor);
        descLabel.setWrap(true);
        descLabel.setAlignment(Align.left);

        entryTable.add(descLabel).width(textWidth).left().padTop(10).row();

        parentTable.add(entryTable).width(textWidth).padBottom(40).padLeft(20).row();
    }

    private void prepareStyles() {
        titleStyle = new Label.LabelStyle();
        titleStyle.font = game.commonWhiteFont;

        nameStyle = new Label.LabelStyle();
        nameStyle.font = game.commonWhiteFont;

        descStyle = new Label.LabelStyle();
        descStyle.font = game.commonWhiteFont;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        if (backgroundTexture != null) {
            game.batch.draw(backgroundTexture, 0, 0, GameSettings.SCREEN_WIDTH, GameSettings.SCREEN_HEIGHT);
        }
        game.batch.end();

        stage.act(delta);
        stage.draw();

        game.batch.begin();
        if (exitButton != null) {
            exitButton.draw(game.batch);
        }
        game.batch.end();

        handleInput();
    }

    private void handleInput() {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (exitButton.isHit(touchX,touchY)) {

                if (game.menuScreen != null) {
                    game.setScreen(game.menuScreen);
                    dispose(); // Вызываем dispose для очистки фона и stage
                } else {
                    Gdx.app.log("AchievementScreen", "MenuScreen is null!");
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
            stage = null;
        }
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
            backgroundTexture = null;
        }
        // ВАЖНО: Мы НЕ удаляем exitButton здесь, так как ButtonView.dispose()
        // ошибочно удаляет общий шрифт игры (commonWhiteFont).
        // Если вы раскомментируете это, игра сломается при перезаходе.

        /* if (exitButton != null) {
            exitButton.dispose();
            exitButton = null;
        }
        */
    }
}
