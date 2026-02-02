package io.github.sufod.graphics;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.sufod.entities.Direction;
import java.util.Map;

public class EntityAnimations {
    // Keep atlas to dispose later with animations.
    private final TextureAtlas atlas;
    // Directional animation sets by action.
    private final Map<Direction, Animation<TextureRegion>> walk;
    private final Map<Direction, Animation<TextureRegion>> idle;
    private final Map<Direction, Animation<TextureRegion>> attack;

    public EntityAnimations(
        TextureAtlas atlas,
        Map<Direction, Animation<TextureRegion>> walk,
        Map<Direction, Animation<TextureRegion>> idle,
        Map<Direction, Animation<TextureRegion>> attack
    ) {
        this.atlas = atlas;
        this.walk = walk;
        this.idle = idle;
        this.attack = attack;
    }

    public Animation<TextureRegion> getWalk(Direction dir) {
        return walk.get(dir);
    }

    public Animation<TextureRegion> getIdle(Direction dir) {
        return idle.get(dir);
    }

    public Animation<TextureRegion> getAttack(Direction dir) {
        return attack.get(dir);
    }

    // Dispose the atlas when the animations are no longer needed.
    public void dispose() {
        atlas.dispose();
    }
}
