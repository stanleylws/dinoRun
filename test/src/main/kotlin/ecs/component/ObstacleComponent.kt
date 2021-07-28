package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import obstacle.Empty
import obstacle.Spike

interface Obstacle {
    fun getDamage(): Float
    fun getAnimationType(): AnimationType
    fun getColliderModifier(): ColliderModifier
    fun onInteraction(self: Entity, other: Entity)
    fun performAction(self: Entity, other: Entity)
}

enum class ObstacleType(
    val obj: Obstacle,
) {
    NONE(Empty()),
    SPIKE(Spike())
}

class ObstacleComponent: Component, Pool.Poolable {
    var type = ObstacleType.NONE

    override fun reset() {
        type = ObstacleType.NONE
    }

    companion object {
        val mapper = mapperFor<ObstacleComponent>()
    }
}