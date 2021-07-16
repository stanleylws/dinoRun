import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.math.Vector2
import core.MyGame
import core.V_WIDTH
import ecs.component.*
import ecs.system.RenderSystem
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.log.logger
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(private val game: MyGame) : KtxScreen {
    private var rainMusic: Music

    private val player = game.engine.entity {
        with<TransformComponent>() {
            size.set(2f, 2f)
            setInitialPosition(V_WIDTH.div(2).toFloat() - size.x / 2f, 1f, 0f)
        }
        with<MoveComponent>()
        with<GraphicComponent>()
        with<PlayerComponent>()
        with<StateComponent>()
        with<AnimationComponent>()
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
        game.uiViewport.update(width, height,true)
    }

    override fun render(delta: Float) {
        game.engine.update(min(MAX_DELTA_TIME, delta))
    }

    override fun dispose() {
        rainMusic.dispose()
    }
}
