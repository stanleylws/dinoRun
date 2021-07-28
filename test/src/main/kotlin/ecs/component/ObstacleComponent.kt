package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import obstacle.Box
import obstacle.Empty
import obstacle.Spike

interface Obstacle {
    fun setSize(width: Float, height: Float)
    fun getSize(): Vector2
    fun getDamage(): Float
    fun getAnimationType(): AnimationType
    fun getColliderModifier(): ColliderModifier
    fun onInteraction(self: Entity, other: Entity, engine: Engine)
    fun performAction(self: Entity, other: Entity,  engine: Engine)
}

enum class ObstacleType {
    NONE,
    SPIKE,
    BOX
}

class ObstacleComponent: Component, Pool.Poolable {
    var instance: Obstacle = Empty()

    override fun reset() {
        instance = Empty()
    }

    companion object {
        val mapper = mapperFor<ObstacleComponent>()
    }
}