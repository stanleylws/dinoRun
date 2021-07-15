package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import core.V_HEIGHT
import core.V_WIDTH
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.get

private const val UPDATE_RATE = 1 / 25f

class MoveSystem:
    IteratingSystem(allOf(TransformComponent::class, MoveComponent::class).get()){

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
        move.speed.x = when(state.currentState) {
            State.WALK -> 0f
            State.RUN -> player.moveSpeed
            else -> -0.3f
        }
        move.speed.y = when(state.currentState) {
            else -> 0f
        }
        moveEntity(transform, move, deltaTime)
    }

    private fun moveEntity(transform: TransformComponent, move: MoveComponent, deltaTime: Float) {
        transform.position.x = MathUtils.clamp(
            transform.position.x + move.speed.x * deltaTime,
            0f,
            V_WIDTH - transform.size.x
        )
        transform.position.y = MathUtils.clamp(
            transform.position.y + move.speed.y * deltaTime,
            1f,
            V_HEIGHT + 1f - transform.size.y
        )
    }
}