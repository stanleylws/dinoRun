package asset

import com.badlogic.gdx.assets.AssetDescriptor
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