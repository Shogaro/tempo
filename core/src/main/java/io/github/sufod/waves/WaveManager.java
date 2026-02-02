package io.github.sufod.waves;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.sufod.boss.BossDefinition;
import io.github.sufod.entities.Boss;
import io.github.sufod.entities.Mob;
import io.github.sufod.graphics.EntityAnimations;
import io.github.sufod.mobs.MobDefinition;

public class WaveManager {
    private final Array<WaveConfig> waves;
    private final int loopStartIndex;
    private int currentIndex;
    private WaveState state;

    private final Array<Mob> activeMobs;
    private Boss activeBoss;

    private final ObjectMap<String, MobDefinition> mobDefs;
    private final ObjectMap<String, BossDefinition> bossDefs;
    private final ObjectMap<String, EntityAnimations> mobAnimations;
    private final ObjectMap<String, EntityAnimations> bossAnimations;
    private final Array<Vector2> spawnPoints;

    public WaveManager(Array<WaveConfig> waves,
                       int loopStartIndex,
                       Array<Mob> activeMobs,
                       ObjectMap<String, MobDefinition> mobDefs,
                       ObjectMap<String, BossDefinition> bossDefs,
                       ObjectMap<String, EntityAnimations> mobAnimations,
                       ObjectMap<String, EntityAnimations> bossAnimations,
                       Array<Vector2> spawnPoints) {
        this.waves = waves;
        this.loopStartIndex = loopStartIndex;
        this.currentIndex = 0;
        this.state = WaveState.SPAWN;
        this.activeMobs = activeMobs;
        this.mobDefs = mobDefs;
        this.bossDefs = bossDefs;
        this.mobAnimations = mobAnimations;
        this.bossAnimations = bossAnimations;
        this.spawnPoints = spawnPoints;
    }

    public void update() {
        if (state == WaveState.SPAWN) {
            spawnCurrentWave();
            state = WaveState.WAIT_CLEAR;
            return;
        }

        if (state == WaveState.WAIT_CLEAR) {
            if (activeMobs.size == 0 && activeBoss == null) {
                currentIndex++;
                if (currentIndex >= waves.size) {
                    currentIndex = loopStartIndex;
                }
                state = WaveState.SPAWN;
            }
        }
    }

    private void spawnCurrentWave() {
        WaveConfig config = waves.get(currentIndex);
        if (config.isBoss) {
            String bossId = pickId(config);
            BossDefinition def = bossDefs.get(bossId);
            if (def == null) {
                return;
            }
            Vector2 spawn = pickSpawnPoint();
            EntityAnimations animations = bossAnimations.get(bossId);
            activeBoss = new Boss(bossId, spawn, def.stats, animations);
            return;
        }

        for (int i = 0; i < config.count; i++) {
            String mobId = pickId(config);
            MobDefinition def = mobDefs.get(mobId);
            if (def == null) {
                continue;
            }
            Vector2 spawn = pickSpawnPoint();
            EntityAnimations animations = mobAnimations.get(mobId);
            activeMobs.add(new Mob(mobId, spawn, def.stats, animations));
        }
    }

    private String pickId(WaveConfig config) {
        if (config.randomChoice) {
            return config.possibleIds.random();
        }
        return config.possibleIds.first();
    }

    private Vector2 pickSpawnPoint() {
        return spawnPoints.random();
    }

    public Boss getActiveBoss() {
        return activeBoss;
    }

    public void clearBoss() {
        activeBoss = null;
    }
}
