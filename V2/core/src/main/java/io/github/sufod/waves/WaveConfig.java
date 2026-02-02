package io.github.sufod.waves;

import com.badlogic.gdx.utils.Array;

public class WaveConfig {
    public final boolean isBoss;
    public final int count;
    public final Array<String> possibleIds;
    public final boolean randomChoice;

    public WaveConfig(boolean isBoss, int count, Array<String> possibleIds, boolean randomChoice) {
        this.isBoss = isBoss;
        this.count = count;
        this.possibleIds = possibleIds;
        this.randomChoice = randomChoice;
    }
}
