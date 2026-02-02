package fr.shogaro.testgame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assassin extends Character{

    private static int HEALTH_MAX = 75;
    private static int HEALTH_CURRENT;
    private static int DAMAGE = 50;
    private static float ATTACK_SPEED = 1.5f;
    private static float ATTACK_RANGE = 3f;
    private static float SPEED = 250;

    public Assassin(float x, float y, float width, float height) {
        super(HEALTH_MAX, DAMAGE, ATTACK_SPEED, ATTACK_RANGE, SPEED);

        System.out.println("x recu = " + x);
        System.out.println("y recu = " + y);

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        loadAnimations();
        update(0f, null, null, 0);
    }


    @Override
    public void move(){
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.Q);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);
        boolean up = Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.Z);
        boolean down = Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S);

        float delta = Gdx.graphics.getDeltaTime();
        float dx = 0f;
        float dy = 0f;

        if (left) {
            dx -= SPEED * delta;
        }
        if (right) {
            dx += SPEED * delta;
        }
        if (up) {
            dy += SPEED * delta;
        }
        if (down) {
            dy -= SPEED * delta;
        }

        moving = dx != 0f || dy != 0f;

        if (moving) {
            x += dx;
            y += dy;

            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + width > Gdx.graphics.getWidth()) {
                x = Gdx.graphics.getWidth() - width;
            }
            if (y + height > Gdx.graphics.getHeight()) {
                y = Gdx.graphics.getHeight() - height;
            }

            if (dx > 0 && dy > 0) {
                setDirection(Direction.TOP_RIGHT);
            } else if (dx < 0 && dy > 0) {
                setDirection(Direction.TOP_LEFT);
            } else if (dx > 0 && dy < 0) {
                setDirection(Direction.BOTTOM_RIGHT);
            } else if (dx < 0 && dy < 0) {
                setDirection(Direction.BOTTOM_LEFT);
            } else if (dx > 0) {
                setDirection(Direction.RIGHT);
            } else if (dx < 0) {
                setDirection(Direction.LEFT);
            } else if (dy > 0) {
                setDirection(Direction.TOP);
            } else if (dy < 0) {
                setDirection(Direction.BOTTOM);
            }
        }

        if (state != State.ATTACK) {
            setState(moving ? State.MOVE : State.IDLE);
        }
    }

    private void loadAnimations() {
        String basePath = "characters/sram";
        float moveFrame = 0.12f;
        float attackFrame = 0.1f;
        float damageFrame = 0.08f;

        addAnimation(State.MOVE, Direction.TOP, buildAnimationSeries(basePath + "/movement/animatedTop", "viewTop", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.BOTTOM, buildAnimationSeries(basePath + "/movement/animatedBottom", "viewBottom", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.LEFT, buildAnimationSeries(basePath + "/movement/animatedLeft", "viewLeft", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.RIGHT, buildAnimationSeries(basePath + "/movement/animatedRight", "viewRight", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.TOP_LEFT, buildAnimationSeries(basePath + "/movement/animatedTopLeft", "viewTopLeft", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.TOP_RIGHT, buildAnimationSeries(basePath + "/movement/animatedTopRight", "viewTopRight", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.BOTTOM_LEFT, buildAnimationSeries(basePath + "/movement/animatedBottomLeft", "viewBottomLeft", 4, moveFrame, Animation.PlayMode.LOOP));
        addAnimation(State.MOVE, Direction.BOTTOM_RIGHT, buildAnimationSeries(basePath + "/movement/animatedBottomRight", "viewBottomRight", 4, moveFrame, Animation.PlayMode.LOOP));

        addAnimation(State.IDLE, Direction.TOP, buildSingleFrame(basePath + "/movement/animatedTop/viewTop1.png"));
        addAnimation(State.IDLE, Direction.BOTTOM, buildSingleFrame(basePath + "/movement/animatedBottom/viewBottom1.png"));
        addAnimation(State.IDLE, Direction.LEFT, buildSingleFrame(basePath + "/movement/animatedLeft/viewLeft1.png"));
        addAnimation(State.IDLE, Direction.RIGHT, buildSingleFrame(basePath + "/movement/animatedRight/viewRight1.png"));
        addAnimation(State.IDLE, Direction.TOP_LEFT, buildSingleFrame(basePath + "/movement/animatedTopLeft/viewTopLeft1.png"));
        addAnimation(State.IDLE, Direction.TOP_RIGHT, buildSingleFrame(basePath + "/movement/animatedTopRight/viewTopRight1.png"));
        addAnimation(State.IDLE, Direction.BOTTOM_LEFT, buildSingleFrame(basePath + "/movement/animatedBottomLeft/viewBottomLeft1.png"));
        addAnimation(State.IDLE, Direction.BOTTOM_RIGHT, buildSingleFrame(basePath + "/movement/animatedBottomRight/viewBottomRight1.png"));

        addAnimation(State.ATTACK, Direction.TOP_LEFT, buildAnimationSeries(basePath + "/attack/animatedTopLeft", "viewTopLeft", 3, attackFrame, Animation.PlayMode.NORMAL));
        addAnimation(State.ATTACK, Direction.TOP_RIGHT, buildAnimationSeries(basePath + "/attack/animatedTopRight", "viewTopRight", 3, attackFrame, Animation.PlayMode.NORMAL));
        addAnimation(State.ATTACK, Direction.BOTTOM_LEFT, buildAnimationSeries(basePath + "/attack/animatedBottomLeft", "viewBottomLeft", 3, attackFrame, Animation.PlayMode.NORMAL));
        addAnimation(State.ATTACK, Direction.BOTTOM_RIGHT, buildAnimationSeries(basePath + "/attack/animatedBottomRight", "viewBottomRight", 3, attackFrame, Animation.PlayMode.NORMAL));

        Animation<TextureRegion> damageAnimation = buildAnimationSeries("vfx/damage", "damage", 4, damageFrame, Animation.PlayMode.NORMAL);
        setDamageOverlayAnimation(damageAnimation);
    }
}
