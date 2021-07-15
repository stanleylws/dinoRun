package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.log.info
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class PlayerInputSystem(
    private val gameViewport: Viewport
): IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, StateComponent::class).get()) {
    private val tmpVec = Vector2()

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }

        if (state.currentState.equals(State.HURT)) return

        state.currentState = when{
            Gdx.input.isKeyPressed(Input.Keys.SPACE) -> State.RUN
            Gdx.input.isKeyPressed(Input.Keys.A) -> State.ATTACK
            Gdx.input.isKeyPressed(Input.Keys.D) -> State.WALK
            else -> State.IDLE
        }
    }
}