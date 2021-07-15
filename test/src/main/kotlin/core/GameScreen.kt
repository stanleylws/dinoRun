import core.MyGame
import ktx.app.KtxScreen

class GameScreen(private val game: MyGame) : KtxScreen {

    override fun show() {
    }

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
    }

    override fun dispose() {
    }
}
