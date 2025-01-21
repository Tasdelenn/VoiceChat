package com.example.voicechat.audio

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import com.example.voicechat.utils.Constants
import java.io.File

class AudioManager(private val context: Context) : IAudioManager {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false

    private fun createMediaRecorder(): MediaRecorder {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    override fun startRecording(outputFile: File): Boolean {
        return try {
            mediaRecorder = createMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }
            isRecording = true
            true
        } catch (e: Exception) {
            Log.e(TAG, "Kayıt başlatılamadı", e)
            false
        }
    }

    override fun stopRecording() {
        try {
            if (isRecording) {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                }
                mediaRecorder = null
                isRecording = false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Kayıt durdurulamadı", e)
            // Hata durumunda bile kaynakları temizleyelim
            mediaRecorder?.release()
            mediaRecorder = null
            isRecording = false
        }
    }

    override fun startPlaying(file: File): Boolean {
        return try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                prepare()
                start()
            }
            isPlaying = true
            true
        } catch (e: Exception) {
            Log.e(TAG, "Oynatma başlatılamadı", e)
            false
        }
    }

    override fun stopPlaying() {
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            isPlaying = false
        } catch (e: Exception) {
            Log.e(TAG, "Oynatma durdurulamadı", e)
        }
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayer?.setOnCompletionListener { listener() }
    }

    override fun release() {
        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun isRecording() = isRecording
    override fun isPlaying() = isPlaying

    companion object {
        private const val TAG = "AudioManager"
    }
} 