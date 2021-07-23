package ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class DamageComponent: Component {
    companion object {
        val mapper = mapperFor<GraphicComponent>()
    }
}