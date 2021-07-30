package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class CollectableType(val animationType: AnimationType) {
    LIFE(AnimationType.HEART),
    DIAMOND(AnimationType.DIAMOND)
}

class CollectableComponent: Component, Pool.Poolable {
    var type = CollectableType.LIFE

    override fun reset() {
        type = CollectableType.LIFE
    }

    companion object {
        val mapper = mapperFor<CollectableComponent>()
    }
}
