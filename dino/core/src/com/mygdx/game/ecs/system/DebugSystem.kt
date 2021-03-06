package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.mygdx.game.IN_DEBUGGING
import ecs.component.*
import event.GameEvent
import event.GameEventManager
import ktx.ashley.allOf
import ktx.ashley.get

class DebugSystem(private val gameEventManager: GameEventManager): IteratingSystem(allOf(PlayerComponent::class).get()) {
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
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }

        Gdx.graphics.setTitle("state:${state.currentState} life: ${player.life} speed:${move.speed} acc: ${move.acceletration}")

        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_1)) {
            player.shield = MAX_SHIELD
        } else if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
            player.diamondCollected++
            gameEventManager.dispatchEvent(GameEvent.Collect.apply {
                this.player = entity
                type = CollectableType.DIAMOND
            })
        }
    }
}
