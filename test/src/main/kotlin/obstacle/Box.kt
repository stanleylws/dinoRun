package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import ecs.component.*
import ktx.ashley.addComponent
import ktx.ashley.get
import kotlin.math.max

private const val HIT_ANIMATION_DURATION = 0.5f
private const val HIT_BUFFER_TIME = 0.8f

class Box: Obstacle {
    private val size = Vector2(1.5f, 1.5f)
    private val damage = 0f
    private val animationType = AnimationType.BOX_IDLE
    private val colliderModifier = ColliderModifier()
    private var hitBuffer = 0f
    private var hitCount = 0

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

        hitBuffer = max(0f, hitBuffer - Gdx.graphics.deltaTime)
        if (hitBuffer <= HIT_ANIMATION_DURATION) {
            self[AnimationComponent.mapper]?.let { animation ->
                animation.type = animationType
            }
        }

        other[StateComponent.mapper]?.let { state ->
            if (hitBuffer <= 0f && state.currentState.equals(State.ATTACK)) {
                performAction(self, other, engine)
            }
        }

    }

    override fun performAction(self: Entity, other: Entity, engine: Engine) {

        self[AnimationComponent.mapper]?.let { animation ->
            if (hitCount < 2) {
                animation.type = AnimationType.BOX_HIT
                hitCount++
                hitBuffer = HIT_BUFFER_TIME
            } else {
                animation.type = AnimationType.BOX_BREAK
                self.addComponent<RemoveComponent>(engine) {
                    delay = 0.1f
                }
            }
        }
    }
}