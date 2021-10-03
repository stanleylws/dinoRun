package core.screen

import asset.*
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mygdx.game.*
import ecs.component.*
import ecs.system.*
import event.GameEvent
import event.GameEventListener
import ktx.actors.*
import ktx.app.KtxScreen
import ktx.ashley.*
import ktx.collections.toGdxArray
import ktx.log.logger
import ktx.preferences.flush
import ktx.preferences.get
import ktx.preferences.set
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table
import ui.GameUI
import kotlin.math.max
import kotlin.math.min

private val LOG = logger<RenderSystem>()
private const val MAX_DELTA_TIME = 1 / 20f

class GameScreen(private val game: MyGame): KtxScreen, GameEventListener {
    private val renderSystem by lazy { game.engine.getSystem<RenderSystem>()  }
    lateinit var touchToBeginLabel: Label
    lateinit var countDownLabel: Label

    private var gameStartCounting = false
    private var gameStarted = false
    private var startCountDown = 3f
    private lateinit var player: Entity

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
        // remove all entities
        game.engine.removeAllEntities()

        val player = spawnPlayer()
        player[PlayerComponent.mapper]?.let {
            ui.updateLife(it.life)
            ui.updateDiamondNumber(it.diamondCollected)
        }

        val obstacleSystem = game.engine.getSystem<ObstacleSystem>()
        obstacleSystem.setSpawning(true)
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
        CURRENT_SCROLL_SPEED = DEFAULT_SCROLL_SPEED * 2
        game.engine.getSystem<PlayerInputSystem>().setProcessing(false)
        game.engine.getSystem<ObstacleSystem>().setProcessing(false)
        game.engine.getSystem<MoveSystem>().setProcessing(false)
    }

    private fun spawnPlayer(): Entity {
        // create player entity
        player = game.engine.entity {
            with<TransformComponent>() {
                size.set(2f, 2f)
                setInitialPosition(V_WIDTH.div(2).toFloat() - size.x / 2f, GROUND_HEIGHT, 1f)
            }
            with<ColliderComponent> {
                modifier = ColliderModifier(0.4f, 0.3f, 0.5f, 0.7f)
            }
            with<MoveComponent>()
            with<GraphicComponent>()
            with<PlayerComponent>()
            with<StateComponent>() {
                currentState = State.WALK
            }
            with<AnimationComponent>()
        }
        return player
    }

    override fun show() {
        game.stage.actors {
            table {
                defaults().fillX().expandX()
                touchToBeginLabel = label("Touch To Begin\n\nBest-${game.preferences["highscore", 0]}m") { cell ->
                    wrap = true
                    setAlignment(Align.center)
                    setFontScale(0.7f)
                    cell.pad(5f)
                }
                row()
                countDownLabel = label("$startCountDown"){ cell ->
                    wrap = true
                    setAlignment(Align.center)
                    color.a = 0f
                    setFontScale(0.7f)
                    cell.pad(5f)
                }
                row()
                setFillParent(true)
                pack()
            }
        }
        touchToBeginLabel += forever(sequence(fadeOut(1f) + fadeIn((1f)) + delay(2f)))
    }

    private fun startGame() {
        game.gameEventManager.run {
            addListener(GameEvent.Collect::class, this@GameScreen)
            addListener(GameEvent.PlayerDamaged::class, this@GameScreen)
            addListener(GameEvent.PlayerDeath::class, this@GameScreen)
        }

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
        game.engine.getSystem<PlayerInputSystem>().setProcessing(true)
        game.engine.getSystem<ObstacleSystem>().setProcessing(true)
        game.engine.getSystem<MoveSystem>().setProcessing(true)
        gameStartCounting = false
        gameStarted = true
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

        if (!gameStarted &&  !gameStartCounting && Gdx.input.justTouched()) {
            touchToBeginLabel.clearActions()
            touchToBeginLabel.color.a = 1f
            touchToBeginLabel.setText("Start In")
            countDownLabel.color.a = 1f
            game.audioService.play(MusicAsset.BGM, 0.5f)
            gameStartCounting = true
        }

        if (gameStartCounting) {
            startCountDown = max(0f, startCountDown - delta)
            if (startCountDown <= 0f) {
                game.stage.clear()
                startGame()
            }
            countDownLabel.setText("${startCountDown.toInt() + 1}")
        }

        player[PlayerComponent.mapper]?.let {
            ui.distanceCountLabel.setText("${it.distance.toInt()}m")
        }

        game.stage.run {
            game.uiViewport.apply()
            act()
            draw()
        }

        if (!game.fadeInCompleted()) game.performFadeIn()
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
                ui.updateLife(0)
                ui.resetButton.isVisible = true
                print(event.distance)
                val highScore = game.preferences.getInteger("highscore")
                game.preferences.flush {
                    if (event.distance > highScore) {
                        this["highscore"] = event.distance
                    }
                }
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
