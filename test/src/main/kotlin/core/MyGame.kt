package core

import asset.TextureAsset
import asset.TextureAtlasAsset
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
import core.screen.GameScreen
import core.screen.LoadingScreen
import ecs.system.*
import event.GameEventManager
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.flatten
import ktx.collections.gdxArrayOf

const val V_WIDTH = 16
const val V_HEIGHT = 9
const val V_WIDTH_PIXELS = 384
const val V_HEIGHT_PIXELS = 216

const val IN_DEBUGGING = false

const val SCROLL_SPEED_TO_WORLD_RATIO =  1f / 0.0625f
const val DEFAULT_SCROLL_SPEED = 0.05f
var CURRENT_SCROLL_SPEED = DEFAULT_SCROLL_SPEED

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    lateinit var shape: ShapeRenderer

    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }
    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
    val gameEventManager = GameEventManager()
    val engine: Engine = PooledEngine()

    override fun create() {
        addScreen(LoadingScreen(this))
        setScreen<LoadingScreen>()

        batch = SpriteBatch()
        font = BitmapFont()
        shape = ShapeRenderer()
    }

    override fun render() {
        super.render()
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        font.dispose()
        shape.dispose()
        assets.dispose()
    }
}
