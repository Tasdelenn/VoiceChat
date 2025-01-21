package com.example.voicechat

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.voicechat.audio.AudioManager
import com.example.voicechat.audio.IAudioManager
import com.example.voicechat.utils.Constants
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.OnBackPressedCallback

class MainActivity : AppCompatActivity() {
    private lateinit var audioManager: IAudioManager
    
    // UI Components
    private lateinit var chronometer: Chronometer
    private lateinit var statusText: TextView
    private lateinit var playButton: Button
    private lateinit var recordButton: Button
    
    // State
    private var isRecording = false
    private var isPlaying = false
    private var outputFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        initializeAudioManager()
        initializeViews()
        setupClickListeners()
        setupBackHandler()
    }

    private fun initializeAudioManager() {
        audioManager = AudioManager(this)
    }

    private fun initializeViews() {
        chronometer = findViewById(R.id.chronometer)
        statusText = findViewById(R.id.statusText)
        playButton = findViewById(R.id.playButton)
        recordButton = findViewById(R.id.recordButton)
    }

    private fun setupClickListeners() {
        recordButton.setOnClickListener {
            if (checkPermission()) {
                handleRecordButtonClick()
            } else {
                requestPermission()
            }
        }

        playButton.setOnClickListener {
            handlePlayButtonClick()
        }
    }

    private fun handleRecordButtonClick() {
        if (!isRecording) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    private fun handlePlayButtonClick() {
        if (!isPlaying) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    private fun startRecording() {
        createOutputFile()?.let { file ->
            if (audioManager.startRecording(file)) {
                updateUIForRecordingStart()
            } else {
                showToast(getString(R.string.recording_start_failed))
            }
        }
    }

    private fun createOutputFile(): File? {
        val timestamp = SimpleDateFormat(Constants.FILE_FORMAT, Locale.getDefault())
            .format(Date())
        return File(getExternalFilesDir(null), "recording_$timestamp.mp3").also {
            outputFile = it
        }
    }

    private fun updateUIForRecordingStart() {
        isRecording = true
        recordButton.text = getString(R.string.stop_recording)
        playButton.isEnabled = false
        chronometer.apply {
            base = SystemClock.elapsedRealtime()
            start()
            visibility = View.VISIBLE
        }
        statusText.text = getString(R.string.recording_in_progress)
    }

    private fun stopRecording() {
        audioManager.stopRecording()
        updateUIForRecordingStop()
    }

    private fun updateUIForRecordingStop() {
        isRecording = false
        recordButton.text = getString(R.string.start_recording)
        playButton.isEnabled = true
        chronometer.apply {
            stop()
            visibility = View.INVISIBLE
        }
        updateStatusText()
        showToast(getString(R.string.recording_stopped))
    }

    private fun startPlaying() {
        outputFile?.let { file ->
            if (audioManager.startPlaying(file)) {
                updateUIForPlayingStart()
                setupPlaybackCompletion()
            } else {
                showToast(getString(R.string.playback_start_failed))
            }
        }
    }

    private fun updateUIForPlayingStart() {
        isPlaying = true
        playButton.text = getString(R.string.stop_playing)
        statusText.text = getString(R.string.playing_in_progress)
    }

    private fun setupPlaybackCompletion() {
        audioManager.setOnCompletionListener {
            stopPlaying()
        }
    }

    private fun stopPlaying() {
        audioManager.stopPlaying()
        updateUIForPlayingStop()
    }

    private fun updateUIForPlayingStop() {
        isPlaying = false
        playButton.text = getString(R.string.start_playing)
        updateStatusText()
    }

    private fun updateStatusText() {
        outputFile?.let {
            statusText.text = getString(
                R.string.last_recording,
                it.name,
                it.length() / 1024
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            Constants.PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showToast(getString(R.string.permission_granted))
            } else {
                showToast(getString(R.string.permission_denied))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSettings() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings_container, SettingsFragment())
            .addToBackStack(null)
            .commit()
        findViewById<View>(R.id.main_content).visibility = View.GONE
        findViewById<View>(R.id.settings_container).visibility = View.VISIBLE
    }

    fun onSettingsChanged() {
        if (isRecording) {
            stopRecording()
            startRecording()
        }
        showToast(getString(R.string.settings_updated))
    }

    override fun onStop() {
        super.onStop()
        audioManager.release()
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (supportFragmentManager.backStackEntryCount > 0) {
                        findViewById<View>(R.id.main_content).visibility = View.VISIBLE
                        findViewById<View>(R.id.settings_container).visibility = View.GONE
                        supportFragmentManager.popBackStack()
                    } else {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )
    }
} 