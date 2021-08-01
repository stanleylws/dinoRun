package core.screen

import asset.SoundAsset
import asset.TextureAsset
import asset.TextureAtlasAsset
import core.MyGame
import ecs.system.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import ktx.app.KtxScreen
import ktx.async.KtxAsync
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.collections.toGdxArray
import ktx.log.info
import ktx.log.logger

private val LOG = logger<LoadingScreen>()

class LoadingScreen(private val game: MyGame): KtxScreen {
    override fun show() {
        val startTime = System.currentTimeMillis()
        // queue asset loading
        val assetRefs = gdxArrayOf(
            TextureAsset.values().map { game.assets.loadAsync(it.descriptor) },
            TextureAtlasAsset.values().map { game.assets.loadAsync(it.descriptor) },
            SoundAsset.values().map { game.assets.loadAsync(it.descriptor) }
        ).flatten()

        // once assets are loaded -> change to GameScreen
        KtxAsync.launch {
            assetRefs.joinAll()
            LOG.info { "asset load time: ${System.currentTimeMillis() - startTime} ms" }
            assetsLoaded()
        }
    }

    private fun assetsLoaded() {
        initGameEngine()
        game.addScreen(GameScreen(game))
        game.setScreen<GameScreen>()
        game.removeScreen<LoadingScreen>()
        dispose()
    }

    private fun initGameEngine() {
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
            addSystem(AnimationSystem(animationAtlas))
            addSystem(
                RenderSystem(game.gameEventManager, game.batch, game.font, game.shape, game.gameViewport, game.uiViewport,
                backgroundTextures, platformTexture)
            )
            addSystem(RemoveSystem())
            addSystem(DebugSystem())
        }
    }
}
