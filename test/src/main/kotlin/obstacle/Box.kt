package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import ecs.component.*
import ecs.system.POWER_UP_HEIGHT
import ktx.ashley.addComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with
import kotlin.math.max
import kotlin.random.Random.Default.nextInt

private const val HIT_ANIMATION_DURATION = 0.4f
private const val HIT_BUFFER_TIME = 0.8f
private const val POWER_UP_SPAWN_SPEED = 5f
private const val POWER_UP_SPAWN_Y_OFFSET = 0.2f

class Box: Obstacle {
    private val size = Vector2(1.5f, 1.5f)
    private val damage = 0
    private val animationType = AnimationType.BOX_IDLE
    private val colliderModifier = ColliderModifier()
    private val numOfHitToBreak = nextInt(1,5)
    private var hitBuffer = 0f
    private var hitCount = 0

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

    override fun onInteraction(self: Entity, other: Entity, engine: Engine) {
        if (other[PlayerComponent.mapper] == null) return

        hitBuffer = max(0f, hitBuffer - Gdx.graphics.deltaTime)
        if (HIT_BUFFER_TIME - hitBuffer >= HIT_ANIMATION_DURATION) {
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
            if (hitCount < numOfHitToBreak) {
                animation.type = AnimationType.BOX_HIT
                hitCount++
                hitBuffer = HIT_BUFFER_TIME
            } else {
                animation.type = AnimationType.BOX_BREAK
                self.addComponent<RemoveComponent>(engine) {
                    delay = 0.1f
                }
                self[TransformComponent.mapper]?.let { transform ->
                    spawnCollectable(CollectableType.DIAMOND, engine, transform.position)
                }
            }
        }
    }

    private fun spawnCollectable(collectableType: CollectableType, engine: Engine, position: Vector3) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(position.x, POWER_UP_HEIGHT + POWER_UP_SPAWN_Y_OFFSET, position.z)
            }
            with<ColliderComponent>()
            with<MoveComponent> {
                speed.y = POWER_UP_SPAWN_SPEED
            }
            with<CollectableComponent> { type = collectableType }
            with<GraphicComponent>()
            with<AnimationComponent> { type = collectableType.animationType }
        }
    }
}
