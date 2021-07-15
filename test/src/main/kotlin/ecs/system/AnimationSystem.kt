package ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import ecs.component.Animation2D
import ecs.component.AnimationComponent
import ecs.component.AnimationType
import ecs.component.GraphicComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.error
import ktx.log.logger
import java.util.*

private val LOG = logger<RenderSystem>()

class AnimationSystem(
    private val atlas: TextureAtlas
) : IteratingSystem(allOf(AnimationComponent::class, GraphicComponent::class).get()), EntityListener {
    private val animationCache = EnumMap<AnimationType, Animation2D>(AnimationType::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
        entity[AnimationComponent.mapper]?.let { aniCmp ->
            aniCmp.animation = getAnimation(aniCmp.type)
            val frame = aniCmp.animation.getKeyFrame(aniCmp.stateTime)
            entity[GraphicComponent.mapper]?.setSpriteRegion(frame)
        }
    }

    override fun entityRemoved(entity: Entity?) = Unit

    private fun getAnimation(type: AnimationType): Animation2D {
        var animation = animationCache[type]
        if (animation != null) return animation

        var regions = atlas.findRegions(type.key)
        if (regions.isEmpty) {
            LOG.error { "No regions founds for ${type}"}
        }

        animation = Animation2D(type, regions, type.playMode, type.speedRate)
        animationCache[type] = animation
        return animation
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val animation = entity[AnimationComponent.mapper]
        requireNotNull(animation) { "Entity |entity| must have a AnimationComponent. entity = $entity"}
        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity |entity| must have a GraphicComponent. entity = $entity"}

        if (animation.type == animation.animation.type) {
            animation.stateTime += deltaTime
        } else {
            animation.stateTime = 0f
            animation.animation = getAnimation(animation.type)
        }

        val frame = animation.animation.getKeyFrame(animation.stateTime)
        graphic.setSpriteRegion(frame)
    }
}