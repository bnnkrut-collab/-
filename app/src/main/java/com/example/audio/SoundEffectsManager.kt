package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.ToneGenerator
import android.media.AudioManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time Audio Synthesizer for "Чекунец Калькулятор"
 * Generates distinct audio feedback:
 * - Mocking Laugh ("Звук насмешки") when cap is closed
 * - Liquid Glug / Drinking sound
 * - Empty Bottle Sound ("Звук опустошенной бутылки")
 * - Bar Order Chime
 */
class SoundEffectsManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default)

    /**
     * Plays mocking laugh sound ("Звук насмешки при закрытии чекушки")
     * Synthesizes a multi-tone staccato laugh "Heh-heh-heh-HA-HA!"
     */
    fun playMockingLaugh() {
        audioScope.launch {
            try {
                val sampleRate = 22050
                // Sequence of frequencies and durations representing a mocking laugh
                val laughPattern = listOf(
                    Pair(440f, 100), // "Heh"
                    Pair(0f, 40),    // pause
                    Pair(480f, 100), // "heh"
                    Pair(0f, 40),
                    Pair(520f, 120), // "heh"
                    Pair(0f, 50),
                    Pair(380f, 180), // "HA!"
                    Pair(0f, 40),
                    Pair(320f, 250)  // "Ha-a-a..."
                )

                playPcmSequence(sampleRate, laughPattern, isVibrato = true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays liquid gurgling / drinking sound ("Выпивание чекушки")
     */
    fun playDrinkSound() {
        audioScope.launch {
            try {
                val sampleRate = 22050
                val glugPattern = mutableListOf<Pair<Float, Int>>()
                var freq = 320f
                for (i in 0..5) {
                    glugPattern.add(Pair(freq, 80))
                    glugPattern.add(Pair(0f, 30))
                    freq -= 25f // Pitch drops as bottle empties
                }
                playPcmSequence(sampleRate, glugPattern, isVibrato = false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays empty bottle sound ("Звук опустошенной бутылки")
     * Synthesizes hollow glass resonance + sigh tone
     */
    fun playEmptyBottleSound() {
        audioScope.launch {
            try {
                val sampleRate = 22050
                // Low hollow resonance like blowing over top of empty glass bottle (~220Hz decay)
                val totalMs = 600
                val numSamples = (sampleRate * totalMs) / 1000
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val envelope = Math.exp(-3.0 * t) // Exponential decay
                    // Glass bottle resonance resonance: 215 Hz + harmonic 430 Hz
                    val sampleVal = (sin(2 * Math.PI * 215 * t) * 0.7 + sin(2 * Math.PI * 430 * t) * 0.3) * envelope
                    buffer[i] = (sampleVal * Short.MAX_VALUE * 0.8).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                playPcmBuffer(sampleRate, buffer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays Bar Order Celebration Bell Chime
     */
    fun playBarOrderChime() {
        audioScope.launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 90)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 300)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Plays soft click sound for calculator buttons
     */
    fun playButtonClick() {
        audioScope.launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 30)
            } catch (e: Exception) {
                // fallback gracefully
            }
        }
    }

    private fun playPcmSequence(sampleRate: Int, pattern: List<Pair<Float, Int>>, isVibrato: Boolean) {
        var totalMs = 0
        pattern.forEach { totalMs += it.second }
        val totalSamples = (sampleRate * totalMs) / 1000
        val buffer = ShortArray(totalSamples)

        var currentSample = 0
        for ((freq, durationMs) in pattern) {
            val samplesForNote = (sampleRate * durationMs) / 1000
            for (i in 0 until samplesForNote) {
                if (currentSample >= totalSamples) break
                val t = i.toDouble() / sampleRate
                if (freq > 0) {
                    val vibrato = if (isVibrato) sin(2 * Math.PI * 12 * t) * 15 else 0.0
                    val currentFreq = freq + vibrato
                    val envelope = sin(Math.PI * i / samplesForNote) // Smooth note window
                    val valSample = sin(2 * Math.PI * currentFreq * t) * envelope
                    buffer[currentSample] = (valSample * Short.MAX_VALUE * 0.75).toInt().toShort()
                } else {
                    buffer[currentSample] = 0
                }
                currentSample++
            }
        }

        playPcmBuffer(sampleRate, buffer)
    }

    private fun playPcmBuffer(sampleRate: Int, buffer: ShortArray) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
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
            .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(buffer, 0, buffer.size)
        track.play()
        audioScope.launch {
            kotlinx.coroutines.delay((buffer.size * 1000L) / sampleRate + 100)
            track.release()
        }
    }
}
