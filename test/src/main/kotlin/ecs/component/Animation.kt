package ecs.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import ktx.ashley.mapperFor
import ktx.collections.GdxArray

private const val DEFAULT_FRAME_DURATION = 1 / 20f
enum class AnimationType(
    val key: String,
    val speedRate: Float = 1f,
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP
) {
    NONE(""),
    DINO_IDLE("dino_idle", 0.5f),
    DINO_WALK("dino_walk", 0.5f),
    DINO_RUN("dino_run", 0.5f),
    DINO_ATTACK("dino_kick", 0.5f, Animation.PlayMode.NORMAL),
    DINO_HURT("dino_hurt", 0.5f, Animation.PlayMode.NORMAL),
    EXPLOSION("explosion", 7f),
    SPIKE("Idle")
}

class Animation2D(
    val type: AnimationType,
    keyFrames: GdxArray<out TextureRegion>,
    playMode: PlayMode = PlayMode.LOOP,
    speedRate: Float = 1f
): Animation<TextureRegion>(DEFAULT_FRAME_DURATION / speedRate, keyFrames, playMode)

class AnimationComponent: Component, Pool.Poolable {
    var type = AnimationType.DINO_IDLE
    var stateTime = 0f
    var offsetTime = 0f

    lateinit var animation: Animation2D

    override fun reset() {
        type = AnimationType.DINO_IDLE
        stateTime = 0f
        offsetTime = 0f
    }

    companion object {
        val mapper = mapperFor<AnimationComponent>()
    }
}