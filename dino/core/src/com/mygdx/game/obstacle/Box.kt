package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.mygdx.game.GRAVITATIONAL_ACCELERATION
import ecs.component.*
import ktx.ashley.addComponent
import ktx.ashley.entity
import ktx.ashley.get
import ktx.ashley.with


private const val POWER_UP_SPAWN_SPEED = 5f

class Box: Obstacle {
    private val size = Vector2(1.5f, 1.5f)
    private val damage = 0
    private val animationType = AnimationType.BOX_IDLE
    private val colliderModifier = ColliderModifier(0.45f, 0.45f, 0.45f, 0.45f)

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
        if (other[PlayerComponent.mapper] == null) return

        other[StateComponent.mapper]?.let { state ->
            if (state.currentState.equals(State.IN_AIR)) {
                performAction(self, other, engine)
            }
        }
    }

    override fun performAction(self: Entity, other: Entity, engine: Engine) {

        self[AnimationComponent.mapper]?.let { animation ->
            animation.type = AnimationType.BOX_BREAK
            self.addComponent<RemoveComponent>(engine) {
                delay = 0.2f
            }
            self[TransformComponent.mapper]?.let { transform ->
                spawnCollectable(CollectableType.LIFE, engine, transform.position)
            }
        }
    }

    private fun spawnCollectable(collectableType: CollectableType, engine: Engine, position: Vector3) {
        engine.entity {
            with<TransformComponent> {
                setInitialPosition(position.x, position.y, position.z)
            }
            with<MoveComponent> {
                speed.y = POWER_UP_SPAWN_SPEED
                acceletration.y = GRAVITATIONAL_ACCELERATION
            }
            with<CollectableComponent> { type = collectableType }
            with<GraphicComponent>()
            with<AnimationComponent> { type = collectableType.animationType }
            with<ColliderComponent>()
        }
    }
}
