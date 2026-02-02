package io.github.sufod;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.sufod.boss.BossDefinition;
import io.github.sufod.boss.BossRepository;
import io.github.sufod.characters.CharacterDefinition;
import io.github.sufod.entities.Boss;
import io.github.sufod.entities.Mob;
import io.github.sufod.graphics.AnimationConfigLoader;
import io.github.sufod.graphics.EntityAnimations;
import io.github.sufod.input.MovementInput;
import io.github.sufod.mobs.MobDefinition;
import io.github.sufod.mobs.MobRepository;
import io.github.sufod.waves.WaveConfig;
import io.github.sufod.waves.WaveManager;
public class GameScreen implements Screen {
    private final CharacterDefinition definition;
    private SpriteBatch batch;
    private io.github.sufod.entities.Character player;
    private EntityAnimations playerAnimations;
    private AnimationConfigLoader animationLoader;
    private MovementInput movementInput;
    private Texture background;
    private Music music;
    private Array<Mob> activeMobs;
    private WaveManager waveManager;
    private int randomLoopStartIndex;
    private ObjectMap<String, EntityAnimations> mobAnimations;
    private ObjectMap<String, EntityAnimations> bossAnimations;
    public GameScreen(CharacterDefinition definition) {
        this.definition = definition;
    }
    @Override
    public void show() {
        batch = new SpriteBatch();
        animationLoader = new AnimationConfigLoader();
        movementInput = new MovementInput();
        Gdx.input.setInputProcessor(movementInput);
        background = new Texture(Gdx.files.internal("map/map.png"));
        music = Gdx.audio.newMusic(Gdx.files.internal("gameMusic.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
        player = new io.github.sufod.entities.Character(
            new Vector2(200, 120),
            definition.stats.maxHealth,
            definition.stats.attackDamage,
            definition.stats.speed,
            definition.stats.attackSpeed,
            definition.stats.maxHealth
        );
        playerAnimations = animationLoader.load(definition.animations);
        player.setAnimations(playerAnimations);
        player.setMovementInput(movementInput);
        activeMobs = new Array<>();
        MobRepository mobRepository = new MobRepository();
        BossRepository bossRepository = new BossRepository();
        Array<MobDefinition> mobList = mobRepository.load("mobs/mobs.json");
        Array<BossDefinition> bossList = bossRepository.load("boss/boss.json");
        ObjectMap<String, MobDefinition> mobDefs = new ObjectMap<>();
        mobAnimations = new ObjectMap<>();
        for (MobDefinition def : mobList) {
            mobDefs.put(def.id, def);
            mobAnimations.put(def.id, animationLoader.load(def.animations));
        }
        ObjectMap<String, BossDefinition> bossDefs = new ObjectMap<>();
        bossAnimations = new ObjectMap<>();
        for (BossDefinition def : bossList) {
            bossDefs.put(def.id, def);
            bossAnimations.put(def.id, animationLoader.load(def.animations));
        }
        Array<Vector2> spawnPoints = buildRandomSpawnPoints(background, 24, 40f);
        Array<WaveConfig> waves = new Array<>();
        Array<String> piouIds = new Array<>();
        piouIds.add("piou");
        addMobWaves(waves, piouIds, 6, 4, false);
        Array<String> piouBoss = new Array<>();
        piouBoss.add("piouRoyal");
        waves.add(new WaveConfig(true, 1, piouBoss, false));
        Array<String> bouftouIds = new Array<>();
        bouftouIds.add("bouftou");
        addMobWaves(waves, bouftouIds, 6, 4, false);
        Array<String> bouftouBoss = new Array<>();
        bouftouBoss.add("bouftouRoyal");
        waves.add(new WaveConfig(true, 1, bouftouBoss, false));
        Array<String> randomMobIds = new Array<>();
        randomMobIds.add("piou");
        randomMobIds.add("bouftou");
        Array<String> randomBossIds = new Array<>();
        randomBossIds.add("piouRoyal");
        randomBossIds.add("bouftouRoyal");
        randomLoopStartIndex = waves.size;
        addMobWaves(waves, randomMobIds, 6, 4, true);
        waves.add(new WaveConfig(true, 1, randomBossIds, true));
        waveManager = new WaveManager(
            waves,
            randomLoopStartIndex,
            activeMobs,
            mobDefs,
            bossDefs,
            mobAnimations,
            bossAnimations,
            spawnPoints
        );
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        movementInput.update();
        player.update(delta);
        updateEnemies(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            activeMobs.clear();
            if (waveManager != null) {
                waveManager.clearBoss();
            }
        }
        pruneDeadEnemies();
        if (waveManager != null) {
            waveManager.update();
        }
        TextureRegion frame = player.getCurrentFrame();
        batch.begin();
        batch.draw(background, 0, 0);
        drawEnemies();
        if (frame != null) {
            batch.draw(frame, player.getPosition().x, player.getPosition().y);
        }
        batch.end();
    }

    private void pruneDeadEnemies() {
        for (int i = activeMobs.size - 1; i >= 0; i--) {
            if (activeMobs.get(i).isDead()) {
                activeMobs.removeIndex(i);
            }
        }

        if (waveManager != null) {
            Boss boss = waveManager.getActiveBoss();
            if (boss != null && boss.isDead()) {
                waveManager.clearBoss();
            }
        }
    }

    private void updateEnemies(float delta) {
        for (int i = 0; i < activeMobs.size; i++) {
            activeMobs.get(i).update(delta);
        }

        if (waveManager != null) {
            Boss boss = waveManager.getActiveBoss();
            if (boss != null) {
                boss.update(delta);
            }
        }
    }

    private void drawEnemies() {
        for (int i = 0; i < activeMobs.size; i++) {
            Mob mob = activeMobs.get(i);
            TextureRegion frame = mob.getCurrentFrame();
            if (frame != null) {
                Vector2 pos = mob.getPosition();
                batch.draw(frame, pos.x, pos.y);
            }
        }

        if (waveManager != null) {
            Boss boss = waveManager.getActiveBoss();
            if (boss != null) {
                TextureRegion frame = boss.getCurrentFrame();
                if (frame != null) {
                    Vector2 pos = boss.getPosition();
                    batch.draw(frame, pos.x, pos.y);
                }
            }
        }
    }
    private Array<Vector2> buildRandomSpawnPoints(Texture mapTexture, int count, float margin) {
        Array<Vector2> points = new Array<>();
        float maxX = Math.max(margin, Gdx.graphics.getWidth() - margin);
        float maxY = Math.max(margin, Gdx.graphics.getHeight() - margin);
        for (int i = 0; i < count; i++) {
            float x = MathUtils.random(margin, maxX);
            float y = MathUtils.random(margin, maxY);
            points.add(new Vector2(x, y));
        }
        return points;
    }
    private void addMobWaves(Array<WaveConfig> waves, Array<String> ids, int count, int repeats, boolean randomChoice) {
        for (int i = 0; i < repeats; i++) {
            waves.add(new WaveConfig(false, count, ids, randomChoice));
        }
    }
    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (playerAnimations != null) {
            playerAnimations.dispose();
        }
        if (mobAnimations != null) {
            for (ObjectMap.Entry<String, EntityAnimations> entry : mobAnimations.entries()) {
                entry.value.dispose();
            }
        }
        if (bossAnimations != null) {
            for (ObjectMap.Entry<String, EntityAnimations> entry : bossAnimations.entries()) {
                entry.value.dispose();
            }
        }
        if (background != null) {
            background.dispose();
        }
        if (music != null) {
            music.stop();
            music.dispose();
        }
    }
}
