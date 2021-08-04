package ui

import asset.BitmapFontAsset
import asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

enum class SkinImage(val atlasKey: String) {
    LIFE_BAR_EMPTY("heart_empty"),
    LIFE_BAR_ONE_HEART("heart_one"),
    LIFE_BAR_TWO_HEART("heart_two"),
    LIFE_BAR_THREE_HEART("heart_three"),
    DIAMOND_COUNT("diamond_count")
}

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]

    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label("default") {
            font = defaultFont
        }
    }
}
