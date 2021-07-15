import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import core.MyGame
import ecs.component.*
import ecs.system.RenderSystem
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MAX_DELTA_TIME = 1 / 20f
private val playerTexture by lazy { Texture(Gdx.files.internal("assets/images/dino_idle_000.png")) }

class GameScreen(private val game: MyGame) : KtxScreen {
    private var rainMusic: Music

    private val player = game.engine.entity {
        with<TransformComponent>() {
            setInitialPosition(2f, 2f, 0f)
        }
        with<GraphicComponent>() {
            setSpriteRegion(TextureRegion(playerTexture))
        }
    }

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
        game.engine.update(min(MAX_DELTA_TIME, delta))
    }

    override fun dispose() {
        rainMusic.dispose()
        playerTexture.dispose()
    }
}
