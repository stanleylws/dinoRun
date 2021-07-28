package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import ecs.component.*
import ecs.system.RenderSystem
import ktx.ashley.get
import ktx.log.info
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class Spike: Obstacle {
    private val size = Vector2(1f, 1f)
    private val damage = 0.5f
    private val animationType = AnimationType.SPIKE
    private val colliderModifier = ColliderModifier(0.05f, 0f, 0.9f, 0.2f)

    override fun setSize(width: Float, height: Float) {
        size.set(width, height)
    }

    override fun getSize(): Vector2 {
        return size
    }

    override fun getDamage(): Float {
        return damage
    }

    override fun getAnimationType(): AnimationType {
        return animationType
    }

    override fun getColliderModifier(): ColliderModifier {
       return colliderModifier
    }

    override fun onInteraction(self: Entity, other: Entity, engine: Engine) {
         if (other[PlayerComponent.mapper] == null) return

        other[StateComponent.mapper]?.let { state ->
            if (state.currentState.equals(State.RUN)) {
                performAction(self, other, engine)
            }
        }
    }

    override fun performAction(self: Entity, other: Entity, engine: Engine) {
        self[TransformComponent.mapper]?.let { transform ->
            transform.position.y = 1.2f
        }
    }
}