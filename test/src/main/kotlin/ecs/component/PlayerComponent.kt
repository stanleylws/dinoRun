package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

const val MAX_LIFE = 100f
const val MAX_SHIELD = 100f

class PlayerComponent: Component, Pool.Poolable {
    var life = 100f
    var maxLife = MAX_LIFE
    var shield = 0f
    var maxShield = MAX_SHIELD
    var moveSpeed = 2f

    override fun reset() {
        life = 100f
        maxLife = MAX_LIFE
        shield = 0f
        maxShield = MAX_SHIELD
        moveSpeed = 1f
    }

    companion object {
        val mapper = mapperFor<PlayerComponent>()
    }
}