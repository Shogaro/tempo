package fr.shogaro.testgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import fr.shogaro.testgame.CharacterType;
import fr.shogaro.testgame.Main;
import fr.shogaro.testgame.render.UiRenderer;
import fr.shogaro.testgame.systems.AssetStore;

public class MenuScreen implements Screen {
    private static final Color COLOR_SELECTED = new Color(1f, 1f, 1f, 1f);
    private static final Color COLOR_UNSELECTED = new Color(0.6f, 0.6f, 0.6f, 1f);

    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final UiRenderer uiRenderer;
    private final AssetStore assetStore;
    private CharacterType selection = CharacterType.IOP;
    private float menuTime;

    public MenuScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();
        this.uiRenderer = game.getUiRenderer();
        this.assetStore = game.getAssetStore();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);
        menuTime += delta;

        handleInput();

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float cardWidth = 220f;
        float cardHeight = 320f;
        float spacing = 120f;
        float centerY = screenHeight * 0.5f - cardHeight * 0.5f;
        float leftX = screenWidth * 0.5f - cardWidth - spacing * 0.5f;
        float rightX = screenWidth * 0.5f + spacing * 0.5f;

        batch.begin();

        renderMenuCharacter(CharacterType.IOP, leftX, centerY, cardWidth, cardHeight);
        renderMenuCharacter(CharacterType.SRAM, rightX, centerY, cardWidth, cardHeight);

        float labelY = centerY - 30f;
        renderMenuLabel("IOP", leftX, labelY, cardWidth, selection == CharacterType.IOP);
        renderMenuLabel("SRAM", rightX, labelY, cardWidth, selection == CharacterType.SRAM);

        uiRenderer.drawCentered(batch, "Choisis ton personnage", screenHeight - 60f, 1.2f, Color.WHITE);
        uiRenderer.drawCentered(batch, "Entree/Espace pour jouer", screenHeight - 95f, 0.9f, Color.WHITE);
        uiRenderer.drawCentered(batch, "S pour settings", screenHeight - 125f, 0.9f, Color.WHITE);

        batch.end();

        float selectedX = selection == CharacterType.IOP ? leftX : rightX;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(selectedX - 8f, centerY - 8f, cardWidth + 16f, cardHeight + 16f);
        shapeRenderer.end();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            selection = CharacterType.IOP;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            selection = CharacterType.SRAM;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.showGame(selection);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            game.showSettings();
        }
    }

    private void renderMenuCharacter(CharacterType choice, float x, float y, float width, float height) {
        boolean selected = selection == choice;
        Animation<TextureRegion> animation = assetStore.getMenuIdleAnimation(choice);
        float time = selected ? menuTime : 0f;

        if (!selected) {
            batch.setColor(0.4f, 0.4f, 0.4f, 1f);
        }

        if (animation != null) {
            TextureRegion frame = animation.getKeyFrame(time, true);
            batch.draw(frame, x, y, width, height);
        }

        if (!selected) {
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    private void renderMenuLabel(String text, float x, float y, float width, boolean selected) {
        Color color = selected ? COLOR_SELECTED : COLOR_UNSELECTED;
        float labelWidth = uiRenderer.getTextWidth(text, 1f);
        uiRenderer.draw(batch, text, x + (width - labelWidth) * 0.5f, y, 1f, color);
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
