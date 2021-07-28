package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import core.CURRENT_SCROLL_SPEED
import core.SCROLL_SPEED_TO_WORLD_RATIO
import core.V_WIDTH
import ecs.component.*
import event.GameEventManager
import event.GameEventPlayerDamaged
import event.GameEventType
import ktx.ashley.*
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
            spawnObstacle(ObstacleType.SPIKE, V_WIDTH.toFloat(), 1f)
        }
    }

    private fun spawnObstacle(obstacleType: ObstacleType, posX: Float, posY: Float) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(posX, posY, 0f)
            }
            with<ColliderComponent> {
                modifier = obstacleType.obj.getColliderModifier()
            }
            with<InteractComponent>()
            with<MoveComponent> {
                speed.x = -1 * CURRENT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO
            }
            with<ObstacleComponent> { type = obstacleType }
            with<GraphicComponent>()
            with<AnimationComponent> { type = obstacleType.obj.getAnimationType() }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val collider = entity[ColliderComponent.mapper]
        requireNotNull(collider) { "Entity |entity| must have a ColliderComponent. entity = $entity" }
        val interact = entity[InteractComponent.mapper]
        requireNotNull(interact) { "Entity |entity| must have a InteractComponent. entity = $entity" }
        val move = entity[MoveComponent.mapper]
        requireNotNull(move) { "Entity |entity| must have a MoveComponent. entity = $entity" }
        val obstacle = entity[ObstacleComponent.mapper]
        requireNotNull(obstacle) { "Entity |entity| must have a ObstacleComponent. entity = $entity" }

        if (transform.position.x <= -1f) {
            entity.addComponent<RemoveComponent>(engine)
            return
        }

        move.speed.x = -1 * CURRENT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO

        // update collider and interaction position
        updateColliderBounding(transform, collider)

        interact.zone.set(
            transform.interpolatedPosition.x,
            transform.interpolatedPosition.y,
            transform.size.x,
            transform.size.y
        )

        playerEntities.forEach { playerEntity ->
            val playerTransform = playerEntity[TransformComponent.mapper]
            requireNotNull(playerTransform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
            val playerCollider = playerEntity[ColliderComponent.mapper]
            requireNotNull(playerCollider) { "Entity |entity| must have a ColliderComponent. entity = $entity" }

            updateColliderBounding(playerTransform, playerCollider)

            if (playerCollider.bounding.overlaps(interact.zone)) {
                obstacle.type.obj.onInteraction(entity, playerEntity)
            }

            if (playerCollider.bounding.overlaps(collider.bounding)) {
                notifyDamage(playerEntity, obstacle.type.obj.getDamage())
            }
        }
    }

    private fun updateColliderBounding(transform: TransformComponent, collider: ColliderComponent) {
        collider.bounding.set(
            transform.interpolatedPosition.x + collider.modifier.offsetX,
            transform.interpolatedPosition.y + collider.modifier.offsetY,
            transform.size.x * collider.modifier.widthScale,
            transform.size.y * collider.modifier.heightScale
        )
    }

    private fun notifyDamage(player: Entity, damage: Float) {
        if (damage <= 0f) return

        gameEventManager.dispatchEvent(
            GameEventType.PLAYER_DAMAGED,
            GameEventPlayerDamaged.apply {
                this.player = player
                this.damage = damage
            }
        )
    }
}
