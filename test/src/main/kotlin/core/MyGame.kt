package core

import asset.BitmapFontAsset
import asset.TextureAtlasAsset
import audio.DefaultAudioService
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import core.screen.LoadingScreen
import event.GameEventManager
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.log.debug
import ktx.log.logger
import ui.createSkin

const val V_WIDTH = 16
const val V_HEIGHT = 9
const val V_WIDTH_PIXELS = 384
const val V_HEIGHT_PIXELS = 216
const val WORLD_TO_PIXEL_RATIO = 24

const val IN_DEBUGGING = true

const val SCROLL_SPEED_TO_WORLD_RATIO =  1f / 0.0625f
const val DEFAULT_SCROLL_SPEED = 0.05f
var CURRENT_SCROLL_SPEED = DEFAULT_SCROLL_SPEED

private val LOG = logger<MyGame>()

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer

    val assets: AssetStorage by lazy {
        KtxAsync.initiate()
        AssetStorage()
    }

    val gameEventManager = GameEventManager()
    val engine: Engine = PooledEngine()
    val audioService by lazy { DefaultAudioService(assets) }
    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val uiViewport = FitViewport(V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
    val stage: Stage by lazy {
        val result = Stage(uiViewport, batch)
        Gdx.input.inputProcessor = result
        result
    }
    val preferences: Preferences by lazy { Gdx.app.getPreferences("dino-run") }

    override fun create() {
        val assetsRef = gdxArrayOf(
            TextureAtlasAsset.values().filter { it.isSkinAtlas }.map { assets.loadAsync(it.descriptor) },
            BitmapFontAsset.values().map { assets.loadAsync(it.descriptor) }
        ).flatten()
        KtxAsync.launch {
            assetsRef.joinAll()
            createSkin(assets)
            addScreen(LoadingScreen(this@MyGame))
            setScreen<LoadingScreen>()
        }

        batch = SpriteBatch()
        shape = ShapeRenderer()
    }

    override fun render() {
        super.render()
        LOG.debug { "RenderCall: ${batch.renderCalls}" }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        shape.dispose()
        assets.dispose()
        stage.dispose()
    }
}
