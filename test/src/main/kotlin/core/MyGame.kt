package core

import GameScreen
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import com.badlogic.gdx.utils.viewport.FitViewport

const val V_WIDTH = 16
const val V_HEIGHT = 9

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()

        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
    }
}
