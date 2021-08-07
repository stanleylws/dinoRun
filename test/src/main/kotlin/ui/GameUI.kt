package ui

import com.badlogic.gdx.scenes.scene2d.Group
import core.V_HEIGHT_PIXELS
import core.V_WIDTH_PIXELS
import ktx.actors.plusAssign
import ktx.scene2d.*

class GameUI: Group() {
    val lifeBarImage = scene2d.image(SkinImage.LIFE_BAR_EMPTY.atlasKey)
    val diamondCountImage = scene2d.image(SkinImage.DIAMOND_COUNT.atlasKey)
    val diamondCountLabel = scene2d.label("")
    val pauseResumeButton = scene2d.imageButton(SkinImageButton.PAUSE_PLAY.name)
    val soundOnOffButton = scene2d.imageButton(SkinImageButton.SOUND_ON_OFF.name)

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

        this += soundOnOffButton.apply {
            setSize(25f, 25f)
            setPosition(V_WIDTH_PIXELS - width - 40f, V_HEIGHT_PIXELS - height - 10f)
        }

        this += pauseResumeButton.apply {
            setSize(25f, 25f)
            setPosition(V_WIDTH_PIXELS - width - 10f, V_HEIGHT_PIXELS - height - 10f)
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
