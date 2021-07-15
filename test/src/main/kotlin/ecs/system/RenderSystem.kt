package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ecs.component.GraphicComponent
import ecs.component.State
import ecs.component.StateComponent
import ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.graphics.use
import ktx.log.error
import ktx.log.logger

private val LOG = logger<RenderSystem>()

private const val BACKGROUND_SCROLL_SPEED = 0.01f

class RenderSystem(
    private val batch: Batch,
    private val gameViewport: Viewport,
    private val uiViewport: Viewport,
    backgroundTextures: Array<Texture>
): SortedIteratingSystem(
    allOf(TransformComponent::class, GraphicComponent::class).get(),
    compareBy { entity -> entity[TransformComponent.mapper]}
) {

    private val backgrounds = backgroundTextures.map { texture ->
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        Sprite(texture)
    }
    private val backgroundScrollSpeed = Vector2.Zero

    override fun update(deltaTime: Float) {
        uiViewport.apply()
        batch.use(uiViewport.camera.combined) {
            // render background
            backgrounds.withIndex().forEach { bkground ->
                bkground.value.run {
                    scroll(
                        bkground.index * backgroundScrollSpeed.x * deltaTime,
                        bkground.index * backgroundScrollSpeed.y * deltaTime)
                    draw(batch)
                }
            }
        }

        forceSort()
        gameViewport.apply()
        batch.use(gameViewport.camera.combined) {
            // render entity
            super.update(deltaTime)
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        require(transform != null) { "Entity |entity| must have a TransformComponent. entity = $entity"}
        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity |entity| must have a GraphicComponent. entity = $entity"}
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }

        var scrollSpeed = when(state.currentState) {
            State.WALK -> BACKGROUND_SCROLL_SPEED
            State.RUN -> BACKGROUND_SCROLL_SPEED * 2
            else -> BACKGROUND_SCROLL_SPEED / 2
        }
        backgroundScrollSpeed.set(scrollSpeed, 0f)

        if (graphic.sprite.texture == null) {
            LOG.error { "Entity has no texture for rendering. entity=$entity" }
            return
        }

        graphic.sprite.run {
            rotation = transform.rotationDeg
            setBounds(transform.interpolatedPosition.x, transform.interpolatedPosition.y, transform.size.x, transform.size.y)
            draw(batch)
        }
    }
}