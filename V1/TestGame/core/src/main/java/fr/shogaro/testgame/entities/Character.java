package fr.shogaro.testgame.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.EnumMap;

abstract public class Character {
    public enum Direction {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public enum State {
        IDLE,
        MOVE,
        ATTACK,
        DAMAGE
    }

    protected int healthMax;
    protected int healthCurrent;
    protected float x,y;
    protected float width, height;
    protected int attackDamage;
    protected float attackSpeed;
    protected float attackRange;
    protected float speed;

    protected Direction direction = Direction.BOTTOM;
    protected Direction lastHorizontal = Direction.RIGHT;
    protected Direction lastVertical = Direction.BOTTOM;
    protected State state = State.IDLE;
    protected boolean moving;

    protected float stateTime;
    protected float attackCooldown;
    protected float damageCooldown;
    protected boolean attackTriggered;

    protected final EnumMap<State, EnumMap<Direction, Animation<TextureRegion>>> animations = new EnumMap<>(State.class);
    protected final Array<Texture> animationTextures = new Array<>();
    protected TextureRegion currentFrame;
    protected Animation<TextureRegion> damageOverlayAnimation;
    protected TextureRegion damageOverlayFrame;
    protected float damageOverlayTime;
    protected boolean damageOverlayActive;
    protected float damageOverlayScale = 1.12f;
    protected float flashTime;
    protected float flashDuration = 0.08f;
    protected boolean flashActive;
    protected float flashScale = 1.03f;
    protected float flashAlpha = 0.35f;

    protected Rectangle attackHitbox = new Rectangle();
    protected Rectangle hurtHitbox = new Rectangle();

    protected Character(int health, int damage, float aSpeed, float aRange, float speed) {
        this.healthMax = health;
        this.healthCurrent = health;
        this.attackDamage = damage;
        this.attackSpeed = aSpeed;
        this.attackRange = aRange;
        this.speed = speed;
    }

    abstract public void move();

    public void update(float delta, Rectangle mobHitbox, Rectangle mobAttackHitbox, int mobDamage){
        attackTriggered = false;
        stateTime += delta;

        if (attackCooldown > 0f) {
            attackCooldown -= delta;
        }

        if (damageCooldown > 0f) {
            damageCooldown -= delta;
        }

        updateHitboxes();

        if (mobHitbox != null && attackCooldown <= 0f && attackHitbox.overlaps(mobHitbox)) {
            triggerAttack();
        }

        if (mobAttackHitbox != null && damageCooldown <= 0f && hurtHitbox.overlaps(mobAttackHitbox)) {
            int damageToApply = mobDamage > 0 ? mobDamage : 10;
            triggerDamage(damageToApply);
        }

        if (state == State.ATTACK && isCurrentAnimationFinished()) {
            setState(moving ? State.MOVE : State.IDLE);
        }

        Animation<TextureRegion> animation = getAnimation(state, resolveDirectionForState(state, direction));
        if (animation != null) {
            boolean looping = state == State.MOVE || state == State.IDLE;
            currentFrame = animation.getKeyFrame(stateTime, looping);
        }

        if (damageOverlayActive && damageOverlayAnimation != null) {
            damageOverlayTime += delta;
            if (damageOverlayAnimation.isAnimationFinished(damageOverlayTime)) {
                damageOverlayActive = false;
            } else {
                damageOverlayFrame = damageOverlayAnimation.getKeyFrame(damageOverlayTime, false);
            }
        }

        if (flashActive) {
            flashTime += delta;
            if (flashTime >= flashDuration) {
                flashActive = false;
            }
        }
    }

    public void render(SpriteBatch batch){
        if(currentFrame != null){
            batch.draw(currentFrame, x, y, width, height);
        }
        if (flashActive && currentFrame != null) {
            float flashWidth = width * flashScale;
            float flashHeight = height * flashScale;
            float flashX = x - (flashWidth - width) * 0.5f;
            float flashY = y - (flashHeight - height) * 0.5f;
            batch.setColor(1f, 1f, 1f, flashAlpha);
            batch.draw(currentFrame, flashX, flashY, flashWidth, flashHeight);
            batch.setColor(1f, 1f, 1f, 1f);
        }
        if (damageOverlayActive && damageOverlayFrame != null) {
            float overlayWidth = width * damageOverlayScale;
            float overlayHeight = height * damageOverlayScale;
            float overlayX = x - (overlayWidth - width) * 0.5f;
            float overlayY = y - (overlayHeight - height) * 0.5f;
            batch.draw(damageOverlayFrame, overlayX, overlayY, overlayWidth, overlayHeight);
        }
    }

    public void dispose() {
        for (Texture texture : animationTextures) {
            texture.dispose();
        }
    }

    public int getHealthMax() {
        return healthMax;
    }

    public int getHealthCurrent() {
        return healthCurrent;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public float getAttackRange() {
        return attackRange;
    }

    public float getSpeed(){
        return speed;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Rectangle getAttackHitbox() {
        return attackHitbox;
    }

    public Rectangle getHurtHitbox() {
        return hurtHitbox;
    }

    public void takingDamage(int damage){
        healthCurrent -= damage;
        if(healthCurrent < 0){
            healthCurrent = 0;
        }
    }

    protected void setState(State newState) {
        if (state != newState) {
            state = newState;
            stateTime = 0f;
        }
    }

    protected void setDirection(Direction newDirection) {
        direction = newDirection;
        if (newDirection == Direction.LEFT || newDirection == Direction.RIGHT) {
            lastHorizontal = newDirection;
        }
        if (newDirection == Direction.TOP || newDirection == Direction.BOTTOM) {
            lastVertical = newDirection;
        }
        if (newDirection == Direction.TOP_LEFT || newDirection == Direction.TOP_RIGHT) {
            lastVertical = Direction.TOP;
        }
        if (newDirection == Direction.BOTTOM_LEFT || newDirection == Direction.BOTTOM_RIGHT) {
            lastVertical = Direction.BOTTOM;
        }
        if (newDirection == Direction.TOP_LEFT || newDirection == Direction.BOTTOM_LEFT) {
            lastHorizontal = Direction.LEFT;
        }
        if (newDirection == Direction.TOP_RIGHT || newDirection == Direction.BOTTOM_RIGHT) {
            lastHorizontal = Direction.RIGHT;
        }
    }

    protected void addAnimation(State state, Direction direction, Animation<TextureRegion> animation) {
        EnumMap<Direction, Animation<TextureRegion>> stateAnimations = animations.get(state);
        if (stateAnimations == null) {
            stateAnimations = new EnumMap<>(Direction.class);
            animations.put(state, stateAnimations);
        }
        stateAnimations.put(direction, animation);
    }

    protected void addAnimationForAllDirections(State state, Animation<TextureRegion> animation) {
        for (Direction dir : Direction.values()) {
            addAnimation(state, dir, animation);
        }
    }

    protected void setDamageOverlayAnimation(Animation<TextureRegion> animation) {
        damageOverlayAnimation = animation;
    }

    protected Animation<TextureRegion> buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            String path = basePath + "/" + framePrefix + i + ".png";
            Texture texture = new Texture(path);
            animationTextures.add(texture);
            frames.add(new TextureRegion(texture));
        }
        Animation<TextureRegion> animation = new Animation<>(frameDuration, frames);
        animation.setPlayMode(playMode);
        return animation;
    }

    protected Animation<TextureRegion> buildSingleFrame(String path) {
        Texture texture = new Texture(path);
        animationTextures.add(texture);
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(texture));
        Animation<TextureRegion> animation = new Animation<>(1f, frames);
        animation.setPlayMode(Animation.PlayMode.NORMAL);
        return animation;
    }

    protected void triggerAttack() {
        setState(State.ATTACK);
        attackCooldown = attackSpeed;
        attackTriggered = true;
    }

    protected void triggerDamage(int damage) {
        takingDamage(damage);
        if (damageOverlayAnimation != null) {
            damageOverlayActive = true;
            damageOverlayTime = 0f;
            damageOverlayFrame = damageOverlayAnimation.getKeyFrame(damageOverlayTime, false);
        }
        flashActive = true;
        flashTime = 0f;
        damageCooldown = 0.35f;
    }

    protected boolean isCurrentAnimationFinished() {
        Animation<TextureRegion> animation = getAnimation(state, resolveDirectionForState(state, direction));
        if (animation == null) {
            return true;
        }
        return animation.isAnimationFinished(stateTime);
    }

    protected Animation<TextureRegion> getAnimation(State state, Direction direction) {
        EnumMap<Direction, Animation<TextureRegion>> stateAnimations = animations.get(state);
        if (stateAnimations == null) {
            return null;
        }
        Animation<TextureRegion> animation = stateAnimations.get(direction);
        if (animation != null) {
            return animation;
        }
        animation = stateAnimations.get(Direction.BOTTOM);
        if (animation != null) {
            return animation;
        }
        animation = stateAnimations.get(Direction.LEFT);
        if (animation != null) {
            return animation;
        }
        return stateAnimations.get(Direction.TOP_LEFT);
    }

    protected Direction resolveDirectionForState(State state, Direction baseDirection) {
        EnumMap<Direction, Animation<TextureRegion>> stateAnimations = animations.get(state);
        if (stateAnimations != null && stateAnimations.containsKey(baseDirection)) {
            return baseDirection;
        }
        if (state == State.ATTACK) {
            if (baseDirection == Direction.TOP) {
                return lastHorizontal == Direction.RIGHT ? Direction.TOP_RIGHT : Direction.TOP_LEFT;
            }
            if (baseDirection == Direction.BOTTOM) {
                return lastHorizontal == Direction.RIGHT ? Direction.BOTTOM_RIGHT : Direction.BOTTOM_LEFT;
            }
            if (baseDirection == Direction.LEFT) {
                return lastVertical == Direction.TOP ? Direction.TOP_LEFT : Direction.BOTTOM_LEFT;
            }
            if (baseDirection == Direction.RIGHT) {
                return lastVertical == Direction.TOP ? Direction.TOP_RIGHT : Direction.BOTTOM_RIGHT;
            }
        }
        if (baseDirection == Direction.TOP_RIGHT) {
            return Direction.TOP_LEFT;
        }
        if (baseDirection == Direction.BOTTOM_RIGHT) {
            return Direction.BOTTOM_LEFT;
        }
        if (baseDirection == Direction.RIGHT) {
            return Direction.LEFT;
        }
        return Direction.BOTTOM;
    }

    protected void updateHitboxes() {
        float hurtWidth = width * 0.45f;
        float hurtHeight = height * 0.45f;
        float hurtX = x + (width - hurtWidth) * 0.5f;
        float hurtY = y + (height - hurtHeight) * 0.5f;
        hurtHitbox.set(hurtX, hurtY, hurtWidth, hurtHeight);

        float rangeMultiplier = attackRange > 0f ? attackRange : 1f;
        float attackWidth = width * 1.1f * rangeMultiplier;
        float attackHeight = height * 1.1f * rangeMultiplier;
        float attackX = x + (width - attackWidth) * 0.5f;
        float attackY = y + (height - attackHeight) * 0.5f;

        attackHitbox.set(attackX, attackY, attackWidth, attackHeight);
    }

    public boolean consumeAttackTriggered() {
        if (attackTriggered) {
            attackTriggered = false;
            return true;
        }
        return false;
    }

    public boolean tryTriggerAttack() {
        if (attackCooldown <= 0f) {
            triggerAttack();
            return true;
        }
        return false;
    }

    public void applyDamage(int damage) {
        if (damageCooldown <= 0f) {
            triggerDamage(damage);
        }
    }

}
