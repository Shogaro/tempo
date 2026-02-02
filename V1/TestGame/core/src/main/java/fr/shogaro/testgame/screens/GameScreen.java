package fr.shogaro.testgame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import fr.shogaro.testgame.CharacterType;
import fr.shogaro.testgame.Main;
import fr.shogaro.testgame.entities.Assassin;
import fr.shogaro.testgame.entities.Character;
import fr.shogaro.testgame.entities.Mob;
import fr.shogaro.testgame.entities.Warrior;
import fr.shogaro.testgame.systems.AssetStore;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final AssetStore assetStore;
    private final Texture mapTexture;
    private Character player;
    private List<Mob> mobs = new ArrayList<>();

    public GameScreen(Main game, CharacterType selection) {
        this.game = game;
        this.batch = game.getBatch();
        this.shapeRenderer = game.getShapeRenderer();
        this.assetStore = game.getAssetStore();
        this.mapTexture = assetStore.getMapTexture();
        startGame(selection);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.12f, 0.12f, 0.18f, 1f);

        if (player == null || mobs.isEmpty()) {
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.showMenu();
            return;
        }

        player.move();
        player.update(delta, null, null, 0);

        Mob closestMob = null;
        float closestDistance = Float.MAX_VALUE;
        float playerCenterX = player.getX() + player.getWidth() * 0.5f;
        float playerCenterY = player.getY() + player.getHeight() * 0.5f;

        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() <= 0) {
                continue;
            }

            mob.update(delta, player.getX(), player.getY(), player.getWidth(), player.getHeight());

            if (mob.getAttackHitbox().overlaps(player.getHurtHitbox())) {
                player.applyDamage(mob.getDamage());
            }

            if (mob.getHitbox().overlaps(player.getAttackHitbox())) {
                float mobCenterX = mob.getHitbox().x + mob.getHitbox().width * 0.5f;
                float mobCenterY = mob.getHitbox().y + mob.getHitbox().height * 0.5f;
                float dx = mobCenterX - playerCenterX;
                float dy = mobCenterY - playerCenterY;
                float distance = dx * dx + dy * dy;
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestMob = mob;
                }
            }
        }

        if (closestMob != null && player.tryTriggerAttack()) {
            if (player.consumeAttackTriggered()) {
                closestMob.takingDamage(player.getAttackDamage());
            }
        }

        boolean anyAlive = false;
        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() > 0) {
                anyAlive = true;
                break;
            }
        }

        if (!anyAlive) {
            game.showEnd("GG");
            return;
        }

        if (player.getHealthCurrent() <= 0) {
            game.showEnd("GAME OVER");
            return;
        }

        batch.begin();
        batch.draw(mapTexture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player.render(batch);
        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() > 0) {
                mob.render(batch);
            }
        }
        batch.end();

        renderHealthBars();
        renderHitboxes();
    }

    private void startGame(CharacterType selection) {
        float startX = 200f;
        float startY = 200f;
        float width = 50f;
        float height = 150f;

        if (selection == CharacterType.IOP) {
            player = new Warrior(startX, startY, width, height);
        } else {
            player = new Assassin(startX, startY, width, height);
        }

        mobs = new ArrayList<>();
        float mobWidth = 70f;
        float mobHeight = 70f;
        float margin = 80f;
        float minX = margin;
        float maxX = Gdx.graphics.getWidth() - mobWidth - margin;
        float minY = margin;
        float maxY = Gdx.graphics.getHeight() - mobHeight - margin;
        for (int i = 0; i < 10; i++) {
            float mobX = minX + (float) Math.random() * Math.max(1f, maxX - minX);
            float mobY = minY + (float) Math.random() * Math.max(1f, maxY - minY);
            mobs.add(new Mob(mobX, mobY, mobWidth, mobHeight, 90f, 10, 60, "mobs/piou"));
        }
    }

    private void renderHealthBars() {
        float screenWidth = Gdx.graphics.getWidth();
        float margin = 20f;
        float barWidth = 240f;
        float barHeight = 16f;
        float playerX = screenWidth - barWidth - margin;
        float playerY = margin;

        float playerRatio = player.getHealthMax() > 0 ? (float) player.getHealthCurrent() / player.getHealthMax() : 0f;
        float playerFill = barWidth * Math.max(0f, Math.min(1f, playerRatio));

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
        shapeRenderer.rect(playerX, playerY, barWidth, barHeight);
        shapeRenderer.setColor(0.2f, 0.9f, 0.2f, 1f);
        shapeRenderer.rect(playerX, playerY, playerFill, barHeight);
        shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() <= 0) {
                continue;
            }
            float mobBarWidth = Math.max(40f, mob.getHitbox().width);
            float mobBarHeight = 8f;
            float mobX = mob.getHitbox().x + (mob.getHitbox().width - mobBarWidth) * 0.5f;
            float mobY = mob.getHitbox().y + mob.getHitbox().height + 8f;
            float mobRatio = mob.getHealthMax() > 0 ? (float) mob.getHealthCurrent() / mob.getHealthMax() : 0f;
            float mobFill = mobBarWidth * Math.max(0f, Math.min(1f, mobRatio));
            shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 0.85f);
            shapeRenderer.rect(mobX, mobY, mobBarWidth, mobBarHeight);
            shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
            shapeRenderer.rect(mobX, mobY, mobFill, mobBarHeight);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 1f, 1f);
        shapeRenderer.rect(playerX, playerY, barWidth, barHeight);
        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() <= 0) {
                continue;
            }
            float mobBarWidth = Math.max(40f, mob.getHitbox().width);
            float mobBarHeight = 8f;
            float mobX = mob.getHitbox().x + (mob.getHitbox().width - mobBarWidth) * 0.5f;
            float mobY = mob.getHitbox().y + mob.getHitbox().height + 8f;
            shapeRenderer.rect(mobX, mobY, mobBarWidth, mobBarHeight);
        }
        shapeRenderer.end();
    }

    private void renderHitboxes() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(player.getHurtHitbox().x, player.getHurtHitbox().y, player.getHurtHitbox().width, player.getHurtHitbox().height);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(player.getAttackHitbox().x, player.getAttackHitbox().y, player.getAttackHitbox().width, player.getAttackHitbox().height);
        shapeRenderer.setColor(0f, 0.6f, 1f, 1f);
        for (Mob mob : mobs) {
            if (mob.getHealthCurrent() <= 0) {
                continue;
            }
            shapeRenderer.rect(mob.getHitbox().x, mob.getHitbox().y, mob.getHitbox().width, mob.getHitbox().height);
            shapeRenderer.setColor(1f, 0.6f, 0f, 1f);
            shapeRenderer.rect(mob.getAttackHitbox().x, mob.getAttackHitbox().y, mob.getAttackHitbox().width, mob.getAttackHitbox().height);
            shapeRenderer.setColor(0f, 0.6f, 1f, 1f);
        }
        shapeRenderer.end();
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
        if (player != null) {
            player.dispose();
        }
        for (Mob mob : mobs) {
            mob.dispose();
        }
    }
}
