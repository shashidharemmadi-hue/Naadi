package com.example.nadi.audio

import com.example.nadi.model.Swara

class MusicEngine(var baseSaFrequency: Double = 220.0) {
    
    /**
     * Calculates the absolute frequency for a given Swara based on the current base Sa frequency.
     */
    fun calculateFrequency(swara: Swara): Double {
        return baseSaFrequency * swara.ratio
    }
}
