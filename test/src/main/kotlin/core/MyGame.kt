package core

import GameScreen
import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import ktx.app.KtxGame
import ktx.app.KtxScreen
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.viewport.FitViewport
import ecs.system.*

const val V_WIDTH = 16
const val V_HEIGHT = 9

class MyGame: KtxGame<KtxScreen>() {
    lateinit var batch: SpriteBatch
    lateinit var font: BitmapFont
    private lateinit var playerAtlas: TextureAtlas

    val gameViewport = FitViewport(V_WIDTH.toFloat(), V_HEIGHT.toFloat())
    val engine: Engine = PooledEngine()

    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()

        playerAtlas = TextureAtlas(Gdx.files.internal("assets/atlas/dino.atlas"))

        engine.apply {
            addSystem(PlayerInputSystem(gameViewport))
            addSystem(MoveSystem())
            addSystem(DamageSystem())
            addSystem(PlayerAnimationSystem())
            addSystem(AnimationSystem(playerAtlas))
            addSystem(RenderSystem(batch, gameViewport))
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
        playerAtlas.dispose()
    }
}
