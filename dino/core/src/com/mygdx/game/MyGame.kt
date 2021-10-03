package com.mygdx.game

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
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.viewport.FitViewport
import core.screen.LoadingScreen
import event.GameEventManager
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.log.debug
import ktx.log.logger
import org.lwjgl.opengl.GL11
import ui.createSkin

const val V_WIDTH = 16
const val V_HEIGHT = 9
const val V_WIDTH_PIXELS = 384
const val V_HEIGHT_PIXELS = 216
const val WORLD_TO_PIXEL_RATIO = 24

const val GROUND_HEIGHT = 1f
const val GRAVITATIONAL_ACCELERATION = -10f
const val IN_DEBUGGING = false

const val SCROLL_SPEED_TO_WORLD_RATIO =  1f / 0.0625f
const val DEFAULT_SCROLL_SPEED = 0.05f
var CURRENT_SCROLL_SPEED = DEFAULT_SCROLL_SPEED

private val LOG = logger<MyGame>()

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var shape: ShapeRenderer
    lateinit var fadeRenderer: ShapeRenderer

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

    private val fadeActor:Actor = Actor()

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
        fadeRenderer = ShapeRenderer(8)
        resetFadeActor(true)
    }

    override fun render() {
        super.render()
        LOG.debug { "RenderCall: ${batch.renderCalls}" }
    }

    fun resetFadeActor(fadeOut: Boolean) {
        fadeActor.color.a = if (fadeOut) 0f else 1f
        fadeActor.clearActions()
        fadeActor.addAction(if (fadeOut) Actions.fadeIn(0.5f) else Actions.fadeOut(0.5f))
    }

    fun fadeOutCompleted(): Boolean {
        return fadeActor.color.a == 1f
    }

    fun performFadeOut() {
        if (fadeOutCompleted()) return

        fadeActor.act(Gdx.graphics.deltaTime)
        fadeActor.actions.isEmpty
        Gdx.gl.glEnable(GL11.GL_BLEND)
        Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        fadeRenderer.use(ShapeRenderer.ShapeType.Filled, uiViewport.camera) {
            it.setColor(0f, 0f, 0f, fadeActor.color.a)
            it.rect(0f, 0f, V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
        }
        Gdx.gl.glDisable(GL11.GL_BLEND)
    }

    fun fadeInCompleted(): Boolean {
        return fadeActor.color.a == 0f
    }

    fun performFadeIn() {
        if (fadeInCompleted()) return

        fadeActor.act(Gdx.graphics.deltaTime)
        fadeActor.actions.isEmpty
        Gdx.gl.glEnable(GL11.GL_BLEND)
        Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        fadeRenderer.use(ShapeRenderer.ShapeType.Filled, uiViewport.camera) {
            it.setColor(0f, 0f, 0f, fadeActor.color.a)
            it.rect(0f, 0f, V_WIDTH_PIXELS.toFloat(), V_HEIGHT_PIXELS.toFloat())
        }
        Gdx.gl.glDisable(GL11.GL_BLEND)
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        shape.dispose()
        fadeRenderer.dispose()
        assets.dispose()
        stage.dispose()
    }
}
