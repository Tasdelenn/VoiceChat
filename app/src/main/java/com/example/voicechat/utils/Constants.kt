package com.example.voicechat.utils

object Constants {
    const val PERMISSION_REQUEST_CODE = 123
    const val FILE_FORMAT = "yyyyMMdd_HHmmss"
    
    object AudioConfig {
        const val DEFAULT_SAMPLE_RATE = 44100
        const val DEFAULT_BIT_DEPTH = 16
    }
    
    object PreferenceKeys {
        const val SAMPLE_RATE = "sample_rate"
        const val BIT_DEPTH = "bit_depth"
        const val ACOUSTIC_ECHO_CANCELLATION = "acoustic_echo_cancellation"
        const val NOISE_SUPPRESSION = "noise_suppression"
        const val AUTO_GAIN_CONTROL = "auto_gain_control"
    }
} 