package com.example.voicechat

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var isRecording = false
    private var isPlaying = false
    private val PERMISSION_REQUEST_CODE = 123
    private lateinit var chronometer: Chronometer
    private lateinit var statusText: TextView
    private lateinit var playButton: Button
    private var outputFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chronometer = findViewById(R.id.chronometer)
        statusText = findViewById(R.id.statusText)
        playButton = findViewById(R.id.playButton)
        val recordButton = findViewById<Button>(R.id.recordButton)

        recordButton.setOnClickListener {
            if (checkPermission()) {
                if (!isRecording) {
                    startRecording()
                } else {
                    stopRecording()
                }
            } else {
                requestPermission()
            }
        }

        playButton.setOnClickListener {
            if (!isPlaying) {
                startPlaying()
            } else {
                stopPlaying()
            }
        }
    }

    private fun startPlaying() {
        outputFile?.let { file ->
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(file.absolutePath)
                    prepare()
                    start()
                }
                isPlaying = true
                playButton.text = "Durdur"
                statusText.text = "Ses oynatılıyor: ${file.name}"

                // Oynatma bittiğinde
                mediaPlayer?.setOnCompletionListener {
                    stopPlaying()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Oynatma başlatılamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopPlaying() {
        mediaPlayer?.apply {
            try {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaPlayer = null
        isPlaying = false
        playButton.text = "Oynat"
        outputFile?.let {
            statusText.text = "Son kayıt: ${it.name}\nBoyut: ${it.length() / 1024}KB"
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun startRecording() {
        // Eğer oynatma devam ediyorsa durdur
        if (isPlaying) {
            stopPlaying()
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        outputFile = File(externalCacheDir, "VOICE_${timestamp}.mp3")

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile?.absolutePath)
            try {
                prepare()
                start()
                isRecording = true
                
                // UI güncellemeleri
                findViewById<Button>(R.id.recordButton).text = "Kaydı Durdur"
                playButton.isEnabled = false
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.visibility = android.view.View.VISIBLE
                chronometer.start()
                statusText.text = "Kayıt yapılıyor..."
                
                Toast.makeText(this@MainActivity, "Kayıt başladı", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Kayıt başlatılamadı", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            try {
                stop()
                reset()
                release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mediaRecorder = null
        isRecording = false

        // UI güncellemeleri
        findViewById<Button>(R.id.recordButton).text = "Kayıt Başlat"
        playButton.isEnabled = true
        chronometer.stop()
        chronometer.visibility = android.view.View.INVISIBLE
        
        outputFile?.let {
            val fileSize = it.length() / 1024 // KB cinsinden
            statusText.text = "Son kayıt: ${it.name}\nBoyut: ${fileSize}KB"
        }
        
        Toast.makeText(this, "Kayıt durduruldu", Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "İzin verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "İzin reddedildi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mediaRecorder?.release()
        mediaRecorder = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
} 