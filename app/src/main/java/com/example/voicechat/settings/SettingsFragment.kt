package com.example.voicechat.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.voicechat.MainActivity
import com.example.voicechat.R
import com.example.voicechat.utils.Constants.PreferenceKeys

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "SettingsFragment"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initializePreferenceSummaries()
    }

    private fun initializePreferenceSummaries() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        
        // Örnekleme hızı
        findPreference<androidx.preference.ListPreference>(PreferenceKeys.SAMPLE_RATE)?.apply {
            summary = sharedPreferences.getString(PreferenceKeys.SAMPLE_RATE, "44100") + " Hz"
        }
        
        // Bit derinliği
        findPreference<androidx.preference.ListPreference>(PreferenceKeys.BIT_DEPTH)?.apply {
            summary = sharedPreferences.getString(PreferenceKeys.BIT_DEPTH, "16") + " bit"
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PreferenceKeys.SAMPLE_RATE -> updateSampleRateSummary(sharedPreferences)
            PreferenceKeys.BIT_DEPTH -> updateBitDepthSummary(sharedPreferences)
            PreferenceKeys.ACOUSTIC_ECHO_CANCELLATION,
            PreferenceKeys.NOISE_SUPPRESSION,
            PreferenceKeys.AUTO_GAIN_CONTROL -> {
                logSettingChange(key, sharedPreferences)
            }
        }
        notifySettingsChanged()
    }

    private fun updateSampleRateSummary(sharedPreferences: SharedPreferences?) {
        findPreference<androidx.preference.ListPreference>(PreferenceKeys.SAMPLE_RATE)?.apply {
            summary = sharedPreferences?.getString(PreferenceKeys.SAMPLE_RATE, "44100") + " Hz"
        }
    }

    private fun updateBitDepthSummary(sharedPreferences: SharedPreferences?) {
        findPreference<androidx.preference.ListPreference>(PreferenceKeys.BIT_DEPTH)?.apply {
            summary = sharedPreferences?.getString(PreferenceKeys.BIT_DEPTH, "16") + " bit"
        }
    }

    private fun logSettingChange(key: String, sharedPreferences: SharedPreferences?) {
        val value = sharedPreferences?.getBoolean(key, true)
        Log.d(TAG, "Ayar değişti: $key = $value")
    }

    private fun notifySettingsChanged() {
        (activity as? MainActivity)?.onSettingsChanged()
    }
} 