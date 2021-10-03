package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class InteractComponent: Component, Pool.Poolable {
    var zone = Rectangle()
    var modifier = ColliderModifier()

    override fun reset() {
        zone = Rectangle()
        modifier = ColliderModifier()
    }

    companion object {
        val mapper = mapperFor<InteractComponent>()
    }
}
