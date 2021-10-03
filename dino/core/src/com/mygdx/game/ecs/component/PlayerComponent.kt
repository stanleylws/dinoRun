package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

const val MAX_LIFE = 3
const val MAX_SHIELD = 6f

class PlayerComponent: Component, Pool.Poolable {
    var life = 3
    var maxLife = MAX_LIFE
    var shield = 0f
    var distance = 0f
    var diamondCollected = 0

    override fun reset() {
        life = 3
        maxLife = MAX_LIFE
        shield = 0f
        distance = 0f
        diamondCollected = 0
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}