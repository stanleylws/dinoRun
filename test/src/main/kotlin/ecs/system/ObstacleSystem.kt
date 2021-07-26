package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import core.CURRENT_SCROLL_SPEED
import core.SCROLL_SPEED_TO_WORLD_RATIO
import core.V_WIDTH
import ecs.component.*
import event.GameEventManager
import event.GameEventPlayerDamaged
import event.GameEventType
import ktx.ashley.*
import ktx.log.info
import ktx.log.logger

private val LOG = logger<ObstacleSystem>()

private const val MAX_SPAWN_INTERVAL = 4.5f
private const val MIN_SPAWN_INTERVAL = 2.9f
private const val LIFE_GAIN = 25f
private const val SHIELD_GAIN = 25f

class ObstacleSystem(
    private val gameEventManager: GameEventManager
): IteratingSystem(allOf(ObstacleComponent::class, TransformComponent::class, MoveComponent::class).exclude(RemoveComponent::class).get()){
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    private var spawnTime = 0f

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        spawnTime -= deltaTime
        if (spawnTime <= 0f) {
            spawnTime = MathUtils.random(MIN_SPAWN_INTERVAL, MAX_SPAWN_INTERVAL)
            spawnObstacle(ObstacleType.SPIKE, V_WIDTH.toFloat(), 1.2f)
        }


    }

    private fun spawnObstacle(obstacleType: ObstacleType, posX: Float, posY: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(posX, posY, 0f)
            }
            with<MoveComponent> {
                speed.x = -1 * CURRENT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO
            }
            with<ObstacleComponent> { type = obstacleType }
            with<GraphicComponent>()
            with<AnimationComponent> { type = obstacleType.animationType}
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val move = entity[MoveComponent.mapper]
        requireNotNull(move) { "Entity |entity| must have a MoveComponent. entity = $entity" }
        val obstacle = entity[ObstacleComponent.mapper]
        requireNotNull(obstacle) { "Entity |entity| must have a ObstacleComponent. entity = $entity" }

        if (transform.position.x <= -1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        move.speed.x = -1 * CURRENT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO
        transform.boundingBox.set(
            transform.interpolatedPosition.x + 0.05f,
            transform.interpolatedPosition.y,
            transform.size.x * 0.9f,
            transform.size.y
        )

        playerEntities.forEach { playerEntity ->
            playerEntity[TransformComponent.mapper]?.let { playerTransform ->
                playerTransform.boundingBox.set(
                    playerTransform.interpolatedPosition.x + 0.4f,
                    playerTransform.interpolatedPosition.y + 0.2f,
                    playerTransform.size.x * 0.5f,
                    playerTransform.size.y
                )

                if (playerTransform.boundingBox.overlaps(transform.boundingBox)) {
                    notifyDamage(playerEntity, obstacle.damage)
                }

            }
        }
    }

    private fun notifyDamage(player: Entity, damage: Float) {
        gameEventManager.dispatchEvent(
            GameEventType.PLAYER_DAMAGED,
            GameEventPlayerDamaged.apply {
                this.player = player
                this.damage = damage
            }
        )
    }
}
