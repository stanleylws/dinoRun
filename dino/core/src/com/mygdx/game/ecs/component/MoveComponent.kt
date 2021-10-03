package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import java.util.*

class MoveComponent: Component, Pool.Poolable {
    val speed = Vector2()
    val acceletration = Vector2()

    override fun reset() {
        speed.set(Vector2.Zero)
        acceletration.set(Vector2.Zero)
    }

    companion object {
        val mapper = mapperFor<MoveComponent>()
    }
}