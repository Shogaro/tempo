package fr.shogaro.testgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import fr.shogaro.testgame.Main;
import fr.shogaro.testgame.render.UiRenderer;
import fr.shogaro.testgame.systems.AudioSystem;

public class SettingsScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final UiRenderer uiRenderer;
    private final AudioSystem audioSystem;

    public SettingsScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
        this.uiRenderer = game.getUiRenderer();
        this.audioSystem = game.getAudioSystem();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showMenu();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            audioSystem.toggleEnabled();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            audioSystem.changeVolume(-0.05f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            audioSystem.changeVolume(0.05f);
        }

        float screenHeight = Gdx.graphics.getHeight();
        float volumePercent = audioSystem.getVolume() * 100f;

        batch.begin();
        uiRenderer.drawCentered(batch, "Settings", screenHeight - 80f, 1.8f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Musique: " + (audioSystem.isEnabled() ? "ON" : "OFF"), screenHeight * 0.55f, 1f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Volume: " + (int) volumePercent + "%", screenHeight * 0.48f, 1f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Entree/Espace pour activer/desactiver", screenHeight * 0.38f, 1f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Fleches gauche/droite pour volume", screenHeight * 0.32f, 1f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Echap pour revenir", screenHeight * 0.26f, 1f, Color.WHITE);

        if (!audioSystem.isAvailable()) {
            uiRenderer.drawCentered(batch, "Musique indisponible", screenHeight * 0.18f, 1f, Color.WHITE);
        }

        batch.end();
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
