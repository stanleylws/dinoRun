package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class ColliderModifier (
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val widthScale: Float = 1f,
    val heightScale: Float = 1f
)

class ColliderComponent: Component, Pool.Poolable {
    var bounding = Rectangle()
    var modifier = ColliderModifier()

    override fun reset() {
        bounding = Rectangle()
        modifier = ColliderModifier()
    }

    companion object {
        val mapper = mapperFor<ColliderComponent>()
    }
}
