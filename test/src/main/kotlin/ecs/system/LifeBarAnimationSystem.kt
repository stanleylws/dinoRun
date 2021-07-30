package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.get


class LifeBarAnimationSystem: IteratingSystem(allOf(LifeBarComponent::class).get()) {
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).get()
        )
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        playerEntities.forEach { playerEntity ->
            val player = playerEntity[PlayerComponent.mapper]
            requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $playerEntity" }

            entity[AnimationComponent.mapper]?.let { animation ->
                animation.type = when(player.life) {
                    1 -> AnimationType.LIFE_UI_ONE_HEART
                    2 -> AnimationType.LIFE_UI_TWO_HEART
                    3 -> AnimationType.LIFE_UI_THREE_HEART
                    else -> AnimationType.LIFE_UI_EMPTY
                }
            }
        }
    }
}
