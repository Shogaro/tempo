package fr.shogaro.testgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import fr.shogaro.testgame.render.UiRenderer;
import fr.shogaro.testgame.screens.EndScreen;
import fr.shogaro.testgame.screens.GameScreen;
import fr.shogaro.testgame.screens.MenuScreen;
import fr.shogaro.testgame.screens.SettingsScreen;
import fr.shogaro.testgame.systems.AssetStore;
import fr.shogaro.testgame.systems.AudioSystem;

public class Main extends Game {
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private UiRenderer uiRenderer;
    private AudioSystem audioSystem;
    private AssetStore assetStore;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        uiRenderer = new UiRenderer(font);
        assetStore = new AssetStore();
        assetStore.load();
        audioSystem = new AudioSystem("audio/gameMusic.mp3");
        audioSystem.initialize();
        showMenu();
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public UiRenderer getUiRenderer() {
        return uiRenderer;
    }

    public AudioSystem getAudioSystem() {
        return audioSystem;
    }

    public AssetStore getAssetStore() {
        return assetStore;
    }

    public void showMenu() {
        switchScreen(new MenuScreen(this));
    }

    public void showSettings() {
        switchScreen(new SettingsScreen(this));
    }

    public void showGame(CharacterType choice) {
        switchScreen(new GameScreen(this, choice));
    }

    public void showEnd(String message) {
        switchScreen(new EndScreen(this, message));
    }

    private void switchScreen(Screen newScreen) {
        Screen oldScreen = getScreen();
        setScreen(newScreen);
        if (oldScreen != null) {
            oldScreen.dispose();
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        Screen screen = getScreen();
        if (screen != null) {
            screen.dispose();
        }
        if (audioSystem != null) {
            audioSystem.dispose();
        }
        if (assetStore != null) {
            assetStore.dispose();
        }
        if (uiRenderer != null) {
            uiRenderer.dispose();
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
