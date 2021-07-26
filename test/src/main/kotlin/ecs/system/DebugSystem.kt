package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import core.IN_DEBUGGING
import ecs.component.PlayerComponent
import ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem

private const val WINDOW_INFO_UPDATE_RATE = 0.25f

class DebugSystem: IntervalIteratingSystem(allOf(PlayerComponent::class).get(), WINDOW_INFO_UPDATE_RATE) {
    init {
        setProcessing(IN_DEBUGGING)
    }

    override fun processEntity(entity: Entity) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val player = entity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $entity" }

        when {
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1) -> {
                // kill player
                transform.position.y = 1f
                player.life = 1f
                player.shield = 0f
            }
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4) -> {
                // disable movement
                engine.getSystem<MoveSystem>().setProcessing(false)
            }
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_5) -> {
                // disable movement
                engine.getSystem<MoveSystem>().setProcessing(true)
            }
        }

        Gdx.graphics.setTitle("DM Debug - pos:${transform.position}, life: ${player.life}, shield:${player.shield}")
    }
}