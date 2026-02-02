package io.github.sufod.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.IntIntMap;
import com.badlogic.gdx.utils.IntSet;

public class MovementInput implements InputProcessor {
    // Tracks keycodes that are currently held down.
    private final IntSet pressedKeys = new IntSet();
    // Maps keycodes to typed characters (layout-aware).
    private final IntIntMap keycodeToChar = new IntIntMap();
    // Last keycode that triggered keyDown (used with keyTyped).
    private int lastKeyDownCode = -1;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    // Call once per frame to update directional booleans.
    public void update() {
        up = false;
        down = false;
        left = false;
        right = false;

        for (IntSet.IntSetIterator it = pressedKeys.iterator(); it.hasNext; ) {
            int keycode = it.next();
            int mapped = keycodeToChar.get(keycode, 0);

            if (mapped == 'z') up = true;
            if (mapped == 's') down = true;
            if (mapped == 'q') left = true;
            if (mapped == 'd') right = true;
        }
    }

    public boolean isUp() {
        return up;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isRight() {
        return right;
    }

    @Override
    public boolean keyDown(int keycode) {
        pressedKeys.add(keycode);
        lastKeyDownCode = keycode;
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        char lower = java.lang.Character.toLowerCase(character);

        if (lower == 'z' || lower == 'q' || lower == 's' || lower == 'd') {
            if (lastKeyDownCode != -1) {
                keycodeToChar.put(lastKeyDownCode, lower);
            }
        }

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
