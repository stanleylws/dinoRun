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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ecs.system.*
import event.GameEventManager

const val V_WIDTH = 16
const val V_HEIGHT = 9
const val V_WIDTH_PIXELS = 384
const val V_HEIGHT_PIXELS = 216

const val IN_DEBUGGING = true

const val SCROLL_SPEED_TO_WORLD_RATIO =  1f / 0.0625f
const val DEFAULT_SCROLL_SPEED = 0.05f
var CURRENT_SCROLL_SPEED = DEFAULT_SCROLL_SPEED

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    lateinit var shapeRenderer: ShapeRenderer

    private lateinit var animationAtlas: TextureAtlas
    private lateinit var platformTexture: Texture
    private lateinit var backgroundTextures: Array<Texture>

    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
    val gameEventManager = GameEventManager()
    val engine: Engine = PooledEngine()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        shapeRenderer = ShapeRenderer()

        animationAtlas = TextureAtlas(Gdx.files.internal("assets/atlas/animation.atlas"))
        backgroundTextures = Array(5) { i ->
            Texture(Gdx.files.internal("assets/backgrounds/plx-${i + 1}.png"))
        }
        platformTexture = Texture(Gdx.files.internal("assets/maps/platform.png"))

        engine.apply {
            addSystem(PlayerInputSystem(gameViewport))
            addSystem(ObstacleSystem(gameEventManager))
            addSystem(DamageSystem(gameEventManager))
            addSystem(MoveSystem())
            addSystem(PlayerAnimationSystem())
            addSystem(AnimationSystem(animationAtlas))
            addSystem(RenderSystem(shapeRenderer, batch, gameViewport, uiViewport, backgroundTextures, platformTexture))
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
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
