package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

class InteractComponent: Component, Pool.Poolable {
    var zone = Rectangle()

    override fun reset() {
        zone = Rectangle()
    }

    companion object {
        val mapper = mapperFor<InteractComponent>()
    }
}
