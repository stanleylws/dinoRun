import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import core.MyGame
import ktx.app.KtxScreen

class GameScreen(private val game: MyGame) : KtxScreen {
    private var rainMusic: Music

    init {
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/rain.mp3"))
        rainMusic.isLooping = true
    }

    override fun show() {
        rainMusic.play()
    }

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
    }

    override fun dispose() {
        rainMusic.dispose()
    }
}
