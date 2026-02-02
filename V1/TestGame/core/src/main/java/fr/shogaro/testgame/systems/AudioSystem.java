package fr.shogaro.testgame.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class AudioSystem {
    private final String musicPath;
    private Music gameMusic;
    private boolean available;
    private boolean enabled = true;
    private float volume = 0.5f;

    public AudioSystem(String musicPath) {
        this.musicPath = musicPath;
    }

    public void initialize() {
        if (musicPath == null) {
            available = false;
            return;
        }
        available = Gdx.files.internal(musicPath).length() > 1024;
        if (available) {
            gameMusic = Gdx.audio.newMusic(Gdx.files.internal(musicPath));
            gameMusic.setLooping(true);
            applySettings();
        }
    }

    public void toggleEnabled() {
        enabled = !enabled;
        applySettings();
    }

    public void setVolume(float newVolume) {
        volume = Math.max(0f, Math.min(1f, newVolume));
        applySettings();
    }

    public void changeVolume(float delta) {
        setVolume(volume + delta);
    }

    public void applySettings() {
        if (!available || gameMusic == null) {
            return;
        }
        gameMusic.setVolume(volume);
        if (enabled) {
            if (!gameMusic.isPlaying()) {
                gameMusic.play();
            }
        } else {
            gameMusic.pause();
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getVolume() {
        return volume;
    }

    public void dispose() {
        if (gameMusic != null) {
            gameMusic.dispose();
        }
    }
}
