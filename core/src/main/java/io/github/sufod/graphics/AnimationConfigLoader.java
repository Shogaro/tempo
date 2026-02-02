package io.github.sufod.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import io.github.sufod.entities.Direction;
import java.util.EnumMap;
import java.util.Map;

public class AnimationConfigLoader {
    private final Json json = new Json();

    // Loads a JSON animation config and builds runtime animations.
    public EntityAnimations load(String jsonPath) {
        EntityAnimationConfig config = json.fromJson(
            EntityAnimationConfig.class,
            Gdx.files.internal(jsonPath)
        );

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(config.atlas));

        Map<Direction, Animation<TextureRegion>> walk = buildDirectional(atlas, config.walk);
        Map<Direction, Animation<TextureRegion>> idle = buildDirectional(atlas, config.idle);
        Map<Direction, Animation<TextureRegion>> attack = buildDirectional(atlas, config.attack);

        return new EntityAnimations(atlas, walk, idle, attack);
    }

    private Map<Direction, Animation<TextureRegion>> buildDirectional(
        TextureAtlas atlas,
        DirectionalAnimations dir
    ) {
        Map<Direction, Animation<TextureRegion>> map = new EnumMap<>(Direction.class);

        if (dir == null) {
            return map;
        }

        putIfPresent(map, Direction.UP, dir.top, atlas);
        putIfPresent(map, Direction.DOWN, dir.bottom, atlas);
        putIfPresent(map, Direction.LEFT, dir.left, atlas);
        putIfPresent(map, Direction.RIGHT, dir.right, atlas);
        putIfPresent(map, Direction.UP_LEFT, dir.topLeft, atlas);
        putIfPresent(map, Direction.UP_RIGHT, dir.topRight, atlas);
        putIfPresent(map, Direction.DOWN_LEFT, dir.bottomLeft, atlas);
        putIfPresent(map, Direction.DOWN_RIGHT, dir.bottomRight, atlas);

        return map;
    }

    private void putIfPresent(
        Map<Direction, Animation<TextureRegion>> map,
        Direction dir,
        AnimationSpec spec,
        TextureAtlas atlas
    ) {
        if (spec == null) {
            return;
        }

        // Collect all regions matching the prefix and build the animation.
        Array<TextureAtlas.AtlasRegion> frames = atlas.findRegions(spec.prefix);
        Animation.PlayMode mode = Animation.PlayMode.valueOf(spec.playMode);

        map.put(dir, new Animation<>(spec.frameDuration, frames, mode));
    }
}
