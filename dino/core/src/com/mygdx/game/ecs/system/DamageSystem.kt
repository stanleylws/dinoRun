package ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.component.*
import event.*
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.max

private val LOG = logger<RenderSystem>()

private const val HIT_ANIMATION_DURATION = 0.4f
private const val DAMAGE_IMMUNE_DURATION = 1f
private const val DEATH_EXPLOSION_DURATION = 2f

class DamageSystem(
    private val gameEventManager: GameEventManager
): GameEventListener,
    IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()) {
    private var immuneTime = 0f

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEvent.PlayerDamaged::class, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEvent.PlayerDamaged::class,this)
    }

    override fun update(deltaTime: Float) {
        immuneTime = max(0f, immuneTime - deltaTime)
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }
        val graphic = entity[GraphicComponent.mapper]
        requireNotNull(graphic) { "Entity |entity| must have a GraphicComponent. entity = $entity" }

        if (immuneTime < DAMAGE_IMMUNE_DURATION && state.currentState == State.HURT) {
            state.currentState = State.IDLE
            graphic.sprite.setAlpha(0.5f)
        } else if (immuneTime <= 0f) {
            graphic.sprite.setAlpha(1f)
        }
    }

    private fun doDamage(playerEntity: Entity, damage: Int) {
        val player = playerEntity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $playerEntity" }
        val state = playerEntity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $playerEntity" }

        if (immuneTime > 0f || player.shield > 0) return

        player.life -= damage
        state.currentState = State.HURT
        immuneTime = HIT_ANIMATION_DURATION + DAMAGE_IMMUNE_DURATION

        if (player.life <= 0f) {
            playerEntity.addComponent<RemoveComponent>(engine) {
                delay = DEATH_EXPLOSION_DURATION
            }
            gameEventManager.dispatchEvent(GameEvent.PlayerDeath.apply { distance = player.distance.toInt() })
            state.currentState = State.FAINT
            playerEntity[MoveComponent.mapper]?.let { move -> move.speed.y = 5f }
        }
    }

    override fun onEvent(event: GameEvent) {
        val damageEvent = event as GameEvent.PlayerDamaged
        doDamage(damageEvent.player, damageEvent.damage)
    }
}
