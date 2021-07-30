package event

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxSet
import java.util.*
import kotlin.reflect.KClass

sealed class GameEvent {
    object PlayerDamaged: GameEvent() {
        lateinit var player: Entity
        var damage = 0

        override fun toString(): String {
            return "PlayerDamaged(damage=$damage)"
        }
    }

    object PlayerDeath: GameEvent() {
        var distance = 0

        override fun toString(): String {
            return "PlayerDeath(distance=$distance)"
        }
    }
}

interface GameEventListener {
    fun onEvent(event: GameEvent)
}

class GameEventManager {
    private val listeners = ObjectMap<KClass<out GameEvent>, GdxSet<GameEventListener>>()

    fun addListener(event: KClass<out GameEvent>, listener: GameEventListener) {
        var eventListeners = listeners[event]
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners.put(event, eventListeners)
        }
        eventListeners.add(listener)
    }

    fun removeListener(event: KClass<out GameEvent>, listener: GameEventListener) {
        listeners[event]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values().forEach { it.remove(listener) }
    }

    fun dispatchEvent(event: GameEvent) {
        listeners[event::class]?.forEach { it.onEvent(event) }
    }
}
