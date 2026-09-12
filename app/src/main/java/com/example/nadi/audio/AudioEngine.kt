package com.example.nadi.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.PI
import kotlin.math.sin

class AudioEngine {
    private val sampleRate = 44100
    private var audioTrack: AudioTrack? = null
    @Volatile
    private var isRunning = false
    private var synthThread: Thread? = null

    private class SynthesizerTone(val frequency: Double) {
        var phase: Double = 0.0
        @Volatile var currentGain: Double = 0.0
        @Volatile var targetGain: Double = 1.0
        @Volatile var isMarkedForRemoval: Boolean = false
    }

    private val activeTones = CopyOnWriteArrayList<SynthesizerTone>()
    private var currentNoteTone: SynthesizerTone? = null
    private val currentDroneTones = CopyOnWriteArrayList<SynthesizerTone>()

    @Volatile
    private var masterVolume: Float = 1.0f

    private val fadeIncrement = 1.0 / (sampleRate * 0.025) // 25ms fade duration to avoid pops

    var onAudioError: ((String) -> Unit)? = null

    fun setVolume(volume: Float) {
        masterVolume = volume.coerceIn(0.0f, 1.0f)
    }

    @Synchronized
    fun startNote(frequency: Double) {
        currentNoteTone?.let { oldTone ->
            oldTone.targetGain = 0.0
            oldTone.isMarkedForRemoval = true
        }

        val newTone = SynthesizerTone(frequency)
        currentNoteTone = newTone
        activeTones.add(newTone)

        ensureTrackStarted()
    }

    @Synchronized
    fun stopNote() {
        currentNoteTone?.let { tone ->
            tone.targetGain = 0.0
            tone.isMarkedForRemoval = true
            currentNoteTone = null
        }
    }

    @Synchronized
    fun startDrone(frequencies: List<Double>) {
        stopDrone()

        for (freq in frequencies) {
            val tone = SynthesizerTone(freq)
            currentDroneTones.add(tone)
            activeTones.add(tone)
        }

        ensureTrackStarted()
    }

    @Synchronized
    fun stopDrone() {
        for (tone in currentDroneTones) {
            tone.targetGain = 0.0
            tone.isMarkedForRemoval = true
        }
        currentDroneTones.clear()
    }

    private fun ensureTrackStarted() {
        if (isRunning) return
        isRunning = true

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize.coerceAtLeast(1024)

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
        } catch (e: Exception) {
            isRunning = false
            onAudioError?.invoke("Failed to initialize audio: ${e.message}")
            return
        }

        synthThread = Thread {
            val buffer = ShortArray(512)
            try {
                while (isRunning) {
                    var hasActiveSounds = false

                    for (i in buffer.indices) {
                        var mixedSample = 0.0

                        for (tone in activeTones) {
                            val cGain = tone.currentGain
                            val tGain = tone.targetGain

                            if (cGain < tGain) {
                                tone.currentGain = (cGain + fadeIncrement).coerceAtMost(tGain)
                            } else if (cGain > tGain) {
                                tone.currentGain = (cGain - fadeIncrement).coerceAtLeast(tGain)
                            }

                            val updatedGain = tone.currentGain
                            if (updatedGain > 0.0 || !tone.isMarkedForRemoval) {
                                mixedSample += sin(tone.phase) * updatedGain
                                tone.phase += 2.0 * PI * tone.frequency / sampleRate
                                if (tone.phase > 2.0 * PI) {
                                    tone.phase -= 2.0 * PI
                                }
                                hasActiveSounds = true
                            }
                        }

                        val maxVolumeScale = 0.4 * masterVolume
                        val sampleValue = (mixedSample * maxVolumeScale * 32767.0).coerceIn(-32768.0, 32767.0)
                        buffer[i] = sampleValue.toInt().toShort()
                    }

                    activeTones.removeAll { it.isMarkedForRemoval && it.currentGain <= 0.0 }

                    val result = audioTrack?.write(buffer, 0, buffer.size) ?: -1
                    if (result < 0) {
                        onAudioError?.invoke("Audio write error: $result")
                        break
                    }

                    if (!hasActiveSounds && activeTones.isEmpty()) {
                        try {
                            Thread.sleep(20)
                        } catch (e: InterruptedException) {
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                onAudioError?.invoke("Audio synthesis error: ${e.message}")
            } finally {
                isRunning = false
            }
        }.apply {
            name = "NadiAudioSynthThread"
            start()
        }
    }

    @Synchronized
    fun release() {
        isRunning = false
        synthThread?.interrupt()
        try {
            synthThread?.join(500)
        } catch (e: Exception) {
            // Ignore
        }
        synthThread = null

        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        }
        audioTrack = null
        activeTones.clear()
        currentNoteTone = null
        currentDroneTones.clear()
    }
}
