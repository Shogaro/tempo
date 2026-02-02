package io.github.sufod.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import io.github.sufod.entities.Direction;
import io.github.sufod.graphics.EntityAnimations;
import io.github.sufod.boss.BossStats;

public class Boss {

    private String id;
    private int health;
    private int maxHealth;
    private int attackDamage;
    private float speed;
    private float attackSpeed;

    private Vector2 position;
    private Vector2 velocity;
    private EntityAnimations animations;
    private float stateTime;

    public Boss(String id, Vector2 spawnPosition, BossStats stats, EntityAnimations animations) {
        this.id = id;
        this.maxHealth = stats.maxHealth;
        this.health = stats.maxHealth;
        this.attackDamage = stats.attackDamage;
        this.speed = stats.speed;
        this.attackSpeed = stats.attackSpeed;

        this.position = spawnPosition;
        this.velocity = new Vector2();
        this.animations = animations;
    }

    public void update(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        if (animations == null) {
            return null;
        }

        Animation<TextureRegion> idle = animations.getIdle(Direction.DOWN_RIGHT);
        if (idle != null) {
            return idle.getKeyFrame(stateTime, true);
        }

        Animation<TextureRegion> walk = animations.getWalk(Direction.DOWN_RIGHT);
        return walk != null ? walk.getKeyFrame(stateTime, true) : null;
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public Vector2 getPosition() {
        return position;
    }
}
