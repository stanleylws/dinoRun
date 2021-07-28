package ecs.system

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import core.V_WIDTH
import ecs.component.*
import event.*
import ktx.ashley.addComponent
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.info
import ktx.log.logger
import kotlin.math.max

private val LOG = logger<RenderSystem>()

private const val DAMAGE_MOVE_OFFSET_SCALE = 2.5f
private const val DAMAGE_AREA_WIDTH = 1f
private const val DAMAGE_PER_HIT = 1f
private const val DAMAGE_BUFFER_DURATION = 1f
private const val DEATH_EXPLOSION_DURATION = 1f

private var immuneTime = 0f

class DamageSystem(
    private val gameEventManager: GameEventManager
): GameEventListener,
    IteratingSystem(allOf(DamageComponent::class, TransformComponent::class).exclude(RemoveComponent::class).get()){
    private var offsetX = 0f
    private val playerEntities by lazy {
        engine.getEntitiesFor(
            allOf(PlayerComponent::class).exclude(RemoveComponent::class).get()
        )
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        gameEventManager.addListener(GameEventType.PLAYER_DAMAGED, this)
    }

    override fun removedFromEngine(engine: Engine?) {
        super.removedFromEngine(engine)
        gameEventManager.removeListener(GameEventType.PLAYER_DAMAGED, this)
    }

    override fun update(deltaTime: Float) {
        immuneTime = max(0f, immuneTime - deltaTime)
        super.update(deltaTime)
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }

        // move in and out of the explosion area
        transform.interpolatedPosition.x = transform.position.x + offsetX

        // do damage
        playerEntities.forEach{ playerEntity ->
            val tf = playerEntity[TransformComponent.mapper]
            requireNotNull(tf) { "Entity |entity| must have a TransformComponent. entity = $entity" }
            val state = playerEntity[StateComponent.mapper]
            requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $playerEntity" }

            if (immuneTime < DAMAGE_BUFFER_DURATION / 2f && state.currentState.equals(State.HURT)) {
                state.currentState = State.IDLE
            }

            if (tf.position.x <= DAMAGE_AREA_WIDTH) {
                doDamage(playerEntity, DAMAGE_PER_HIT)
            }

            offsetX = DAMAGE_MOVE_OFFSET_SCALE * (V_WIDTH / 2f - tf.position.x) / (V_WIDTH / 2)
        }
    }

    private fun doDamage(playerEntity: Entity, damage: Float) {
        val player = playerEntity[PlayerComponent.mapper]
        requireNotNull(player) { "Entity |entity| must have a PlayerComponent. entity = $playerEntity" }
        val state = playerEntity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $playerEntity" }

        if (immuneTime > 0f) return

        if (player.shield > 0f) {
            val blockAmount = player.shield
            player.shield = max(0f, player.shield - damage)
            val finalDamage = damage - blockAmount

            if (finalDamage <= 0f) return
        }

        player.life -= damage
        state.currentState = State.HURT
        immuneTime = DAMAGE_BUFFER_DURATION

        if (player.life <= 0f) {
            playerEntity.addComponent<RemoveComponent>(engine) {
                delay = DEATH_EXPLOSION_DURATION
            }
        }
    }

    override fun onEvent(type: GameEventType, data: GameEvent?) {
        if (type == GameEventType.PLAYER_DAMAGED) {
            val eventData = data as GameEventPlayerDamaged
            doDamage(eventData.player, eventData.damage)
        }
    }
}
