package core.screen

import asset.*
import core.MyGame
import core.V_WIDTH
import ecs.component.*
import ecs.system.*
import event.GameEvent
import event.GameEventListener
import ktx.actors.onChangeEvent
import ktx.actors.onClick
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.ashley.*
import ktx.collections.toGdxArray
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.set
import ui.GameUI
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(private val game: MyGame): KtxScreen, GameEventListener {
    private val renderSystem by lazy { game.engine.getSystem<RenderSystem>()  }

    private val ui = GameUI().apply {
        resetButton.onClick {
            resetGame()
            isVisible = false
        }

        soundOnOffButton.onChangeEvent {
            when(this.isChecked) {
                true -> game.audioService.pause()
                else -> game.audioService.resume()
            }
        }

        pauseResumeButton.onChangeEvent {
            when(this.isChecked) {
                true -> game.audioService.pause()
                else -> if (soundOnOffButton.isChecked) Unit else game.audioService.resume()
            }
        }

    }

    private fun resetGame() {
        // remove existing obstacles and collectables
        game.engine.getEntitiesFor(allOf(ObstacleComponent::class, CollectableComponent::class).exclude(RemoveComponent::class).get()).forEach {
            it.addComponent<RemoveComponent>(game.engine)
        }
        game.engine.getSystem<ObstacleSystem>().setSpawning(true)
        spawnPlayer()
    }

    init {
        val font = game.assets[BitmapFontAsset.FONT_DEFAULT.descriptor]
        // add system into engine
        game.engine.apply {
            val animationAtlas = game.assets[TextureAtlasAsset.ANIMATION.descriptor]
            val platformTexture = game.assets[TextureAsset.PLATFORM.descriptor]
            val backgroundTextures = TextureAsset.values().filter { it.toString().startsWith("BACKGROUND") }
                .map { game.assets[it.descriptor] }.toGdxArray()

            addSystem(PlayerInputSystem(game.gameViewport))
            addSystem(ObstacleSystem(game.gameEventManager))
            addSystem(CollectSystem(game.gameEventManager))
            addSystem(DamageSystem(game.gameEventManager))
            addSystem(MoveSystem())
            addSystem(PlayerAnimationSystem())
            addSystem(AnimationSystem(animationAtlas, game.audioService))
            addSystem(
                RenderSystem(game.gameEventManager, game.batch, font, game.shape,
                    game.assets[ShaderProgramAsset.OUTLINE.descriptor], game.gameViewport, game.uiViewport,
                    backgroundTextures, platformTexture)
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem(game.gameEventManager))
        }

        spawnPlayer()
    }

    private fun spawnPlayer() {
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
    }

    override fun show() {
        game.gameEventManager.run {
            addListener(GameEvent.Collect::class, this@GameScreen)
            addListener(GameEvent.PlayerDamaged::class, this@GameScreen)
            addListener(GameEvent.PlayerDeath::class, this@GameScreen)
        }
        game.audioService.play(MusicAsset.BGM, 0.5f)
        ui.run {
            updateLife(MAX_LIFE)
            resetButton.isVisible = false
            soundOnOffButton.run {
                this.isChecked = false
            }
            pauseResumeButton.run {
                this.isChecked = false
            }
        }
        game.stage += ui
    }

    override fun hide() {
        super.hide()
        game.gameEventManager.run {
            removeListener(GameEvent.Collect::class, this@GameScreen)
            removeListener(GameEvent.PlayerDamaged::class, this@GameScreen)
            removeListener(GameEvent.PlayerDeath::class, this@GameScreen)

        }
    }

    override fun resize(width: Int, height: Int) {
        game.gameViewport.update(width, height, true)
        game.uiViewport.update(width, height,true)
    }

    override fun render(delta: Float) {
        val deltaTime = min(delta, MAX_DELTA_TIME)
        if (ui.pauseResumeButton.isChecked) {
            renderSystem.update(0f)
        } else {
            game.engine.update(deltaTime)
            game.audioService.update()
        }

        game.stage.run {
            viewport.apply()
            act()
            draw()
        }
    }

    override fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.Collect -> PlayerOnCollect(event)
            is GameEvent.PlayerDamaged -> {
                event.player[PlayerComponent.mapper]?.let {
                    ui.updateLife(it.life)
                }
            }
            is GameEvent.PlayerDeath -> {
                game.preferences.flush {
                    this["highscore"] = event.distance
                }
                ui.resetButton.isVisible = true
            }
        }
    }

    private fun PlayerOnCollect(event: GameEvent.Collect) {
        val player = event.player[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = ${event.player}" }

        when (event.type) {
            CollectableType.LIFE -> ui.updateLife(player.life)
            CollectableType.DIAMOND -> ui.updateDiamondNumber(player.diamondCollected)
        }
    }
}
