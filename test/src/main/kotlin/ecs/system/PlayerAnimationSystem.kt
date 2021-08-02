package ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.systems.IteratingSystem
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.get
import kotlin.math.max

class PlayerAnimationSystem: IteratingSystem(allOf(PlayerComponent::class, StateComponent::class, AnimationComponent::class).get()),
    EntityListener {

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        engine.addEntityListener(family, this)
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        engine.removeEntityListener(this)
    }

    override fun entityAdded(entity: Entity) {
    }

    override fun entityRemoved(entity: Entity) = Unit

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }
        val animation = entity[AnimationComponent.mapper]
        requireNotNull(animation) { "Entity |entity| must have a AnimationComponent. entity = $entity" }
        val player = entity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $entity" }

        animation.type = when(state.currentState) {
            State.WALK -> AnimationType.DINO_WALK
            State.RUN -> AnimationType.DINO_RUN
            State.ATTACK -> AnimationType.DINO_ATTACK
            State.HURT -> AnimationType.DINO_HURT
            State.FAINT -> AnimationType.DINO_HURT
            else -> AnimationType.DINO_IDLE
        }

        player.shield = max(0f, player.shield - deltaTime)
    }
}
