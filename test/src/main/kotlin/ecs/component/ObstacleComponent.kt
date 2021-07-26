package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class ObstacleType(
    val animationType: AnimationType,
    val damage: Float = 0f
) {
    NONE(AnimationType.NONE),
    SPIKE(AnimationType.SPIKE, 25f),
}

class ObstacleComponent: Component, Pool.Poolable  {
    var type = ObstacleType.NONE
    var damage = 0f

    override fun reset() {
        type = ObstacleType.NONE
        damage = 0f
    }

    companion object {
        val mapper = mapperFor<ObstacleComponent>()
    }
}