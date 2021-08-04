package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import core.CURRENT_SCROLL_SPEED
import core.SCROLL_SPEED_TO_WORLD_RATIO
import ecs.component.*
import event.GameEvent
import event.GameEventManager
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import kotlin.math.min

const val POWER_UP_HEIGHT = 1.5f
const val GRAVITATIONAL_ACCELERATION = -10f
private const val LIFE_GAIN = 1

class CollectSystem(private val gameEventManager: GameEventManager)
    : IteratingSystem(allOf(CollectableComponent::class).exclude(RemoveComponent::class).get()) {

    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val move = entity[MoveComponent.mapper]
        requireNotNull(move) { "Entity |entity| must have a MoveComponent. entity = $entity" }
        val collider = entity[ColliderComponent.mapper]
        requireNotNull(collider) { "Entity |entity| must have a ColliderComponent. entity = $entity" }
        val collectable = entity[CollectableComponent.mapper]
        requireNotNull(collectable) { "Entity |entity| must have a CollectableComponent. entity = $entity" }

        if (transform.position.x <= -1f) {
            entity.addComponent<RemoveComponent>(engine)
        }

        move.speed.x = -1 * CURRENT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO + if (transform.position.y > POWER_UP_HEIGHT) 1f else 0f
        if (transform.position.y > POWER_UP_HEIGHT) {
            move.acceletration.y = GRAVITATIONAL_ACCELERATION
        } else {
            move.speed.y = 0f
            move.acceletration.y = 0f
        }

        playerEntities.forEach { playerEntity ->
            val player = playerEntity[PlayerComponent.mapper]
            requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $playerEntity" }
            val playerCollider = playerEntity[ColliderComponent.mapper]
            requireNotNull(playerCollider) { "Entity |entity| must have a ColliderComponent. entity = $playerEntity" }

            if (transform.position.y <= POWER_UP_HEIGHT && playerCollider.bounding.overlaps(collider.bounding)) {
                entity.addComponent<RemoveComponent>(engine)
                when (collectable.type) {
                    CollectableType.LIFE -> player.life = min(player.maxLife, player.life + LIFE_GAIN)
                    CollectableType.DIAMOND -> player.diamondCollected++
                }
                gameEventManager.dispatchEvent(GameEvent.Collect.apply {
                    this.player = playerEntity
                    this.type = collectable.type
                })
            }
        }
    }
}
