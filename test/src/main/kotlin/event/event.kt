package event

import com.badlogic.ashley.core.Entity
import ktx.collections.GdxSet
import java.util.*

enum class GameEventType {
    PLAYER_DAMAGED
}

interface GameEvent

object GameEventPlayerDamaged: GameEvent {
    lateinit var player: Entity
    var damage = 0

    override fun toString(): String {
        return "GameEventPlayerDamaged(damage=$damage)"
    }
}

interface GameEventListener {
    fun onEvent(type: GameEventType, data: GameEvent? = null)
}

class GameEventManager {
    private val listeners = EnumMap<GameEventType, GdxSet<GameEventListener>>(GameEventType::class.java)

    fun addListener(type: GameEventType, listener: GameEventListener) {
        var eventListeners = listeners[type]
        if (eventListeners == null) {
            eventListeners = GdxSet()
            listeners[type] = eventListeners
        }
        eventListeners.add(listener)
    }

    fun removeListener(type: GameEventType, listener: GameEventListener) {
        listeners[type]?.remove(listener)
    }

    fun removeListener(listener: GameEventListener) {
        listeners.values.forEach { it.remove(listener) }
    }

    fun dispatchEvent(type: GameEventType, data: GameEvent? = null) {
        listeners[type]?.forEach { it.onEvent(type, data) }
    }
}