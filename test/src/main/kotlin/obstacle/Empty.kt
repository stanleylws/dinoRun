package obstacle

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import ecs.component.AnimationType
import ecs.component.ColliderModifier
import ecs.component.Obstacle

class Empty: Obstacle {
    override fun setSize(width: Float, height: Float) = Unit

    override fun getSize(): Vector2 {
        return Vector2(1f, 1f)
    }

    override fun getDamage(): Int {
        return 0
    }

    override fun getAnimationType(): AnimationType {
        return AnimationType.NONE
    }

    override fun getColliderModifier(): ColliderModifier {
        return ColliderModifier()
    }

    override fun getInteractZoneModifier(): ColliderModifier {
        return ColliderModifier()
    }

    override fun onInteraction(self: Entity, other: Entity, engine: Engine) = Unit

    override fun performAction(self: Entity, other: Entity, engine: Engine) = Unit
}
