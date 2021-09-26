package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import core.GROUND_HEIGHT
import core.V_WIDTH
import ktx.ashley.mapperFor
import obstacle.Box
import obstacle.Empty
import obstacle.Spike

interface Obstacle {
    fun setSize(width: Float, height: Float)
    fun getSize(): Vector2
    fun getDamage(): Int
    fun getAnimationType(): AnimationType
    fun getColliderModifier(): ColliderModifier
    fun getInteractZoneModifier(): ColliderModifier
    fun onInteraction(self: Entity, other: Entity, engine: Engine)
    fun performAction(self: Entity, other: Entity,  engine: Engine)
}

enum class ObstacleType(
    val spawnPosition: Vector2
) {
    NONE(Vector2()),
    SPIKE(Vector2(V_WIDTH.toFloat(), GROUND_HEIGHT + 0.2f)),
    BOX(Vector2(V_WIDTH.toFloat(), GROUND_HEIGHT + 2f)),
    TRAP(Vector2(V_WIDTH.toFloat(), GROUND_HEIGHT - 0.3f))
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