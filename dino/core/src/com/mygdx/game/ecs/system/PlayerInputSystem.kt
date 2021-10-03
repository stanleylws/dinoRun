package ecs.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import com.mygdx.game.CURRENT_SCROLL_SPEED
import com.mygdx.game.DEFAULT_SCROLL_SPEED
import com.mygdx.game.GROUND_HEIGHT
import com.mygdx.game.V_HEIGHT
import ecs.component.*
import ktx.ashley.allOf
import ktx.ashley.exclude
import ktx.ashley.get
import ktx.log.logger
import kotlin.math.abs
import kotlin.math.max

private val LOG = logger<RenderSystem>()

private const val TOUCH_INTERVAL = 0.25f
private const val GAME_HUD_HEIGHT = 2f
private const val JUMP_COLD_DOWN = 0.25f
private const val FLOAT_EPSILON = 1e-5

class PlayerInputSystem(
    private val gameViewport: Viewport
): IteratingSystem(allOf(PlayerComponent::class, TransformComponent::class, StateComponent::class)
    .exclude(RemoveComponent::class).get()) {
    private var jumpCd = 0f
    private var touchInterval = 0f
    private var touchCount = 0
    private val tmpVec = Vector2()
    private var lastState: State = State.IDLE

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val state = entity[StateComponent.mapper]
        requireNotNull(state) { "Entity |entity| must have a StateComponent. entity = $entity" }
        val transform = entity[TransformComponent.mapper]
        requireNotNull(transform) { "Entity |entity| must have a TransformComponent. entity = $entity" }

        jumpCd = max(0f, jumpCd - deltaTime)

        val onGround = abs(transform.position.y - GROUND_HEIGHT) <= FLOAT_EPSILON
        if (onGround && state.currentState == State.IN_AIR) {
            jumpCd = JUMP_COLD_DOWN
        }
        val canJump = jumpCd <= 0f && onGround && state.currentState != State.JUMP && state.currentState != State.LEAP && state.currentState != State.IN_AIR

        touchInterval = max(0f, touchInterval - deltaTime)
        tmpVec.y = Gdx.input.y.toFloat()
        if (touchInterval <= 0f && touchCount > 0 && !Gdx.input.isTouched(0)) touchCount = 0
        if (Gdx.input.justTouched() && gameViewport.unproject(tmpVec).y < V_HEIGHT - GAME_HUD_HEIGHT) {
            touchCount++
            touchInterval = TOUCH_INTERVAL
        }

        state.currentState = when {
            // not processing when in hurt state
            state.currentState == State.HURT -> state.currentState
            !onGround && lastState == State.IN_AIR -> state.currentState
            !onGround && (lastState == State.JUMP || lastState == State.LEAP) -> State.IN_AIR
            // touch input
            Gdx.input.isTouched && Gdx.input.deltaY < -10  && canJump && lastState != State.RUN -> State.JUMP
            Gdx.input.isTouched && Gdx.input.deltaY < -10  && canJump && lastState == State.RUN -> State.LEAP
            touchCount == 1 && Gdx.input.isTouched(0) ->  State.WALK
            touchCount >= 2 && Gdx.input.isTouched(0) -> State.RUN

            // keyboard input
            Gdx.input.isKeyPressed(Input.Keys.SPACE) && canJump && lastState != State.RUN -> State.JUMP
            Gdx.input.isKeyPressed(Input.Keys.SPACE) && canJump && lastState == State.RUN -> State.LEAP
            Gdx.input.isKeyPressed(Input.Keys.A) -> State.WALK
            Gdx.input.isKeyPressed(Input.Keys.D) -> State.RUN
            else -> State.IDLE
        }
        lastState = state.currentState

        CURRENT_SCROLL_SPEED = when(state.currentState) {
            State.IN_AIR -> CURRENT_SCROLL_SPEED
            State.JUMP -> DEFAULT_SCROLL_SPEED
            State.LEAP -> DEFAULT_SCROLL_SPEED * 4
            State.WALK -> DEFAULT_SCROLL_SPEED * 2
            State.RUN -> DEFAULT_SCROLL_SPEED * 4
            else -> DEFAULT_SCROLL_SPEED
        }
    }
}
