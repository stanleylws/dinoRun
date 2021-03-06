package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.mygdx.game.*
import core.*
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.logger

private const val UPDATE_RATE = 1 / 60f
private const val JUMP_SPEED = 5f
private val LOG = logger<RenderSystem>()

class MoveSystem:
    IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).get()){
    private var accumulator = 0f

    override fun update(deltaTime: Float) {
        accumulator += deltaTime
        while (accumulator >= UPDATE_RATE) {
            accumulator -= UPDATE_RATE

            // save prev. position before calling update
            entities.forEach { entity ->
                entity[TransformComponent.mapper]?.let { transform ->
                    transform.prevPosition.set(transform.position)
                }
            }

            super.update(UPDATE_RATE)
        }

        // interpolate rendering position between prev. position and current position
        val alpha = accumulator / UPDATE_RATE
        entities.forEach { entity ->
            entity[TransformComponent.mapper]?.let { transform ->
                transform.interpolatedPosition.set(
                    MathUtils.lerp(transform.prevPosition.x, transform.position.x, alpha),
                    MathUtils.lerp(transform.prevPosition.y, transform.position.y, alpha),
                    transform.position.z
                )

                entity[ColliderComponent.mapper]?.let { collider ->
                    collider.bounding.set(
                        transform.interpolatedPosition.x + collider.modifier.offsetX,
                        transform.interpolatedPosition.y + collider.modifier.offsetY,
                        transform.size.x * collider.modifier.widthScale,
                        transform.size.y * collider.modifier.heightScale
                    )
                }
            }
        }
    }
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val move = entity[MoveComponent.mapper]
        requireNotNull(move) { "Entity |entity| must have a MoveComponent. entity = $entity" }

        val player = entity[PlayerComponent.mapper]
        if (player != null) {
            entity[StateComponent.mapper]?.let { state ->
                movePlayer(transform, move, player, state, deltaTime)
            }
        } else {
            moveEntity(transform, move, deltaTime)
        }
    }

    private fun movePlayer(transform: TransformComponent, move: MoveComponent,
                           player: PlayerComponent, state:StateComponent, deltaTime: Float) {
        val worldScrollSpeed = DEFAULT_SCROLL_SPEED * SCROLL_SPEED_TO_WORLD_RATIO
        move.speed.x = when(state.currentState) {
            State.WALK -> 0f
            State.RUN -> worldScrollSpeed
            State.JUMP -> -1 * worldScrollSpeed
            State.LEAP -> worldScrollSpeed
            State.IN_AIR -> move.speed.x
            else -> -1 * worldScrollSpeed
        }

        move.speed.y = when(state.currentState) {
            State.JUMP -> JUMP_SPEED
            State.LEAP -> JUMP_SPEED
            State.IN_AIR -> move.speed.y
            State.FAINT -> move.speed.y
            else -> 0f
        }

        move.acceletration.y = GRAVITATIONAL_ACCELERATION

        player.distance += (move.speed.x + worldScrollSpeed) * deltaTime
        moveEntity(transform, move, deltaTime)

        transform.position.x = MathUtils.clamp(
            transform.position.x,
            0f ,
            V_WIDTH.toFloat() - transform.size.x,
        )

        transform.position.y = MathUtils.clamp(
            transform.position.y,
            if (state.currentState == State.FAINT) -1f else GROUND_HEIGHT,
            V_HEIGHT + 1f - transform.size.y
        )
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        move.speed.x = move.speed.x + move.acceletration.x * deltaTime
        move.speed.y = move.speed.y + move.acceletration.y * deltaTime

        transform.position.x = MathUtils.clamp(
            transform.position.x + move.speed.x * deltaTime,
            -1f,
            V_WIDTH.toFloat(),
        )
        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            -1f,
            V_HEIGHT + 1f - transform.size.y
        )
    }
}