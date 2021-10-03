package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.mygdx.game.GROUND_HEIGHT
import ecs.component.*
import ecs.system.RenderSystem
import ktx.ashley.get
import ktx.log.logger

private val LOG = logger<RenderSystem>()

class Trap: Obstacle {
    private val size = Vector2(1f, 1f)
    private var damage = 0
    private val animationType = AnimationType.TRAP
    private val colliderModifier = ColliderModifier(0.3f, 0.3f, 0.5f, 0.6f)

    override fun setSize(width: Float, height: Float) {
        size.set(width, height)
    }

    override fun getSize(): Vector2 {
        return size
    }

    override fun getDamage(): Int {
        return damage
    }

    override fun getAnimationType(): AnimationType {
        return animationType
    }

    override fun getColliderModifier(): ColliderModifier {
       return colliderModifier
    }

    override fun getInteractZoneModifier(): ColliderModifier {
        return colliderModifier
    }

    override fun onInteraction(self: Entity, other: Entity, engine: Engine) {
        self[AnimationComponent.mapper]?.let { animation ->
            if (animation.type.equals(AnimationType.TRAP_ACTIVATE)) {
                damage = 0
                return
            }
        }

        other[StateComponent.mapper]?.let { state ->
            if (state.currentState.equals(State.RUN)) {
                damage = 1
                performAction(self, other, engine)
            }
        }
    }

    override fun performAction(self: Entity, other: Entity, engine: Engine) {
        self[TransformComponent.mapper]?.let { transform ->
            transform.position.y = GROUND_HEIGHT
        }
        self[AnimationComponent.mapper]?.let { animation ->
            animation.type = AnimationType.TRAP_ACTIVATE
        }
    }
}
