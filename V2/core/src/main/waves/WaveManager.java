package io.github.sufod.waves;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.sufod.boss.BossDefinition;
import io.github.sufod.entities.Boss;
import io.github.sufod.entities.Mob;
import io.github.sufod.mobs.MobDefinition;

public class WaveManager {
    private final Array<WaveConfig> waves;
    private int currentIndex = 0;
    private WaveState state = WaveState.SPAWN;
    private final Array<Mob> activeMobs;
    private Boss activeBoss;
    private final ObjectMap<String, MobDefinition> mobDefs;
    private final ObjectMap<String, BossDefinition> bossDefs;
    private final Array<Vector2> spawnPoints;
    
    public WaveManager(Array<WaveConfig> waves, Array<Mob> activeMobs, ObjectMap<String, MobDefinition> mobDefs, ObjectMap<String, BossDefinition> bossDefs, Array<Vector2> spawnPoints) {
        this.waves = waves;
        this.activeMobs = activeMobs;
        this.mobDefs = mobDefs;
        this.bossDefs = bossDefs;
        this.spawnPoints = spawnPoints;
    }

    public void update() {
        if (state == WaveState.DONE) {
            return;
        }

        if (state == WaveState.SPAWN) {
            spawnCurrentWave();
            state = WaveState.WAIT_CLEAR;
            return;
        }

        if (state == WaveState.WAIT_CLEAR) {
            if (activeMobs.size == 0 && activeBoss == null) {
                currentIndex++;
                if (currentIndex >= waves.size) {
                    state = WaveState.DONE;
                } else {
                    state = WaveState.SPAWN;
                }
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
            activeBoss = new Boss(spawn, def.stats);
        } else {
            for (int i = 0; i < config.count; i++) {
                String mobId = pickId(config);
                MobDefinition def = mobDefs.get(mobId);
                if (def == null) {
                    continue;
                }
                Vector2 spawn = pickSpawnPoint();
                activeMobs.add(new Mob(spawn, def.stats));
            }
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