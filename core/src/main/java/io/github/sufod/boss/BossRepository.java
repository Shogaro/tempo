package io.github.sufod.boss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class BossRepository {
    private final Json json = new Json();

    public Array<BossDefinition> load(String path) {
        return json.fromJson(
            Array.class,
            BossDefinition.class,
            Gdx.files.internal(path)
        );
    }
}
