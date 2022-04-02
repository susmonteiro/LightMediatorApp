package com.example.lightmediator

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.IOException
import java.io.InputStream

class AudioInputStream(
    audioSource: Int = MediaRecorder.AudioSource.DEFAULT,
    sampleRate: Int = 44100,
    channelConfig: Int = AudioFormat.CHANNEL_IN_MONO,
    audioFormat: Int = AudioFormat.ENCODING_PCM_8BIT,
    bufferSizeInBytes: Int = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) : InputStream() {

    // todo check permissions to use mic
    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSizeInBytes).apply {
        startRecording()
    }

    @Deprecated("Use read(audioData, offset, length)")
    @Throws(IOException::class)
    override fun read(): Int {
        val tmp = byteArrayOf(0)
        read(tmp, 0, 1)
        return tmp[0].toInt()
    }

    @Throws(IOException::class)
    override fun read(audioData: ByteArray, offset: Int, length: Int): Int {
        try {
            return audioRecord.read(audioData, offset, length)
        } catch (e: Exception) {
            throw IOException(e)
        }
    }

    override fun close() {
        audioRecord.stop()
        audioRecord.release()
        super.close()
    }
}