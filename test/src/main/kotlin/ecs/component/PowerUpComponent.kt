package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class PowerUpType(val animationType: AnimationType) {
    LIFE(AnimationType.HEART),
    DIAMOND(AnimationType.DIAMOND)
}

class PowerUpComponent: Component, Pool.Poolable {
    var type = PowerUpType.LIFE

    override fun reset() {
        type = PowerUpType.LIFE
    }

    companion object {
        val mapper = mapperFor<PowerUpComponent>()
    }
}
