package core

import GameScreen
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import ecs.system.*

const val V_WIDTH = 16
const val V_HEIGHT = 9
const val V_WIDTH_PIXELS = 384
const val V_HEIGHT_PIXELS = 216

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    private lateinit var animationAtlas: TextureAtlas
    private lateinit var platformTexture: Texture
    private lateinit var backgroundTextures: Array<Texture>

    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())

    val engine: Engine = PooledEngine()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()

        animationAtlas = TextureAtlas(Gdx.files.internal("assets/atlas/animations.atlas"))
        backgroundTextures = Array(5) { i ->
            Texture(Gdx.files.internal("assets/backgrounds/plx-${i + 1}.png"))
        }
        platformTexture = Texture(Gdx.files.internal("assets/maps/platform.png"))

        engine.apply {
            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(DamageSystem())
            addSystem(PlayerAnimationSystem())
            addSystem(AnimationSystem(animationAtlas))
            addSystem(RenderSystem(batch, gameViewport, uiViewport, backgroundTextures, platformTexture))
            addSystem(RemoveSystem())
        }

        addScreen(GameScreen(this))
        setScreen<GameScreen>()
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
        animationAtlas.dispose()
        backgroundTextures.forEach { it.dispose() }
    }
}
