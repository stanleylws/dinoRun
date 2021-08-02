package core.screen

import asset.MusicAsset
import asset.ShaderProgramAsset
import asset.TextureAsset
import asset.TextureAtlasAsset
import core.MyGame
import core.V_WIDTH
import ecs.component.*
import ecs.system.*
import event.GameEvent
import event.GameEventListener
import ktx.app.KtxScreen
import ktx.ashley.entity
import ktx.ashley.with
import ktx.collections.toGdxArray
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.set
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MAX_DELTA_TIME = 1 / 20f
private const val GOLDEN_RATIO = 1.618f

class GameScreen(private val game: MyGame): KtxScreen, GameEventListener {

    init {
        // add system into engine
        game.engine.apply {
            val animationAtlas = game.assets[TextureAtlasAsset.ANIMATION.descriptor]
            val platformTexture = game.assets[TextureAsset.PLATFORM.descriptor]
            val backgroundTextures = TextureAsset.values().filter { it.toString().startsWith("BACKGROUND") }
                .map { game.assets[it.descriptor] }.toGdxArray()

            addSystem(PlayerInputSystem(game.gameViewport))
            addSystem(ObstacleSystem(game.gameEventManager))
            addSystem(PowerUpSystem())
            addSystem(DamageSystem(game.gameEventManager))
            addSystem(LifeBarAnimationSystem())
            addSystem(MoveSystem())
            addSystem(PlayerAnimationSystem())
            addSystem(AnimationSystem(animationAtlas, game.audioService))
            addSystem(
                RenderSystem(game.gameEventManager, game.batch, game.font, game.shape,
                    game.assets[ShaderProgramAsset.OUTLINE.descriptor], game.gameViewport, game.uiViewport,
                    backgroundTextures, platformTexture)
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }

        // create player entity
        game.engine.entity {
            with<TransformComponent>() {
                size.set(2f, 2f)
                setInitialPosition(V_WIDTH.div(2).toFloat() - size.x / 2f, 1f, 1f)
            }
            with<ColliderComponent> {
                modifier = ColliderModifier(0.4f, 0.3f, 0.5f, 1f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<StateComponent>()
            with<AnimationComponent>()
        }

        // create life bar
        game.engine.entity {
            with<TransformComponent> {
                size.set(4f, 2f)
                setInitialPosition(0.5f, 6.8f,3f)
            }
            with<LifeBarComponent>()
            with<GraphicComponent>()
            with<AnimationComponent> { type = AnimationType.LIFE_UI_EMPTY }
        }

        repeat(6){ index ->
            var randomSize = 5f + Math.random().toFloat() * GOLDEN_RATIO
            game.engine.entity {
                with<TransformComponent>() {
                    size.set(randomSize, randomSize)
                    setInitialPosition(-1f - size.x / 2f - index / 3f,-0.5f, if (index % 2 == 0) 2f else 0f)
                }
                with<DamageComponent>()
                with<GraphicComponent>()
                with<AnimationComponent>() {
                    type = AnimationType.EXPLOSION
                    offsetTime = index * GOLDEN_RATIO
                }
            }
        }
    }

    override fun show() {
        game.gameEventManager.addListener(GameEvent.PlayerDeath::class, this)
        game.audioService.play(MusicAsset.BGM, 0.5f)
    }

    override fun hide() {
        super.hide()
        game.gameEventManager.removeListener(GameEvent.PlayerDeath::class, this)
    }

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
        game.uiViewport.update(width, height,true)
    }

    override fun render(delta: Float) {
        game.engine.update(min(MAX_DELTA_TIME, delta))
        game.audioService.update()
    }

    override fun onEvent(event: GameEvent) {
        game.preferences.flush {
            this["highscore"] = (event as GameEvent.PlayerDeath).distance
        }
    }
}
