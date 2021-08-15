package ecs.component

import asset.SoundAsset
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
    val playMode: Animation.PlayMode = Animation.PlayMode.LOOP,
    val soundAsset: SoundAsset? = null,
    val volume: Float = 1f,
    val loopSound: Boolean = false
) {
    NONE(""),
    DINO_IDLE("dino_idle", 0.5f),
    DINO_WALK("dino_walk", 0.5f, soundAsset = SoundAsset.PLAYER_WALK, volume = 0.2f, loopSound = true),
    DINO_RUN("dino_run", 0.6f, soundAsset = SoundAsset.PLAYER_RUN, volume = 0.2f, loopSound = true),
    DINO_ATTACK("dino_kick", 0.5f, Animation.PlayMode.NORMAL),
    DINO_NORMAL_JUMP("dino_jump_normal", soundAsset = SoundAsset.PLAYER_JUMP, volume = 0.2f),
    DINO_RUNNING_JUMP("dino_jump_running", soundAsset = SoundAsset.PLAYER_JUMP, volume = 0.2f),
    DINO_HURT("dino_hurt", 0.5f, Animation.PlayMode.NORMAL, SoundAsset.PLAYER_DAMAGED, 0.2f),
    SPIKE("spike_idle"),
    BOX_IDLE("box_idle"),
    BOX_HIT("box_hit", 0.5f, Animation.PlayMode.NORMAL, SoundAsset.BOX_HIT, 0.2f),
    BOX_BREAK("box_break", 0.5f, Animation.PlayMode.NORMAL, SoundAsset.BOX_BREAK, 0.2f),
    HEART("heart", 0.5f),
    DIAMOND("diamond", 0.5f)
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