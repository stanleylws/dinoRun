package asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum class TextureAsset(
    fileName: String,
    directory: String = "assets/graphics",
    val descriptor: AssetDescriptor<Texture> = AssetDescriptor("$directory/$fileName", Texture::class.java)
) {
    BACKGROUND_ONE("plx-1.png"),
    BACKGROUND_TWO("plx-2.png"),
    BACKGROUND_THREE("plx-3.png"),
    BACKGROUND_FOUR("plx-4.png"),
    BACKGROUND_FIVE("plx-5.png"),
    PLATFORM("platform.png")
}

enum class TextureAtlasAsset(
    fileName: String,
    directory: String = "assets/atlas",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$fileName", TextureAtlas::class.java)
) {
    ANIMATION("animation.atlas")
}

enum class SoundAsset(
    fileName: String,
    directory: String = "assets/sound",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$fileName", Sound::class.java)
) {
    BOX_HIT("box_hit.wav"),
    BOX_BREAK("box_break.wav"),
    PLAYER_DAMAGED("player_damaged.wav"),
    PLAYER_WALK("player_walk.wav"),
    PLAYER_RUN("player_run.wav")
}

enum class MusicAsset(
    fileName: String,
    directory: String = "assets/music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName", Music::class.java)
) {
    RAIN("rain.mp3"),
    BGM("bgm_energy.mp3")
}
