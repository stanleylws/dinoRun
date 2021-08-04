package ui

import asset.BitmapFontAsset
import asset.TextureAtlasAsset
import ktx.assets.async.AssetStorage
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin

fun createSkin(assets: AssetStorage) {
    val atlas = assets[TextureAtlasAsset.UI.descriptor]
    val defaultFont = assets[BitmapFontAsset.FONT_DEFAULT.descriptor]

    Scene2DSkin.defaultSkin = skin(atlas) { skin ->
        label("default") {
            font = defaultFont
        }
    }
}
