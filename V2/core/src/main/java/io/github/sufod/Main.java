package io.github.sufod;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    @Override
    public void create() {
        // Start the character selection screen.
        setScreen(new CharacterSelectScreen(this));
    }
}
