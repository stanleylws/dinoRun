package audio

import asset.MusicAsset
import asset.SoundAsset
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.utils.Pool
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import java.util.*
import kotlin.math.max

private val LOG = logger<AudioService>()
private const val MAX_SOUND_INSTANCES = 16

interface AudioService {
    fun play(soundAsset: SoundAsset, volume: Float = 1f, loop: Boolean = false)
    fun play(musicAsset: MusicAsset, volume: Float = 1f, loop: Boolean = true)
    fun pause()
    fun resume()
    fun stop(clearSounds: Boolean = true)
    fun stopSound(soundAsset: SoundAsset)
    fun update()
}

private class SoundRequest: Pool.Poolable {
    lateinit var soundAsset: SoundAsset
    var volume = 1f
    var loop = false

    override fun reset() {
        volume = 1f
        var loop = false
    }
}

private class SoundRequestPool: Pool<SoundRequest>() {
    override fun newObject() = SoundRequest()
}

class DefaultAudioService(private val assets: AssetStorage): AudioService {
    private val soundCache = EnumMap<SoundAsset, Sound>(SoundAsset::class.java)
    private val soundRequestPool = SoundRequestPool()
    private val soundRequests = EnumMap<SoundAsset, SoundRequest>(SoundAsset::class.java)
    private var currentMusic: Music? = null
    private var currentMusicAsset = MusicAsset.RAIN

    override fun play(soundAsset: SoundAsset, volume: Float, loop: Boolean) {
        when {
            soundAsset in soundRequests -> {
                // same sound request is done in one frame multiple times
                // play sound only once with the highest volume of both requests
                soundRequests[soundAsset]?.let { request ->
                    request.volume = max(request.volume, volume)
                }
            }
            soundRequests.size >= MAX_SOUND_INSTANCES -> {
                LOG.info { "Maximum sound request reached" }
                return
            }
            else -> {
                if (soundAsset.descriptor !in assets) {
                    LOG.error { "Trying to play a sound which is not loaded: $soundAsset" }
                } else if (soundAsset !in soundCache) {
                    soundCache[soundAsset] = assets[soundAsset.descriptor]
                }

                soundRequests[soundAsset] = soundRequestPool.obtain().apply {
                    this.soundAsset = soundAsset
                    this.volume = volume
                    this.loop = loop
                }
            }
        }
    }

    override fun play(musicAsset: MusicAsset, volume: Float, loop: Boolean) {
        if (currentMusic != null) {
            currentMusic?.stop()
            KtxAsync.launch {
                assets.unload(musicAsset.descriptor)
            }
        }

        val musicDeferred = assets.loadAsync(musicAsset.descriptor)
        KtxAsync.launch {
            musicDeferred.join()
            if (assets.isLoaded(musicAsset.descriptor)) {
                currentMusicAsset = musicAsset
                currentMusic = assets[musicAsset.descriptor].apply {
                    this.volume = volume
                    this.isLooping = loop
                    play()
                }
            }
        }
    }

    override fun pause() {
        currentMusic?.pause()
    }

    override fun resume() {
        currentMusic?.play()
    }

    override fun stop(clearSounds: Boolean) {
        currentMusic?.stop()
        if (clearSounds) {
            soundRequests.clear()
        }
    }

    override fun stopSound(soundAsset: SoundAsset) {
        soundCache[soundAsset]?.stop()
    }

    override fun update() {
        if (!soundRequests.isEmpty()) {
            soundRequests.values.forEach { request ->
                if (request.loop) {
                    soundCache[request.soundAsset]?.loop(request.volume)
                } else {
                    soundCache[request.soundAsset]?.play(request.volume)
                }
                soundRequestPool.free(request)
            }
            soundRequests.clear()
        }
    }
}
