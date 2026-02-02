package fr.shogaro.testgame.systems;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import fr.shogaro.testgame.CharacterType;

public class AssetStore {
    private Texture mapTexture;
    private Animation<TextureRegion> iopIdleAnimation;
    private Animation<TextureRegion> sramIdleAnimation;
    private final Array<Texture> ownedTextures = new Array<>();

    public void load() {
        mapTexture = new Texture("maps/map.png");
        iopIdleAnimation = buildAnimationSeries("characters/iop/movement/animatedBottom", "viewBottom", 4, 0.15f);
        sramIdleAnimation = buildAnimationSeries("characters/sram/movement/animatedBottom", "viewBottom", 4, 0.15f);
    }

    public Texture getMapTexture() {
        return mapTexture;
    }

    public Animation<TextureRegion> getMenuIdleAnimation(CharacterType type) {
        return type == CharacterType.IOP ? iopIdleAnimation : sramIdleAnimation;
    }

    private Animation<TextureRegion> buildAnimationSeries(String basePath, String framePrefix, int frameCount, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= frameCount; i++) {
            String path = basePath + "/" + framePrefix + i + ".png";
            Texture texture = new Texture(path);
            ownedTextures.add(texture);
            frames.add(new TextureRegion(texture));
        }
        return new Animation<>(frameDuration, frames);
    }

    public void dispose() {
        if (mapTexture != null) {
            mapTexture.dispose();
        }
        for (Texture texture : ownedTextures) {
            texture.dispose();
        }
    }
}
