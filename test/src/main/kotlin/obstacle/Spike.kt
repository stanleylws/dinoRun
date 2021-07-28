package obstacle

import com.badlogic.ashley.core.Entity
import ecs.component.*
import ecs.system.RenderSystem
import ktx.ashley.get
import ktx.log.info
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class Spike: Obstacle {

    private val damage = 0.5f
    private val animationType = AnimationType.SPIKE
    private val colliderModifier = ColliderModifier(0.05f, 0f, 0.9f, 0.2f)

    override fun getDamage(): Float {
        return damage
    }

    override fun getAnimationType(): AnimationType {
        return animationType
    }

    override fun getColliderModifier(): ColliderModifier {
       return colliderModifier
    }

    override fun onInteraction(self: Entity, other: Entity) {
         if (other[PlayerComponent.mapper] == null) return

        other[StateComponent.mapper]?.let { state ->
            if (state.currentState.equals(State.RUN)) {
                performAction(self, other)
            }
        }
    }

    override fun performAction(self: Entity, other: Entity) {
        self[TransformComponent.mapper]?.let { transform ->
            transform.position.y = 1.2f
        }
    }
}