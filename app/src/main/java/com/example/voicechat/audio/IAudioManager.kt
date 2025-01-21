package com.example.voicechat.audio

import java.io.File

interface IAudioManager {
    fun startRecording(outputFile: File): Boolean
    fun stopRecording()
    fun startPlaying(file: File): Boolean
    fun stopPlaying()
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
    fun isRecording(): Boolean
    fun isPlaying(): Boolean
} 