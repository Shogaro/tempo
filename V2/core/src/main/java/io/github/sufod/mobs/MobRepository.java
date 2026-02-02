package io.github.sufod.mobs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class MobRepository {

    private final Json json = new Json();

    public Array<MobDefinition> load(String path) {
        return json.fromJson(
            Array.class,
            MobDefinition.class,
            Gdx.files.internal(path)
        );
    }
}
