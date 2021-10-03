package core.screen

import asset.ShaderProgramAsset
import asset.SoundAsset
import asset.TextureAsset
import asset.TextureAtlasAsset
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.mygdx.game.MyGame
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.actors.plus
import ktx.actors.plusAssign
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import ktx.collections.gdxArrayOf
import ktx.graphics.use
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.actors
import ktx.scene2d.label
import ktx.scene2d.table

private val LOG = logger<LoadingScreen>()

class LoadingScreen(private val game: MyGame): KtxScreen {
    lateinit var loadingLabel: Label
    val background = Texture(Gdx.files.internal("graphics/background.png"))
    var fadeOutAlpha = 0f

    override fun show() {
        val startTime = System.currentTimeMillis()
        // queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { game.assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { game.assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { game.assets.loadAsync(it.descriptor) },
            ShaderProgramAsset.values().map { game.assets.loadAsync(it.descriptor) }
        ).flatten()

        // once assets are loaded -> change to GameScreen
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.info { "asset load time: ${System.currentTimeMillis() - startTime} ms" }
            assetsLoaded()
        }

        setupUI()
    }

    override fun hide() {
        game.stage.clear()
    }

    private fun setupUI() {
        game.stage.actors {
            table {
                defaults().fillX().expandX()

                loadingLabel = label("Loading") { cell ->
                    wrap = true
                    setAlignment(Align.center)
                    cell.pad(5f)
                }

                setFillParent(true)
                pack()
            }
        }
        loadingLabel += forever(sequence(alpha(1f) + delay(0.5f) + alpha(0f) + delay(0.5f)))
    }

    override fun resize(width: Int, height: Int) {
        game.uiViewport.update(width, height,true)
    }

    override fun render(delta: Float) {
        game.batch.use(game.uiViewport.camera) {
            it.draw(background, 0f, 0f)
        }
        game.stage.run {
            game.uiViewport.apply()
            act()
            draw()
        }

        if (game.assets.progress.isFinished && game.containsScreen<GameScreen>()) {
            game.performFadeOut()

            if (game.fadeOutCompleted()) {
                game.resetFadeActor(false)
                game.setScreen<GameScreen>()
                game.removeScreen<LoadingScreen>()
                dispose()
            }
        }
    }

    private fun assetsLoaded() {
        game.addScreen(GameScreen(game))
    }
}
