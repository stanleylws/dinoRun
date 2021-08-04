package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalIteratingSystem
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import core.IN_DEBUGGING
import ecs.component.MAX_SHIELD
import ecs.component.MoveComponent
import ecs.component.PlayerComponent
import ecs.component.TransformComponent
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.getSystem

class DebugSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    init {
        setProcessing(IN_DEBUGGING)
    }

    override fun processEntity(entity: Entity, delta: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val player = entity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $entity" }
        val move = entity[MoveComponent.mapper]
        requireNotNull(move) { "Entity |entity| must have a MoveComponent. entity = $entity" }

        Gdx.graphics.setTitle("fps:${(1 / delta).toInt()} life: ${player.life} speed:${move.speed} acc: ${move.acceletration}")

        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) {
            player.shield = MAX_SHIELD
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
            player.diamondCollected++
        }
    }
}
