package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.component.*
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.max

private val LOG = logger<RenderSystem>()

private const val DAMAGE_AREA_WIDTH = 0.5f
private const val DAMAGE_PER_SECOND = 25f
private const val DAMAGE_BUFFER_DURATION = 1f
private const val DEATH_EXPLOSION_DURATION = 1f

private var immuneTime = 0f

class DamageSystem:
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()){
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val player = entity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $entity" }
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }

        if (immuneTime < DAMAGE_BUFFER_DURATION / 2 && state.currentState.equals(State.HURT)) {
            state.currentState = State.IDLE
        }

        if (immuneTime == 0f && transform.position.x <= DAMAGE_AREA_WIDTH) {
            var damage = DAMAGE_PER_SECOND  * deltaTime
            if (player.shield > 0f) {
                val blockAmount = player.shield
                player.shield = max(0f, player.shield - damage)
                damage -= blockAmount

                if (damage <= 0f) return
            }

            player.life -= damage
            state.currentState = State.HURT
            immuneTime = DAMAGE_BUFFER_DURATION

            if (player.life <= 0f) {
                entity.addComponent<RemoveComponent>(engine) {
                    delay = DEATH_EXPLOSION_DURATION
                }
            }
        }

        immuneTime = max(0f, immuneTime - deltaTime)
    }
}