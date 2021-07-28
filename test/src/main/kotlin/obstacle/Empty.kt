package obstacle

import com.badlogic.ashley.core.Entity
import ecs.component.AnimationType
import ecs.component.ColliderModifier
import ecs.component.Obstacle

class Empty: Obstacle {
    override fun getDamage(): Float {
        return 0f
    }

    override fun getAnimationType(): AnimationType {
        return AnimationType.NONE
    }

    override fun getColliderModifier(): ColliderModifier {
        return ColliderModifier()
    }

    override fun onInteraction(self: Entity, other: Entity) = Unit

    override fun performAction(self: Entity, other: Entity) = Unit
}