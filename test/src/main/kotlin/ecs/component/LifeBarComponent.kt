package ecs.component

import com.badlogic.ashley.core.Component
import ktx.ashley.mapperFor

class LifeBarComponent: Component{
    companion object {
        val mapper = mapperFor<LifeBarComponent>()
    }
}