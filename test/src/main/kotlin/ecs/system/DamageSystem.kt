package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import core.V_WIDTH
import ecs.component.*
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.max

private val LOG = logger<RenderSystem>()

private const val DAMAGE_MOVE_OFFSET_SCALE = 2.5f
private const val DAMAGE_AREA_WIDTH = 1f
private const val DAMAGE_PER_SECOND = 25f
private const val DAMAGE_BUFFER_DURATION = 1f
private const val DEATH_EXPLOSION_DURATION = 1f

private var immuneTime = 0f

class DamageSystem:
    IteratingSystem(allOf(DamageComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()){
    private var offsetX = 0f
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    override fun update(deltaTime: Float) {
        immuneTime = max(0f, immuneTime - deltaTime)
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }

        // move in and out of the explosion area
        transform.interpolatedPosition.x = transform.position.x + offsetX

        // do damage
        playerEntities.forEach{ playerEntity ->
            val tf = playerEntity[TransformComponent.mapper]
            requireNotNull(tf) { "Entity |entity| must have a TransformComponent. entity = $entity" }
            val player = playerEntity[PlayerComponent.mapper]
            requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $entity" }
            val state = playerEntity[StateComponent.mapper]
            requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }

            if (immuneTime < DAMAGE_BUFFER_DURATION / 2 && state.currentState.equals(State.HURT)) {
                state.currentState = State.IDLE
            }

            if (immuneTime == 0f && tf.position.x <= DAMAGE_AREA_WIDTH) {
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

            offsetX = DAMAGE_MOVE_OFFSET_SCALE * (V_WIDTH / 2f - tf.position.x) / (V_WIDTH / 2)
        }
    }
}
