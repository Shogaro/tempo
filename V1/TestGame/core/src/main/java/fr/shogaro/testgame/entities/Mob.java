package fr.shogaro.testgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class Mob {
    private int healthMax;
    private int healthCurrent;
    private float x;
    private float y;
    private float width;
    private float height;
    private float speed;
    private int damage;
    private float attackRangeMultiplier = 1.2f;

    private Animation<TextureRegion> leftAnimation;
    private Animation<TextureRegion> rightAnimation;
    private TextureRegion currentFrame;
    private final Array<Texture> animationTextures = new Array<>();
    private float stateTime;
    private boolean facingRight = true;

    private final Rectangle hitbox = new Rectangle();
    private final Rectangle attackHitbox = new Rectangle();
    public Mob(float x, float y, float width, float height, float speed, int damage, int health, String basePath) {
        this.healthMax = health;
        this.healthCurrent = health;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.damage = damage;
        loadAnimations(basePath);
        updateHitboxes();
    }

    public void update(float delta, float targetX, float targetY, float targetWidth, float targetHeight) {
        stateTime += delta;
        float targetCenterX = targetX + targetWidth * 0.5f;
        float targetCenterY = targetY + targetHeight * 0.5f;
        float centerX = x + width * 0.5f;
        float centerY = y + height * 0.5f;

        float dx = targetCenterX - centerX;
        float dy = targetCenterY - centerY;
        float len = (float) Math.sqrt(dx * dx + dy * dy);

        facingRight = dx >= 0f;

        if (len > 0.001f) {
            float vx = (dx / len) * speed * delta;
            float vy = (dy / len) * speed * delta;
            x += vx;
            y += vy;
        }

        Animation<TextureRegion> activeAnimation = facingRight ? rightAnimation : leftAnimation;
        if (activeAnimation != null) {
            currentFrame = activeAnimation.getKeyFrame(stateTime, true);
        }

        updateHitboxes();
    }

    public void render(SpriteBatch batch) {
        if (currentFrame != null) {
            batch.draw(currentFrame, x, y, width, height);
        }
    }

    public void dispose() {
        for (Texture texture : animationTextures) {
            texture.dispose();
        }
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public int getHealthMax() {
        return healthMax;
    }

    public int getHealthCurrent() {
        return healthCurrent;
    }

    public int getDamage() {
        return damage;
    }

    public void takingDamage(int damage) {
        healthCurrent -= damage;
        if (healthCurrent < 0) {
            healthCurrent = 0;
        }
    }

    private void updateHitboxes() {
        hitbox.set(x, y, width, height);
        attackHitbox.set(x, y, width, height);
    }

    private void loadAnimations(String basePath) {
        float frameDuration = 0.18f;
        leftAnimation = buildAnimationSeries(basePath + "/movement/viewLeft", "viewLeft", 2, frameDuration);
        rightAnimation = buildAnimationSeries(basePath + "/movement/viewRight", "viewRight", 2, frameDuration);
    }

    private Animation<TextureRegion> buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            String path = basePath + "/" + framePrefix + i + ".png";
            Texture texture = new Texture(path);
            animationTextures.add(texture);
            frames.add(new TextureRegion(texture));
        }
        return new Animation<>(frameDuration, frames);
    }
}
