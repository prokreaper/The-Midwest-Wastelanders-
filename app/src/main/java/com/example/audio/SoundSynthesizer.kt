package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Procedural low-latency arcade sound synthesizer using Android AudioTrack.
 * Generates dynamic waveforms for punches, kicks, blocks, special energy moves, and KOs.
 */
class SoundSynthesizer {

    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isMuted = false

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun isMuted(): Boolean = isMuted

    private fun playPcm(samples: ShortArray) {
        if (isMuted) return
        scope.launch {
            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                // Wait for playback to finish then release
                val durationMs = (samples.size * 1000L) / sampleRate
                kotlinx.coroutines.delay(durationMs + 30)
                track.stop()
                track.release()
            } catch (_: Exception) {
                // Audio device busy or unsupported - silently ignore
            }
        }
    }

    /**
     * Crisp light punch / jab hit sound (duration ~70ms)
     */
    fun playHitLight() {
        val count = (sampleRate * 0.07).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 60.0)
            val freq = 280.0 - (t * 1200.0)
            val wave = sin(2.0 * PI * freq * t) + (Random.nextDouble(-0.35, 0.35))
            buffer[i] = (wave * env * 24000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Heavy hit / knockdown impact with deep sub-bass thud (duration ~160ms)
     */
    fun playHitHeavy() {
        val count = (sampleRate * 0.16).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 24.0)
            val freq = 160.0 - (t * 600.0)
            val square = if (sin(2.0 * PI * freq * t) > 0) 0.6 else -0.6
            val noise = Random.nextDouble(-0.5, 0.5)
            val combined = (square * 0.7 + noise * 0.3)
            buffer[i] = (combined * env * 29000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Bone-breaking crack impact sound (duration ~140ms)
     */
    fun playBoneSnap() {
        val count = (sampleRate * 0.14).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 45.0)
            val snap = if (sin(2.0 * PI * 1800.0 * t) > 0) 0.8 else -0.8
            val crunch = Random.nextDouble(-0.6, 0.6)
            val bass = sin(2.0 * PI * 120.0 * t) * 0.5
            buffer[i] = ((snap * 0.4 + crunch * 0.4 + bass * 0.2) * env * 31000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Wet arterial blood splatter impact sound (duration ~120ms)
     */
    fun playGoreSplat() {
        val count = (sampleRate * 0.12).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 30.0)
            val wet = Random.nextDouble(-0.75, 0.75) * sin(2.0 * PI * 440.0 * t)
            val lowSquish = sin(2.0 * PI * 220.0 * t) * 0.4
            buffer[i] = ((wet + lowSquish) * env * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Guard / block deflection clink (duration ~90ms)
     */
    fun playBlock() {
        val count = (sampleRate * 0.09).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 40.0)
            val tone1 = sin(2.0 * PI * 880.0 * t)
            val tone2 = sin(2.0 * PI * 1320.0 * t) * 0.5
            buffer[i] = ((tone1 + tone2) * env * 26000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Attack swing whoosh (duration ~100ms)
     */
    fun playWhoosh() {
        val count = (sampleRate * 0.10).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = sin(PI * (i.toDouble() / count)) // Bell curve envelope
            val noise = Random.nextDouble(-0.7, 0.7)
            val sweep = sin(2.0 * PI * (400.0 - 250.0 * (i.toDouble() / count)) * t)
            buffer[i] = ((noise * 0.6 + sweep * 0.4) * env * 22000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Energy projectile blast launch (duration ~180ms)
     */
    fun playEnergyBlast() {
        val count = (sampleRate * 0.18).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 12.0)
            val freq = 300.0 + (t * 800.0) + sin(t * 120.0) * 50.0
            val wave = sin(2.0 * PI * freq * t)
            buffer[i] = (wave * env * 27000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Super Ultra move activation flare (duration ~320ms)
     */
    fun playSuperActivation() {
        val count = (sampleRate * 0.32).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = (i.toDouble() / count).coerceIn(0.0, 1.0) * exp(-t * 4.0)
            val f1 = 440.0 * (1.0 + t * 2.5)
            val f2 = 660.0 * (1.0 + t * 2.5)
            val f3 = 880.0 * (1.0 + t * 2.5)
            val wave = (sin(2.0 * PI * f1 * t) + sin(2.0 * PI * f2 * t) + sin(2.0 * PI * f3 * t)) / 3.0
            buffer[i] = (wave * env * 29000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Round start / Fight gong sound (duration ~400ms)
     */
    fun playFightGong() {
        val count = (sampleRate * 0.40).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 7.0)
            val f1 = 523.25 // C5
            val f2 = 783.99 // G5
            val wave = sin(2.0 * PI * f1 * t) * 0.6 + sin(2.0 * PI * f2 * t) * 0.4
            buffer[i] = (wave * env * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * Knockout buzzer / decisive climax chime (duration ~500ms)
     */
    fun playKoChime() {
        val count = (sampleRate * 0.50).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 5.0)
            val f1 = 220.0
            val f2 = 277.18
            val f3 = 329.63
            val wave = (sin(2.0 * PI * f1 * t) + sin(2.0 * PI * f2 * t) + sin(2.0 * PI * f3 * t)) / 3.0
            val sub = sin(2.0 * PI * 110.0 * t) * 0.4
            buffer[i] = ((wave + sub) * env * 28000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }

    /**
     * UI Select button blip
     */
    fun playUiClick() {
        val count = (sampleRate * 0.04).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val env = exp(-t * 70.0)
            val wave = sin(2.0 * PI * 800.0 * t)
            buffer[i] = (wave * env * 20000).toInt().coerceIn(-32767, 32767).toShort()
        }
        playPcm(buffer)
    }
}
