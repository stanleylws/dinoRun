package ui

import com.badlogic.gdx.scenes.scene2d.Group
import core.V_HEIGHT_PIXELS
import ktx.actors.plusAssign
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.label
import ktx.scene2d.scene2d

class GameUI: Group() {
    private val lifeBarImage = scene2d.image(SkinImage.LIFE_BAR_EMPTY.atlasKey)
    private val diamondCountImage = scene2d.image(SkinImage.DIAMOND_COUNT.atlasKey)
    private val diamondCountLabel = scene2d.label("")

    init {
        this += lifeBarImage.apply {
            setSize(width * 1.5f, height * 1.5f)
            setPosition(0f, V_HEIGHT_PIXELS - height)
        }

        this += diamondCountImage.apply {
            setSize(width * 1.5f, height * 1.5f)
            setPosition(20f, V_HEIGHT_PIXELS - height - 35f)
        }

        this += diamondCountLabel.apply {
            setText(0)
            setFontScale(0.4f)
            setPosition(50f, V_HEIGHT_PIXELS - 45f)
        }
    }

    fun updateLife(life: Int) {
        val atlasKey = when(life) {
            1 -> SkinImage.LIFE_BAR_ONE_HEART.atlasKey
            2 -> SkinImage.LIFE_BAR_TWO_HEART.atlasKey
            3 -> SkinImage.LIFE_BAR_THREE_HEART.atlasKey
            else -> SkinImage.LIFE_BAR_EMPTY.atlasKey
        }
        lifeBarImage.setDrawable(Scene2DSkin.defaultSkin, atlasKey)
    }

    fun updateDiamondNumber(diamondCollected: Int) {
        diamondCountLabel.setText(diamondCollected.toString())
    }
}
