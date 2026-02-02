package io.github.sufod;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Music;
import io.github.sufod.characters.CharacterDefinition;
import io.github.sufod.characters.CharacterRepository;
import io.github.sufod.entities.Direction;
import io.github.sufod.graphics.AnimationConfigLoader;
import io.github.sufod.graphics.EntityAnimations;
import com.badlogic.gdx.graphics.g2d.Animation;

public class CharacterSelectScreen implements Screen {
    private final Game game;
    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private BitmapFont font;
    private AnimationConfigLoader animationLoader;
    private Array<CharacterDefinition> characters;
    private Array<EntityAnimations> animations;
    private Array<Rectangle> cards;
    private float stateTime;
    private int selectedIndex;
    private Music music;

    public CharacterSelectScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();
        animationLoader = new AnimationConfigLoader();

        // Menu music.
        music = Gdx.audio.newMusic(Gdx.files.internal("gameMusic.mp3"));
        music.setLooping(true);
        music.setVolume(0.2f);
        music.play();

        // Load playable definitions and their animations.
        CharacterRepository repository = new CharacterRepository();
        characters = repository.load("characters/playables.json");
        animations = new Array<>();
        cards = new Array<>();

        for (CharacterDefinition definition : characters) {
            animations.add(animationLoader.load(definition.animations));
        }

        buildLayout();
        Gdx.input.setInputProcessor(new SelectionInput());
    }

    private void buildLayout() {
        cards.clear();

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float cardWidth = 220f;
        float cardHeight = 220f;
        float spacing = 40f;
        float totalWidth = characters.size * cardWidth + (characters.size - 1) * spacing;
        float startX = (width - totalWidth) * 0.5f;
        float y = height * 0.5f - cardHeight * 0.5f;

        for (int i = 0; i < characters.size; i++) {
            float x = startX + i * (cardWidth + spacing);
            cards.add(new Rectangle(x, y, cardWidth, cardHeight));
        }
    }

    @Override
    public void render(float delta) {
        stateTime += delta;

        Gdx.gl.glClearColor(0.06f, 0.06f, 0.06f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapes.begin(ShapeRenderer.ShapeType.Line);
        for (int i = 0; i < cards.size; i++) {
            Rectangle card = cards.get(i);
            if (i == selectedIndex) {
                shapes.setColor(0.9f, 0.8f, 0.2f, 1f);
            } else {
                shapes.setColor(0.4f, 0.4f, 0.4f, 1f);
            }
            shapes.rect(card.x, card.y, card.width, card.height);
        }
        shapes.end();

        batch.begin();
        for (int i = 0; i < characters.size; i++) {
            Rectangle card = cards.get(i);
            CharacterDefinition definition = characters.get(i);
            EntityAnimations entityAnimations = animations.get(i);
            Animation<TextureRegion> animation = getPreviewAnimation(entityAnimations);

            if (animation != null) {
                TextureRegion frame = animation.getKeyFrame(stateTime, true);
                float frameX = card.x + (card.width - frame.getRegionWidth()) * 0.5f;
                float frameY = card.y + (card.height - frame.getRegionHeight()) * 0.5f + 10f;
                batch.draw(frame, frameX, frameY);
            }

            font.setColor(i == selectedIndex ? 1f : 0.8f, 0.8f, 0.8f, 1f);
            font.draw(batch, definition.name, card.x + 12f, card.y + 24f);
        }
        batch.end();
    }

    private Animation<TextureRegion> getPreviewAnimation(EntityAnimations entityAnimations) {
        // Prefer idle diagonal; fallback to walk if idle is missing.
        Animation<TextureRegion> idle = entityAnimations.getIdle(Direction.DOWN_LEFT);
        if (idle == null) {
            idle = entityAnimations.getIdle(Direction.DOWN_RIGHT);
        }
        if (idle == null) {
            idle = entityAnimations.getWalk(Direction.DOWN_LEFT);
        }
        if (idle == null) {
            idle = entityAnimations.getWalk(Direction.DOWN_RIGHT);
        }
        return idle;
    }

    private void chooseSelected() {
        CharacterDefinition definition = characters.get(selectedIndex);
        game.setScreen(new GameScreen(definition));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        buildLayout();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (shapes != null) {
            shapes.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (animations != null) {
            for (EntityAnimations entityAnimations : animations) {
                entityAnimations.dispose();
            }
        }
        if (music != null) {
            music.stop();
            music.dispose();
        }
    }

    private class SelectionInput extends InputAdapter {
        @Override
        public boolean keyDown(int keycode) {
            // Keyboard selection: arrows, enter, or number keys.
            if (keycode == Input.Keys.LEFT) {
                selectedIndex = (selectedIndex - 1 + characters.size) % characters.size;
                return true;
            }
            if (keycode == Input.Keys.RIGHT) {
                selectedIndex = (selectedIndex + 1) % characters.size;
                return true;
            }
            if (keycode == Input.Keys.ENTER) {
                chooseSelected();
                return true;
            }
            if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9) {
                int index = keycode - Input.Keys.NUM_1;
                if (index < characters.size) {
                    selectedIndex = index;
                    chooseSelected();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            // Mouse selection: click a card to choose.
            if (button != Input.Buttons.LEFT) {
                return false;
            }

            float y = Gdx.graphics.getHeight() - screenY;
            for (int i = 0; i < cards.size; i++) {
                if (cards.get(i).contains(screenX, y)) {
                    selectedIndex = i;
                    chooseSelected();
                    return true;
                }
            }

            return false;
        }
    }
}
