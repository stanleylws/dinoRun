package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor

enum class State {
    IDLE, WALK, RUN, ATTACK, HURT, FAINT
}

class StateComponent: Component, Pool.Poolable {
    var currentState = State.IDLE

    override fun reset() {
        currentState = State.IDLE
    }

    companion object {
        val mapper = mapperFor<StateComponent>()
    }
}
