package fr.shogaro.testgame.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class UiRenderer {
    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public UiRenderer(BitmapFont font) {
        this.font = font;
    }

    public void drawCentered(SpriteBatch batch, String text, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        layout.setText(font, text);
        float x = (Gdx.graphics.getWidth() - layout.width) * 0.5f;
        font.draw(batch, layout, x, y);
        resetFont();
    }

    public void draw(SpriteBatch batch, String text, float x, float y, float scale, Color color) {
        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, text, x, y);
        resetFont();
    }

    public float getTextWidth(String text, float scale) {
        font.getData().setScale(scale);
        layout.setText(font, text);
        float width = layout.width;
        resetFont();
        return width;
    }

    private void resetFont() {
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    public void dispose() {
    }
}
