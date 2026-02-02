package io.github.sufod.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.sufod.input.MovementInput;
import io.github.sufod.graphics.DirectionUtils;
import io.github.sufod.graphics.EntityAnimations;

public class Character {
    private int health;
    private int maxHealth;
    private float speed;
    private int attackDamage;
    private float attackSpeed;
    private float attackRange;

    private Vector2 position;
    private Vector2 velocity;

    private Direction direction;
    private float stateTime;
    // Attack-only timer for non-looping animations.
    private float attackStateTime;
    // Whether attack animation is playing.
    private boolean attacking;
    // Last horizontal input: -1 = left, 1 = right.
    private int lastHorizontalSign = 1;

    // Animation set injected per entity.
    private EntityAnimations animations;
    // Movement input injected by the screen.
    private MovementInput movementInput;

    private float timeSinceLastAttack;

    public Character(Vector2 initialPosition, int maxHealth, int attackDamage, float speed, float attackSpeed, int health) {
            this.maxHealth = maxHealth;
            this.health = health;
            this.attackDamage = attackDamage;
            this.speed = speed;
            this.attackSpeed = attackSpeed;
            this.position = initialPosition;
            this.velocity = new Vector2(0, 0);
        }
    
    public void update (float delta) {
        stateTime += delta;
        timeSinceLastAttack += delta;

        // Keep a separate timer while attacking.
        if (attacking) {
            attackStateTime += delta;
        } else {
            attackStateTime = 0f;
        }

        handleInput();
        updateDirection();
        move(delta);
    }

    private void handleInput() {
        float x = 0;
        float y = 0;

        if (movementInput != null) {
            if (movementInput.isUp()) y += 1; // Haut
            if (movementInput.isDown()) y -= 1; // Bas
            if (movementInput.isLeft()) x -= 1; // Gauche
            if (movementInput.isRight()) x += 1; // Droite
        }

        // Store horizontal intent for diagonal fallback in idle/attack.
        if (x < 0) {
            lastHorizontalSign = -1;
        } else if (x > 0) {
            lastHorizontalSign = 1;
        }

        velocity.set(x, y);

        if (velocity.len2() > 0) {
            velocity.nor();
        }

    }

    private void updateDirection() {
        float x = velocity.x;
        float y = velocity.y;

        if (x == 0 && y == 0) return;

        if(x> 0 && y> 0) direction = Direction.UP_RIGHT;
        else if(x> 0 && y< 0) direction = Direction.DOWN_RIGHT;
        else if(x< 0 && y> 0) direction = Direction.UP_LEFT;
        else if(x< 0 && y< 0) direction = Direction.DOWN_LEFT;
        else if(x> 0) direction = Direction.RIGHT;
        else if(x< 0) direction = Direction.LEFT;
        else if(y> 0) direction = Direction.UP;
        else if(y< 0) direction = Direction.DOWN;

    }

    private void move(float delta) {
        position.x += velocity.x * speed * delta;
        position.y += velocity.y * speed * delta;
    }

    public boolean canAttack() {
        return timeSinceLastAttack >= (1f / attackSpeed);
    }

    public void attack() {
        if (canAttack()) {
            // Logic to deal damage to the target would go here.
            timeSinceLastAttack = 0f;
            attackStateTime = 0f;
            attacking = true;
        }
    }

    public void stopAttack() {
        attacking = false;
        attackStateTime = 0f;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isMoving() {
        return velocity.len2() > 0;
    }

    public TextureRegion getCurrentFrame() {
        if (animations == null) {
            return null;
        }

        // Use a safe default if direction hasn't been set yet.
        Direction currentDirection = direction != null ? direction : Direction.DOWN_RIGHT;

        if (attacking) {
            Direction diagonal = DirectionUtils.toClosestDiagonal(currentDirection, lastHorizontalSign);
            Animation<TextureRegion> attackAnimation = animations.getAttack(diagonal);
            if (attackAnimation == null) {
                attackAnimation = animations.getIdle(diagonal);
            }
            if (attackAnimation == null) {
                attackAnimation = animations.getWalk(currentDirection);
            }
            if (attackAnimation == null) {
                return null;
            }

            // End attack once the animation finishes.
            if (attackAnimation.isAnimationFinished(attackStateTime)) {
                attacking = false;
            }

            return attackAnimation.getKeyFrame(attackStateTime, false);
        }

        if (isMoving()) {
            Animation<TextureRegion> walkAnimation = animations.getWalk(currentDirection);
            if (walkAnimation != null) {
                return walkAnimation.getKeyFrame(stateTime, true);
            }
        }

        // Idle uses diagonal animations when available.
        Direction diagonal = DirectionUtils.toClosestDiagonal(currentDirection, lastHorizontalSign);
        Animation<TextureRegion> idleAnimation = animations.getIdle(diagonal);
        if (idleAnimation == null) {
            idleAnimation = animations.getWalk(currentDirection);
        }

        return idleAnimation != null ? idleAnimation.getKeyFrame(stateTime, true) : null;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setAnimations(EntityAnimations animations) {
        this.animations = animations;
    }

    public void setMovementInput(MovementInput movementInput) {
        this.movementInput = movementInput;
    }

    
}
