package io.github.sufod.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class CharacterRepository {
    private final Json json = new Json();

    // Loads playable character definitions from a JSON file.
    public Array<CharacterDefinition> load(String path) {
        return json.fromJson(Array.class, CharacterDefinition.class, Gdx.files.internal(path));
    }
}
