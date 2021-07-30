package ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import core.CURRENT_SCROLL_SPEED
import core.IN_DEBUGGING
import ecs.component.*
import event.GameEvent
import event.GameEventListener
import event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class RenderSystem(
    private val gameEventManager: GameEventManager,
    private val batch: Batch,
    private val font: BitmapFont,
    private val shapeRenderer: ShapeRenderer,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTextures: Array<Texture>,
    platformTexture: Texture,
): GameEventListener,
    SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper]}
) {
    private val platform = Sprite(platformTexture.apply { setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat) })
    private val backgrounds = backgroundTextures.map { texture ->
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        Sprite(texture)
    }
    private val backgroundScrollSpeed = Vector2()

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerDeath::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.PlayerDeath::class, this)
    }

    override fun update(deltaTime: Float) {
        backgroundScrollSpeed.set(CURRENT_SCROLL_SPEED, 0f)

        backgrounds.withIndex().forEach { bkground ->
            bkground.value.run {
                scroll(
                    bkground.index.toFloat() / 4 * backgroundScrollSpeed.x * deltaTime,
                    bkground.index.toFloat() / 4 * backgroundScrollSpeed.y * deltaTime)
            }
        }

        platform.scroll(backgroundScrollSpeed.x * deltaTime, backgroundScrollSpeed.y * deltaTime)

        // render background
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            backgrounds.forEach { bg -> bg.draw(it)}
        }

        // render entity & bounding box
        forceSort()
        gameViewport.apply()
        super.update(deltaTime)

        // render platform
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            platform.draw(it)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity = $entity"}
        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity |entity| must have a GraphicComponent. entity = $entity"}

        if (graphic.sprite.texture == null) {
            LOG.error { "Entity has no texture for rendering. entity=$entity" }
            return
        }

        batch.use(gameViewport.camera.combined) {
            graphic.sprite.run {
                rotation = transform.rotationDeg
                setBounds(transform.interpolatedPosition.x, transform.interpolatedPosition.y, transform.size.x, transform.size.y)
                draw(it)
            }
        }

        if (IN_DEBUGGING) {
            shapeRenderer.use(ShapeRenderer.ShapeType.Line, gameViewport.camera.combined) {
                // render collider
                entity[ColliderComponent.mapper]?.let { collider ->
                    shapeRenderer.setColor(Color.GREEN)
                    shapeRenderer.rect(collider.bounding.x, collider.bounding.y,
                        collider.bounding.width, collider.bounding.height)
                }

                // render interaction zone
                entity[InteractComponent.mapper]?.let { interact ->
                    shapeRenderer.setColor(Color.RED)
                    shapeRenderer.rect(interact.zone.x, interact.zone.y,
                        interact.zone.width, interact.zone.height)
                }
            }
        }
    }

    override fun onEvent(event: GameEvent) {
        val deathEvent = event as GameEvent.PlayerDeath
        CURRENT_SCROLL_SPEED = 0f
    }
}
