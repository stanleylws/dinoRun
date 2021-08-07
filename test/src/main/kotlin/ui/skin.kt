package ui

import asset.BitmapFontAsset
import asset.TextureAtlasAsset
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.imageButton
import ktx.style.label
import ktx.style.skin

enum class SkinImageButton {
    PAUSE_PLAY, SOUND_ON_OFF
}

enum class SkinImage(val atlasKey: String) {
    LIFE_BAR_EMPTY("heart_empty"),
    LIFE_BAR_ONE_HEART("heart_one"),
    LIFE_BAR_TWO_HEART("heart_two"),
    LIFE_BAR_THREE_HEART("heart_three"),
    DIAMOND_COUNT("diamond_count"),
    PLAY("play_button"),
    PAUSE("pause_button"),
    SOUND_ON("music_button"),
    SOUND_OFF("mute_button")
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]

    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label("default") {
            skin.createImageButtonStyles(skin)
            font = defaultFont
        }
    }
}

private fun Skin.createImageButtonStyles(skin: Skin) {
    imageButton(SkinImageButton.SOUND_ON_OFF.name) {
        imageUp = skin.getDrawable(SkinImage.SOUND_ON.atlasKey)
        imageChecked = skin.getDrawable(SkinImage.SOUND_OFF.atlasKey)
        imageDown = imageChecked
    }

    imageButton(SkinImageButton.PAUSE_PLAY.name) {
        imageUp = skin.getDrawable(SkinImage.PAUSE.atlasKey)
        imageChecked = skin.getDrawable(SkinImage.PLAY.atlasKey)
        imageDown = imageChecked
    }
}
