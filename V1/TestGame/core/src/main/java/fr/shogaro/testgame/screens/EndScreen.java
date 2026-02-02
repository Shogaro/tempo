package fr.shogaro.testgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import fr.shogaro.testgame.Main;
import fr.shogaro.testgame.render.UiRenderer;

public class EndScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final UiRenderer uiRenderer;
    private final String message;

    public EndScreen(Main game, String message) {
        this.game = game;
        this.batch = game.getBatch();
        this.uiRenderer = game.getUiRenderer();
        this.message = message;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);

        float screenHeight = Gdx.graphics.getHeight();

        batch.begin();
        uiRenderer.drawCentered(batch, message, screenHeight * 0.6f, 2.2f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Entree/Espace pour retourner au menu", screenHeight * 0.5f, 1f, Color.WHITE);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.showMenu();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
