package asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.loaders.BitmapFontLoader
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.glutils.ShaderProgram

enum class TextureAsset(
    fileName: String,
    directory: String = "graphics",
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
    val isSkinAtlas: Boolean,
    fileName: String,
    directory: String = "atlas",
    val descriptor: AssetDescriptor<TextureAtlas> = AssetDescriptor("$directory/$fileName", TextureAtlas::class.java)
) {
    ANIMATION(false, "animation.atlas"),
    UI(true,"ui.atlas")
}

enum class SoundAsset(
    fileName: String,
    directory: String = "sound",
    val descriptor: AssetDescriptor<Sound> = AssetDescriptor("$directory/$fileName", Sound::class.java)
) {
    BOX_HIT("box-hit.wav"),
    BOX_BREAK("box-break.wav"),
    PLAYER_DAMAGED("player-damaged.wav"),
    PLAYER_WALK("player-walk.wav"),
    PLAYER_RUN("player-run.wav"),
    PLAYER_JUMP("player-jump.wav")
}

enum class MusicAsset(
    fileName: String,
    directory: String = "music",
    val descriptor: AssetDescriptor<Music> = AssetDescriptor("$directory/$fileName", Music::class.java)
) {
    RAIN("rain.mp3"),
    BGM("bgm-energy.mp3")
}

enum class ShaderProgramAsset (
    vertexFileName: String,
    fragmentFileName: String,
    directory: String = "shader",
    val descriptor: AssetDescriptor<ShaderProgram> = AssetDescriptor(
        "$directory/$vertexFileName/$fragmentFileName",
        ShaderProgram::class.java,
        ShaderProgramLoader.ShaderProgramParameter().apply {
            vertexFile = "$directory/$vertexFileName"
            fragmentFile = "$directory/$fragmentFileName"
        }
    )
) {
    OUTLINE("default.vert", "outline.frag")
}

enum class BitmapFontAsset(
    fileName: String,
    directory: String = "ui",
    val descriptor: AssetDescriptor<BitmapFont> = AssetDescriptor(
        "$directory/$fileName",
        BitmapFont::class.java,
        BitmapFontLoader.BitmapFontParameter().apply {
            atlasName = TextureAtlasAsset.UI.descriptor.fileName
        }
    )
) {
    FONT_DEFAULT("font_default.fnt")
}
